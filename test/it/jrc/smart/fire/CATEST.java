package it.jrc.smart.fire;

import javax.persistence.Temporal;

import org.hibernate.Session;
import org.junit.Test;
import org.wcs.smart.ca.Area;
import org.wcs.smart.ca.Area.AreaType;

public class CATEST {

	@Test
	public void x() {
		
		Session session = Hibernate.openSession();
		Area a = SmartAccess.getArea(session, AreaType.BA);
		
		
		System.out.println(a.getName());
	}
}
