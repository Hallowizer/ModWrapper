package com.hallowizer.modwrapper.api;

/**
 * Class loader that calls transformers and name transformers.
 * 
 * @author Hallowizer
 *
 */
public abstract class ConfigurableClassLoader extends ClassLoader {
	public ConfigurableClassLoader() {
		super(null); // The one thing I forget to do and it ruins the tests.
	}
	
	/**
	 * Marks a package/class as transformer excluded. This means no transformers will run on this package/class. Subpackages can be included in transformation using {@link addTransformerInclusion}
	 * 
	 * @param name The thing to exclude. End with a dot for a package, and end with a normal character for a class name.
	 */
	public abstract void addTransformerExclusion(String name);
	
	/**
	 * Marks a package/class as transformer included. This means transformers will run normally on this package/class, even if a parent package was transformer excluded. Subpackages can be excluded from transformation using {@link addTransformerExclusion}
	 * 
	 * @param name The thing to include. End with a dot for a package, and end with a normal character for a class name.
	 */
	public abstract void addTransformerInclusion(String name);
	
	/**
	 * Injects a transformer into this class loader. This transformer will be called on all classes that are not transformer excluded. Additionally, this transformer can transform names if it implements {@link IClassNameTransformer}
	 * 
	 * @param transformer The transformer to inject.
	 */
	public abstract void registerTransformer(IClassTransformer transformer);
}
