package game.scenes;

import davecode.util.MathUtil;
import game.main.Renderable;
import game.main.Window;
import game.objects.StartOverlay;

public class TransitionToGame extends Renderable {
  @SuppressWarnings("unused") // Required for Resource Preloader
  public TransitionToGame() { }

  private Renderable menu;
  public Renderable game;
  private StartOverlay overlay;

  private float transitionValue = 0;
  private int transitionTime = 0;

  private TransitionToGame(Renderable currentScene, Renderable newScene) {
    menu = currentScene;
    game = newScene;
  }

  public static void transitionTo(Renderable newScene) {
    RootScene.instance.currentScene = createRenderable(new TransitionToGame(RootScene.instance.currentScene, newScene));
  }

  public void preload() { }

  public void setup() {
    overlay = (StartOverlay) createRenderable(new StartOverlay());
  }

  public void draw() {
    Window window = getWindow();
    menu.draw();

    window.translate(0, (1 - transitionValue) * 1100);
    game.draw();
    window.translate(0, (1 - transitionValue) * -1100);

    if(overlay != null) {
      overlay.draw();
    }
  }

  public void update() {
    transitionValue = MathUtil.lerp(transitionValue, 1f, 0.03f);
    transitionTime++;

    menu.update();

    if(transitionValue >= 0.999f) {
      if (RootScene.instance.currentScene instanceof TransitionFromGame) {
        ((TransitionFromGame) RootScene.instance.currentScene).game = game;
      } else {
        RootScene.instance.currentScene = game;
      }
    }

    if(overlay != null) {
      overlay.update();
      if(overlay.dead) {
        overlay = null;
      }
    }

    if(transitionTime >= 120) {
      game.update();
    }
  }
}
