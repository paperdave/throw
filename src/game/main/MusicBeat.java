package game.main;

public class MusicBeat {
  private static final int[] beatTimes = {
    // Music 1
    22,
    // Music 2
    22,
    // Music 3
    24,
    // Music 4
    47,
    // Music 5
    19,
    // Music 6
    24,
  };

  public static int getBeatLength() {
    return beatTimes[songID - 1];
  }

  public static int songID = 0;
}
