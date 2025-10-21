package com.csse3200.game.data.credits;

import java.util.List;

public final class CreditsData {
  private CreditsData() {}

  private static final Section tutors =
      new Section(
          "Super duper cool tutors",
          List.of(new Entry("Rachel", "Leathers"), new Entry("Lucas", "Hicks")));

  public static final List<Section> SECTIONS =
      List.of(
          tutors,
          new Section(
              "TEAM 1",
              List.of(
                  new Entry("Youqing", "Mao"),
                  new Entry("Zijie", "Sun"),
                  new Entry("Yihan", "Ge"),
                  new Entry("Ying", "Yang"),
                  new Entry("Yiwen", "Chen"))),
          new Section(
              "TEAM 2",
              List.of(
                  new Entry("Finbar", "O'Donnell"),
                  new Entry("Samuel", "Patterson"),
                  new Entry("Cate", "Brown"),
                  new Entry("Joshua", "Mann"),
                  new Entry("Senuri", "Panadura Arachchige"))),
          new Section(
              "TEAM 3",
              List.of(
                  new Entry("Ansh", "Kataria"),
                  new Entry("Branden Rease", "Abrol"),
                  new Entry("Sakshi", "Manchanda"),
                  new Entry("Mayanka", "Marwah"),
                  new Entry("Harshil", "Walia"))),
          new Section(
              "TEAM 4",
              List.of(
                  new Entry("Jordan", "Grieve"),
                  new Entry("Simar", "Wadhawan"),
                  new Entry("Abhya", "Garg"),
                  new Entry("Vida", "Sadeghi Varkani"),
                  new Entry("Janvhi", "Sharma"))),
          new Section(
              "TEAM 5",
              List.of(
                  new Entry("Konstantin", "Reznik"),
                  new Entry("Danny", "Ly"),
                  new Entry("Pham Hung Cuong", "Le"),
                  new Entry("Louisa", "Wu"),
                  new Entry("Dzaky", "Razzansyah"))),
          new Section(
              "TEAM 6",
              List.of(
                  new Entry("Sam", "Tran"),
                  new Entry("Shivam", "Trivedi"),
                  new Entry("Matthew", "Moore"),
                  new Entry("Arush", "Shukla"),
                  new Entry("Gia Hung", "Huynh"))),
          new Section(
              "TEAM 7",
              List.of(
                  new Entry("Noah", "Trevena"),
                  new Entry("Jiatong", "Wang"),
                  new Entry("Joseph", "Thorne"),
                  new Entry("Nathan", "Vassie"),
                  new Entry("Geon", "Song"))),
          new Section(
              "TEAM 8",
              List.of(
                  new Entry("Bianca", "Leathers"),
                  new Entry("Jack", "Limmage"),
                  new Entry("Brandon", "Lee"),
                  new Entry("Jackie", "Ding"),
                  new Entry("Ben", "Schenk"))),
          tutors);
}
