// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui;


public class GPSData {
    public double[][] susAltLatLong_sim= {null,null,null};

    public GPSData(){
        double[] susAlt_sim= {0};
        double[] susLat_sim= {};
        double[] susLong_sim= {};

        susAltLatLong_sim[0]= susAlt_sim;
        susAltLatLong_sim[1]= susLat_sim;
        susAltLatLong_sim[2]= susLong_sim;
    }

}
