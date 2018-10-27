package com.hallowizer.modwrapper.test.inner;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestTarget {
	public void main(String[] args) {
		System.out.println("UntransformedText");
		
		try {
			sneakyCatch(ClassNotFoundException.class);
			TestTarget.class.getName();
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			System.out.println("Mapped name, success!");
			e.printStackTrace();
		}
	}
	
	private <T extends Throwable> void sneakyCatch(Class<T> clazz) throws T {
		// NOOP
	}
}
