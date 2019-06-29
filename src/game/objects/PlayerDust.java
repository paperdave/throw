package game.objects;

import davecode.util.MathUtil;
import davecode.util.RandomUtil;
import game.main.GameItem;
import game.main.Window;

import java.awt.Color;

// Player Explody Bits
public class PlayerDust extends GameItem {

  private float x;
  private float y;
  private float z;
  private float mx;
  private float mmx = 0.85f;
  private float my;
  private float mz;
  private float mmz = -0.075f;
  private Color color = new Color(0x5e461f);
  private int size;
  private boolean dead = false;

  @SuppressWarnings("unused") // Required for Resource Preloader
  public PlayerDust() {}
  public PlayerDust(double x, double y, Color c) {
    this.x = (int) x;
    this.y = (int) y;
    this.color = c;
  }

  public void preload() {

  }

  public void setup() {
    mx = RandomUtil.randomFloat(0f, 7f) - 3.5f;
    my = RandomUtil.randomFloat(0f, 1f) - 0.5f;

    y -= RandomUtil.randomFloat(0f, 75f);
    x -= RandomUtil.randomFloat(0f, 20f) - 10f;

    size = RandomUtil.randomInt(4, 6);
  }

  public void draw() {
    Window window = getWindow();

    // Your Render Here
    window.translate(x, y + z);

    window.blendMode(window.ADD);

    window.noStroke();
    float opacity = 255f;
    window.fill(color.getRed(), color.getGreen(), color.getBlue(), opacity);
    window.rect(-size/2f, -size/2f, size, size);

    window.blendMode(window.NORMAL);

    window.translate(-x, -(y + z));

    sortY = y + (size / 2f);
  }

  public void update() {
    if(dead) return;

    x += mx;
    y += my;
    z += mz;
    mx *= mmx;
    mmx = MathUtil.lerp(mmx, 1f, 0.4f);
    my *= 0.95f;
    mz += mmz;
    mmz -= 0.005;

    if(z > 1000) {
      dead = true;
    }
  }
}
