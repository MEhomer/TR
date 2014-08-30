import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.utils.MapPosition;
import processing.core.PGraphics;

import java.util.List;

/**
 * Created by dimitar on 28.8.14.
 */
public class LinesMarker extends SimpleLinesMarker {
    public LinesMarker(List<Location> locationList){
        super(locationList);
    }

    public void draw(PGraphics pg, List<MapPosition> mapPositions){
        pg.pushStyle();

        pg.strokeWeight(1);
        pg.stroke(255, 255, 0);
        pg.noFill();
        pg.beginShape();
        for (MapPosition mapPosition : mapPositions) {
            pg.vertex(mapPosition.x, mapPosition.y);
        }
        pg.endShape();

        pg.popStyle();
    }
}
