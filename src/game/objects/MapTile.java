package game.objects;

import davecode.util.RandomUtil;
import game.main.GameItem;
import game.main.Level;
import game.main.Level.Block;
import game.main.Window;

import java.awt.Point;

// This is just a template
public class MapTile extends GameItem {
  private Block block;

  @SuppressWarnings("unused") // Required for Resource Preloader
  public MapTile() { }
  public MapTile(Block block) {
    this.block = block;
  }

  public void preload() {
    loadImage("block1");
    loadImage("block2");
    loadImage("block3");
    loadImage("block4");
    loadImage("blockshadow");
  }

  public void setup() {

  }

  public void draw() {
    Window window = getWindow();

    // Draw the block image.
    Point point = Level.mapToPixel(new Point(block.x, block.y));
    window.image(
      getImage("block" + block.variant),
      point.x,
      point.y + Math.round(block.z),
      64, 64
    );

    sortY = point.y + 16;
  }

  public void update() {
    if (block.falling && block.z == 0) {
      if(RandomUtil.randomInt(0, 4) != 0) return;
    }
    if (block.falling && block.z < 1000) {
      block.zSpeed += 0.5;
      block.z += block.zSpeed;
    }
  }
}
