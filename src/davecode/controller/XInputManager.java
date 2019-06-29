package davecode.controller;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.enums.XInputAxis;
import com.github.strikerx3.jxinput.enums.XInputButton;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;

import davecode.log.Logger;

public class XInputManager implements Runnable {
	private static XInputDevice[] devices;
	private static XInputButtons[] buttons = new XInputButtons[4];
	private static XInputAxes[] axes = new XInputAxes[4];
	
	// thread stuff
	private static Thread thread;
	private static boolean isRunning = false;
	
	public static void startThread() {
		if (isRunning) {
			return;
		}
		isRunning = true;
		thread = new Thread(new XInputManager(), "XInputManager");
		thread.start();
	}
	
	public static void stopThread() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		Logger.info("Starting XInputManager");

		if(!XInputDevice.isAvailable()) {
			Logger.error("Cannot Load JXInput as it is not available, you wont be able to use any controllers.");
			return;
		}

		try {
			devices = XInputDevice.getAllDevices();

			while (isRunning) {
				int i = 0;
				XInputComponents components;
				for (XInputDevice device : devices) {
					device.poll();
					
					components = device.getComponents();
					buttons[i] = components.getButtons();
					axes[i] = components.getAxes();
					i++;
				}
			}
		} catch (XInputNotLoadedException e) {
			Logger.error("JXInput has crashed! See stack trace below.");
			e.printStackTrace();
		}
		
		Logger.info("Shutting Down.");
	}

	public static int getCount() {
		int count = 0;
		for (XInputDevice device : devices) {
			if(device.isConnected()) {
				count++;
			}
		}
		return count;
	}
	
	// Getter Methods for all the stuff.
	private static boolean getXInputButtonValue(XInputButtons buttons, XInputButton button) {
		switch(button) {
			case A: return buttons.a;
			case B: return buttons.b;
			case X: return buttons.x;
			case Y: return buttons.y;
			case BACK: return buttons.back;
			case DPAD_DOWN: return buttons.down;
			case DPAD_LEFT: return buttons.left;
			case DPAD_RIGHT: return buttons.right;
			case DPAD_UP: return buttons.up;
			case GUIDE_BUTTON: return buttons.guide;
			case LEFT_SHOULDER: return buttons.lShoulder;
			case RIGHT_SHOULDER: return buttons.rShoulder;
			case RIGHT_THUMBSTICK: return buttons.rThumb;
			case START: return buttons.start;
			case LEFT_THUMBSTICK: return buttons.rThumb;
			case UNKNOWN: return buttons.unknown;
		}
		return false;
	}
	public static boolean getButton(int player, XInputButton button) {
		return getXInputButtonValue(buttons[player], button);
	}
	private static float getXInputAxisValue(XInputAxes axes, XInputAxis axis) {
		switch(axis) {
			case DPAD: return axes.dpad;
			case LEFT_THUMBSTICK_X: return axes.lx;
			case LEFT_THUMBSTICK_Y: return axes.ly;
			case LEFT_TRIGGER: return axes.lt;
			case RIGHT_THUMBSTICK_X: return axes.rx;
			case RIGHT_THUMBSTICK_Y: return axes.ry;
			case RIGHT_TRIGGER: return axes.rt;
		}
		return 0f;
	}
	public static float getAxis(int player, XInputAxis axis) {
		return getXInputAxisValue(axes[player], axis);
	}
}
