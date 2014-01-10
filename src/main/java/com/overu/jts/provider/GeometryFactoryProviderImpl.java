package com.overu.jts.provider;

import com.overu.jts.interfaces.GeometryFactoryProvider;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.geotools.geometry.jts.JTSFactoryFinder;

public class GeometryFactoryProviderImpl implements GeometryFactoryProvider {

  public GeometryFactory geometryFactory() {
    return JTSFactoryFinder.getGeometryFactory(null);
  }

}
