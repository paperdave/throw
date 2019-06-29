package game.objects;

import davecode.util.MathUtil;
import davecode.util.RandomUtil;
import game.main.*;
import game.main.Level.Block;
import game.scenes.GameScene;
import game.scenes.RootScene;

import java.awt.Point;

import static game.scenes.GameScene.*;

// This is just a template
public class Fireball extends GameItem {
  private int angle = 0;

  private int time = 0;

  private double x = 0;
  private double y = 0;

  private float xOffset = 0;
  private float xSpeed = 0;
  private float ySpeed = 0;

  private double targetX = 0;
  private double targetY = 0;
  private float scale = 0;

  private boolean failedExplodeCheck = false;

  private float z = -14f;
  private float zSpeed = -14f;

  @SuppressWarnings("unused") // Required for Resource Preloader
  public Fireball() {}
  public Fireball(double x, double y, float targetX, float targetY) {
    this.x = x;
    this.y = y;
    this.targetX = targetX;
    this.targetY = targetY;
  }

  public void preload() {
    loadImage("fireball1");
    loadImage("fireball2");
    loadImage("fireball3");
    loadSound("throwBig");
    loadSound("crash1");
    loadSound("crash2");
    loadSound("crash3");
    loadSound("crash4");
    loadSound("crash5");
  }

  public void setup() {
    final float AIRTIME = 56f; // hardcoded from the 14 zSpeed

    xSpeed = ((float)targetX - (float)x) / AIRTIME;
    ySpeed = ((float)targetY - (float)y) / AIRTIME;

    getWindow().shake(2, 30);
  }

  public void draw() {
    Window window = getWindow();

    float xPos = (float) Math.round(x) - 32 + xOffset + 32;
    float yPos = (float) Math.round(y) - 80 + z + 32;

    // Your Render Here
    window.translate(xPos, yPos);
    window.rotate((float)((angle / 360.0) * 4 * Math.PI));
    window.scale(scale);
    window.imageScaled(getImage("fireball" + RandomUtil.randomInt(1, 3)), -32, -32, 2);
    window.scale(1/scale);
    window.rotate(-(float)((angle / 360.0) * 4 * Math.PI));
    window.translate(-xPos, -yPos);

    sortY = (float) (targetY + 32);
  }

  private Block getBlockAt(int relX, int relY) {
    Point pos = Level.pixelToMap(
      new Point(
        (int)Math.round(x + xOffset),
        (int)Math.round(targetY)
      )
    );
    pos.x += relX;
    pos.y += relY;
    return Level.currentLevel.blockAt(
      pos
    );
  }

  public void update() {
    if(dead) return;

    angle += 4;
    if(angle >= 360) {
      angle -= 360;
    }
    scale = MathUtil.lerp(scale, 1, 0.5f);

    zSpeed += 0.5f;
    z += zSpeed;
    if (z <= 0) {
      time++;
    } else if(!failedExplodeCheck) {
      // Explode
      Block standingBlock = Level.currentLevel.blockAt(
        Level.pixelToMap(
          new Point(
            (int)Math.round(x + xOffset),
            (int)Math.round(targetY)
          )
        )
      );

      if(
        standingBlock == null
          || standingBlock.falling
      ) {
        failedExplodeCheck = true;      sortToBack = true;

      } else {
        dead = true;
        getWindow().shake(10, 30);

        int soundVariant = RandomUtil.randomInt(1, 5);
        playSound("crash" + soundVariant);
        playSound("crash" + soundVariant);
        playSound("crash" + soundVariant);

        // parts
        int count = RandomUtil.randomInt(20, 30);
        GameScene gameScene = getGlobalGameScene();
        for (int i = 0; i < count; i++) {
          gameScene.layerFront.add(
            (ExplosionPart) createRenderable(new ExplosionPart(xOffset + x, targetY))
          );
        }

        // break
        Block direct = getBlockAt(0, 0);
        if(RandomUtil.randomInt(1, 5) != 5) {
          direct.falling = true;
        }

        for (int i = 0; i < 3; i++) {
          for (int j = 0; j < 3; j++) {
            Block found = getBlockAt(i - 1, j - 1);
            if(found != null) {
              if(RandomUtil.randomInt(1,5) <= 2) {
                found.falling = true;
              }
            }
          }
        }
        Block far = getBlockAt(0, 2);
        if(far != null && RandomUtil.randomInt(1,4) == 3) {
          far.falling = true;
        }
        far = getBlockAt(-2,0);
        if(far != null && RandomUtil.randomInt(1,4) == 3) {
          far.falling = true;
        }
        far = getBlockAt(0, -2);
        if(far != null && RandomUtil.randomInt(1,4) == 3) {
          far.falling = true;
        }
        far = getBlockAt(2, 0);
        if(far != null && RandomUtil.randomInt(1,4) == 3) {
          far.falling = true;
        }

        if(RootScene.instance.currentScene instanceof GameScene) {
          for (Player player : gameScene.players) {
            float xPos = (float) X_OFFSET + Math.round(player.x);
            float yPos = (float) Y_OFFSET + Math.round(player.y);

            float dist = (float) Math.sqrt(Math.pow(xPos - targetX, 2) + Math.pow(yPos - targetY, 2));

            if(dist <= 30) {
              player.hp -= 2;
              player.hpFlash = 2;
            } else if(dist <= 80) {
              player.hp -= 1;
              player.hpFlash = 1;
            }
          }
        }
      }
    }

    xOffset += xSpeed;
    y += ySpeed;

    if(z > 2000) {
      dead = true;
    }

    getGlobalGameScene().layerFront.add(
      (FireballParticle) createRenderable(new FireballParticle(xOffset + x, y + z))
    );
  }
}
