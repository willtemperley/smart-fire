package it.jrc.smart.fire.model;

import java.util.Set;

public interface ICategory {
	
	public Set<String> getRequiredAttributes() ;

	public String getName();

	public Set<String> getSources();

}
