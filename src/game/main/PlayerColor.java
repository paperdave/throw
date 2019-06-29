package game.main;

import java.awt.Point;
import java.awt.Color;

public class PlayerColor {
  public static final Color Player1Color = new Color(0xf93b31);
  public static final Color Player2Color = new Color(0x1B76F9);
  public static final Color Player3Color = new Color(0x20F907);
  public static final Color Player4Color = new Color(0xFC00FF);

  public static Color getPlayerColor(int id) {
    if(id == 0) return Player1Color;
    if(id == 1) return Player2Color;
    if(id == 2) return Player3Color;
    if(id == 3) return Player4Color;

    return Color.white;
  }
}
