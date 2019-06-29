package game.scenes;

import game.main.Renderable;
import game.main.Window;
import processing.core.PApplet;

// This is just a template
public class LoadingScene extends Renderable {
  public boolean isLoaded = false;
  public int time = 0;

  public int opacity = 0;

  public void preload() {

  }

  public void setup() {

  }

  public void draw() {
    Window window = getWindow();

    window.background(50);
    window.fill(255);

    window.noStroke();
    window.fill(255,255,255);
    window.textSize(24);
    window.textAlign(window.LEFT, window.TOP);
    window.text("Throw v1.0.0", 10, 10 + 24 * 2);
    window.text("Processing v3.5.2", 10, 10 + 24 * 3);
    window.text("Java v" + PApplet.javaVersionName, 10, 10 + 24 * 4);

    window.textAlign(window.CENTER, window.CENTER);
    window.textSize(128);

    String s = "Loading...";

    for (int i = 0; i < s.length(); i++) {
      float sin = (float) Math.sin(time * 0.05f + i * 0.5f);

      window.text(s.charAt(i), Window.WIDTH / 2 - s.length() * 32 + i * 64, Window.HEIGHT / 2 + sin * 30);
    }

    window.fill(46, 40, 127, opacity);
    window.rect(0,0,Window.WIDTH,Window.HEIGHT);
  }

  public void update() {
    time++;
    if(isLoaded) {
      opacity += 10;
      if(opacity >= 255) {
        Window.instance.scene = createRenderable(new RootScene());
      }
    }
  }
}
