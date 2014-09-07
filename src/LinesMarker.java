import dbInterface.Point;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.utils.MapPosition;
import processing.core.PGraphics;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dimitar on 28.8.14.
 */
public class LinesMarker extends SimpleLinesMarker {
    private int id;
    private boolean selected = false;

    private static double MAX = 29700.0;
    private static double SECOND_MAX = 2500.0;
    private static double MIN = 0.0;

    public PointMarker startPoint;
    public PointMarker endPoint;

    public LinesMarker(LinkedList<Location> locationList, int id){
        super(locationList);
        startPoint = new PointMarker(locationList.getFirst(), true);
        endPoint = new PointMarker(locationList.getLast(), false);
        this.id = id;
    }

    public int getID(){
        return id;
    }

    public void setSelected(boolean value){
        selected = value;
    }

    public void draw(PGraphics pg, List<MapPosition> mapPositions){
        pg.pushStyle();

        pg.strokeWeight(5);
        pg.noFill();
        pg.beginShape(pg.LINES);
        Iterator<MapPosition> walkerMP = mapPositions.iterator();
        Iterator<Point> walkerP = MapHelper.workout_object_map.get(this.id).getPoints().iterator();
        MapPosition start = null;
        Point startP = null;
        if (walkerMP.hasNext()){
            start = walkerMP.next();
            startP = walkerP.next();
        }
        while(walkerMP.hasNext()){
            MapPosition end = walkerMP.next();
            Point endP = walkerP.next();
            double averageAlt = Math.abs((endP.getAlt() + startP.getAlt())/2);
            if (averageAlt > SECOND_MAX) averageAlt = SECOND_MAX;
            int yellow = (int) (255 - (averageAlt / SECOND_MAX) * 255);
            if (this.selected) {
                pg.stroke(0, yellow, 255);
                pg.strokeWeight(6);
            }
            else {
                pg.stroke(255, yellow, 0, 150);
                pg.strokeWeight(4);
            }
            pg.vertex(start.x, start.y);
            pg.vertex(end.x, end.y);
            start = end;
            startP = endP;
        }
        pg.endShape();

        pg.popStyle();
    }
}
