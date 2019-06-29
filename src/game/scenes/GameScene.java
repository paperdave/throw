package game.scenes;

import davecode.log.Logger;
import davecode.util.RandomUtil;
import game.main.GameItem;
import game.main.Level;
import game.main.MusicBeat;
import game.main.Renderable;
import game.objects.MapTile;
import game.objects.Player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;

import static game.main.ResourceManager.loadMusic;
import static game.main.ResourceManager.playMusic;
import static game.main.Window.HEIGHT;
import static game.main.Window.WIDTH;

// The Game
public class GameScene extends Renderable {
  public static GameScene getGlobalGameScene() {
    Renderable scene = RootScene.instance.currentScene;

    GameScene gameScene;
    if(scene instanceof GameScene) {
      gameScene = (GameScene) scene;
    } else if (scene instanceof TransitionToGame) {
      gameScene = (GameScene) ((TransitionToGame) scene).game;
    } else if (scene instanceof TransitionFromGame) {
      scene = ((TransitionFromGame) scene).game;
      if(scene instanceof GameScene) {
        gameScene = (GameScene) scene;
      } else if (scene instanceof TransitionToGame) {
        gameScene = (GameScene) ((TransitionToGame) scene).game;
      } else {
        throw new Error("Cannot find GameScene");
      }
    } else {
      throw new Error("Cannot find GameScene");
    }

    return gameScene;
  }

  public static int X_OFFSET, Y_OFFSET;

  public static void recalculateOffsets() {
    X_OFFSET = (int) ((Math.round(WIDTH) / 2f) - 32);
    Y_OFFSET = (int) ((Math.round(HEIGHT) / 2f) - 12 * 16 + 16);
  }

  public Level level;

  public GameScene() {}

  Player.InfoE[] initData;

  public GameScene(Player.InfoE[] metadata) {
    initData = metadata;
  }

  public ArrayList<Player> players = new ArrayList<>();

  public ArrayList<GameItem> layerBack = new ArrayList<>();
  public ArrayList<GameItem> layerFront = new ArrayList<>();

  public void preload() {
    loadMusic("music1");
    loadMusic("music2");
    loadMusic("music3");
    loadMusic("music4");
    loadMusic("music5");
    loadMusic("music6");
  }

  public boolean hasFirstTick = false;
  public boolean gameDone = false;

  public int musicID = RandomUtil.randomInt(1, 6);
  public void firstTick() {
    MusicBeat.songID = musicID;
    playMusic("music" + musicID);
  }

  public void setup() {
    level = new Level();
    Level.currentLevel = level;

    for (int i = 0; i < 12; i++) {
      for (int j = 0; j < 12; j++) {
        layerBack.add(
          (GameItem)createRenderable(
            new MapTile(
              level.blockAt(new Point(i, j))
            )
          )
        );
      }
    }

    for (int i = 0; i < 4; i++) {
      if(!initData[i].in) {
        continue;
      }
      Player player = (Player) createRenderable(new Player());

      do {
        Point pos = Level.mapToPixel(new Point(RandomUtil.randomInt(1, 10), RandomUtil.randomInt(1, 10)));

        player.x = pos.x - X_OFFSET;
        player.y = pos.y - Y_OFFSET + 5;
      } while (spotIsTaken(player.x, player.y));

      player.playerID = i;
      player.info = new Player.Info(initData[i].body ,initData[i].shirt);
      layerFront.add(player);
      players.add(player);
    }
  }

  private boolean spotIsTaken(double x, double y) {
    for (Player player : players) {
      // calculate distance
      double distance = Math.sqrt(Math.pow((x - player.x), 2) + Math.pow((y - player.y), 2));
      // if really close return true
      if(distance < 100.0) {
        return true;
      }
    }
    return false;
  }

  public void draw() {
    for (int i = 0; i < layerBack.size(); i++) {
      GameItem item = layerBack.get(i);
      item.draw();
    }
    for (int i = 0; i < layerFront.size(); i++) {
      GameItem item = layerFront.get(i);
      item.draw();
      if(item instanceof Player) {
        ((Player) item).postDraw();
      }
    }
  }

  public void update() {
    if(!hasFirstTick) {
      hasFirstTick = true;
      firstTick();
    }

    try {
      layerBack.sort(Comparator.comparingInt(o -> Math.round(o.sortY)));
      layerFront.sort(Comparator.comparingInt(o -> Math.round(o.sortY)));
    } catch (IllegalArgumentException e) {
      Logger.error("GAME CRASH REPORT");
      Logger.error("=================");
      e.printStackTrace();
      Logger.error("This happened barsIn the update() method for GameScene, while sorting the layers. Here are the layer informations");
      for (GameItem i: layerBack) {
        Logger.error("BackLayer" +i.getClass().getName() + " SORT Y " + i.sortY);
      }
      for (GameItem i: layerFront) {
        Logger.error("FrontLayer " + i.getClass().getName() + " SORT Y " + i.sortY);
      }
      System.exit(100);
    }

    for (int i = 0; i < layerBack.size(); i++) {
      GameItem item = layerBack.get(i);

      if(item.dead) {
        layerBack.remove(item);
        i--;
      } else if(item.sortToFront) {
        layerBack.remove(item);
        layerFront.add(item);
        item.sortToFront = false;
        i--;
      } else {
        item.update();
      }
    }
    for (int i = 0; i < layerFront.size(); i++) {
      GameItem item = layerFront.get(i);

      boolean isPlayerAndDone = item instanceof Player && gameDone;

      if(isPlayerAndDone) {
        ((Player) item).cooldown = 0;
        ((Player) item).isReadyThrow = false;
      }

      if(item.dead) {
        layerFront.remove(item);
        i--;
      } else if(item.sortToBack) {
        layerBack.add(item);
        layerFront.remove(item);
        item.sortToBack = false;
        if (!(isPlayerAndDone)) {
          item.update();
        }
        i--;
      } else {
        if (!(isPlayerAndDone)) {
          item.update();
        }
      }
    }

    for (Player p: players) {
      p.updateButItAlwaysRuns();

      if(gameDone) {
        if(p.dead) {
          layerFront.remove(p);
          layerBack.remove(p);
        }
      }
    }

    if(!gameDone) {
      int aliveCount = 0;
      Player lastStanding = null;
      for (int i = 0; i < players.size(); i++) {
        Player item = players.get(i);
        if(!item.dead && !item.isFalling) {
          aliveCount++;
          lastStanding = item;
        }
      }

      if(aliveCount == 1) {
        gameDone = true;
        int xPos = (int) ( X_OFFSET + Math.round(lastStanding.x));
        int yPos = (int) ( Y_OFFSET + Math.round(lastStanding.y) - 40);

        TransitionFromGame.transitionFrom(new Point(xPos, yPos), lastStanding.playerID);
      }
      if(aliveCount == 0) {
        gameDone = true;
        TransitionFromGame.transitionFrom(new Point((int)WIDTH/2, (int)HEIGHT/2), 0);
      }
    }
  }
}
