// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.osm;

import java.awt.Color;

import org.openstreetmap.josm.data.coor.LatLon;

public class GPSBodyNode extends Node{
    public Color color;

    public GPSBodyNode(LatLon latlon, Color color){
        super(latlon);
        this.color= color;
    }



}
