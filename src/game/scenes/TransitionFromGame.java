package game.scenes;

import davecode.util.MathUtil;
import game.main.PlayerColor;
import game.main.Renderable;
import game.main.ResourceManager;
import game.main.Window;

import java.awt.Point;
import java.awt.Color;

// This is just a template
public class TransitionFromGame extends Renderable {

  private static String getWinStr(int id) {
    String name = "Nobody";
    if(id == 0) name = "Red";
    if(id == 1) name = "Blue";
    if(id == 2) name = "Green";
    if(id == 3) name = "Purple";

    return name + " Wins";
  }

  private static final int TIME_TO_SWITCHSOUND = 149;
  private static final int TIME_TO_FADE = 180;
  private int winnerID;
  private Point center;

  private int time = 0;

  private float barsIn = 0f;

  private float red = 0;
  private float green = 0;
  private float blue = 0;
  private float opacity = 255;

  public Renderable game;
  private Renderable menu;
  private float xo;
  private float yo;
  private float scale = 1f;

  @SuppressWarnings("unused") // Required for Resource Preloader
  public TransitionFromGame() { }

  public static void transitionFrom(Point center, int winnerID) {
    Renderable scene = RootScene.instance.currentScene;
    RootScene.instance.currentScene = createRenderable(new TransitionFromGame(scene, createRenderable(new MainMenu()), center, winnerID));
  }
  private TransitionFromGame(Renderable game, Renderable menu, Point center, int winnerID) {
    this.game = game;
    this.menu = menu;
    this.center = center;
    this.winnerID = winnerID;
  }

  public void preload() {
    loadSound("ending");
  }

  public void setup() {
    playSound("ending");
    playSound("ending");
    ResourceManager.stopMusic();
  }

  public void draw() {
    Window window = getWindow();

    if(time < TIME_TO_FADE) {
      window.translate(-xo * scale, -yo * scale);
      window.scale(scale);
      window.translate((Window.WIDTH / scale - Window.WIDTH) / 2, (Window.HEIGHT / scale - Window.HEIGHT) / 2);

      game.draw();
      window.translate((Window.WIDTH / scale - Window.WIDTH) / -2f, (Window.HEIGHT / scale - Window.HEIGHT) / -2f);
      window.scale(1f / scale);
      window.translate(xo * scale, yo * scale);
    }

    window.fill(red, green, blue, opacity);
    window.rect(-10, -10, Window.WIDTH + 20, 10 + Window.HEIGHT * Math.min(barsIn, 0.5f));
    window.rect(-10, Window.HEIGHT - Window.HEIGHT * Math.min(barsIn, 0.5f), Window.WIDTH + 20, Window.HEIGHT * barsIn + 10);

    window.textAlign(window.CENTER, window.CENTER);
    window.textSize(48);
    Color c = PlayerColor.getPlayerColor(winnerID);
    float textOpacity = Math.min(255, Math.max(0, MathUtil.lerp(255f, 0f, (barsIn - 0.15f) * 3.5f)));
    window.fill(c.getRed(), c.getGreen(), c.getBlue(), textOpacity);
    window.text(getWinStr(winnerID), Window.WIDTH/2, Window.HEIGHT - barsIn * Window.HEIGHT/2);
    menu.draw();
  }

  public void update() {
    game.update();

    time++;
    if(time >= TIME_TO_SWITCHSOUND) {
      barsIn = MathUtil.lerp(barsIn, 0.55f, 0.1f);
      red = MathUtil.lerp(red, 40, 0.2f);
      green = MathUtil.lerp(green, 35, 0.2f);
      blue = MathUtil.lerp(blue, 110, 0.2f);
    } else {
      barsIn = MathUtil.lerp(barsIn, 0.15f, 0.1f);
    }

    if(time >= TIME_TO_FADE) {
      opacity -= 5;
      menu.update();
    }

    float xot = center.x - Window.WIDTH/2;
    float yot = center.y - Window.HEIGHT/2;

    xo = MathUtil.lerp(xo, xot, 0.05f);
    yo = MathUtil.lerp(yo, yot, 0.05f);
    scale = MathUtil.lerp(scale, 2f, 0.04f);

    if(time >= TIME_TO_FADE + 55) {
      RootScene.instance.currentScene = menu;
    }
  }
}
