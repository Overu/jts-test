package com.overu.jts.provider;

import com.overu.jts.interfaces.WKTReaderProvider;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

public class WKTReaderProviderImpl implements WKTReaderProvider {

  private GeometryFactory geometryFactory;

  public WKTReaderProviderImpl(GeometryFactory geometryFactory) {
    this.geometryFactory = geometryFactory;
  }

  public WKTReader wktReader() {
    return new WKTReader(geometryFactory);
  }

}
