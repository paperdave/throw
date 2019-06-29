package davecode.log;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("ALL")
public class Logger {
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	
	private static void loglevel(String level, Object data) {
		System.out.println("[" + timeFormat.format(new Date()) + "] [" + Thread.currentThread().getName() + "] [" + level + "] " + data.toString());
	}
	private static void loglevelerr(String level, Object data) {
		System.err.println("[" + timeFormat.format(new Date()) + "] [" + Thread.currentThread().getName() + "] [" + level + "] " + data.toString());
	}
	public static void info(Object data) {
		loglevel("INFO", data);
	}
	public static void debug(Object data) {
		loglevel("DEBUG", data);
	}
	public static void warn(Object data) {
		loglevel("WARN", data);
	}
	public static void error(Object data) {
		loglevelerr("ERROR", data);
	}
	public static void fatal(Object data) {
		loglevelerr("FATAL", data);
		System.exit(1);
	}
	public static void fatal(Object data, int status) {
		loglevelerr("FATAL", data);
		System.exit(status);
	}
}
