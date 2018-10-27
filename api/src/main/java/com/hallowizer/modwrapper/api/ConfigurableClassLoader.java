package com.hallowizer.modwrapper.api;

public abstract class ConfigurableClassLoader extends ClassLoader {
	public ConfigurableClassLoader() {
		super(null); // The one thing I forget to do and it ruins the tests.
	}
	
	public abstract void addTransformerExclusion(String prefix);
	public abstract void addTransformerInclusion(String prefix);
	
	public abstract void registerTransformer(IClassTransformer transformer);
}
