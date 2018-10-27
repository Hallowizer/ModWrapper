package com.hallowizer.modwrapper.api;

import java.security.ProtectionDomain;

/**
 * A transformer to modify class bytecode.
 * 
 * @author Hallowizer
 *
 */
public interface IClassTransformer {
	/**
	 * Transforms the bytecode.
	 * 
	 * @param name The untransformed name. This is where the bytes came from.
	 * @param transformedName The transformed name. This is what the class will be named in {@link ClassLoader#defineClass(String,byte[],String,String,ProtectionDomain) ClassLoader.defineClass}
	 * @param classData The class bytes.
	 * @return The transformed class bytes.
	 */
	public abstract byte[] transform(String name, String transformedName, byte[] classData);
}
