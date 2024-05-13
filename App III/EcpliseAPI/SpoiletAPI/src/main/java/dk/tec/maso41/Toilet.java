package dk.tec.maso41;


public class Toilet {
    int id;
    double longitude;
    double latitude;
    double altitude;
    float bearing;
    boolean favorite = false;
    
    public Toilet() {};

    public Toilet(int id, double longitude, double latitude, double altitude, float bearing, boolean favorite) {
		super();
		this.id = id;
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
		this.bearing = bearing;
		this.favorite = favorite;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public float getBearing() {
		return bearing;
	}

	public void setBearing(float bearing) {
		this.bearing = bearing;
	}
    
	public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
    public boolean getFavorite() { return favorite; }
}
