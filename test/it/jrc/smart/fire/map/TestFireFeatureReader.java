package it.jrc.smart.fire.map;

import it.jrc.smart.fire.job.TestHibernateSessionManager;

import java.io.IOException;
import java.util.Date;

import org.geotools.data.FeatureReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class TestFireFeatureReader {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		FireDataSource fds = new FireDataSource(TestHibernateSessionManager.openSession(), new Date(0), new Date());
		FireService s = new FireService(fds);

		FeatureReader<SimpleFeatureType, SimpleFeature> fr = fds.getFeatureReader("FirePoint");

		while (fr.hasNext()) {
			SimpleFeature obj = fr.next();
			System.out.println(obj);
		}
	}

}
