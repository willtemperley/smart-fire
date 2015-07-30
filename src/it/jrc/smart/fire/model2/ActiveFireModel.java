package it.jrc.smart.fire.model2;

import java.util.HashSet;
import java.util.Set;

public class ActiveFireModel implements ICategory {

	@Override
	public Set<String> getRequiredAttributes() {
		HashSet<String> hashSet = new HashSet<String>();
		hashSet.add("frp");
		hashSet.add("confidence");
		return hashSet;
	}

	@Override
	public String getName() {
		return "activefire";
	}

	@Override
	public Set<String> getSources() {
		HashSet<String> sources = new HashSet<String>();
		sources.add("MODIS-5.1");
		sources.add("MODIS-5.0");
		return sources;
	}
	
}
