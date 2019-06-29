package game.scenes;

import com.github.strikerx3.jxinput.enums.XInputAxis;
import com.github.strikerx3.jxinput.enums.XInputButton;
import davecode.controller.XInputManager;
import davecode.util.MathUtil;
import davecode.util.RandomUtil;
import game.main.PlayerColor;
import game.main.Renderable;
import game.main.Window;
import game.objects.MenuParticle;
import game.objects.Player;
import processing.core.PImage;

import java.awt.Color;
import java.util.ArrayList;

// The Main Menu
public class MainMenu extends Renderable {
  private static final int TUTORIAL_FADE_LEN = 80;
  private static final int TUTORIAL_TOTAL_LEN = 180;
  private final String[] tutorial = {
    "Use the Left Stick to move",
    "Use the Right Stick to aim",
    "Press the Right Stick to shoot!",
    "Hold A to Join the Game",
  };
  private int tutorialText = 0;
  private int tutorialCooldown = TUTORIAL_TOTAL_LEN;
  private float tutorialFade = 1f;

  private final Player.Info[] metadata = new Player.Info[4];
  private final boolean[] isInGame = new boolean[4];

  private final ArrayList<MenuParticle> particles = new ArrayList<>();
  private final ArrayList<MenuParticle> particles2 = new ArrayList<>();

  private final boolean[] pressingLB = new boolean[4];
  private final boolean[] pressingLT = new boolean[4];
  private final boolean[] pressingRB = new boolean[4];
  private final boolean[] pressingRT = new boolean[4];

  private static final float MAX_COOLDOWN = 1.25f * 60;
  private float cooldown = 0;

  private boolean started = false;

  private float transition = 0;

  private float angle = 0;

  public void preload() {
    loadImage("playerbody1gs");
    loadImage("playerbody2gs");
    loadImage("playerbody3gs");
    loadImage("playerbody4gs");
    loadImage("playerbody5gs");
    loadImage("logo");
  }

  public void setup() {
    for (int i = 0; i < 4; i++) {
      metadata[i] = new Player.Info(null, null);
    }
    for (int i = 0; i < 200; i++) {
      tickParticles();
    }
  }

  public void draw() {
    Window window = getWindow();

    // Player barsIn Corners
    for (int i = 0; i < 4; i++) {
      Player.Info info = metadata[i];

      float scale = 6f;

      int x = 0;
      int y = 0;
      // Top Left
      if(i == 0) {
        x = (int) ((Window.WIDTH/4 - (32f * scale / 2f)) - (1-transition) * (Window.WIDTH / 2));
        y = (int) ((Window.HEIGHT/4 - (48f * scale / 2f)));
      }
      // Bottom Left
      final float bottomY = (Window.HEIGHT / 4 + Window.HEIGHT / 2 - (48f * scale / 2f)) - 50;
      if(i == 2) {
        x = (int) ((Window.WIDTH/4 - (32f * scale / 2f)) - (1-transition) * (Window.WIDTH / 2));
        y = (int) bottomY;
      }
      // Top Right
      if(i == 1) {
        x = (int) ((Window.WIDTH/4 + Window.WIDTH/2 - (32f * scale / 2f)) + (1-transition) * (Window.WIDTH / 2));
        y = (int) ((Window.HEIGHT/4 - (48f * scale / 2f)));
      }
      // Bottom Right
      if(i == 3) {
        x = (int) ((Window.WIDTH/4 + Window.WIDTH/2 - (32f * scale / 2f)) + (1-transition) * (Window.WIDTH / 2));
        y = (int) bottomY;
      }

      boolean selected = isInGame[i];

      Color color = PlayerColor.getPlayerColor(i);

      PImage imgBody = getImage("playerbody" + info.body + (selected ? "" : "gs"));
      PImage imgShirt = getImage("playershirt" + info.shirt);
      PImage imgHat = getImage("hat1");

      window.imageScaled(imgBody, x, y, scale);

      if(selected) {
        window.tint(color.getRed(), color.getGreen(), color.getBlue(), 255);
      } else {
        window.tint(200, 255);
      }
      window.imageScaled(imgShirt, x, y, scale);
      window.imageScaled(imgHat, x + 25, y + 20, scale);
      window.noTint();
    }

    window.translate(0, (1-transition) * 100);

    // Text
    final String line = tutorial[tutorialText];
    final float fade1 = tutorialFade * 2f;
    final float oneFade = 5f / line.length();
    window.textSize(64);
    window.textAlign(window.CENTER, window.CENTER);
    for (int i = 0; i < line.length(); i++) {
      float opacity = ((fade1 + oneFade * fade1) - (i * oneFade / 5f)) / oneFade;
      window.fill(255,255,255,opacity * 255);
      window.text(line.charAt(i), Window.WIDTH/2 - (line.length() * 16) + i * 32, Window.HEIGHT - 80);
    }

    String nextLine = tutorial[(tutorialText + 1) == tutorial.length ? 0 : tutorialText + 1];
    final float oneFade2 = 5f / nextLine.length();
    final float fade2 = ((1 - tutorialFade) - 0.5f) * 2f;
    for (int i = 0; i < nextLine.length(); i++) {
      float opacity = ((fade2 + oneFade2 * fade2) - ((nextLine.length() - i) * oneFade2 / 5f)) / oneFade2;
      window.fill(255,255,255,opacity * 255);
      window.text(nextLine.charAt(i), Window.WIDTH/2 - (nextLine.length() * 16) + i * 32, Window.HEIGHT - 80);
    }


    // The bar
    window.strokeWeight(10);
    window.fill(23, 19, 66, 255);
    window.rect(0,Window.HEIGHT - 38, Window.WIDTH, 32);
    window.fill(36, 30, 104,255);
    window.rect(0,Window.HEIGHT - 32, Window.WIDTH, 32);
    window.fill(0,255,255,255);
    window.rect(0,Window.HEIGHT - 32, (cooldown / MAX_COOLDOWN) * Window.WIDTH, 32);

    float sin = (float)Math.sin(angle / 60) * 8f;

    window.translate(0, (1-transition) * -100 + (1-transition) * -(Window.HEIGHT / 2) + sin);

    for (MenuParticle p: particles2) {
      p.draw();
    }

    window.imageScaled(getImage("logo"), Window.WIDTH/2 - 406*1.5f*0.5f, 78, 1.5f);

    for (MenuParticle p: particles) {
      p.draw();
    }

    window.translate(0, (1-transition) * (Window.HEIGHT / 2) - sin);
  }

  private void tickParticles() {
    int count = 1 + (RandomUtil.randomInt(1, 4) == 1 ? 1 : 0);
    for (int i = 0; i < count; i++) {
      particles2.add((MenuParticle) createRenderable(new MenuParticle(Window.WIDTH/2, 282)));
    }
    particles.add((MenuParticle) createRenderable(new MenuParticle(Window.WIDTH/2, 282)));

    for (int i = 0; i < particles.size(); i++) {
      MenuParticle p = particles.get(i);
      p.update();
      if(p.dead) {
        particles.remove(p);
        i--;
      }
    }
    for (int i = 0; i < particles2.size(); i++) {
      MenuParticle p = particles2.get(i);
      p.update();
      if(p.dead) {
        particles2.remove(p);
        i--;
      }
    }
  }
  public void update() {
    tickParticles();

    angle++;

    tutorialCooldown--;
    if(tutorialCooldown < TUTORIAL_FADE_LEN) {
      tutorialFade -= 1 / (float)TUTORIAL_FADE_LEN;
    }
    if(tutorialCooldown == 0) {
      tutorialCooldown = TUTORIAL_TOTAL_LEN;
      tutorialText++;
      tutorialFade = 1f;
      if(tutorialText >= tutorial.length) {
        tutorialText = 0;
      }
    }

    if(!started) {

      int playingCount = 0;

      for (int i = 0; i < 4; i++) {
        isInGame[i] = XInputManager.getButton(i, XInputButton.A);

        if(isInGame[i]) playingCount++;

        if(XInputManager.getButton(i, XInputButton.LEFT_SHOULDER)) {
          if(!pressingLB[i]) {
            metadata[i].body--;
            if(metadata[i].body < 1) {
              metadata[i].body = 5;
            }
          }
          pressingLB[i] = true;
        } else {
          pressingLB[i] = false;
        }

        if(XInputManager.getButton(i, XInputButton.RIGHT_SHOULDER)) {
          if(!pressingRB[i]) {
            metadata[i].body++;
            if(metadata[i].body > 5) {
              metadata[i].body = 1;
            }
          }
          pressingRB[i] = true;
        } else {
          pressingRB[i] = false;
        }

        if(XInputManager.getAxis(i, XInputAxis.LEFT_TRIGGER) >= 0.5f) {
          if(!pressingLT[i]) {
            metadata[i].shirt--;
            if(metadata[i].shirt < 1) {
              metadata[i].shirt = 7;
            }
          }
          pressingLT[i] = true;
        } else {
          pressingLT[i] = false;
        }

        if(XInputManager.getAxis(i, XInputAxis.RIGHT_TRIGGER) >= 0.5f) {
          if(!pressingRT[i]) {
            metadata[i].shirt++;
            if(metadata[i].shirt > 7) {
              metadata[i].shirt = 1;
            }
          }
          pressingRT[i] = true;
        } else {
          pressingRT[i] = false;
        }
      }

      if(playingCount >= 2) {
        cooldown++;
      } else {
        cooldown *= 0.8f;
      }

      if(cooldown >= MAX_COOLDOWN) {
        Player.InfoE[] initData = new Player.InfoE[4];
        for (int i = 0; i < 4; i++) {
          initData[i] = new Player.InfoE(metadata[i]);
          initData[i].in = isInGame[i];
        }
        started = true;
        TransitionToGame.transitionTo(createRenderable(new GameScene(initData)));
      }
    }

    // magic
    if(started) {
      transition = MathUtil.lerp(transition, 0f, 0.1f);
    } else {
      transition = MathUtil.lerp(transition, 1f, 0.05f);
    }
  }
}
