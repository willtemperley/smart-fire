package it.jrc.smart.fire.model;

import it.jrc.smart.fire.ConservationAreaDAO;
import it.jrc.smart.fire.job.TestHibernateSessionManager;

import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.wcs.smart.ca.Area;
import org.wcs.smart.ca.Area.AreaType;
import org.wcs.smart.ca.ConservationArea;
import org.wcs.smart.ca.datamodel.Attribute;
import org.wcs.smart.ca.datamodel.Attribute.AttributeType;
import org.wcs.smart.ca.datamodel.Category;
import org.wcs.smart.ca.datamodel.CategoryAttribute;
import org.wcs.smart.ca.datamodel.DataModel;
import org.wcs.smart.hibernate.HibernateManager;

public class ConservationAreaDaoTest {

//	@Test
	public static Area getManagementArea(Session session) {
		
		
		Query q = session.createQuery("from ConservationArea");
		List l = q.list();
		for (Object object : l) {

			ConservationArea ca = (ConservationArea) object;
			System.out.println(ca.getName());
			if (ca.getName().equals("W National Park of Niger")) {
				
				ConservationAreaDAO dao = new ConservationAreaDAO(ca);
				Area a = dao.getArea(session, AreaType.BA);
				return a;
			}

		}
		return null;
	}
	
	public static ConservationArea getCA(Session session) {
		
		Query q = session.createQuery("from ConservationArea");
		List l = q.list();
		for (Object object : l) {

			ConservationArea ca = (ConservationArea) object;
			System.out.println(ca.getName());
			if (ca.getName().equals("W National Park of Niger")) {
				return ca;
			}

		}
		return null;
		
	}

	public static void main(String[] args) {
		
		Session session = TestHibernateSessionManager.openSession();
		ConservationArea ca = getCA(session);

		Query q = session.createQuery("from Category where conservationArea.uuid = :caUuid and keyId = :keyId");
		q.setParameter("caUuid", ca.getUuid());
		q.setParameter("keyId", "activefire2");
		Category fireCat = (Category) q.uniqueResult();
		if (fireCat != null) {
			System.out.println("have fireCat " + fireCat);
			return;
		}

		session.getTransaction().begin();

		Category category = new Category();
		category.updateName(ca.getDefaultLanguage(), "ActiveFire");
		category.setKeyId("activefire");
		category.setConservationArea(ca);
		category.setHkey("");

		session.save(category);
		
		String[] attNames = new String[]{"frp", "confidence"};
		List<CategoryAttribute> catts = new ArrayList<CategoryAttribute>();
		for (int i = 0; i < attNames.length; i++) {
			
			Attribute att = new Attribute();
			att.setType(AttributeType.NUMERIC);
			att.updateName(ca.getDefaultLanguage(), attNames[i]);
			att.setKeyId("activefire." + attNames[i]);
			att.setConservationArea(ca);
			session.save(att);

			CategoryAttribute catt = new CategoryAttribute(category, att);
			catts.add(catt);
			session.save(catt);

		}

		category.setAttributes(catts);
		
		session.getTransaction().commit();
		
		print(ca, session);

	}
	
	public static void print(ConservationArea ca, Session session) {
		
		Category activeFireCat = null;

		DataModel dm = HibernateManager.loadDataModel(ca, session);
		List<Category> cats = dm.getCategories();
		for (Category category : cats) {
			if (category.getDefaultName().equals("ActiveFire")) {
				activeFireCat = category;
			}

			List<CategoryAttribute> atts = category.getAttributes();
			System.out.println(category.getDefaultName());
			for (CategoryAttribute categoryAttribute : atts) {
				System.out.print("--->");
				Attribute attribute = categoryAttribute.getAttribute();
				System.out.println(attribute.getDefaultName());
			}
		}
	}
}