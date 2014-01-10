package com.overu.jts;

import com.overu.jts.interfaces.GeometryFactoryProvider;
import com.overu.jts.interfaces.WKTReaderProvider;
import com.overu.jts.provider.GeometryFactoryProviderImpl;
import com.overu.jts.provider.WKTReaderProviderImpl;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.util.ArrayList;
import java.util.List;

public class JTSProvider {

  private static volatile JTSProvider INSTANCE;

  public static JTSProvider get() {
    if (INSTANCE == null) {
      INSTANCE = new JTSProvider();
    }
    return INSTANCE;
  }

  private GeometryFactory geometryFactory;
  private WKTReader reader;

  private JTSProvider() {
    GeometryFactoryProvider gfp = new GeometryFactoryProviderImpl();
    geometryFactory = gfp.geometryFactory();

    WKTReaderProvider wrp = new WKTReaderProviderImpl(geometryFactory);
    reader = wrp.wktReader();
  }

  public Geometry buildGeoByWKT(String build) throws ParseException {
    return getWKTReader().read(build);
  }

  public Polygon createCircle(double x, double y, double radius) {
    final int SIDES = 32;
    Coordinate coords[] = new Coordinate[SIDES + 1];
    for (int i = 0; i < SIDES; i++) {
      double angle = ((double) i / (double) SIDES) * Math.PI * 2.0;
      double dx = Math.cos(angle) * radius;
      double dy = Math.sin(angle) * radius;
      coords[i] = new Coordinate(x + dx, y + dy);
    }
    coords[SIDES] = coords[0];
    LinearRing ring = geometryFactory.createLinearRing(coords);
    Polygon polygon = geometryFactory.createPolygon(ring, null);
    return polygon;
  }

  public GeometryCollection createGeoCollection() throws ParseException {
    LineString line = createLine();
    Polygon polygon = createPolygonByWKT();
    Geometry g1 = geometryFactory.createGeometry(line);
    Geometry g2 = geometryFactory.createGeometry(polygon);
    Geometry[] geos = new Geometry[] { g1, g2 };
    GeometryCollection geoCollection = geometryFactory.createGeometryCollection(geos);
    return geoCollection;
  }

  public LineString createLine() {
    return createLine(new Coordinate(2, 2), new Coordinate(2, 5));
  }

  public LineString createLine(Coordinate ls, Coordinate rs) {
    ArrayList<Coordinate> points = new ArrayList<Coordinate>();
    points.add(ls);
    points.add(rs);
    return createLine(points);
  };

  public LineString createLine(List<Coordinate> points) {
    Coordinate[] coors = points.toArray(new Coordinate[points.size()]);
    LineString lineString = geometryFactory.createLineString(coors);
    return lineString;
  };

  public LineString createLineByWKT() throws ParseException {
    return createLineByWKT("LINESTRING(0 0, 2 0)");
  }

  public LineString createLineByWKT(String wktStr) throws ParseException {
    LineString lineString = (LineString) getWKTReader().read(wktStr);
    return lineString;
  }

  public MultiLineString createMLine() {
    Coordinate[] coord1 = new Coordinate[] { new Coordinate(2, 2), new Coordinate(2, 2) };
    LineString line1 = geometryFactory.createLineString(coord1);
    Coordinate[] coord2 = new Coordinate[] { new Coordinate(2, 2), new Coordinate(2, 2) };
    LineString line2 = geometryFactory.createLineString(coord2);

    LineString[] lineStrings = new LineString[2];
    lineStrings[0] = line1;
    lineStrings[1] = line2;

    MultiLineString mLine = geometryFactory.createMultiLineString(lineStrings);
    return mLine;
  }

  public MultiLineString createMLineByWKT() throws ParseException {
    MultiLineString mLine = (MultiLineString) getWKTReader().read("MultiLineString((0 0, 2 0),(1 1,2 2))");
    return mLine;
  }

  public MultiPolygon createMPolygonByWKT() throws ParseException {
    MultiPolygon multiPolygon =
        (MultiPolygon) getWKTReader().read("MULTIPOLYGON(((40 10, 30 0, 40 10, 30 20, 40 10),(30 10, 30 0, 40 10, 30 20, 30 10)))");
    return multiPolygon;
  }

  public MultiPoint createMulPointByWKT() throws ParseException {
    MultiPoint multiPoint = (MultiPoint) getWKTReader().read("MULTIPOINT(109.013388 32.715519,119.32488 31.435678)");
    return multiPoint;
  }

  public Point createPoint() {
    return createPoint(109.013388, 32.715519);
  }

  public Point createPoint(double x, double y) {
    Coordinate coord = new Coordinate(x, y);
    Point point = geometryFactory.createPoint(coord);
    return point;
  }

  public Point createPointByWKT() throws ParseException {
    Point point = (Point) getWKTReader().read("POINT(109.013388 32.715519)");
    return point;
  }

  public Polygon createPolygonByWKT() throws ParseException {
    Polygon polygon = (Polygon) getWKTReader().read("POLYGON((20 10, 30 0, 40 10, 30 20, 20 10))");
    return polygon;
  }

  public void setGeometryFactoryProvider(GeometryFactoryProvider provider) {
    if (provider == null) {
      throw new IllegalArgumentException("GeometryFactoryProvider is null");
    }
    geometryFactory = provider.geometryFactory();
  }

  public void setWKTReaderProvier(WKTReaderProvider provider) {
    if (provider == null) {
      throw new IllegalArgumentException("WKTReaderProvider is null");
    }
    reader = provider.wktReader();
  }

  private WKTReader getWKTReader() {
    assert reader == null : "WKTReader is null";
    return reader;
  }

}
