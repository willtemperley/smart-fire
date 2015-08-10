package it.jrc.smart.fire.map;

import java.util.List;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.hibernate.Query;
import org.hibernate.Session;
import org.wcs.smart.ca.Area;
import org.wcs.smart.ca.Area.AreaType;
import org.wcs.smart.ca.ConservationArea;
import org.wcs.smart.hibernate.SmartDB;
import org.wcs.smart.ui.map.MapView;

public class SmartAccess {


	/**
	 * Get the map viewer
	 * 
	 * Eclipse3 specific.
	 * 
	 * @return
	 */
	public static MapView getMapView() {
		IViewPart x = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MapView.ID);
		MapView mapView = (MapView) x;
		return mapView;
	}
	
	
	public static Area getArea(Session session, AreaType areaType) {
		ConservationArea ca = SmartDB.getCurrentConservationArea();

		Query q = session.createQuery("from Area where type = :areaType and conservationArea.uuid = :caUuid");
		q.setParameter("areaType", areaType);
		q.setParameter("caUuid", ca.getUuid());

		@SuppressWarnings("unchecked")
		List<Area> areas = q.list();
		return areas.get(0);
	}
	
}
