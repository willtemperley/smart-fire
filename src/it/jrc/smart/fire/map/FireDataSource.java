package it.jrc.smart.fire.map;

import it.jrc.smart.fire.FireDAO;
import it.jrc.smart.fire.SmartAccess;
import it.jrc.smart.fire.internal.messages.Messages;

import java.io.IOException;
import java.util.HashMap;

import org.geotools.data.AbstractDataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureReader;
import org.geotools.feature.SchemaException;
import org.hibernate.Session;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.wcs.smart.ca.Area.AreaType;
import org.wcs.smart.hibernate.SmartHibernateManager;

/**
 *
 */
public class FireDataSource extends AbstractDataStore{

	public static final String FEATURE_TYPE = "FirePoint"; //$NON-NLS-1$
	
	private HashMap<String, SimpleFeatureType> schemas = new HashMap<String, SimpleFeatureType>();
	
	public FireDataSource(){
		

	}

	@Override
	public String[] getTypeNames()  {
		return new String[]{FEATURE_TYPE};
	}

	@Override
	protected FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(String typeName) throws IOException {
		return new FireFeatureReader(getSchema(typeName));
	}

	@Override
	public SimpleFeatureType getSchema(String typeName) throws IOException {
		SimpleFeatureType type = schemas.get(typeName);
		if (type == null){
			try {
				if (typeName.equals(FEATURE_TYPE)) {
					type = createPointSchema();
				}
			}catch(SchemaException ex){
				throw new IOException(Messages.FireDataSource_SchemaNotSupported + ex.getLocalizedMessage(), ex);
			}
			schemas.put(typeName, type);
		}
		return type;
	}

	private SimpleFeatureType createPointSchema() throws SchemaException{
		String spec = "fid:String,geom:Point:srid=4326"; //$NON-NLS-1$
		SimpleFeatureType type =  DataUtilities.createType("smart." + FEATURE_TYPE, spec); //$NON-NLS-1$
		return type;
	}
}
