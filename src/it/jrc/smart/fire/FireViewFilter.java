package it.jrc.smart.fire;

import org.hibernate.Query;
import org.hibernate.Session;
import org.wcs.smart.hibernate.SmartDB;

/**
 * Filter for the intelligence view.  Filters
 * have a received date filter, from/to dates filter and name filter.
 * 
 * @author elitvin
 * @since 1.0.0
 */
public class FireViewFilter {	
	
	
	public FireViewFilter() {
	}
	
	public Query buildQuery(Session s) { 
		
		Query query = s.createQuery("");
		query.setParameter("ca", SmartDB.getCurrentConservationArea()); //$NON-NLS-1$


		return query;
	}	


}
