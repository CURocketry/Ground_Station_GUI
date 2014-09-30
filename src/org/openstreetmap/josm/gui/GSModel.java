// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.openstreetmap.josm.data.osm.GPSBodyNode;

public class GSModel {

    public static final int BOOSTER= 0;
    public static final int SUSTAINER= 1;
    public static final int RANDT= 2;
    public static final int PAYLOAD= 3;
    public static final int NOSECONE=4;
    public static final int EMU1= 5;
    public static final int EMU2= 6;
    public static final int EMU3= 7;

    public static final File boosterSimFile = new File("/Users/sjd227/Documents/6th Semester/Rocketry/Sim_feb20_booster.xls");
    public static final File susSimFile = new File("/Users/sjd227/Documents/6th Semester/Rocketry/Sim_feb20.xls");

    /*
    public static GPSObject boosterSim=
            new GPSObject(BOOSTER,
                    Color.red);
    public static GPSObject susSim=
            new GPSObject(SUSTAINER,
                    Color.green);
                    */

    public static SimGPSObject boosterSim=

            new SimGPSObject(BOOSTER,
                    Color.red,
                    boosterSimFile);
    public static SimGPSObject susSim=
            new SimGPSObject(SUSTAINER,
                    Color.green,
                    susSimFile);
    //-------------//
    //IS IT SIM OR IS XBEE COMMUNICATION INVOLVED?:
    public static boolean isSim= true;
    //-------------//
    public static GPSObject[] launchTabObjects= {boosterSim,susSim};
    public static GPSObject[] otherObjects= {};
    private GPSObject[] objects= (GPSObject[]) ArrayUtils.addAll(launchTabObjects,otherObjects);

    public final double launchSiteLat= 45;
    public final double launchSiteLng= 0;

    public final double minLat= launchSiteLat - 0.01;
    public final double maxLat= launchSiteLat + 0.01;
    public final double minLng= launchSiteLng - 0.01;
    public final double maxLng= launchSiteLng + 0.01;
    public final double minAlt= 0;
    public final double maxAlt= 10000;

    public static final String fName=
            "/Users/sjd227/Documents/Programs/Eclipse/EdgeTest/src/flats.png"; //(used by the GUI)

    public static Float lowThresh= 15f;     //(default properties used by GUI)
    public static Float highThresh= 20f;
    public static int patchRes= 20;
    public static int nbrRad= 1;
    //these will eventually be final
    public static int hzrdImgWidth= 923;
    public static int hzrdImgHeight= 482;





    private ArrayList<GPSBodyNode> osmSelNodes= new ArrayList<GPSBodyNode>();
    private HzrdGroup[] hazards;

    private boolean[] isSeparated= new boolean[8]; //indicates if sections listed above are separated
    public boolean newHzrdsReceived;



    public boolean[] getSeparated(){
        return isSeparated;
    }

    public void setSeparated(boolean[] isSeparated){
        this.isSeparated= isSeparated;
    }

    public GPSObject[] getObjects(){
        return objects;
    }
    public void addSelectedNode(GPSBodyNode node){
        osmSelNodes.add(node);
    }

    public ArrayList<GPSBodyNode> getSelectedNodes(){
        return osmSelNodes;
    }

    public HzrdGroup[] getHazards(){
        return hazards;
    }

    public void setHazards(HzrdGroup[] hazards){
        this.hazards= hazards;
    }


}
