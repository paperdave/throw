package game.main;

import processing.core.PImage;

// Simple Class for making rendering barsIn other files way easier.
abstract public class Renderable {
  public abstract void preload();
  public abstract void setup();
  public abstract void draw();
  public abstract void update();

  // Get render window
  protected Window getWindow() {
    return Window.instance;
  }

  // Resource Manager
  protected static void loadImage(String id) {
    ResourceManager.loadImage(id);
  }
  protected static PImage getImage(String id) {
    return ResourceManager.getImage(id);
  }
  protected static void loadSound(String id) {
    ResourceManager.loadSound(id);
  }
  protected static void playSound(String id) { ResourceManager.playSound(id); }

  // Create another renderable
  protected static Renderable createRenderable(Renderable renderable) {
    renderable.setup();
    return renderable;
  }
}
