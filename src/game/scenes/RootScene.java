package game.scenes;

import game.main.Renderable;
import game.main.Window;

// The root of all rendering, renders the background and the other scenes.
public class RootScene extends Renderable {
  public static RootScene instance;

  public void preload() {
  }

  private Renderable starScene;
  public Renderable currentScene;

  public void setup() {
    instance = this;

    starScene = createRenderable(new StarScene());
    currentScene = createRenderable(new MainMenu());
  }

  public void draw() {
    Window window = getWindow();

    // Background
    window.background(0x2E287F);

    // Scale
//    float zoom = 0.5f;
//    window.scale(zoom);
//    window.translate((Window.WIDTH / zoom - Window.WIDTH) / 2, (Window.HEIGHT / zoom - Window.HEIGHT) / 2);

    starScene.draw();
    currentScene.draw();
//    window.translate((Window.WIDTH / zoom - Window.WIDTH) / -2f, (Window.HEIGHT / zoom - Window.HEIGHT) / -2f);
//    window.scale(1f / zoom);
  }

  public void update() {
    starScene.update();
    currentScene.update();
  }
}
