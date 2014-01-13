package com.overu.jts;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.algorithm.HCoordinate;
import com.vividsolutions.jts.algorithm.NotRepresentableException;
import com.vividsolutions.jts.algorithm.PointLocator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geomgraph.PlanarGraph;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.linemerge.LineMerger;
import com.vividsolutions.jts.operation.overlay.OverlayOp;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Main {
  private static JTSProvider jtsProvider = JTSProvider.get();

  public static void algorithmTest() {
    Coordinate c1 = new Coordinate(0, 0);
    Coordinate c2 = new Coordinate(1, 1);
    Coordinate c3 = new Coordinate(4, 3);

    Angle angle = new Angle();
    double radia1 = angle.angle(c1, c2);
    double radia2 = angle.angle(c2, c3);
    System.out.println("radia1 = " + radia1);
    System.out.println("radia2 = " + radia2);
    System.out.println("diff radia2 = " + angle.toDegrees(angle.diff(radia1, radia2)));

    System.out.println("between = " + angle.toDegrees(angle.angleBetween(c1, c2, c3)));

    System.out.println("turn = " + angle.getTurn(radia1, radia2));

    System.out.println("isAcute = " + angle.isAcute(c1, c2, c3));

    System.out.println("isObtuse = " + angle.isObtuse(c1, c2, c3));

    System.out.println("toDegrees = " + angle.toDegrees(Math.PI));

    System.out.println("toRadians = " + angle.toRadians(180));
  }

  public static void algorithmTestByCGA() throws ParseException {
    Geometry g = jtsProvider.buildGeoByWKT("LINESTRING (0 0,1 1,2 2, 5 5,0 5,0 0)");
    Coordinate[] coordinates = g.getCoordinates();

    System.out.println("isPointInRing = " + CGAlgorithms.isPointInRing(new Coordinate(4, 4), coordinates));
    System.out.println("isPointInRing = " + CGAlgorithms.isPointInRing(new Coordinate(1, 2), coordinates));

    System.out.println("distanceLineLine = "
        + CGAlgorithms.distanceLineLine(new Coordinate(1, 1), new Coordinate(2, 2), new Coordinate(2, 0), new Coordinate(3, 0)));

    System.out.println("isCCW = "
        + CGAlgorithms.isCCW(new Coordinate[] { new Coordinate(1, 1), new Coordinate(2, 0), new Coordinate(2, 2), new Coordinate(1, 1) }));

    System.out.println("isOnLine = "
        + CGAlgorithms
            .isOnLine(new Coordinate(2, 1), new Coordinate[] { new Coordinate(2, 0), new Coordinate(2, 2), new Coordinate(1, 1) }));

    HCoordinate h = new HCoordinate();
    try {
      System.out.println(h.intersection(new Coordinate(0, 0), new Coordinate(5, 5), new Coordinate(0, 4), new Coordinate(4, 0)));
    } catch (NotRepresentableException e) {
      e.printStackTrace();
    }

    PointLocator pl = new PointLocator();
    // 在线上
    System.out.println(pl.locate(new Coordinate(0, 0), jtsProvider.buildGeoByWKT("LINESTRING (0 0,1 1,2 2, 5 5,0 5)")));
    // 在面上
    System.out.println(pl.locate(new Coordinate(0, 0), jtsProvider.buildGeoByWKT("POLYGON ((0 0,1 1,2 2,5 5,0 5,0 0))")));
    // 在面内
    System.out.println(pl.locate(new Coordinate(1, 3), jtsProvider.buildGeoByWKT("POLYGON ((0 0,1 1,2 2,5 5,0 5,0 0))")));
    // 在面外
    System.out.println(pl.locate(new Coordinate(10, 10), jtsProvider.buildGeoByWKT("POLYGON ((0 0,1 1,2 2,5 5,0 5,0 0))")));
  }

  public static void bufferTest() throws ParseException {
    String line1 = "LINESTRING (0 0, 1 1, 2 2,3 3)";
    Geometry g1 = jtsProvider.buildGeoByWKT(line1);
    Geometry g2 = g1.buffer(2);
    System.out.println("buffer 1 = " + g2.toText());
    BufferOp op = new BufferOp(g1);
    op.setEndCapStyle(BufferOp.CAP_ROUND);
    Geometry g3 = op.getResultGeometry(2);
    System.out.println("buffer 2 = " + g3.toText());

    List<Geometry> list = new ArrayList<Geometry>();
    list.add(jtsProvider.buildGeoByWKT("LINESTRING (0 0,1 1)"));
    list.add(jtsProvider.buildGeoByWKT("LINESTRING (6 3,6 10)"));
    list.add(jtsProvider.buildGeoByWKT("LINESTRING (2 2,4 4,6 3)"));
    list.add(jtsProvider.buildGeoByWKT("LINESTRING (2 2,5 1,6 3)"));
    list.add(jtsProvider.buildGeoByWKT("LINESTRING (6 3,6 4)"));
    list.add(jtsProvider.buildGeoByWKT("LINESTRING (9 5,7 1,6 4)"));
    list.add(jtsProvider.buildGeoByWKT("LINESTRING (9 5,8 8,6 4)"));
    Polygonizer p = new Polygonizer();
    p.add(list);
    Collection<Geometry> polygons = p.getPolygons();
    Collection<Geometry> dangles = p.getDangles();
    Collection<Geometry> cutEdges = p.getCutEdges();
    System.out.println(polygons.size() + ":" + polygons.toString());
    System.out.println(dangles.size() + ":" + dangles.toString());
    System.out.println(cutEdges.size() + ":" + cutEdges.toString());

    LineMerger lm = new LineMerger();
    list = new ArrayList<Geometry>();
    list.add(jtsProvider.buildGeoByWKT("LINESTRING (3 3,2 2,0 0)"));
    list.add(jtsProvider.buildGeoByWKT("LINESTRING (3 3,6 6,0 10)"));
    list.add(jtsProvider.buildGeoByWKT("LINESTRING (0 10,3 1,10 1)"));
    lm.add(list);
    Collection<Geometry> mergedLineStrings = lm.getMergedLineStrings();
    for (Geometry g : mergedLineStrings) {
      System.out.println(g.toText());
    }

    list = new ArrayList<Geometry>();
    list.add(jtsProvider.buildGeoByWKT("LINESTRING (10 10,2 2,0 0)"));
    list.add(jtsProvider.buildGeoByWKT("LINESTRING (10 0,6 6,0 10)"));
    list.add(jtsProvider.buildGeoByWKT("LINESTRING (1 1,3 1,10 1)"));
    Geometry nodedLine = list.get(0);
    for (int i = 1; i < list.size(); i++) {
      nodedLine = nodedLine.union(list.get(i));
    }
    int num = nodedLine.getNumGeometries();
    for (int i = 0; i < num; i++) {
      Geometry geometryN = nodedLine.getGeometryN(i);
      System.out.println("union = " + geometryN.toText());
    }

    Polygon p1 = (Polygon) jtsProvider.buildGeoByWKT("POLYGON((0 0, 2 0 ,2 2, 0 2,0 0))");
    Polygon p2 = (Polygon) jtsProvider.buildGeoByWKT("POLYGON((0 0, 4 0 , 4 1, 0 1, 0 0))");
    OverlayOp oop = new OverlayOp(p1, p2);
    Geometry ge1 = oop.getResultGeometry(OverlayOp.INTERSECTION);
    Geometry ge2 = oop.getResultGeometry(OverlayOp.UNION);
    Geometry ge3 = oop.getResultGeometry(OverlayOp.DIFFERENCE);
    Geometry ge4 = oop.getResultGeometry(OverlayOp.SYMDIFFERENCE);
    PlanarGraph graph = oop.getGraph();
    System.out.println(graph.toString());
  }

  public static void createTest() throws ParseException {
    Point createPoint = jtsProvider.createPointByWKT();
    System.out.println(createPoint.getX() + "--" + createPoint.getY());

    MultiPoint multiPoint = jtsProvider.createMulPointByWKT();
    System.out.println("MultiPoint = " + multiPoint.getCoordinates()[0].x);

    LineString lineString = jtsProvider.createLine();
    System.out.println("LineString = " + lineString.getLength());

    LineString lineStringByWKT = jtsProvider.createLineByWKT();
    System.out.println("LineStringByWKT = " + lineStringByWKT.getLength());

    MultiLineString mLine = jtsProvider.createMLine();
    System.out.println("MultiLineString = " + mLine.getLength());

    MultiLineString mLineWKT = jtsProvider.createMLineByWKT();
    System.out.println("MultiLineStringByWKT = " + mLineWKT.getLength());

    Polygon polygon = jtsProvider.createPolygonByWKT();
    System.out.println("polygon = " + polygon.getLength());

    MultiPolygon multiPolygon = jtsProvider.createMPolygonByWKT();
    System.out.println("MultiPolygon = " + multiPolygon.getLength());

    GeometryCollection geometryCollection = jtsProvider.createGeoCollection();
    System.out.println("GeometryCollection = " + geometryCollection.getLength());

    Polygon p = jtsProvider.createCircle(0, 1, 2);
    Coordinate coords[] = p.getCoordinates();
    for (Coordinate coord : coords) {
      System.out.println(coord.x + "," + coord.y);
    }
    System.out.println("circle = " + p.getLength());
  }

  public static void main(String[] args) throws ParseException {
    // createTest();
    // relatedTest();
    // operationTest();
    // bufferTest();
    // algorithmTest();
    algorithmTestByCGA();

  }

  public static void operationTest() throws ParseException {
    List<Coordinate> points1 = new ArrayList<Coordinate>();
    points1.add(toPoint(0, 0));
    points1.add(toPoint(1, 3));
    points1.add(toPoint(2, 3));
    LineString line1 = jtsProvider.createLine(points1);

    List<Coordinate> points2 = new ArrayList<Coordinate>();
    points2.add(toPoint(3, 0));
    points2.add(toPoint(3, 3));
    points2.add(toPoint(5, 6));
    LineString line2 = jtsProvider.createLine(points2);

    System.out.println("distance = " + line1.distance(line2));
    System.out.println("intersection = " + line1.intersection(line2));
    System.out.println("union = " + line1.union(line2));
    System.out.println("difference = " + line1.difference(line2));

  }

  public static void relatedTest() throws ParseException {
    LineString line1 = jtsProvider.createLineByWKT("LINESTRING(0 0, 2 0, 5 0)");
    LineString line2 = jtsProvider.createLineByWKT("LINESTRING(5 0, 0 0)");
    System.out.println("equalsGeo = " + line1.equals(line2));

    LineString line3 = jtsProvider.createLineByWKT("LINESTRING(0 1, 0 2)");
    System.out.println("disjointGeo = " + line1.equals(line3));

    LineString line4 = jtsProvider.createLineByWKT("LINESTRING(0 0, 0 2)");
    Geometry interGeo = line1.intersection(line4);
    System.out.println("intersection = " + interGeo.toText());
    System.out.println("intersects = " + line1.intersects(line4));

    System.out.println("crosses = " + line1.crosses(line4));
    System.out.println("overlaps = " + line1.overlaps(line4));

  }

  public static Coordinate toPoint(double x, double y) {
    return new Coordinate(x, y);
  }

}
