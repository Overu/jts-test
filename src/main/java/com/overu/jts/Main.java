package com.overu.jts;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.operation.buffer.BufferOp;

import java.util.ArrayList;
import java.util.List;

public class Main {
  private static JTSProvider jtsProvider = JTSProvider.get();

  public static void bufferTest() throws ParseException {
    String line1 = "LINESTRING (0 0, 1 1, 2 2,3 3)";
    Geometry g1 = jtsProvider.buildGeoByWKT(line1);

    Geometry g2 = g1.buffer(2);
    System.out.println("buffer 1 = " + g2.toText());

    BufferOp op = new BufferOp(g1);
    op.setEndCapStyle(BufferOp.CAP_ROUND);
    Geometry g3 = op.getResultGeometry(2);
    System.out.println("buffer 2 = " + g3.toText());
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
    bufferTest();

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
