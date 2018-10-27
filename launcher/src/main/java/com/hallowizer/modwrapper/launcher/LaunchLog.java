package com.hallowizer.modwrapper.launcher;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LaunchLog {
//	private final Logger log = LogManager.getLogger("ModWrapper");
	
	private final boolean debug = Boolean.parseBoolean(System.getProperty("modwrapper.debug", "false"));
	
	public void debug(String message) {
		if (debug)
			System.out.println(message);
	}
	
	public void debug(Throwable e) {
		if (debug)
			e.printStackTrace();
	}
}
