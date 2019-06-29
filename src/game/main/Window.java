package game.main;

import com.github.strikerx3.jxinput.enums.XInputButton;
import davecode.controller.XInputManager;
import davecode.log.Logger;
import davecode.util.GameLoop;
import davecode.util.MathUtil;
import davecode.util.RandomUtil;
import game.scenes.GameScene;
import game.scenes.LoadingScene;
import game.scenes.RootScene;
import org.reflections.Reflections;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

import java.lang.reflect.Modifier;
import java.util.Set;

public class Window extends PApplet {
  public boolean isFullscreen = true;

  public static float WIDTH = 1280;
  public static float HEIGHT = 720;

  // Singleton
  public static Window instance;
  private PFont font;
  private GameLoop loop;

  long lastTime = System.nanoTime();
  double ns = 1000000000 / 60;
  double delta = 0;

  int fps = 0;
  int ups = 0;
  int frames = 0;
  int updates = 0;
  long oneSecondAgo = System.currentTimeMillis();
  int exitCooldown = 0;
  float exitVIN = 0;
  private Thread preloadThread;

  public Window() {
    instance = this;
  }

  // Vars
  public Renderable scene;

  private boolean isPressingReset = false;

  // Window Settings
  public void settings() {
    // Disable Smoothing
    noSmooth();
    // Size
    size(1280, 720);
    // full
    if(isFullscreen) {
      WIDTH = displayWidth;
      HEIGHT = displayHeight;
      fullScreen();
    } else {
      WIDTH = 1280;
      HEIGHT = 720;
    }

    GameScene.recalculateOffsets();
  }

  public void setup() {
    surface.setTitle("Throw");

    Logger.info("Setting Up...");

    font = createFont("res/font/ProggyClean.ttf", 24);

    // Frame rate
    frameRate(60);

    ResourceManager.setup();

    scene = new LoadingScene();
    scene.setup();

    noCursor();

    // Preload All Scenes
    preloadThread = new Thread(() -> {
      Reflections reflections = new Reflections("game");
      Set<Class<? extends Renderable>> classes = reflections.getSubTypesOf(Renderable.class);
      for (Class<? extends Renderable> aClass : classes) {
        try {

          Logger.info("Preloading " + aClass.getName());

          if(!Modifier.isAbstract(aClass.getModifiers())) {
            Renderable renderable = aClass.newInstance();
            renderable.preload();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      // Init our root scene.
      ((LoadingScene)scene).isLoaded = true;

      try {
        Thread.currentThread().join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }, "ResourceLoader");
    preloadThread.start();

    background(0);
  }

  public void draw() {
    long now = System.nanoTime();
    delta += (now - lastTime) / ns;

    while (delta >= 1) {
      actualUpdate();
      updates++;
      delta--;
    }

    lastTime = now;

    long nowMs = System.currentTimeMillis();
    frames++;

    if(oneSecondAgo + 1000 <= nowMs) {
      oneSecondAgo = nowMs;
      fps = frames;
      ups = updates;
      updates = 0;
      frames = 0;
    }

    actualDraw();
  }

  public void actualDraw() {
    // Font
    textFont(font, 24);

    //shake
    float shakeX = RandomUtil.randomFloat(0, shakeRemain * 2 + 0.00000001f) - shakeRemain;
    float shakeY = RandomUtil.randomFloat(0, shakeRemain * 2 + 0.00000001f) - shakeRemain;

    // Update and Render Our Scene

    translate(shakeX, shakeY);
    fill(255,255,255);
    scene.draw();
    translate(-shakeX, -shakeY);

    // Error Screens
    int count = XInputManager.getCount();
    if(count == 0) {
      fill(0, 0, 0, 175);
      rect(-10, -10, Window.WIDTH + 20, Window.HEIGHT + 20);

      fill(255);
      textSize(64);
      textAlign(CENTER, CENTER);
      text("No Controllers Connected!", Window.WIDTH / 2, Window.HEIGHT/2 - 100);

      textSize(32);
      text("This game only supports playing with controllers (XInput),", Window.WIDTH / 2, Window.HEIGHT / 2);
      text("so you need to connect those up!", Window.WIDTH / 2, Window.HEIGHT / 2 + 32);
    }
    if(count == 1) {
      fill(0, 0, 0, 175);
      rect(-10, -10, Window.WIDTH + 20, Window.HEIGHT + 20);

      fill(255);
      textSize(64);
      textAlign(CENTER, CENTER);
      text("Not enough Players!", Window.WIDTH / 2, Window.HEIGHT/2 - 100);

      textSize(32);
      text("You need at least 2 controllers (XInput) to play,", Window.WIDTH / 2, Window.HEIGHT / 2);
      text("so you need to connect those up!", Window.WIDTH / 2, Window.HEIGHT / 2 + 32);
    }

    // FPS Counter
    noStroke();
    fill(255,255,255);
    textSize(24);
    textAlign(LEFT, TOP);
    text("FPS: " + Math.round(fps) + " (" + Math.round(ups) + " UPS)", 10, 10);
    text("By Dave (davecode.me)", 10, 10 + 24);

    // exit visuals
    if(exitVIN >= 0.01f) {
      fill(0, 0, 0, 255);
      rect(-10, -10, Window.WIDTH + 20, 10 + Window.HEIGHT * Math.min(exitVIN, 0.5f));
      rect(-10, Window.HEIGHT - Window.HEIGHT * Math.min(exitVIN, 0.5f), Window.WIDTH + 20, Window.HEIGHT * exitVIN + 10);
    }
  }

  public void actualUpdate() {
    scene.update();

    shakeRemain = Math.max(0.0f, shakeRemain - ((1 / shakeLength) * shakeMagnitude));

    // Reset Switch
//    if(XInputManager.getButton(0, XInputButton.BACK) || (keyCode == 112 && keyPressed)) {
//      if(!isPressingReset) {
//        scene = new RootScene();
//        scene.setup();
//        isPressingReset = true;
//        ResourceManager.stopAllSounds();
//      }
//    } else {
//      isPressingReset = false;
//    }

    boolean isPressingExit = XInputManager.getButton(0, XInputButton.START) || key == ESC;
    if(isPressingExit) {
      exitCooldown++;
      exitVIN = MathUtil.lerp(exitVIN, 0.5f, 0.15f);
      if(exitCooldown >= 60) {
        exit();
      }
    } else {
      exitCooldown *= 0.2f;
      exitVIN = MathUtil.lerp(exitVIN, 0, 0.15f);
    }
  }

  public void imageScaled(PImage img, float x, float y, float scale) {
    image(img, x, y, img.width * scale, img.height * scale);
  }
  public void imageScaled(PImage img, double x, double y, float scale) {
    image(img, (float)x, (float)y, img.width * scale, img.height * 2);
  }

  float shakeMagnitude = 1f;
  float shakeLength = 1f;
  float shakeRemain = 0f;

  public void shake(float magnitude, int frames) {
    shakeMagnitude = Math.max(shakeRemain, magnitude);
    shakeRemain += magnitude;
    shakeLength = frames;
  }
  public void keyPressed() {
    if (key == ESC) {
      key = 0;
    }
  }
  public void keyReleased() {
    key = 0;
  }

  public void stop() {
    // always close Minim audio classes when you are done with them
    super.stop();
    try {
      preloadThread.join();
    } catch (InterruptedException e) {}
  }
}
