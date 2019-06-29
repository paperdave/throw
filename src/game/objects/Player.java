package game.objects;

import com.github.strikerx3.jxinput.enums.XInputAxis;
import com.github.strikerx3.jxinput.enums.XInputButton;
import davecode.controller.XInputManager;
import davecode.util.MathUtil;
import davecode.util.RandomUtil;
import game.main.*;
import game.main.Level.Block;
import game.scenes.GameScene;
import processing.core.PImage;
import java.awt.Point;
import java.awt.Color;
import java.util.Objects;

import static game.scenes.GameScene.*;

// Is a player, draws a player.
public class Player extends GameItem {
  public int playerID = 0;

  private float animWalk = 0;
  private int animBounceTime = 0;

  public double x = 0;
  public double y = 0;
  private double lastX = 0;
  private double lastY = 0;
  private double z = 0;
  private double lagX = 0;
  private double lagY = 0;
  public Info info = new Info();

  private static final float maxSpeed = 4f;
  private float speed = 0f;

  public boolean isFalling = false;
  private double fallSpeed = 0;

  public boolean isReadyThrow = false;
  private float targetX = 0;
  private float targetY = 0;
  private float angle = 0;

  private boolean throwBtnPressed = false;
  public int cooldown = 0;
  private float cooldownBarIn = 0;

  private static final int MAX_COOLDOWN = (int)(2.2 * 60);

  public int hp = 3;
  public float hpFlash = 0;
  private boolean isHoldingY = false;

  public void preload() {
    loadImage("playerbody1");
    loadImage("playerbody2");
    loadImage("playerbody3");
    loadImage("playerbody4");
    loadImage("playerbody5");
    loadImage("playerbody1b");
    loadImage("playerbody2b");
    loadImage("playerbody3b");
    loadImage("playerbody4b");
    loadImage("playerbody5b");
    loadImage("playerbody1w1");
    loadImage("playerbody2w1");
    loadImage("playerbody3w1");
    loadImage("playerbody4w1");
    loadImage("playerbody5w1");
    loadImage("playerbody1bw1");
    loadImage("playerbody2bw1");
    loadImage("playerbody3bw1");
    loadImage("playerbody4bw1");
    loadImage("playerbody5bw1");
    loadImage("playerbody1w2");
    loadImage("playerbody2w2");
    loadImage("playerbody3w2");
    loadImage("playerbody4w2");
    loadImage("playerbody5w2");
    loadImage("playerbody1bw2");
    loadImage("playerbody2bw2");
    loadImage("playerbody3bw2");
    loadImage("playerbody4bw2");
    loadImage("playerbody5bw2");
    loadImage("playershirt1");
    loadImage("playershirt2");
    loadImage("playershirt3");
    loadImage("playershirt4");
    loadImage("playershirt5");
    loadImage("playershirt6");
    loadImage("playershirt7");
    loadImage("playershirt1b");
    loadImage("playershirt2b");
    loadImage("playershirt3b");
    loadImage("playershirt4b");
    loadImage("playershirt5b");
    loadImage("playershirt6b");
    loadImage("playershirt7b");
    loadImage("hat1");
    loadImage("cross");
    loadImage("shadow");
    loadSound("drop");
    loadSound("death1");
    loadSound("death2");
  }

  public void setup() {
  }

  public void draw() {
    Window window = getWindow();
    Color color = PlayerColor.getPlayerColor(playerID);

    float xPos = (float) X_OFFSET + Math.round(x) - 32;
    float yPos = (float) Y_OFFSET + Math.round(y) - 80 + (float)z;

    if(!isFalling) {
      float shadowScale = 1.2f;
      window.tint(0, 50);
      window.imageScaled(getImage("shadow"), xPos + 32 - 16 * shadowScale, yPos + 80 - 16 * shadowScale, shadowScale);
      window.noTint();
    }

    boolean isBounce = animBounceTime < 5;
    boolean isWalking = x != lastX || y != lastY;
    String walkFrame = animWalk >= 4 ? "2" : "1";

    PImage imgBody = getImage("playerbody" + info.body + (isBounce ? "b" : "") + (isWalking ? "w" + walkFrame : ""));
    PImage imgShirt = getImage("playershirt" + info.shirt + (isBounce ? "b" : ""));
    PImage imgHat = getImage("hat1");

    window.tint(255, MathUtil.lerp(255,0f, hpFlash), MathUtil.lerp(255,0f, hpFlash));
    window.imageScaled(imgBody, xPos, yPos, 2);

    window.tint(
      MathUtil.lerp(color.getRed(),255f, hpFlash),
      MathUtil.lerp(color.getGreen(),0f, hpFlash),
      MathUtil.lerp(color.getBlue(),0f, hpFlash)
    );

    window.imageScaled(imgShirt, xPos, yPos, 2);
    window.imageScaled(imgHat, xPos + 7, yPos + 5 + (isBounce ? 2 : 0), 2);

    window.noTint();

    sortY = Y_OFFSET + Math.round(y);
  }

  public void postDraw() {
    Window window = getWindow();
    Color color = PlayerColor.getPlayerColor(playerID);

    if (isReadyThrow && !isFalling && cooldown <= 0) {
      float scale = (float)Math.sin(angle * 1.5f) * 0.5f + 4f;

      float xPos = (float) X_OFFSET + Math.round(x) - 32;
      float yPos = (float) Y_OFFSET + Math.round(y) - 80;

      // draw target cross
      window.translate(xPos + targetX + 7.5f + 3f, yPos + targetY + 7.5f + 3f);
      window.rotate(angle);
      window.scale(scale);

      window.tint(0, 0, 0, 150);
      window.imageScaled(getImage("cross"), -7.5f * 1, -7.5f * 1, 1);
      window.noTint();

      window.scale(1 / scale);
      window.rotate(-angle);
      window.translate(-(xPos + targetX  + 7.5f + 3f), -(yPos + targetY + 7.5f  + 3f));

      // draw target cross
      window.translate(xPos + targetX + 7.5f, yPos + targetY + 7.5f);
      window.rotate(angle);
      window.scale(scale);

      window.tint(color.getRed(), color.getGreen(), color.getBlue(), 255);
      window.imageScaled(getImage("cross"), -7.5f * 1, -7.5f * 1, 1);
      window.noTint();

      window.scale(1 / scale);
      window.rotate(-angle);
      window.translate(-(xPos + targetX  + 7.5f * 1), -(yPos + targetY + 7.5f * 1));

      float originX = xPos + 32;
      float originY = yPos + 50;

      float targetXX = xPos + targetX;
      float targetYY = yPos + targetY;

      float avgX = (originX + targetXX) / 2;
      float avgY = (originY + targetYY) / 2 - 200;

      window.noFill();
      window.stroke(0, 0, 0);
      window.strokeWeight((float)Math.sin(angle * 3f) * 4f + 11f);
      window.bezier(MathUtil.lerp(avgX, originX, 0.8f), MathUtil.lerp(avgY, originY, 0.85f), MathUtil.lerp(avgX, originX, 0.3f), avgY, MathUtil.lerp(avgX, targetXX, 0.3f), avgY, MathUtil.lerp(avgX, targetXX, 0.8f), MathUtil.lerp(avgY, targetYY, 0.85f));
      window.stroke(color.getRed(), color.getGreen(), color.getBlue());
      window.strokeWeight((float)Math.sin(angle * 3f) * 3f + 8f);
      window.bezier(MathUtil.lerp(avgX, originX, 0.8f), MathUtil.lerp(avgY, originY, 0.85f), MathUtil.lerp(avgX, originX, 0.3f), avgY, MathUtil.lerp(avgX, targetXX, 0.3f), avgY, MathUtil.lerp(avgX, targetXX, 0.8f), MathUtil.lerp(avgY, targetYY, 0.85f));
      window.noStroke();
      window.noFill();
    }

    if(cooldownBarIn > 0.05f) {
      float xPos = (float) X_OFFSET + Math.round(lagX) - 32;
      float yPos = (float) Y_OFFSET + Math.round(lagY);

      float oX = cooldownBarIn * 12;

      window.fill(50, 50, 50, 255);
      window.stroke(255,255,255, 255);
      window.strokeWeight(1);
      window.rect(xPos, yPos + 5 + 6 - oX / 2f, 64, oX);

      window.noStroke();
      window.fill(0, 255, 0, 255);
      window.rect(xPos + 1, yPos + 6 + 6 - oX / 2f, (1f - ((float)cooldown) / ((float)MAX_COOLDOWN)) * 63f, oX - 1);
    }

    if(cooldown > 0) {
      cooldownBarIn = MathUtil.lerp(cooldownBarIn, 1f, 0.4f);
    } else {
      cooldownBarIn = MathUtil.lerp(cooldownBarIn, 0f, 0.2f);
    }
  }

  public void update() {
    boolean isWalking = x != lastX || y != lastY;

    if(isWalking) {
      double distance = Math.sqrt(Math.pow(x - lastX, 2) + Math.pow(y - lastY, 2));

      animWalk += distance * 0.2f;
      if(animWalk >= 8) {
        animWalk = 0;
      }
    } else {
      animWalk = 8;
    }

    lastY = y;
    lastX = x;


    if(dead) return;

    lagX = MathUtil.lerp((float)lagX, (float)x, 0.6f);
    lagY = MathUtil.lerp((float)lagY, (float)y, 0.6f);

    float moveX = XInputManager.getAxis(playerID, XInputAxis.LEFT_THUMBSTICK_X);
    float moveY = -XInputManager.getAxis(playerID, XInputAxis.LEFT_THUMBSTICK_Y);

    double distance = Math.sqrt(moveX*moveX + moveY*moveY);

    if(distance >= 0.2) {
      speed = Math.min(speed + 1f, maxSpeed);
    } else {
      speed = Math.max(speed - 1f, 0);
    }

    x += moveX * speed;

    if (isFalling) {
      fallSpeed += 0.5f;
      z += fallSpeed;
    } else {
      y += (moveY * speed) / 1.25f;
    }

    // Fall Off Logic
    Block standingBlock = Level.currentLevel.blockAt(Level.pixelToMap(new Point((int)Math.round(X_OFFSET + x), (int)Math.round(Y_OFFSET + y))));

    if(
      (
        standingBlock == null
        || standingBlock.falling
      )
      && !isFalling
    ) {
      sortToBack = true;
      isFalling = true;
      playSound("drop");
      playSound("drop");
      playSound("drop");
    }

    float targetX = XInputManager.getAxis(playerID, XInputAxis.RIGHT_THUMBSTICK_X);
    float targetY = -XInputManager.getAxis(playerID, XInputAxis.RIGHT_THUMBSTICK_Y);

    isReadyThrow = Math.sqrt(targetX*targetX + targetY*targetY) > 0.2f;
    this.targetX = -targetX * 300;
    this.targetY = -targetY * 200;

    if(isReadyThrow) {
      angle += 0.05;
    }
    if (XInputManager.getButton(playerID, XInputButton.RIGHT_THUMBSTICK) ) {
      if(isReadyThrow && !throwBtnPressed && !isFalling && cooldown <= 0) {
        GameScene gameScene = getGlobalGameScene();

        float xPos = (float) X_OFFSET + Math.round(x) - 32;
        float yPos = (float) Y_OFFSET + Math.round(y) - 80;
        gameScene.layerFront.add(
          (Fireball)createRenderable(
            new Fireball(
              xPos + 32,
              yPos + 10,
              xPos + this.targetX + 7.5f + 3f,
              yPos + this.targetY + 7.5f + 3f
            )
          )
        );
        playSound("throwBig");
        playSound("throwBig");
        playSound("throwBig");
        cooldown = MAX_COOLDOWN;
      }
      throwBtnPressed = true;
    } else {
      throwBtnPressed = false;
    }
  }

  public void updateButItAlwaysRuns() {
    animBounceTime++;
    if(animBounceTime > MusicBeat.getBeatLength()) {
      animBounceTime = 0;
    }

    if(hp <= 0 && (!dead || isHoldingY)) {
      if(XInputManager.getButton(playerID, XInputButton.Y)) {
        isHoldingY = true;
      }

      dead = true;
      getWindow().shake(4, 30);

      int soundVar = RandomUtil.randomInt(1, 2);
      playSound("death" + soundVar);
      playSound("death" + soundVar);

      // parts
      int count = RandomUtil.randomInt(70, 150);
      GameScene gameScene = getGlobalGameScene();
      for (int i = 0; i < count; i++) {

        gameScene.layerFront.add(
          (PlayerDust) createRenderable(new PlayerDust(X_OFFSET + x, Y_OFFSET + y, PlayerColor.getPlayerColor(playerID)))
        );
      }
    }

    if(cooldown > 0) {
      cooldown--;
    }
    if(hpFlash >= 0) {
      hpFlash -= 0.05f;
    }
  }

  public static class Info {
    public int body;
    public int shirt;

    Info() {
      this(null, null);
    }
    public Info(Integer body, Integer shirt) {
      this.body = Objects.requireNonNullElseGet(body, () -> RandomUtil.randomInt(1, 5));
      this.shirt = Objects.requireNonNullElseGet(shirt, () -> RandomUtil.randomInt(1, 7));
    }
  }

  public static class InfoE extends Info {
    public boolean in = false;

    public InfoE(Info info) {
      this.body = info.body;
      this.shirt = info.shirt;
    }
  }
}
