/*
 * Copyright (C) 2012 Wildlife Conservation Society
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package it.jrc.smart.fire.map;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.geotools.data.FeatureReader;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.hibernate.Query;
import org.hibernate.Session;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.wcs.smart.hibernate.SmartHibernateManager;
import org.wcs.smart.observation.model.Waypoint;
import org.wcs.smart.observation.model.WaypointObservation;
import org.wcs.smart.observation.model.WaypointObservationAttribute;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 *
 */
public class FireFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature> {

	private SimpleFeatureType featureType;
	private int listIndex = 0;
	private GeometryFactory gf = new GeometryFactory();
	private List<WaypointObservation> fires;
	
	public FireFeatureReader(SimpleFeatureType type, Session session, Date fromDate, Date toDate) {
		this.featureType = type;

//		Session session = SmartHibernateManager.openSession();
		System.out.println(fromDate);
		System.out.println(toDate);

		Query q = session.createQuery("from WaypointObservation where (waypoint.sourceId = 'MODIS-5.0' or waypoint.sourceId = 'MODIS-5.1') and waypoint.dateTime >= :t1 and waypoint.dateTime <= :t2 order by dateTime desc");
		
		q.setParameter("t1", fromDate);
		q.setParameter("t2", toDate);

//		.setMaxResults(100);
		
		fires = q.list();
		
		for (WaypointObservation obj : fires) {
			System.out.print("Fire at date: ");
			System.out.println(obj.getWaypoint().getDateTime());
		}
		
	}
	
	@Override
	public SimpleFeatureType getFeatureType() {
		return this.featureType;
	}

	@Override
	public SimpleFeature next() throws IOException, IllegalArgumentException,
			NoSuchElementException {

		SimpleFeature feature = createFeature(fires.get(listIndex));
		listIndex++;
		return feature;
	}

	private SimpleFeature createFeature(WaypointObservation obs) {
		String fid = "" + obs.hashCode();

//		WaypointObservation obs = waypoint.getObservations().get(0);
		List<WaypointObservationAttribute> attrs = obs.getAttributes();

		Double frp = null;
		Double confidence = null;
		for (WaypointObservationAttribute waypointObservationAttribute : attrs) {
			String defaultName = waypointObservationAttribute.getAttribute().getDefaultName();
			if (defaultName.equals("frp")) {
				frp = waypointObservationAttribute.getNumberValue();
			} else if (defaultName.equals("confidence")) {
				confidence = waypointObservationAttribute.getNumberValue();
			}
		}

		Point pnt = gf.createPoint(new Coordinate(obs.getWaypoint().getX(), obs.getWaypoint().getY()));
		return SimpleFeatureBuilder.build(featureType, new Object[]{fid, frp, confidence, pnt},fid);
	}

	@Override
	public boolean hasNext() throws IOException {
		return listIndex < fires.size();
	}

	@Override
	public void close() throws IOException {
	}
	
//	private SimpleFeature createFeature(){
//		String fid = "asdf";
//		Point pnt = gf.createPoint(new Coordinate(2.45, 12.30));
//		return SimpleFeatureBuilder.build(featureType, new Object[]{fid,pnt},fid);
//	}
}
