package com.hallowizer.modwrapper.api;

import java.io.File;
import java.util.List;

/**
 * A mod loader that launches with ModWrapper.
 * 
 * @author Hallowizer
 *
 */
public interface IModLoader {
	/**
	 * Provides the mod loader with data.
	 * 
	 * @param args The launch arguments. This list will be passed to the game, so add/remove any args.
	 * @param version The version that is being launched, or <code>"unknown"</code> if the version was not specified.
	 * @param gameDir The directory where game files are stored, or the current directory if it was not specified.
	 */
	public abstract void injectData(List<String> args, String version, File gameDir);
	
	/**
	 * Injects transformers into the class loader.
	 * 
	 * @param classLoader The class loader to inject transformers into.
	 */
	public abstract void configureClassLoader(ConfigurableClassLoader classLoader);
	
	/**
	 * Gets the main class that should be launched.
	 * 
	 * @return The fully qualified name of the main class.
	 */
	public abstract String getMainClass();
}
