package com.hallowizer.modwrapper.launcher.parentloaded;

import java.net.URL;
import java.net.URLClassLoader;

import lombok.Setter;

public final class NonTransformingClassLoader extends URLClassLoader {
	private static final String EXCLUDE_PREFIX = "com.hallowizer.modwrapper.launcher.parentloaded.";
	
	@Setter
	private IManagerClassLoader managerLoader;
	private final ClassLoader parentLoader = getClass().getClassLoader();
	private final URL[] sources;
	
	public NonTransformingClassLoader(URL[] urls) {
		super(urls, null); // Set the parent to null. The loadClass implementation checks the parent class loader before checking us, so we need to give it nothing to check.
		this.sources = urls;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (name.startsWith(EXCLUDE_PREFIX))
			return parentLoader.loadClass(name);
		
		if (managerLoader != null && managerLoader.isIncluded(name))
			return managerLoader.loadInner(name);
		
		return loadDirect(name);
	}
	
	public Class<?> loadDirect(String name) throws ClassNotFoundException {
		return super.findClass(name);
	}
	
	public URL[] getSources() {
		return sources.clone();
	}
}
