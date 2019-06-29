package game.objects;

import davecode.util.MathUtil;
import davecode.util.RandomUtil;
import game.main.GameItem;
import game.main.Window;
import processing.core.PImage;

import java.awt.Color;

public class StartOverlay extends GameItem {
  private static final int TIME_PER_FRAME = 24; //ish

  public int time = TIME_PER_FRAME;
  public int frame = 0;

  float scale = 10f;

  float opacity = 1f;

  public int red = colors[0].getRed();
  public int green = colors[0].getGreen();
  public int blue = colors[0].getBlue();

  public float xScale = 0.5f;
  public float yScale = 1.2f;

  private static final Color[] colors = {
    new Color(255, 51, 0,255),
    new Color(255, 244, 0,255),
    new Color(134,255, 0,255),
    new Color(0,255, 209,255),
  };

  public void preload() {
    loadImage("countdownThree");
    loadImage("countdownTwo");
    loadImage("countdownOne");
    loadImage("countdownGo");
    loadSound("intro1");
    loadSound("intro2");
    loadSound("intro3");
    loadSound("intro4");
    loadSound("intro5");
    loadSound("intro6");
    loadSound("intro7");
    loadSound("intro8");
  }

  public void setup() {
    if(RandomUtil.randomInt(1,2) == 1) {
      int var = RandomUtil.randomInt(2, 8);
      playSound("intro" + var);
      playSound("intro" + var);
    } else {
      playSound("intro1");
      playSound("intro1");
    }
    sortY = 100000000;
  }

  public void draw() {
    Window window = getWindow();

    String img = "countdownGo";
    if(frame == 0) img = "countdownThree";
    if(frame == 1) img = "countdownTwo";
    if(frame == 2) img = "countdownOne";


    // Your Render Here
    window.tint(red,green,blue,opacity * 255);
    PImage pImage = getImage(img);
    window.image(
      pImage,
      Window.WIDTH / 2 - 64 * ((scale * xScale) / 2),
      Window.HEIGHT / 2 - 64 * scale / 2 - (scale * yScale * pImage.height) + (scale * pImage.height),
      scale * xScale * pImage.width,
      scale * yScale * pImage.height
    );
    window.noTint();
  }

  public void update() {
    if(dead) return;

    if ((frame == 2 && time < 4) || frame >= 3) {
      scale = MathUtil.lerp(scale, 10f, 0.05f);
    } else {
      scale = MathUtil.lerp(scale, 4.5f, 0.085f);
    }

    if ((frame == 3 && time < 30) || frame >= 4) {
      opacity -= 0.035;
    }

    red = (int)MathUtil.lerp((float)red, (float)colors[Math.min(frame, 3)].getRed(), 0.15f);
    green = (int)MathUtil.lerp((float)green, (float)colors[Math.min(frame, 3)].getGreen(), 0.15f);
    blue = (int)MathUtil.lerp((float)blue, (float)colors[Math.min(frame, 3)].getBlue(), 0.15f);

    if(frame >= 3) {
      xScale = MathUtil.lerp(xScale, 1, 0.3f);
    } else {
      xScale = MathUtil.lerp(xScale, 1.2f, 0.3f);
    }
    yScale = MathUtil.lerp(yScale, 1, 0.3f);

    time--;
    if(time <= 0) {
      xScale = 0.5f;
      yScale = 1.2f;

      frame++;
      if(frame == 3) {
        xScale = 0.5f;
        yScale = 1f;

        time = 60;
      } else if(frame == 4) {
        dead = true;
      } else {
        time = TIME_PER_FRAME;
      }
    }
  }
}
