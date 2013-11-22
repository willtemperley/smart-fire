package it.jrc.estation;

import it.jrc.estation.domain.ActiveFire;

import org.hibernate.Session;
import org.wcs.smart.hibernate.SmartHibernateManager;

public class FireHibernateManager extends SmartHibernateManager {
	
	/**
	 * 
	 * 
	 * 
	 */
	public static boolean saveActiveFire(ActiveFire activeFire, Session session) {
		session.beginTransaction();
		try {
			session.saveOrUpdate(activeFire);
			session.getTransaction().commit();
			return true;
		} catch (Exception ex) {
			session.getTransaction().rollback();
			ex.printStackTrace();
//			Activator.di.displayLog("Error in saving active fire", ex); //$NON-NLS-1$
			return false;
		}
	}
	

}
