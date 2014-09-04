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

import it.jrc.smart.fire.FireDAO;
import it.jrc.smart.fire.SmartAccess;
import it.jrc.smart.fire.model.ActiveFire;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.geotools.data.FeatureReader;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.hibernate.Session;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.wcs.smart.ca.Area;
import org.wcs.smart.ca.Area.AreaType;
import org.wcs.smart.hibernate.SmartHibernateManager;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * Feature reader for intelligence points.
 * @author Emily
 *
 */
public class FireFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature> {

	private SimpleFeatureType featureType;
	private int listIndex = 0;
	private GeometryFactory gf = new GeometryFactory();
	private List<ActiveFire> fires;
	
	public FireFeatureReader(SimpleFeatureType type) {
		this.featureType = type;

		Session session = SmartHibernateManager.openSession();
		Area a = SmartAccess.getArea(session, AreaType.BA);

		Envelope env = a.getGeometry().getEnvelopeInternal();
		
		System.out.println("CREATING FIREFEATUREREADER");

		fires = FireDAO.getRecentFires(env, new Date());
		
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

	private SimpleFeature createFeature(ActiveFire activeFire) {
		// TODO Auto-generated method stub
		String fid = "" + activeFire.hashCode();
		Point pnt = gf.createPoint(new Coordinate(activeFire.getX(), activeFire.getY()));
		return SimpleFeatureBuilder.build(featureType, new Object[]{fid,pnt},fid);
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
