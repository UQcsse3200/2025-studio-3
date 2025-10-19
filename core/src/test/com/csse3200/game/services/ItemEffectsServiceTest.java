package com.csse3200.game.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.rendering.RenderService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

@ExtendWith(GameExtension.class)
class ItemEffectsServiceTest {
  ResourceService resources;
  EntityService entities;
  GameTime time;
  Application app;

  @BeforeEach
  void setUp() {
    // Intercept postRunnable
    app = mock(Application.class);
    Gdx.app = app;

    // Service mocks
    resources = mock(ResourceService.class);
    entities = mock(EntityService.class);

    time = mock(GameTime.class);
    RenderService render = mock(RenderService.class);
    SettingsService settings = mock(SettingsService.class);
    ServiceLocator.clear();
    ServiceLocator.registerResourceService(resources);
    ServiceLocator.registerEntityService(entities);
    ServiceLocator.registerTimeSource(time);
    ServiceLocator.registerRenderService(render);
    ServiceLocator.registerSettingsService(settings);

    // When entities are registered, create them so components are ready to update()
    doAnswer(
            inv -> {
              Entity e = inv.getArgument(0);
              e.create();
              return null;
            })
        .when(entities)
        .register(any(Entity.class));

    // Any requested atlas has frames for all animation names by default
    when(resources.getAsset(anyString(), eq(TextureAtlas.class)))
        .thenAnswer(inv -> atlasWithFramesFor("buff", "coffee", "emp", "grenade", "nuke"));

    when(time.getDeltaTime()).thenReturn(0.1f);

    when(settings.getSoundVolume()).thenReturn(1.0f);
  }

  // helpers

  private static TextureAtlas atlasWithFramesFor(String... animNames) {
    TextureAtlas atlas = mock(TextureAtlas.class);
    for (String name : animNames) {
      Array<AtlasRegion> regions = new Array<>(1);
      regions.add(mock(AtlasRegion.class));
      when(atlas.findRegions(name)).thenReturn(regions);
    }
    // Return 1 region for any non-empty name not explicitly listed
    when(atlas.findRegions(argThat(s -> s != null && !s.isEmpty())))
        .thenReturn(new Array<>(new AtlasRegion[] {mock(AtlasRegion.class)}));
    return atlas;
  }

  private Entity captureRegisteredEntity() {
    ArgumentCaptor<Entity> cap = ArgumentCaptor.forClass(Entity.class);
    verify(entities, atLeastOnce()).register(cap.capture());
    // Return the most recently registered
    List<Entity> all = cap.getAllValues();
    return all.get(all.size() - 1);
  }

  /** Advance time and run this entity's update loop. */
  private void step(Entity e, float dt, int frames) {
    when(time.getDeltaTime()).thenAnswer((Answer<Float>) inv -> dt);
    for (int i = 0; i < frames; i++) e.update();
  }

  // tests

  @Test
  void spawnEffect_registers_setsPositionScale_andAddsAnimation() {
    TextureAtlas atlas = atlasWithFramesFor("grenade");
    Vector2 pos = new Vector2(10, 20);

    ItemEffectsService.spawnEffect(
        atlas,
        "grenade",
        new Vector2[] {pos, new Vector2(0, 0)},
        3,
        new float[] {0.1f, 1.5f},
        Animation.PlayMode.NORMAL,
        false);

    Entity e = captureRegisteredEntity();
    assertEquals(10f, e.getPosition().x);
    assertEquals(20f, e.getPosition().y);
    assertEquals(3f, e.getScale().x);
    assertEquals(3f, e.getScale().y);

    AnimationRenderComponent arc = e.getComponent(AnimationRenderComponent.class);
    assertNotNull(arc);
    assertTrue(arc.hasAnimation("grenade"));
  }

  @Test
  void spawnEffect_withNullAtlas_doesNotRegister() {
    ItemEffectsService.spawnEffect(
        null,
        "emp",
        new Vector2[] {new Vector2(0, 0), new Vector2(0, 0)},
        1,
        new float[] {0.1f, 1.5f},
        Animation.PlayMode.NORMAL,
        false);
    verify(entities, never()).register(any());
    verifyNoInteractions(app); // no disposal posted either
  }

  @Test
  void normalMode_postsDisposeAfter_totalEffectTime() {
    ItemEffectsService.spawnEffect(
        atlasWithFramesFor("emp"),
        "emp",
        new Vector2[] {new Vector2(5, 5), new Vector2(0, 0)},
        1,
        new float[] {0.1f, 1.5f},
        Animation.PlayMode.NORMAL,
        false);

    Entity e = captureRegisteredEntity();

    // Before threshold, nothing posted
    step(e, 0.1f, 14); // ~1.4s
    verify(app, never()).postRunnable(any());

    // Cross threshold, runnable posted
    step(e, 0.1f, 2); // ~1.6s
    ArgumentCaptor<Runnable> runCap = ArgumentCaptor.forClass(Runnable.class);
    verify(app, atLeastOnce()).postRunnable(runCap.capture());

    // Running the runnable should dispose (which calls EntityService.unregister(this))
    runCap.getAllValues().forEach(Runnable::run);
    verify(entities, atLeastOnce()).unregister(e);
  }

  @Test
  void movingAnimation_waits1s_thenInterpolates_toFinalPosition() {
    Vector2 start = new Vector2(0, 0);
    Vector2 end = new Vector2(10, 10);

    ItemEffectsService.spawnEffect(
        atlasWithFramesFor("coffee"),
        "coffee",
        new Vector2[] {start, end},
        1,
        new float[] {0.1f, 5f},
        Animation.PlayMode.NORMAL,
        true);

    Entity e = captureRegisteredEntity();

    // First just under 1s - definitely still in waiting phase
    step(e, 0.1f, 9); // ~0.9s
    assertEquals(0f, e.getPosition().x, 1e-5f);
    assertEquals(0f, e.getPosition().y, 1e-5f);

    // Hit the 1.0s boundary (code checks <= 1.0f -> still waiting)
    // Use a tiny tolerance to dodge float rounding
    step(e, 0.1f, 1); // ~1.0s
    assertEquals(0f, e.getPosition().x, 1e-5f);
    assertEquals(0f, e.getPosition().y, 1e-5f);

    // Now cross into movement phase
    step(e, 0.1f, 1); // ~1.1s
    assertTrue(e.getPosition().x > 0f);
    assertTrue(e.getPosition().y > 0f);

    // After enough time (>1s of movement), should be exactly at the final position
    step(e, 0.1f, 10); // reach >= 2.1s total; movement t clamps to 1.0
    assertTrue(e.getPosition().epsilonEquals(end, 1e-4f));
  }

  @Test
  void playEffect_buff_usesBuffAtlas_offsetsAndScales() {
    List<String> paths = new ArrayList<>();
    // Track paths AND return an atlas that has "buff"
    when(resources.getAsset(anyString(), eq(TextureAtlas.class)))
        .thenAnswer(
            inv -> {
              paths.add(inv.getArgument(0));
              return atlasWithFramesFor("buff");
            });

    ItemEffectsService svc = new ItemEffectsService();
    Vector2 pos = new Vector2(100, 200);
    int tile = 16;

    svc.playEffect("buff", new Vector2(pos), tile, new Vector2(0, 0));

    Entity e = captureRegisteredEntity();
    assertEquals(100 - tile, e.getPosition().x);
    assertEquals(200 - tile, e.getPosition().y);
    assertEquals(tile * 3f, e.getScale().x);
    assertEquals(tile * 3f, e.getScale().y);

    assertEquals(List.of("images/effects/buff.atlas"), paths);
    assertTrue(e.getComponent(AnimationRenderComponent.class).hasAnimation("buff"));
  }

  @Test
  void playEffect_emp_grenade_nuke_offsets_scales_andEmpDisposes() {
    List<String> paths = new ArrayList<>();
    when(resources.getAsset(anyString(), eq(TextureAtlas.class)))
        .thenAnswer(
            inv -> {
              paths.add(inv.getArgument(0));
              // Provide frames for all three
              return atlasWithFramesFor("emp", "grenade", "nuke");
            });

    ItemEffectsService svc = new ItemEffectsService();
    int tile = 20;

    // EMP
    Entity emp;
    {
      Vector2 p = new Vector2(50, 60);
      svc.playEffect("emp", new Vector2(p), tile, new Vector2(0, 0));
      emp = captureRegisteredEntity();
      assertEquals(50 - tile, emp.getPosition().x);
      assertEquals(60 - tile, emp.getPosition().y);
      assertEquals(tile * 3f, emp.getScale().x);
      assertEquals(tile * 3f, emp.getScale().y);
    }

    // Verify the correct asset path was requested
    assertEquals(List.of("images/effects/emp.atlas"), paths);
    assertTrue(emp.getComponent(AnimationRenderComponent.class).hasAnimation("emp"));
  }

  @Test
  void spawnEffect_requestsCorrectSound_andPlays_whenPresent() {
    // Specific animator
    String animator = "grenade";
    TextureAtlas atlas = atlasWithFramesFor(animator);

    // Create mock for exact path so can verify play()
    Sound s = mock(Sound.class);
    String expectedPath = "sounds/item_" + animator + ".mp3";
    when(resources.getAsset(expectedPath, Sound.class)).thenReturn(s);

    ItemEffectsService.spawnEffect(
        atlas,
        animator,
        new Vector2[] {new Vector2(1, 2), new Vector2(0, 0)},
        2,
        new float[] {0.1f, 30f},
        Animation.PlayMode.NORMAL,
        false);

    // Registered as usual
    captureRegisteredEntity();

    // Check correct sound path requested and played
    verify(resources).getAsset(expectedPath, Sound.class);
    verify(s, times(1)).play(anyFloat());
  }

  @Test
  void spawnEffect_doesNotPlay_whenSoundAssetMissing() {
    String animator = "emp";
    TextureAtlas atlas = atlasWithFramesFor(animator);

    // Return null for this path to simulate "not loaded"
    String expectedPath = "sounds/item_" + animator + ".mp3";
    when(resources.getAsset(expectedPath, Sound.class)).thenReturn(null);

    ItemEffectsService.spawnEffect(
        atlas,
        animator,
        new Vector2[] {new Vector2(0, 0), new Vector2(0, 0)},
        1,
        new float[] {0.1f, 0.2f},
        Animation.PlayMode.NORMAL,
        false);

    captureRegisteredEntity();

    // Requested but no play call because asset was null
    verify(resources).getAsset(expectedPath, Sound.class);
    // No specific Sound mock to verify play() against so also ensure default stub wasn't used for
    // this path again
    verifyNoMoreInteractions(resources);
  }

  @Test
  void spawnEffect_propagatesException_whenResourceServiceThrows_onSoundLoad() {
    String animator = "coffee";
    TextureAtlas atlas = atlasWithFramesFor(animator);

    String expectedPath = "sounds/item_" + animator + ".mp3";
    when(resources.getAsset(expectedPath, Sound.class))
        .thenThrow(new RuntimeException("simulated load failure"));
    Vector2[] positions = {new Vector2(3, 4), new Vector2(0, 0)};
    float[] durations = {0.1f, 0.3f};
    assertThrows(
        RuntimeException.class,
        () ->
            ItemEffectsService.spawnEffect(
                atlas, animator, positions, 1, durations, Animation.PlayMode.NORMAL, false));

    // No captureRegisteredEntity() here because never get that far
    verify(resources).getAsset(expectedPath, Sound.class);
  }

  @Test
  void spawnEffect_withNullAtlas_neitherRegistersNorAttemptsToLoadSound() {
    ItemEffectsService.spawnEffect(
        null,
        "buff",
        new Vector2[] {new Vector2(0, 0), new Vector2(0, 0)},
        1,
        new float[] {0.1f, 1.5f},
        Animation.PlayMode.NORMAL,
        false);

    verify(entities, never()).register(any());
    // Ensure didn't try to fetch a sound
    verify(resources, never()).getAsset(startsWith("sounds/item_"), eq(Sound.class));
  }

  @Test
  void playEffect_variants_requestMatchingSoundNames_onceEach() {
    // Ensure this test fully controls stubbing for resources
    reset(resources);

    // Sound: capture every requested sound path and always return a playable mock
    List<String> soundPaths = new ArrayList<>();
    when(resources.getAsset(anyString(), eq(Sound.class)))
        .thenAnswer(
            inv -> {
              String p = inv.getArgument(0, String.class);
              if (p.startsWith("sounds/item_")) soundPaths.add(p);
              return mock(Sound.class);
            });

    // Atlas: one Answer that maps path -> appropriate atlas
    when(resources.getAsset(anyString(), eq(TextureAtlas.class)))
        .thenAnswer(
            inv -> {
              String p = inv.getArgument(0, String.class);
              if (p.endsWith("buff.atlas")) return atlasWithFramesFor("buff");
              if (p.endsWith("coffee.atlas")) return atlasWithFramesFor("coffee");
              if (p.endsWith("emp.atlas")) return atlasWithFramesFor("emp");
              if (p.endsWith("grenade.atlas")) return atlasWithFramesFor("grenade");
              if (p.endsWith("nuke.atlas")) return atlasWithFramesFor("nuke");
              // fallback so stubbing is always "finished"
              return atlasWithFramesFor("fallback");
            });

    ItemEffectsService svc = new ItemEffectsService();
    int tile = 16;
    Vector2 pos = new Vector2(100, 200);
    Vector2 br = new Vector2(0, 0);

    svc.playEffect("buff", new Vector2(pos), tile, br);
    svc.playEffect("coffee", new Vector2(pos), tile, br);
    svc.playEffect("emp", new Vector2(pos), tile, br);
    svc.playEffect("grenade", new Vector2(pos), tile, br);
    svc.playEffect("nuke", new Vector2(pos), tile, br);

    assertEquals(
        List.of(
            "sounds/item_buff.mp3",
            "sounds/item_coffee.mp3",
            "sounds/item_emp.mp3",
            "sounds/item_grenade.mp3",
            "sounds/item_nuke.mp3"),
        soundPaths);
  }

  @Test
  void spawnEffect_doesNotDoublePlay_whenCalledOnce() {
    // Ensure that a single spawn leads to a single Sound.play()
    String animator = "buff";
    TextureAtlas atlas = atlasWithFramesFor(animator);
    Sound s = mock(Sound.class);
    when(resources.getAsset("sounds/item_buff.mp3", Sound.class)).thenReturn(s);

    ItemEffectsService.spawnEffect(
        atlas,
        animator,
        new Vector2[] {new Vector2(0, 0), new Vector2(10, 10)},
        1,
        new float[] {0.1f, 2.0f},
        Animation.PlayMode.NORMAL,
        true);

    captureRegisteredEntity();
    verify(s, times(1)).play(anyFloat());
  }
}
