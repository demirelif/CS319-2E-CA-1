import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

class Location {
    int x;
    int y;
    enum Types { CORNER, SIDE, TILE }
    Types type;

    Location() {
        this(0,0,null);
    }

    Location( int x, int y, Types type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public String toString() {
        return "x: " + x + ", y: " + y + "type: " +  type.name();
    }

    Location translated(int x, int y) {
        return translated( x, y , null);
    }

    Location translated( int x, int y, Types type) {
        type = ( type == null ) ? this.type : type;
        return new Location( this.x + x, this.y + y, type);
    }

    ArrayList<Location> getAdjacentTiles() {
        return getAdjacentTiles( this, null);
    }

    ArrayList<Location> getAdjacentTiles( Location loc ) {
        return getAdjacentTiles( loc, null);
    }

    ArrayList<Location> getAdjacentTiles ( Location loc, Location exclude ) {
        //only for corners
        ArrayList<Location> res = new ArrayList<Location>();
        Location tile1;
        tile1 = new Location( x / 2, y, Types.TILE );
        res.add(tile1);
        if ( loc.x % 2 == 0 ) {
            res.add( tile1.translated( -1, 0));
            res.add( tile1.translated( -1, -1));
        } else {
            res.add( tile1.translated( 0, -1));
            res.add( tile1.translated( -1, -1));
        }
        return res;
    }

    ArrayList<Location> getAdjacentSides( Location loc, Location exclude ) {
        ArrayList<Location> res = new ArrayList<>();
        if (loc.type == Types.CORNER) {
            return null;
        } else if (loc.type == Types.SIDE) {
            ArrayList<Location> corners = getAdjacentCorners(loc);
            for (Location l : corners) {
                res.addAll(getAdjacentSides(l, loc));
            }
        }
        return res;
    }

    ArrayList<Location> getAdjacentCorners() {
        return getAdjacentCorners( this );
    }

    ArrayList<Location> getAdjacentCorners( Location loc ) {
        return getAdjacentCorners( loc, null);
    }

    ArrayList<Location> getAdjacentCorners( Location loc, ArrayList<Location> exclude ) {
        ArrayList<Location> res = new ArrayList<>();
        if (loc.type == Types.CORNER) {
            res.add( translated( -1, 0) );
            res.add( translated( 1, 0 ) );
            if( x % 2 == 0 ) {
                res.add( translated( 1, 1 ));
            } else {
                res.add(translated(-1, -1));
            }
        } else if ( loc.type == Types.SIDE ) {
            Location cor1 = new Location( loc.x / 3 * 2, loc.y, Types.CORNER );
            if ( loc.x % 3 == 0 ) {
                res.add( cor1 );
                res.add( cor1.translated( 1, 1 ) );
            } else if ( loc.x % 3 == 1 ) {
                res.add( cor1 );
                res.add( cor1.translated(  1, 0 ));
            } else {
                res.add( cor1.translated( 1, 0));
                res.add( cor1.translated( 2, 0));
            }
        }
        return res;
    }

    Point2D getRawDisplayPosition() {
        return getRawDisplayPosition(this );
    }

    Point2D getRawDisplayPosition( Location loc ) {
        double x = 0;
        double y = 0;
        double xOffset = 60.0;
        double yOffset = 60.0;
        double spread = 0;
        if ( loc.type == Types.CORNER ) {
            spread = 10.0;
            x = (loc.x - loc.y) * Math.sqrt(3) * spread;
            y = ( ( loc.y * 3 ) - (loc.x % 2 == 0 ? 0 : 1 ) ) * spread;
            x += xOffset;
            y += yOffset;
        } else if ( loc.type == Types.SIDE ) {
            ArrayList<Location> cors = getAdjacentCorners( loc );
            Location cor1 = cors.get(0);
            Location cor2 = cors.get(1);
            Point2D pos1 = cors.get(0).getRawDisplayPosition();
            Point2D pos2 = cors.get(1).getRawDisplayPosition();
            x = ( pos1.getX() + pos2.getX() ) / 2.0;
            y = ( pos1.getY() + pos2.getY() ) / 2.0;
        } else if (type == Types.TILE ) {
            x = loc.x * Math.sqrt(3) * 2;
            y = loc.y * 4;
        }
        return new Point2D.Double( x , y );
    }
}
