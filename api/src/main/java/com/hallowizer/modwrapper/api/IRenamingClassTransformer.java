package com.hallowizer.modwrapper.api;

public interface IRenamingClassTransformer extends IClassTransformer {
	public abstract String transformName(String name);
	public abstract String untransformName(String name);
}
