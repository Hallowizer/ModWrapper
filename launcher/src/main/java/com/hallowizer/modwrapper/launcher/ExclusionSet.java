package com.hallowizer.modwrapper.launcher;

import java.util.HashSet;
import java.util.Set;

public final class ExclusionSet {
	private final Set<String> exclusions = new HashSet<>();
	private final Set<String> inclusions = new HashSet<>();
	
	public void addExclusion(String prefix) {
		exclusions.add(prefix);
	}
	
	public void addInclusion(String prefix) {
		inclusions.add(prefix);
	}
	
	public boolean isExcluded(String name) {
		return isExcluded(name, exclusions, inclusions, "");
	}
	
	private boolean isExcluded(String name, Set<String> exclusions, Set<String> inclusions, String searchPrefix) {
		for (String exclusion : exclusions)
			if (exclusion.startsWith(searchPrefix) && name.startsWith(exclusion) && !isExcluded(name, inclusions, exclusions, exclusion))
				return true;
		
		return false;
	}
	
	public boolean containsExclusion(String prefix) {
		return exclusions.contains(prefix);
	}
}
