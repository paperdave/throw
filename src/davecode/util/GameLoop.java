package davecode.util;

import davecode.log.Logger;

public class GameLoop {
	public boolean running = true;
	public Thread thread;

	public static void GameLoopRunner(int tps, Runnable tick, Runnable onCycle) {
		Logger.info("Starting Game Loop");
		boolean running = true;

		long lastTime = System.nanoTime();
		double ns = 1000000000 / tps;
		double delta = 0;

		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick.run();
				delta--;
			}

			if(onCycle != null) {
				onCycle.run();
			}
		}
	}
	/**
	 * A Game Loop
	 * 
	 * @param tps The Speed of calling tick.run()
	 * @param tick A <code>Runnable</code> that is ran `tps` times a second
	 * @param onCycle called every cycle
	 * @param name A thread name
	 */	
	public GameLoop(int tps, Runnable tick, Runnable onCycle, String name) {
		thread = new Thread(new Runnable() {
			public void run() {
				GameLoopRunner(tps, tick, onCycle);
			}
		}, name);
		
		thread.start();
	}
	
	/**
	 * A Game Loop
	 * 
	 * @param tps The Speed of calling tick.run()
	 * @param tick A <code>Runnable</code> that is ran `tps` times a second
	 */
	public GameLoop(int tps, Runnable tick) {
		this(tps, tick, null, "GameLogic");
	}
	/**
	 * A Game Loop
	 * 
	 * @param tps The Speed of calling tick.run()
	 * @param tick A <code>Runnable</code> that is ran `tps` times a second
	 * @param name A thread name
	 */
	public GameLoop(int tps, Runnable tick, String name) {
		this(tps, tick, null, name);
	}
}