package dbInterface;

import java.io.Serializable;

/**
 * Created by Dimitar on 13.2.14.
 */
// Point class
public class Point implements Serializable {
    private double lat; // Latitude of the point
    private double lng; // Longitude of the point
    private double alt; // Altitude of the point
    private long ts;  // Timestamp of the point
    // Constructor with four arguments
    public Point(double lat, double lng, double alt, long ts){
        this.lat = lat; // Setting the latitude of the point
        this.lng = lng; // Setting the longitude of the point
        this.alt = alt; // Setting the altitude of the point
        this.ts = ts;   // Setting the timestamp of the point
    }

    public double getLat(){
        return lat;
    }

    public double getLng(){
        return lng;
    }

    public double getAlt(){
        return alt;
    }

    public double getTs() { return ts; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        } else if (obj.getClass() != getClass()){
            return false;
        } else {
            Point other = (Point) obj;
            if (other.lat == lat && other.lng == lng && other.alt == alt && other.ts == ts){
                return true;
            }

            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Alt: " + alt);
        sb.append(", ");
        sb.append("Lat: " + lat);
        sb.append(", ");
        sb.append("Lng: " + lng);
        sb.append("\n");

        return sb.toString();
    }
}
