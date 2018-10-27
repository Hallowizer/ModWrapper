package com.hallowizer.modwrapper.api;

/**
 * An {@link IClassTransformer} that renames classes. If no name mapping is found, the old name should be returned.
 * 
 * @author Hallowizer
 *
 */
public interface IRenamingClassTransformer extends IClassTransformer {
	/**
	 * Transforms the supplied name. Returning null will use the original name.
	 * 
	 * @param name The name to transform.
	 * @return The transformed name. This will be the name used when defining the class.
	 */
	public abstract String transformName(String name);
	
	/**
	 * Untransforms the supplied name. Returning null will cause a {@link ClassNotFoundException}.
	 * 
	 * @param name The name to untransform.
	 * @return The untransformed name. This is the path where the class bytes will be located.
	 */
	public abstract String untransformName(String name);
}
