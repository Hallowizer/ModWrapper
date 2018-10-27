package com.hallowizer.modwrapper.test.inner;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestLaunched {
	public void main(String[] args) {
		try {
			TestTarget.class.getName();
		} catch (NoClassDefFoundError e) {
			System.out.println("Success!");
			e.printStackTrace();
		}
		
		TransformedTestTarget.main(args);
	}
}
