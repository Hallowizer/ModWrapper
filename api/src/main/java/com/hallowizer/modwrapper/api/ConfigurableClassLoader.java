package com.hallowizer.modwrapper.api;

public abstract class ConfigurableClassLoader extends ClassLoader {
	public abstract void addTransformerExclusion(String prefix);
	public abstract void addTransformerInclusion(String prefix);
	
	public abstract void registerTransformer(IClassTransformer transformer);
}
