package com.hallowizer.modwrapper.api;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/**
 * Launcher for ModWrapper.
 * 
 * @author Hallowizer
 *
 */
@UtilityClass
public class ModWrapper {
	/**
	 * Main method, for launching a client directly from the Minecraft Launcher.
	 * 
	 * @param args The command line arguments. Unrecognized options will be passed to the game.<br>
	 * <br>
	 * Recognized options:<br>
	 * <b>--version &lt;version&gt;</b> The version that is being launched. This will be passed to the mod loader. Optional arg.<br>
	 * <b>--gameDir &lt;gameDir&gt;</b> The directory where game files are stored. This will be passed to the mod loader. Optional arg.<br>
	 * <b>--modLoader &lt;implClass</b> The mod loader to call. This is a fully qualified class name that represents a class implementing IModLoader. Required arg.
	 */
	public void main(String[] args) {
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		
		OptionSpec<String> versionOption = parser.accepts("version", "The version that is being launched. This will be passed to the mod loader. Optional arg.").withOptionalArg().defaultsTo("unknown");
		OptionSpec<File> gameDirOption = parser.accepts("gameDir", "The directory where game files are stored. This will be passed to the mod loader. Optional arg.").withOptionalArg().ofType(File.class).defaultsTo(new File("."));
		OptionSpec<String> modLoaderOption = parser.accepts("modLoader", "The mod loader to call. This is a fully qualified class name that represents a class implementing IModLoader. Required arg.").withRequiredArg();
		OptionSpec<String> nons = parser.nonOptions();
		
		OptionSet options = parser.parse(args);
		
		String version = options.valueOf(versionOption);
		File gameDir = options.valueOf(gameDirOption);
		String modLoader = options.valueOf(modLoaderOption);
		List<String> extras = options.valuesOf(nons);
		
		launch(extras, version, gameDir, modLoader);
	}
	
	/**
	 * Launches the mod loader without any args or other info.
	 * 
	 * @param modLoader The mod loader to launch.
	 */
	public void launch(String modLoader) {
		launch(new ArrayList<>(), modLoader);
	}
	
	/**
	 * Launches the mod loader with a list of args.
	 * 
	 * @param args The args to pass.
	 * @param modLoader The mod loader to launch.
	 */
	public void launch(List<String> args, String modLoader) {
		launch(args, "unknown", new File("."), modLoader);
	}
	
	/**
	 * Launches the mod loader with a list of args, and a version.
	 * 
	 * @param args The args to pass.
	 * @param version The version that is being launched.
	 * @param modLoader The mod loader to launch.
	 */
	public void launch(List<String> args, String version, String modLoader) {
		launch(args, version, new File("."), modLoader);
	}
	
	/**
	 * Launches the mod loader with a list of args, and a game directory.
	 * 
	 * @param args The args to pass.
	 * @param gameDir The directory where game files are stored.
	 * @param modLoader The mod loader to launch.
	 */
	public void launch(List<String> args, File gameDir, String modLoader) {
		launch(args, "unknown", gameDir, modLoader);
	}
	
	/**
	 * Launches the mod loader with a list of args, a version, and a game directory.
	 * 
	 * @param args The args to pass.
	 * @param version The version that is being launched.
	 * @param gameDir The directory where game files are stored.
	 * @param modLoader The mod loader to launch.
	 */
	@SneakyThrows
	public void launch(List<String> args, String version, File gameDir, String modLoader) {
		Class<?> clazz;
		
		try {
			clazz = Class.forName("com.hallowizer.modwrapper.launcher.Launcher", true, ModWrapper.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("ModWrapper is being launched without the launcher present. Please check your classpath.");
		}
		
		Method launch = clazz.getDeclaredMethod("launch", List.class, String.class, File.class, String.class);
		
		try {
			launch.invoke(null, args, version, gameDir, modLoader);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
}
