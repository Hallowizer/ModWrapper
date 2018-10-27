package com.hallowizer.modwrapper.launcher.parentloaded;

public interface IManagerClassLoader {
	public abstract boolean isIncluded(String name);
	public abstract Class<?> loadInner(String name) throws ClassNotFoundException;
}
