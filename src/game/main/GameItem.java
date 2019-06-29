package game.main;

public abstract class GameItem extends Renderable {
  public boolean dead = false;
  public boolean sortToBack = false;
  public boolean sortToFront = false;

  public float sortY = 0;
}
