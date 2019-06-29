package game.objects;

import davecode.util.RandomUtil;
import game.main.GameItem;
import game.main.Level;
import game.main.Window;

import java.awt.Point;
import java.awt.Color;

// This is just a template
public class ExplosionPart extends GameItem {
  private boolean failedExplodeCheck = false;

  public Color chooseRandomColor() {
    int id = RandomUtil.randomInt(0, 7);
    switch (id) {
      case 0: return new Color(0x383838);
      case 1: return new Color(0x834D08);
      case 2: return new Color(0x4E2F00);
      case 3: return new Color(0x664200);
      case 4: return new Color(0xE36912);
      case 5: return new Color(0xD9E300);
      case 6: return new Color(0xE38200);
      case 7: return new Color(0xE3AE00);
    }

    throw new Error("Got " + id);
  }

  public int angle;
  public float x;
  public float y;
  public float z;
  public float mx;
  public float my;
  public float mz;
  public float ma;
  public Color color = new Color(0x5e461f);
  public int size;
  public boolean dead = false;
  public float opacity = 255f;

  public ExplosionPart() {}
  public ExplosionPart(double x, double y) {
    this.x = (int) x;
    this.y = (int) y;
  }

  public void preload() {

  }

  public void setup() {
    mz = -RandomUtil.randomFloat(12f, 14f);
    mx = RandomUtil.randomFloat(0f, 4f) - 2;
    my = RandomUtil.randomFloat(0f, 4f) - 2;
    ma = RandomUtil.randomFloat(0f, 5f) + 2;
    size = RandomUtil.randomInt(12, 20);
    color = chooseRandomColor();
  }

  public void draw() {
    Window window = getWindow();

    // Your Render Here
    window.translate(x, y + z);
    window.rotate(angle);

    window.noStroke();
    window.fill(color.getRed(), color.getGreen(), color.getBlue(), opacity);
    window.rect(-size/2f, -size/2f, size, size);

    window.rotate(-angle);
    window.translate(-x, -(y + z));

    sortY = y + (size / 2f);
  }

  public void update() {
    if(dead) return;

    x += mx;
    y += my;
    z += mz;
    if(Math.abs(mz) > 0) {
      mz += 0.5;
    }
    angle += ma;

    if(z >= 1000) {
      dead = true;
    }
    if(z >= 0 && !failedExplodeCheck) {
      // Explode
      Level.Block standingBlock = Level.currentLevel.blockAt(
        Level.pixelToMap(
          new Point(
            Math.round(x),
            Math.round(y + z)
          )
        )
      );

      if(
        standingBlock == null
          || standingBlock.falling
      ) {
        failedExplodeCheck = true;
        sortToBack = true;
      } else {
        if(Math.abs(mz) >= 0.4f) {
          mz *= -0.5f;
          mx *= 0.5f;
          my *= 0.5f;
          ma *= 0.4f;
        } else {
          mx = 0;
          my = 0;
          ma = 0;
          mz = 0;
        }
        opacity *= 0.992f;
      }
    }
    opacity *= 0.992f;

    if(opacity <= 0.4f) {
      dead = true;
    }
  }
}
