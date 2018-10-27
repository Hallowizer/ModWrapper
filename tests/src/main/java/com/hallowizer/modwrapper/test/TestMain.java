package com.hallowizer.modwrapper.test;

import java.io.File;
import java.util.Arrays;

import com.hallowizer.modwrapper.api.ModWrapper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestMain {
	public void main(String[] args) {
		System.setProperty("modwrapper.debug", "true");
		ModWrapper.launch(Arrays.asList(args), "TestVersion", new File("TestDir"), "com.hallowizer.modwrapper.test.TestModLoader");
	}
}
