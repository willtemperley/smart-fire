package it.jrc.smart.fire.map;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.jws.soap.SOAPBinding.Style;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.core.internal.CorePlugin;
import net.refractions.udig.style.sld.SLDContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.XMLMemento;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class FireGeoResource extends IGeoResource {
	
	private URL url;

	String dataType = "FirePoint";


	public FireGeoResource(FireService service) {
		this.service = service;
		try{
			this.url = new URL(service.getIdentifier(), service.getIdentifier().toExternalForm() + "#" + dataType, CorePlugin.RELAXED_HANDLER); //$NON-NLS-1$
		 } catch (MalformedURLException e) {
             throw new IllegalArgumentException("The service URL must not contain a #", e); //$NON-NLS-1$
         }
	}

	@Override
	public Status getStatus() {
		return Status.CONNECTED;
	}

	@Override
	public Throwable getMessage() {
		return new Throwable("Status ok.");
	}

	@Override
	protected IGeoResourceInfo createInfo(IProgressMonitor monitor)
			throws IOException {

		return new FireGeoResourceInfo(this, monitor);
	}

	@Override
	public URL getIdentifier() {
		return url;
	}

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {

    	if (adaptee.isAssignableFrom(IGeoResourceInfo.class)){
    		return adaptee.cast(super.getInfo(monitor));
    	}
      
        if (adaptee.isAssignableFrom(FeatureSource.class) || adaptee.isAssignableFrom(SimpleFeatureSource.class) ){
        	 DataStore ds = ((FireService)service).getDataStore(monitor);
             if (ds != null) {
                 FeatureSource<SimpleFeatureType, SimpleFeature> fs = ds.getFeatureSource("FirePoint");
                 if (fs != null)
                     return adaptee.cast(fs);
             }else{
            	 return null;
             }
        }
        if (adaptee.isAssignableFrom(FeatureStore.class) || adaptee.isAssignableFrom(SimpleFeatureStore.class)){
        	 @SuppressWarnings("unchecked")
			FeatureSource<SimpleFeatureType, SimpleFeature> fs = resolve(FeatureSource.class, monitor);
             if (fs != null && fs instanceof FeatureStore) {
                 return adaptee.cast(fs);
             }
        }
        
        if (adaptee.isAssignableFrom(Style.class)){
        	Style s = createStyle();
        	if (s != null){
        		return adaptee.cast(s);
        	}
        }
        
        return super.resolve(adaptee, monitor);
    }

	  public <T> boolean canResolve( Class<T> adaptee ) {
	        if (adaptee == null)
	            return false;

	        return adaptee.isAssignableFrom(IGeoResourceInfo.class)
	                || adaptee.isAssignableFrom(IService.class)
	                || adaptee.isAssignableFrom(FeatureSource.class)
	                || adaptee.isAssignableFrom(FeatureStore.class)
	                || adaptee.isAssignableFrom(SimpleFeatureStore.class)
	                || adaptee.isAssignableFrom(SimpleFeatureSource.class)
	                || adaptee.isAssignableFrom(Style.class)
	                || super.canResolve(adaptee);
	    }

	private Style createStyle(){
		String sld = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " //$NON-NLS-1$
				+ "<styleEntry type=\"SLDStyle\" version=\"1.0\">" //$NON-NLS-1$
				+ "&lt;sld:StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" xmlns:sld=\"http://www.opengis.net/sld\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\" version=\"1.0.0\"&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:UserLayer&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:LayerFeatureConstraints&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:FeatureTypeConstraint/&gt;" //$NON-NLS-1$
		    	+ "&lt;/sld:LayerFeatureConstraints&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:UserStyle&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:Name&gt;IntelPoint&lt;/sld:Name&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:Title/&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:FeatureTypeStyle&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:Name&gt;group 0&lt;/sld:Name&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:FeatureTypeName&gt;Feature&lt;/sld:FeatureTypeName&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:SemanticTypeIdentifier&gt;generic:geometry&lt;/sld:SemanticTypeIdentifier&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:SemanticTypeIdentifier&gt;simple&lt;/sld:SemanticTypeIdentifier&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:Rule&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:Name&gt;default rule&lt;/sld:Name&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:PointSymbolizer&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:Graphic&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:Mark&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:WellKnownName&gt;star&lt;/sld:WellKnownName&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:Fill&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:CssParameter name=\"fill\"&gt;#FF0000&lt;/sld:CssParameter&gt;" //$NON-NLS-1$
		    	+ "&lt;/sld:Fill&gt;" //$NON-NLS-1$
		    	+ "&lt;/sld:Mark&gt;" //$NON-NLS-1$
		    	+ "&lt;sld:Size&gt;12&lt;/sld:Size&gt;" //$NON-NLS-1$
		    	+ "&lt;/sld:Graphic&gt;" //$NON-NLS-1$
		    	+ "&lt;/sld:PointSymbolizer&gt;" //$NON-NLS-1$
		    	+ "&lt;/sld:Rule&gt;" //$NON-NLS-1$
		    	+ "&lt;/sld:FeatureTypeStyle&gt;" //$NON-NLS-1$
		    	+ "&lt;/sld:UserStyle&gt;" //$NON-NLS-1$
		    	+ "&lt;/sld:UserLayer&gt;" //$NON-NLS-1$
		    	+ "&lt;/sld:StyledLayerDescriptor&gt;" //$NON-NLS-1$
				+ "</styleEntry>"; //$NON-NLS-1$
	
		try {
			XMLMemento memento = XMLMemento.createReadRoot(new StringReader(sld));
			SLDContent c = new SLDContent();
			Style style = (Style) c.load(memento);
			return style;
		} catch (Exception ex) {
//			IntelligencePlugIn.log("Error generating smart style", ex); //$NON-NLS-1$
			return null;
		}
	}
}
