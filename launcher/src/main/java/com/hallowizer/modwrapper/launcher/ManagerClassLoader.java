package com.hallowizer.modwrapper.launcher;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;
import com.hallowizer.modwrapper.api.ConfigurableClassLoader;
import com.hallowizer.modwrapper.api.IClassTransformer;
import com.hallowizer.modwrapper.launcher.parentloaded.IManagerClassLoader;
import com.hallowizer.modwrapper.launcher.parentloaded.NonTransformingClassLoader;

public final class ManagerClassLoader extends ConfigurableClassLoader implements IManagerClassLoader {
	private final NonTransformingClassLoader outerLoader;
	private final TransformingClassLoader innerLoader;
	
	private final ExclusionSet transformerExclusions = new ExclusionSet();
	private final ExclusionSet transformerInclusions = new ExclusionSet();
	
	private final List<URL> sources = new ArrayList<>();
	
	public ManagerClassLoader(NonTransformingClassLoader classLoader) {
		this.outerLoader = classLoader;
		this.innerLoader = new TransformingClassLoader(this, outerLoader);
		
		sources.addAll(Arrays.asList(outerLoader.getSources()));
		outerLoader.setManagerLoader(this);
		
		addTransformerExclusion("org.objectweb.asm.");
		addTransformerExclusion("com.hallowizer.modwrapper.");
	}
	
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		return innerLoader.loadClass(name);
	}
	
	@Override
	public void addTransformerExclusion(String prefix) {
		Preconditions.checkState(!transformerInclusions.containsExclusion(prefix), "Attempting to add a transformer exclusion that was previously included!");
		
		transformerExclusions.addExclusion(prefix);
		transformerInclusions.addInclusion(prefix);
	}
	
	@Override
	public void addTransformerInclusion(String prefix) {
		Preconditions.checkState(!transformerExclusions.containsExclusion(prefix), "Attempting to add a transformer inclusion that was previously excluded!");
		
		transformerExclusions.addInclusion(prefix);
		transformerInclusions.addExclusion(prefix);
	}
	
	@Override
	public boolean isIncluded(String name) {
		return transformerInclusions.isExcluded(name);
	}
	
	public boolean isExcluded(String name) {
		return transformerExclusions.isExcluded(name);
	}
	
	@Override
	public Class<?> loadInner(String name) throws ClassNotFoundException {
		return innerLoader.loadDirect(name);
	}
	
	public Class<?> loadOuter(String name) throws ClassNotFoundException {
		return outerLoader.loadDirect(name);
	}
	
	@Override
	public void registerTransformer(IClassTransformer transformer) {
		innerLoader.registerTransformer(transformer);
	}
}
