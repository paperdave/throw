package game.objects;

import davecode.util.MathUtil;
import davecode.util.RandomUtil;
import game.main.GameItem;
import game.main.Window;

import java.awt.*;

// Player Explody Bits
public class MenuParticle extends GameItem {
  private float x;
  private float y;
  private float z;
  private static final Color color = new Color(0xFF0000);
  private static final Color color2 = new Color(0xFFCA00);
  private float red = color.getRed();
  private float green = color.getGreen();
  private float blue = color.getBlue();
  private int size;
  public boolean dead = false;
  private float opacity = 1f;
  private float mz = 0;
  private float mmz = 0;
  private float angle = 0;

  @SuppressWarnings("unused") // Required for Resource Preloader
  public MenuParticle() {}
  public MenuParticle(double x, double y) {
    this.x = (float) (x + RandomUtil.randomFloat(0, 560) - 280f);
    this.y = (float) (y - 9);
    this.mmz = RandomUtil.randomFloat(0.001f, 0.008f);
  }

  public void preload() {

  }

  public void setup() {
    size = RandomUtil.randomInt(6, 18);
  }

  public void draw() {
    Window window = getWindow();

    float sin = (float)Math.sin(angle / 5) * 6f;

    // Your Render Here
    window.translate(x + sin, y + z);

    window.blendMode(window.ADD);

    window.noStroke();
    window.fill(red, green, blue, opacity * 255);
    window.circle(-size/2f, -size/2f, size);

    window.blendMode(window.NORMAL);

    window.translate(-x - sin, -(y + z));

    sortY = y + (size / 2f);
  }

  public void update() {
    float perc = 0.015f;
    red = MathUtil.lerp(red, color2.getRed(), perc);
    green = MathUtil.lerp(green, color2.getGreen(), perc);
    blue = MathUtil.lerp(blue, color2.getBlue(), perc);

    if(dead) return;

    z += mz;
    mz -= mmz;

    opacity -= 0.004f;

    if(opacity <= 0) {
      dead = true;
    }

    angle++;
  }
}
