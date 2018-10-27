package com.hallowizer.modwrapper.test;

import java.io.File;
import java.util.List;

import com.hallowizer.modwrapper.api.ConfigurableClassLoader;
import com.hallowizer.modwrapper.api.IModLoader;
import com.hallowizer.modwrapper.launcher.LaunchLog;

public final class TestModLoader implements IModLoader {
	public TestModLoader() {
		LaunchLog.debug("TestModLoader's classloader: " + getClass().getClassLoader().getClass().getName());
	}
	
	@Override
	public void injectData(List<String> args, String version, File gameDir) {
		// NOOP
	}
	
	@Override
	public void configureClassLoader(ConfigurableClassLoader classLoader) {
		classLoader.addTransformerInclusion("com.hallowizer.modwrapper.test.inner.");
		classLoader.registerTransformer(new TestTransformer());
	}
	
	@Override
	public String getMainClass() {
		return "com.hallowizer.modwrapper.test.inner.TransformedTestLaunched";
	}
}
