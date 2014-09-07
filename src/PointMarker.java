import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

/**
 * Created by dimitar on 2.9.14.
 */
public class PointMarker extends SimplePointMarker {
    private boolean start;
    public PointMarker(Location location, boolean start) {
        super(location);
        this.start = start;
    }

    public void draw(PGraphics pg, float x, float y) {
        pg.pushStyle();
        pg.noStroke();
        if (start) pg.fill(255, 50, 0, 100);
        else pg.fill(0, 0, 0, 100);
        pg.ellipse(x, y, 20, 20);
        pg.fill(255, 100);
        pg.ellipse(x, y, 15, 15);
        pg.popStyle();
    }
}
