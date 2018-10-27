package com.hallowizer.modwrapper.api;

import java.io.File;
import java.util.List;

public interface IModLoader {
	public abstract void injectData(List<String> args, String version, File gameDir);
	public abstract void configureClassLoader(ConfigurableClassLoader classLoader);
	public abstract String getMainClass();
}
