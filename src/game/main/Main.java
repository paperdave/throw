package game.main;

import com.github.strikerx3.jxinput.XInputDevice;

import davecode.controller.XInputManager;
import davecode.log.Logger;
import processing.core.PApplet;

public class Main {
  // Entry, Init
  public static void main(String[] args) {
    Logger.info("Starting Game...");

    // The sanity check
    if(!XInputDevice.isAvailable()) {
      Logger.error("Contoller Support is NOT Available");
      return;
    }

    XInputManager.startThread();

    // Processing
    PApplet.main(Window.class);
  }
}
