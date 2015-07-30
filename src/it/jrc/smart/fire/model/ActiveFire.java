package it.jrc.smart.fire.model;

import java.util.Date;


/**
 * @author willtemperley@gmail.com
 * 
 * 
 */
public class ActiveFire {

//	private byte[] uuid;
//
//	@Id
//	@GeneratedValue(generator="uuid")
//	@GenericGenerator(name= "uuid", strategy="uuid2")
//	public byte[] getUuid() {
//		return uuid;
//	}
//	public void setUuid(byte[] uuid) {
//		this.uuid = uuid;
//	}
    
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

    private Double track;

    public Double getTrack() {
        return track;
    }

    public void setTrack(Double track) {
        this.track = track;
    }

    private Double scan;

    public Double getScan() {
        return scan;
    }

    public void setScan(Double scan) {
        this.scan = scan;
    }

    private String satellite;

    public String getSatellite() {
        return satellite;
    }

    public void setSatellite(String satellite) {
        this.satellite = satellite;
    }

    private Double version;

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    private Integer confidence;

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    private Long decade;

    public Long getDecade() {
        return decade;
    }

    public void setDecade(Long decade) {
        this.decade = decade;
    }

    private Date stamp;

    public Date getStamp() {
        return stamp;
    }

    public void setStamp(Date stamp) {
        this.stamp = stamp;
    }

    private Double frp;

    public Double getFrp() {
        return frp;
    }

    public void setFrp(Double frp) {
        this.frp = frp;
    }

    private Double brightness;

    public Double getBrightness() {
        return brightness;
    }

    public void setBrightness(Double brightness) {
        this.brightness = brightness;
    }

    private Double brightT31;

    public Double getBrightT31() {
        return brightT31;
    }

    public void setBrightT31(Double brightT31) {
        this.brightT31 = brightT31;
    }


}
