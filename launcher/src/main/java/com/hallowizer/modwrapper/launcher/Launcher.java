package com.hallowizer.modwrapper.launcher;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import com.hallowizer.modwrapper.api.IModLoader;
import com.hallowizer.modwrapper.launcher.parentloaded.NonTransformingClassLoader;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Launcher {
	@SneakyThrows
	public void launch(List<String> args, String version, File gameDir, String modLoader) {
		NonTransformingClassLoader classLoader = new NonTransformingClassLoader(findClasspathUrls());
		
		Class<?> clazz = Class.forName("com.hallowizer.modwrapper.launcher.Launcher", true, classLoader);
		Method continueInsideClassLoader = clazz.getDeclaredMethod("continueInsideClassLoader", List.class, String.class, File.class, String.class);
		continueInsideClassLoader.setAccessible(true);
		
		try {
			continueInsideClassLoader.invoke(null, args, version, gameDir, modLoader);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
	
	private URL[] findClasspathUrls() {
		ClassLoader classLoader = Launcher.class.getClassLoader();
		
		if (classLoader instanceof URLClassLoader)
			return ((URLClassLoader) classLoader).getURLs();
		else if (classLoader.getClass().getName().equals("jdk.internal.loader.ClassLoaders$AppClassLoader"))
			return findJava9Classpath(classLoader);
		else
			throw new UnsupportedOperationException("Unsupported class loader " + classLoader);
	}
	
	@SuppressWarnings("unchecked")
	@SneakyThrows
	private URL[] findJava9Classpath(ClassLoader classLoader) {
		Class<?> appClassLoader = classLoader.getClass();
		Class<?> builtinClassLoader = appClassLoader.getSuperclass();
		
		Field ucpField = builtinClassLoader.getDeclaredField("ucp");
		ucpField.setAccessible(true);
		Object ucp = ucpField.get(classLoader);
		
		Class<?> urlClassPath = ucp.getClass();
		
		Field pathField = urlClassPath.getDeclaredField("path");
		pathField.setAccessible(true);
		List<URL> urls = (List<URL>) pathField.get(ucp);
		
		return urls.toArray(new URL[urls.size()]);
	}
	
	@SuppressWarnings("unchecked")
	@SneakyThrows
	private void continueInsideClassLoader(List<String> args, String version, File gameDir, String modLoader) {
		LaunchLog.debug("Inner launcher classloader: " + Launcher.class.getClassLoader().getClass().getName());
		
		NonTransformingClassLoader oldLoader = (NonTransformingClassLoader) Launcher.class.getClassLoader();
		ManagerClassLoader classLoader = new ManagerClassLoader(oldLoader);
		
		classLoader.addTransformerExclusion(modLoader);
		
		Class<?> clazz;
		try {
			clazz = Class.forName(modLoader, true, classLoader);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Attempting to use mod loader " + modLoader + ", which does not exist");
		}
		
		Class<? extends IModLoader> loaderClass = (Class<? extends IModLoader>) clazz; // Seriously, clazz.asSubinterface doesn't exist.
		Constructor<? extends IModLoader> constructor;
		
		try {
			constructor = loaderClass.getDeclaredConstructor();
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Mod loader does not have a constructor with no parameters!");
		}
		
		IModLoader loader;
		try {
			loader = constructor.newInstance();
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
		
		loader.injectData(args, version, gameDir);
		loader.configureClassLoader(classLoader);
		
		String mainName = loader.getMainClass();
		Class<?> mainClass = Class.forName(mainName, true, classLoader);
		Method main = mainClass.getDeclaredMethod("main", String[].class);
		String[] launchArgs = args.toArray(new String[args.size()]);
		
		try {
			main.invoke(null, (Object) launchArgs);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
}
