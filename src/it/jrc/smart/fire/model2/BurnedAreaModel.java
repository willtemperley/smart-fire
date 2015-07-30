package it.jrc.smart.fire.model2;

import java.util.HashSet;
import java.util.Set;

public class BurnedAreaModel implements ICategory {
	

	@Override
	public Set<String> getRequiredAttributes() {
		
		HashSet<String> hashSet = new HashSet<String>();
		return hashSet;
		
	}

	@Override
	public String getName() {
		return "burnedarea";
	}

	@Override
	public Set<String> getSources() {
		HashSet<String> hashSet = new HashSet<String>();
		hashSet.add("MODIS-BA");
		return hashSet;
	}
	

}
