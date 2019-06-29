package game.scenes;

import game.main.Renderable;
import game.main.Window;
import davecode.util.RandomUtil;
import java.util.ArrayList;

// Renders A Star Background
public class StarScene extends Renderable {
  private int starScale;
  private ArrayList<Star> stars = new ArrayList<>();

  int opacity = 0;

  // Preload is to load images and other resources
  public void preload() {
    loadImage("star");
  }

  // Setup is basically the constructor
  public void setup() {
    starScale = getImage("star").width * 4;

    for (int i = 0; i < 100; i++) {
      stars.add(
        new Star(
          RandomUtil.randomInt(0, Window.WIDTH / starScale) * starScale,
          RandomUtil.randomInt(0, Window.HEIGHT / starScale) * starScale,
          RandomUtil.randomInt(1, 3)
        )
      );
    }
  }

  public void update() {
    for (Star star : stars) {
      star.x += star.speed;
      if(star.x > Window.WIDTH) {
        star.x = 0;
        star.speed = RandomUtil.randomInt(1, 3);
        star.y = RandomUtil.randomInt(0, Window.HEIGHT / starScale) * starScale;
      }
    }
    opacity = Math.min(opacity + 5, 100);
  }

  public void draw() {
    Window window = getWindow();

    window.tint(255,255,255, opacity);
      for (Star star : stars) {
      window.image(getImage("star"), star.x, star.y, starScale, starScale);
    }
    window.tint(255,255,255,255);
  }

  class Star {
    public int x;
    public int y;
    public int speed;
    public Star(int x, int y, int speed) {
      this.x = x;
      this.y = y;
      this.speed = speed;
    }
  }
}
