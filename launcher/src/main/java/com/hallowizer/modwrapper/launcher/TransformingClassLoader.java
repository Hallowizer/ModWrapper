package com.hallowizer.modwrapper.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.hallowizer.modwrapper.api.IClassTransformer;
import com.hallowizer.modwrapper.api.IRenamingClassTransformer;
import com.hallowizer.modwrapper.launcher.parentloaded.NonTransformingClassLoader;

public final class TransformingClassLoader extends URLClassLoader {
	private final ManagerClassLoader managerLoader;
	
	private final List<IClassTransformer> transformers = new ArrayList<>();
	private final List<IRenamingClassTransformer> nameTransformers = new ArrayList<>();
	private final List<IRenamingClassTransformer> reverseNameTransformers = new ArrayList<>();
	
	private final Map<String,Class<?>> classCache = new HashMap<>();
	private final Map<Package,URL> sealedPackageMap = new HashMap<>();
	
	public TransformingClassLoader(ManagerClassLoader managerLoader, NonTransformingClassLoader outerLoader) {
		super(outerLoader.getSources(), null);
		this.managerLoader = managerLoader;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (managerLoader.isExcluded(name))
			return managerLoader.loadOuter(name);
		
		return loadDirect(name);
	}
	
	public Class<?> loadDirect(String name) throws ClassNotFoundException {
		if (classCache.containsKey(name))
			return classCache.get(name);
		
		String transformedName = transformName(name);
		String untransformedName = untransformName(name);
		
		if (transformedName == null)
			transformedName = name;
		
		if (untransformedName == null)
			throw new ClassNotFoundException(name);
		
		if (classCache.containsKey(transformedName))
			return classCache.get(transformedName);
		
		int lastDot = untransformedName.lastIndexOf('.');
		String packageName = lastDot == -1 ? "" : untransformedName.substring(0, lastDot);
		String resource = untransformedName.replace('.', '/') + ".class";
		
		try {
			URLConnection connection;
			try {
				connection = findResource(resource).openConnection();
				connection.getClass(); // Null test
			} catch (NullPointerException e) {
				LaunchLog.debug("Got NPE when getting resource of class " + untransformedName + " (" + transformedName + ")");
				LaunchLog.debug(e);
				throw new ClassNotFoundException(name);
			}
			
			CodeSigner[] signers = null;
			
			if (connection instanceof JarURLConnection) {
				JarURLConnection cast = (JarURLConnection) connection;
				JarFile jar = cast.getJarFile();
				
				if (jar != null && jar.getManifest() != null) {
					Manifest manifest = jar.getManifest();
					JarEntry entry = jar.getJarEntry(resource);
					
					Package pkg = getPackage(packageName);
					signers = entry.getCodeSigners();
					
					if (pkg == null) {
						pkg = definePackage(packageName, manifest, cast.getJarFileURL());
						
						if (pkg.isSealed())
							sealedPackageMap.put(pkg, cast.getJarFileURL());
					} else if (pkg.isSealed() && !pkg.isSealed(cast.getJarFileURL()))
						throw new SecurityException("Seal violation: Class " + untransformedName + "(" + transformedName + ") is in sealed package " + packageName);
					else if (isSealed(packageName, manifest) && !sealedPackageMap.get(pkg).equals(connection.getURL()))
						throw new SecurityException("Seal violation: Duplicate sealed packages, from class " + untransformedName + "(" + transformedName + ") in package " + packageName);
						
				}
			} else {
				Package pkg = getPackage(packageName);
				if (pkg == null) {
					pkg = definePackage(packageName, null, null, null, null, null, null, null);
					sealedPackageMap.put(pkg, new URL(connection.getURL(), new File(connection.getURL().getPath()).getParentFile().getName()));
				} else if (pkg.isSealed() && !sealedPackageMap.get(pkg).equals(new URL(connection.getURL(), new File(connection.getURL().getPath()).getParentFile().getName())))
					throw new SecurityException("Seal violation: Class " + untransformedName + "(" + transformedName + ") is in sealed package " + packageName);
			}
			
			byte[] classData = getClassData(untransformedName);
			if (classData == null)
				throw new ClassNotFoundException(name);
			
			byte[] transformedData = transformClass(untransformedName, transformedName, classData);
			CodeSource source = new CodeSource(connection.getURL(), signers);
			
			LaunchLog.debug("Defining transformed class " + transformedName);
			Class<?> clazz = defineClass(transformedName, transformedData, 0, transformedData.length, source);
			classCache.put(transformedName, clazz);
			return clazz;
		} catch (SecurityException | ClassNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new ClassNotFoundException(name, e);
		}
	}
	
	private String transformName(String name) {
		for (IRenamingClassTransformer transformer : nameTransformers)
			name = transformer.transformName(name);
		
		return name;
	}
	
	public String untransformName(String name) {
		for (IRenamingClassTransformer transformer : reverseNameTransformers)
			name = transformer.untransformName(name);
		
		return name;
	}
	
	private boolean isSealed(String pkg, Manifest manifest) {
		Attributes attrs = manifest.getAttributes(pkg);
		String sealed = null;
		
		if (attrs != null)
			sealed = attrs.getValue(Name.SEALED);
		
		if (sealed == null) {
			attrs = manifest.getMainAttributes();
			sealed = attrs.getValue(Name.SEALED);
		}
		
		return "true".equalsIgnoreCase(sealed);
	}
	
	public byte[] getClassData(String name) {
		String path = name.replace('.', '/') + ".class";
		
		try {
			InputStream in;
			try {
				in = findResource(path).openStream();
			} catch (NullPointerException e) {
				return null;
			}
			
			byte[] buf = new byte[in.available()];
			in.read(buf);
			return buf;
		} catch (IOException e) {
			LaunchLog.debug("Got IOE when getting class bytes of " + name);
			LaunchLog.debug(e);
			return null;
		}
	}
	
	private byte[] transformClass(String name, String transformedName, byte[] classData) {
		for (IClassTransformer transformer : transformers)
			classData = transformer.transform(name, transformedName, classData);
		
		return classData;
	}
	
	public void registerTransformer(IClassTransformer transformer) {
		transformers.add(transformer);
		
		if (transformer instanceof IRenamingClassTransformer) {
			IRenamingClassTransformer rename = (IRenamingClassTransformer) transformer;
			
			nameTransformers.add(rename);
			reverseNameTransformers.add(0, rename);
		}
	}
}
