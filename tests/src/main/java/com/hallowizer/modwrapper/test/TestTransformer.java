package com.hallowizer.modwrapper.test;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.hallowizer.modwrapper.api.IRenamingClassTransformer;
import com.hallowizer.modwrapper.launcher.LaunchLog;

public final class TestTransformer implements IRenamingClassTransformer {
	@Override
	public byte[] transform(String name, String transformedName, byte[] classData) {
		if (!name.equals("com.hallowizer.modwrapper.test.inner.TestTarget") && !name.equals("com.hallowizer.modwrapper.test.inner.TransformedTestTarget"))
			return classData;
		
		LaunchLog.debug("Transforming TestTarget");
		
		ClassNode clazz = new ClassNode();
		new ClassReader(classData).accept(clazz, 0);
		
		clazz.name = transformedName.replace('.', '/');
		
		for (MethodNode method : clazz.methods)
			for (AbstractInsnNode insn : (Iterable<AbstractInsnNode>) () -> method.instructions.iterator())
				if (insn instanceof LdcInsnNode) {
					LdcInsnNode ldc = (LdcInsnNode) insn;
					if (ldc.cst.equals("UntransformedText"))
						ldc.cst = "TransformedText";
				}
		
		ClassWriter cw = new ClassWriter(0);
		clazz.accept(cw);
		return cw.toByteArray();
	}
	
	@Override
	public String transformName(String name) {
		if (name.equals("com.hallowizer.modwrapper.test.inner.TestTarget")) {
			LaunchLog.debug("Transforming name PartTransformedTestTarget -> TransformedTestTarget");
			return "com.hallowizer.modwrapper.test.inner.TransformedTestTarget";
		} else if (name.equals("com.hallowizer.modwrapper.test.inner.TransformedTestTarget"))
			return "com.hallowizer.modwrapper.test.inner.DummyTestTarget";
		
		return name;
	}
	
	@Override
	public String untransformName(String name) {
		if (name.equals("com.hallowizer.modwrapper.test.inner.PartTransformedTestTarget")) {
			LaunchLog.debug("Untransforming name PartTransformedTestTarget -> TestTarget");
			return "com.hallowizer.modwrapper.test.inner.TestTarget";
		}
		
		return name;
	}
}
