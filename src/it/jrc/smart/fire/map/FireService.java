package it.jrc.smart.fire.map;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataStore;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;

public class FireService extends IService {

	public static final String SERVICE_ID = "http://smart.fire.jrc.it"; //$NON-NLS-1$

	private ArrayList<FireGeoResource> members;

	private FireDataSource fireDataSource;

	public FireService(FireDataSource fireDataSource) {
		this.fireDataSource = fireDataSource;
	}

	@Override
	public Throwable getMessage() {
		return new Throwable("Fire service message.");
	}

	@Override
	public URL getIdentifier() {
		try {
			return new URL(SERVICE_ID);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<? extends IGeoResource> resources(IProgressMonitor monitor)
			throws IOException {
		if (members == null){
			synchronized (this) {
				if (members == null){
					members = new ArrayList<FireGeoResource>();
					members.add(new FireGeoResource(this));
				}
			}
		}
		return members;
	}

	@Override
	protected IServiceInfo createInfo(IProgressMonitor monitor)
			throws IOException {
		return new FireServiceInfo(this);
	}

	@Override
	public Map<String, Serializable> getConnectionParams() {
		// TODO Auto-generated method stub
		return null;
	}

	public DataStore getDataStore(IProgressMonitor monitor) {
		System.out.println("getting DS: " + fireDataSource);
		return fireDataSource;
	}

	public void setDataSource(FireDataSource fireDataSource) {
		this.fireDataSource = fireDataSource;
	}

}
