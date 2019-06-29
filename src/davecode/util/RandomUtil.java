package davecode.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
	public static int randomInt(int low, int high) {
		return ThreadLocalRandom.current().nextInt(low, high + 1);
	}
	public static int randomInt(double low, double high) {
		return randomInt((int)Math.round(low), (int)Math.round(high));
	}
	public static int randomInt(float low, float high) {
		return randomInt((int)Math.round(low), (int)Math.round(high));
	}
	public static double randomDouble(double low, double high) {
		return ThreadLocalRandom.current().nextDouble(low, high);
	}
	public static double randomDouble(int low, int high) {
		return randomDouble(low, high);
	}
	public static float randomFloat(float low, float high) {
		return (float)randomDouble(low, high);
	}
	public static double randomFloat(int low, int high) {
		return randomFloat((float)low, (float)high);
	}
}
