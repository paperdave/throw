package game.objects;

import davecode.util.RandomUtil;
import game.main.GameItem;
import game.main.Window;

import java.awt.Color;

// Player Explody Bits
public class FireballParticle extends GameItem {

  private float x;
  private float y;
  private float z;
  private final Color color = new Color(0xFF7B00);
  private int size;
  private boolean dead = false;
  private float opacity = 1f;

  @SuppressWarnings("unused") // Required for Resource Preloader
  public FireballParticle() {}
  public FireballParticle(double x, double y) {
    this.x = (float) (x + RandomUtil.randomFloat(0, 48) - 24f);
    this.y = (float) (y - 32 + RandomUtil.randomFloat(0, 48) - 24f);
  }

  public void preload() {

  }

  public void setup() {
    size = RandomUtil.randomInt(4, 8);
  }

  public void draw() {
    Window window = getWindow();

    // Your Render Here
    window.translate(x, y + z);

    window.blendMode(window.ADD);

    window.noStroke();
    window.fill(color.getRed(), color.getGreen(), color.getBlue(), opacity * 255);
    window.circle(-size/2f, -size/2f, size);

    window.blendMode(window.NORMAL);

    window.translate(-x, -(y + z));

    sortY = y + (size / 2f);
  }

  public void update() {
    if(dead) return;

    z -= 0.1;

    opacity -= 0.01f;

    if(opacity <= 0) {
      dead = true;
    }
  }
}
