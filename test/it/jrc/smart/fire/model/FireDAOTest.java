package it.jrc.smart.fire.model;

import it.jrc.smart.fire.FireDAO;

import java.util.Date;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;

public class FireDAOTest {

	public static void main(String[] args) {

		double minx = 10.9615703155282533;
		double miny = 11.802171870755888;
		double maxx = 11.8940815542246203;
		double maxy = 12.672085534005626;

		Envelope env = new Envelope(minx, miny, maxx, maxy);
		
		List<ActiveFire> af = FireDAO.getFires(env, new Date(0));
		for (ActiveFire activeFire : af) {
			System.out.println(activeFire.getStamp());
			System.out.println(activeFire.getX());
			System.out.println(activeFire.getY());
		}

		List<BurnedArea> ba = FireDAO.getBurnedAreas(env, new Date(0));

		for (BurnedArea activeFire : ba) {
			System.out.println(activeFire.getStamp());
		}

	}
}
