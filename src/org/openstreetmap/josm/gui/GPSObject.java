// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JLabel;

import org.openstreetmap.josm.data.osm.GPSBodyNode;


public class GPSObject {
    public double[] lat= new double[10000];
    public double[] lng= new double[10000];
    public double[] alt= new double[10000];
    public double[] vel= new double[10000];

    public Color osmNodeColor;
    public GPSBodyNode osmNode;
    public int gIdx;

    public String name;

    public ArrayList<JLabel> latLabels= new ArrayList<JLabel>();
    public ArrayList<JLabel> lngLabels= new ArrayList<JLabel>();
    public ArrayList<JLabel> altLabels= new ArrayList<JLabel>();

    public double testNodeLat= 40.8713635;
    public double testNodeLng= -73.600983;


    public GPSObject(int sectionID, Color osmNodeColor){
        if (sectionID==GSModel.BOOSTER) name= "Booster";
        else if (sectionID==GSModel.SUSTAINER) name= "Sustainer";
        this.osmNodeColor= osmNodeColor;
    }

    public int newLatLabelIdx(){
        JLabel newLabel= new JLabel("---", JLabel.CENTER);
        latLabels.add(newLabel);
        return latLabels.size() - 1;
    }

    public int newLngLabelIdx(){
        JLabel newLabel= new JLabel("---", JLabel.CENTER);
        lngLabels.add(newLabel);
        return lngLabels.size() - 1;
    }

    public int newAltLabelIdx(){
        JLabel newLabel= new JLabel("---", JLabel.CENTER);
        altLabels.add(newLabel);
        return altLabels.size() - 1;
    }

    /*
    public GPSCollector getGPSCollector(){
        return new GPSCollector();
    }


    public class GPSCollector extends Thread {

        @Override
        public void run() {
            System.out.println("here212");
        }

    }
    */

}
