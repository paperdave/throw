package davecode.util;

public class MathUtil {
  public static double lerp(int low, int hi, double perc) {
    return low + (hi - low) * perc;
  }
  public static float lerp(float low, float hi, float perc) {
    return low + (hi - low) * perc;
  }
}
