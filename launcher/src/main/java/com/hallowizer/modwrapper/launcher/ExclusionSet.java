package com.hallowizer.modwrapper.launcher;

import java.util.HashSet;
import java.util.Set;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ExclusionSet {
	private final String label;
	private final Set<String> exclusions = new HashSet<>();
	private final Set<String> inclusions = new HashSet<>();
	
	public void addExclusion(String prefix) {
		exclusions.add(prefix);
	}
	
	public void addInclusion(String prefix) {
		inclusions.add(prefix);
	}
	
	public boolean isExcluded(String name) {
		LaunchLog.debug("Testing " + name + " for " + label);
		
		boolean exclude = isExcluded(name, exclusions, inclusions, "", "\t");
		LaunchLog.debug("Concluding that " + name + " is " + (exclude ? "" : "not ") + "present in the " + label);
		return exclude;
	}
	
	private boolean isExcluded(String name, Set<String> exclusions, Set<String> inclusions, String searchPrefix, String debugPrefix) {
		LaunchLog.debug(debugPrefix + "Testing " + name + " with search prefix " + searchPrefix);
		
		for (String exclusion : exclusions)
			if (exclusion.startsWith(searchPrefix) && name.startsWith(exclusion) && !isExcluded(name, inclusions, exclusions, exclusion, debugPrefix + "\t")) {
				LaunchLog.debug(debugPrefix + "Returning true");
				return true;
			} else
				LaunchLog.debug(debugPrefix + "Prefix " + exclusion + " failed.");
		
		LaunchLog.debug(debugPrefix + "All has failed, returning false");
		return false;
	}
	
	public boolean containsExclusion(String prefix) {
		return exclusions.contains(prefix);
	}
}
