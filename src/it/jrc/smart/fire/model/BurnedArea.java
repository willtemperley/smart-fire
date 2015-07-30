package it.jrc.smart.fire.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

public class BurnedArea {

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
