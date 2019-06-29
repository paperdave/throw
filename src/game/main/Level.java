package game.main;

import davecode.util.RandomUtil;

import java.awt.Point;

import static game.scenes.GameScene.X_OFFSET;
import static game.scenes.GameScene.Y_OFFSET;

public class Level {
  public static Level currentLevel = null;

  public Block[][] blocks = new Block[12][12];

  public Level() {
    for (int i = 0; i < 12; i++) {
      for (int j = 0; j < 12; j++) {
        blocks[i][j] = new Block(i, j);
      }
    }
  }

  public static class Block {
    public int variant;
    public int x = 0;
    public int y = 0;
    public double z = 0;
    public double zSpeed = 0;
    public boolean falling = false;

    public Block(int x, int y) {
      this.x = x;
      this.y = y;
      variant = RandomUtil.randomInt(1, 4);
    }
  }

  public static Point pixelToMap(Point pixel) {
    pixel.x -= X_OFFSET + 32;
    pixel.y -= Y_OFFSET;

    return new Point(
      Math.round((((float)pixel.x) / 32f + (((float)pixel.y) / 17f)) / 2f),
      Math.round((((float)pixel.y) / 17f - (((float)pixel.x) / 32f)) / 2f)
    );
  }
  public static Point mapToPixel(Point map) {
    return new Point(
      X_OFFSET + map.x * 32 - map.y * 32,
      Y_OFFSET + map.x* 16 + map.y * 16
    );
  }
  public Block blockAt(Point position) {
    if(position.x < 0) return null;
    if(position.y < 0) return null;
    if(position.x > 11) return null;
    if(position.y > 11) return null;
    return blocks[position.x][position.y];
  }
}
