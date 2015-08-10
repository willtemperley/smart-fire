package it.jrc.smart.fire.model;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;

public class BurnedArea {

	public static class BurnedAreaModel implements ICategory {

		@Override
		public Set<String> getRequiredAttributes() {
			HashSet<String> hashSet = new HashSet<String>();
			return hashSet;
		}

		@Override
		public String getName() {
			return "burnedarea";
		}

		@Override
		public Set<String> getSources() {
			HashSet<String> hashSet = new HashSet<String>();
			hashSet.add("MODIS-BA");
			return hashSet;
		}

	}

	public static ICategory model = new BurnedAreaModel();

	private Double x;

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	private Double y;

	public double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	private Date stamp;

	@Column
	public Date getStamp() {
		return stamp;
	}

	public void setStamp(Date stamp) {
		this.stamp = stamp;
	}

}
