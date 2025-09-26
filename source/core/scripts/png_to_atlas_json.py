#!/usr/bin/env python3
"""
Grid-based PNG -> LibGDX .atlas + TexturePacker-like JSON (Hash)
g
This tool slices a sprite sheet into fixed-size tiles (grid mode) and emits:
  1) A LibGDX-style .atlas file
  2) A TexturePacker-like JSON (Hash) alongside it

It also supports aliasing via --dup so you can add extra region names that
point to an already-sliced tile without consuming another grid slot, e.g.:
    --dup default=idle:0
"""
import argparse, json, sys
from pathlib import Path
from typing import List, Tuple, Dict, Any
from PIL import Image

# ----- Rich setup -----
# pip install rich rich-argparse
from rich.console import Console
from rich.theme import Theme
from rich.traceback import install as rich_traceback_install
from rich_argparse import RichHelpFormatter
from textwrap import dedent


RichHelpFormatter.styles.update({
    "argparse.prog": "bold cyan",
    "argparse.usage": "bold green",
    "argparse.metavar": "yellow",
    "argparse.text": "",          # description/epilog
    "argparse.help": "dim",
    "argparse.groups": "bold magenta",
})

EXAMPLES = dedent("""\
[bold magenta]Examples:[/bold magenta]

  [yellow]# 1[/yellow] Basic grid slice (32x32), names with counts
    [cyan]png_to_atlas_json.py[/cyan] sheet.png --grid 32 32 --names idle:4 walk:8 attack:6

  [yellow]# 2[/yellow] With margin and spacing
    [cyan]png_to_atlas_json.py[/cyan] sheet.png --grid 64 64 --margin 2 2 --spacing 1 1 --names coin:10

  [yellow]# 3[/yellow] Atlas only
    [cyan]png_to_atlas_json.py[/cyan] sheet.png --grid 16 16 --names tile:64 --out atlas

  [yellow]# 4[/yellow] JSON only + custom format/filter/repeat
    [cyan]png_to_atlas_json.py[/cyan] sheet.png --grid 48 48 --names enemy:12 --out json
      --format RGBA4444 --filter Nearest,Nearest --repeat x

  [yellow]# 5[/yellow] Aliases (dup)
    [cyan]png_to_atlas_json.py[/cyan] sheet.png --grid 32 32 --names idle:4 walk:4
      --dup default=idle:0,stand=idle:0 walk_fast:1=walk:1

  [yellow]# 6[/yellow] Mixed name list (implicit count = 1)
    [cyan]png_to_atlas_json.py[/cyan] sheet.png --grid 64 32 --names banner victory:3 lose:2 logo
""")

custom_theme = Theme({
    "ok": "bold green",
    "warn": "yellow",
    "err": "bold red",
    "hint": "cyan",
})
console = Console(theme=custom_theme)
rich_traceback_install(show_locals=False)

# ---------- Core grid slicing ----------
def regions_from_grid(img_w: int, img_h: int,
                      tile_w: int, tile_h: int,
                      mx: int, my: int,
                      sx: int, sy: int,
                      name_counts: List[Tuple[str, int]]) -> List[Dict[str, Any]]:
    """Lay out tiles left->right, top->bottom and assign in the order of name_counts."""
    regions: List[Dict[str, Any]] = []
    expanded: List[Tuple[str, int]] = []
    for name, count in name_counts:
        for idx in range(count):
            expanded.append((name, idx))

    row = col = i = 0
    while i < len(expanded):
        x = mx + col * (tile_w + sx)
        y = my + row * (tile_h + sy)

        # wrap to next row when hitting width
        if x + tile_w > img_w:
            row += 1
            col = 0
            if my + row * (tile_h + sy) + tile_h > img_h:
                break
            continue
        if y + tile_h > img_h:
            break

        name, idx = expanded[i]
        regions.append({
            "name": name, "index": idx,
            "x": x, "y": y, "w": tile_w, "h": tile_h,
            "rotate": False,
            "orig_w": tile_w, "orig_h": tile_h,
            "off_x": 0, "off_y": 0
        })
        i += 1
        col += 1

    if i < len(expanded):
        console.print(f"[warn][warn][/warn] Only produced {i}/{len(expanded)} tiles before hitting image bounds.", style="warn", highlight=False)
    return regions

# ---------- Aliases (dup) ----------
def parse_name_counts(seq: List[str]) -> List[Tuple[str, int]]:
    out: List[Tuple[str, int]] = []
    for item in seq:
        if ":" in item:
            name, count = item.split(":", 1)
            out.append((name, int(count)))
        else:
            out.append((item, 1))
    return out

def parse_dups(dup_args: List[str]) -> List[Tuple[str, int, str, int]]:
    items: List[str] = []
    for arg in dup_args:
        items.extend([s for s in arg.split(",") if s])

    result: List[Tuple[str, int, str, int]] = []
    for it in items:
        if "=" not in it or ":" not in it.split("=", 1)[1]:
            raise ValueError(f"Bad --dup entry: {it!r}")
        left, right = it.split("=", 1)
        tgt_name, tgt_idx_s = right.split(":", 1)
        tgt_idx = int(tgt_idx_s)

        if ":" in left:
            alias_name, alias_idx_s = left.split(":", 1)
            alias_idx = int(alias_idx_s)
        else:
            alias_name, alias_idx = left, 0

        result.append((alias_name, alias_idx, tgt_name, tgt_idx))
    return result

def apply_dups(regions: List[Dict[str, Any]],
               dups: List[Tuple[str, int, str, int]]) -> List[Dict[str, Any]]:
    lut: Dict[Tuple[str, int], Dict[str, Any]] = {(r["name"], r["index"]): r for r in regions}
    alias_regions: List[Dict[str, Any]] = []
    for alias_name, alias_idx, tgt_name, tgt_idx in dups:
        key = (tgt_name, tgt_idx)
        if key not in lut:
            raise ValueError(f"--dup target not found: {tgt_name}:{tgt_idx}")
        t = lut[key]
        alias_regions.append({
            "name": alias_name, "index": alias_idx,
            "x": t["x"], "y": t["y"], "w": t["w"], "h": t["h"],
            "rotate": t["rotate"],
            "orig_w": t["orig_w"], "orig_h": t["orig_h"],
            "off_x": t["off_x"], "off_y": t["off_y"]
        })
    return alias_regions + regions

# ---------- Writers ----------
def write_atlas(png_name: str, img_w: int, img_h: int,
                regions: List[Dict[str, Any]],
                atlas_path: Path,
                fmt: str, filt: str, repeat: str) -> None:
    lines: List[str] = [
        png_name,
        f"size: {img_w}, {img_h}",
        f"format: {fmt}",
        f"filter: {filt}",
        f"repeat: {repeat}",
        ""
    ]
    for r in regions:
        lines += [
            f"{r['name']}",
            f"  rotate: {'true' if r['rotate'] else 'false'}",
            f"  xy: {r['x']}, {r['y']}",
            f"  size: {r['w']}, {r['h']}",
            f"  orig: {r['orig_w']}, {r['orig_h']}",
            f"  offset: {r['off_x']}, {r['off_y']}",
            f"  index: {r['index']}"
        ]
    atlas_path.write_text("\n".join(lines) + "\n", encoding="utf-8")

def write_json(png_name: str, img_w: int, img_h: int,
               regions: List[Dict[str, Any]],
               json_path: Path) -> None:
    frames: Dict[str, Any] = {}
    for r in regions:
        key = f"{r['name']}_{r['index']}"
        frames[key] = {
            "frame": {"x": r["x"], "y": r["y"], "w": r["w"], "h": r["h"]},
            "rotated": r["rotate"],
            "trimmed": (r["off_x"] != 0 or r["off_y"] != 0 or r["w"] != r["orig_w"] or r["h"] != r["orig_h"]),
            "spriteSourceSize": {"x": r["off_x"], "y": r["off_y"], "w": r["w"], "h": r["h"]},
            "sourceSize": {"w": r["orig_w"], "h": r["orig_h"]},
            "pivot": {"x": 0.5, "y": 0.5},
            "index": r["index"]
        }
    meta = {
        "app": "png_to_atlas_json.py",
        "version": "1.5",
        "image": png_name,
        "size": {"w": img_w, "h": img_h},
        "scale": "1"
    }
    json_path.write_text(json.dumps({"frames": frames, "meta": meta}, indent=2), encoding="utf-8")

# ---------- CLI ----------
def main():
    class _Formatter(RichHelpFormatter, argparse.ArgumentDefaultsHelpFormatter, argparse.RawDescriptionHelpFormatter):
        pass

    parser = argparse.ArgumentParser(
        prog="png_to_atlas_json.py",
        description="Convert a PNG sprite sheet into a LibGDX .atlas and/or a TexturePacker-like JSON (Hash).",
        formatter_class=lambda prog: _Formatter(prog, max_help_position=30, width=120),
        epilog=EXAMPLES
    )

    parser.add_argument("png", type=Path, help="Path to the PNG sprite sheet to slice.")
    parser.add_argument("--grid", nargs=2, type=int, metavar=("TILE_W", "TILE_H"), required=True,
                        help="Tile width and height.")
    parser.add_argument("--margin", nargs=2, type=int, default=(0, 0), metavar=("MX", "MY"),
                        help="Margin from top-left in pixels.")
    parser.add_argument("--spacing", nargs=2, type=int, default=(0, 0), metavar=("SX", "SY"),
                        help="Spacing between tiles in pixels.")
    parser.add_argument("--names", nargs="+", required=True,
                        help="Sequence of names, optionally with counts like walk:8 idle:4 attack:6.")
    parser.add_argument("--dup", nargs="*", default=[],
                        help="Aliases mapping. Example: alias=targetName:idx or alias:idx=targetName:idx. "
                             "Comma separated entries allowed.")
    parser.add_argument("--format", default="RGBA8888",
        help="Pixel format to write in the .atlas file (e.g., RGBA8888, RGB565).")
    parser.add_argument( "--filter", default="Linear,Linear",
        help="Texture filtering for minification/magnification, written into the atlas header.")
    parser.add_argument("--repeat", default="none",
        help="Texture repeat mode: none, x, y, or xy.")
    parser.add_argument("--out", choices=["both", "atlas", "json"], default="both",
        help="Which outputs to generate: both .atlas and .json, or just one of them.")

    args = parser.parse_args()

    if not args.png.exists():
        console.print("PNG not found.", style="err")
        sys.exit(1)

    with console.status("[hint]Loading image[/hint]"):
        try:
            with Image.open(args.png) as im:
                im = im.convert("RGBA")
                img_w, img_h = im.size
        except Exception as e:
            console.print(f"Failed to open image: {e}", style="err")
            sys.exit(1)

    try:
        name_counts = parse_name_counts(args.names)
        regions = regions_from_grid(img_w, img_h,
                                    args.grid[0], args.grid[1],
                                    args.margin[0], args.margin[1],
                                    args.spacing[0], args.spacing[1],
                                    name_counts)
        if args.dup:
            dups = parse_dups(args.dup)
            regions = apply_dups(regions, dups)
    except Exception as e:
        console.print(str(e), style="err")
        sys.exit(1)

    wrote_any = False
    if args.out in ("both", "atlas"):
        atlas_path = args.png.with_suffix(".atlas")
        write_atlas(args.png.name, img_w, img_h, regions, atlas_path, args.format, args.filter, args.repeat)
        console.print(f"✔ Wrote {atlas_path}", style="ok")
        wrote_any = True

    if args.out in ("both", "json"):
        json_path  = args.png.with_suffix(".json")
        write_json(args.png.name, img_w, img_h, regions, json_path)
        console.print(f"✔ Wrote {json_path}", style="ok")
        wrote_any = True

    if not wrote_any:
        console.print("Nothing written (unexpected --out value).", style="err")
        sys.exit(1)

if __name__ == "__main__":
    main()
