// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

//Suppress further error dialogs for this session

public class SimGPSObject extends GPSObject{
    public File simFile;

    //Excel data...
    public final int simDataStartLine= 10;
    public final int simDataEndLine= 989;
    public final int simTimeColumn= 1;
    public final int simAltColumn= 2;
    public final int simLatColumn= 13;
    public final int simLngColumn= 14;

    public final int simDataMaxL= simDataEndLine-simDataStartLine+1;
    public int simDataL;
    public double[] simTime= new double[simDataMaxL];
    public double[] simLat= new double[simDataMaxL];
    public double[] simLng= new double[simDataMaxL];
    public double[] simAlt= new double[simDataMaxL];

    public int sIdx;


    public SimGPSObject(int sectionID, Color osmNodeColor, File simFile){
        super(sectionID, osmNodeColor);
        this.simFile= simFile;
        parseSimData();
    }

    /*
    @Override
    public GPSCollector getGPSCollector(){
        return new SimGPSCollector();
    }
    */

    public void parseSimData(){
        try {
            //Get Excel columns...
            Workbook simBook = Workbook.getWorkbook(simFile);
            Sheet simSheet= simBook.getSheet(0);
            Cell[] timeColumn= simSheet.getColumn(simTimeColumn-1);
            Cell[] altColumn= simSheet.getColumn(simAltColumn-1);
            Cell[] latColumn= simSheet.getColumn(simLatColumn-1);
            Cell[] lngColumn= simSheet.getColumn(simLngColumn-1);

            //Parse
            int k=0;
            for (int i=simDataStartLine-1; i<simDataEndLine; i++){
                if (!altColumn[i].getContents().equals("")){
                    simTime[k]= Double.parseDouble(timeColumn[i].getContents());
                    simAlt[k]= Double.parseDouble(altColumn[i].getContents());
                    simLat[k]= Double.parseDouble(latColumn[i].getContents());
                    simLng[k]= Double.parseDouble(lngColumn[i].getContents());
                    k++;
                }
            }

            //TESTING: to separate plotting data...
            if (osmNodeColor.equals(Color.red)){

            }

            simDataL=k;
            //System.out.println(Arrays.toString(Arrays.copyOfRange(simLat,0, simDataL)));
            //System.out.println(Arrays.toString(simTime));



        } catch (BiffException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
    public class SimGPSCollector extends GPSCollector implements Runnable {

        @Override
        public void run() {
            int simIdx= 0;
            int nextSimIdx= 0;
            long refreshTime= 500;
            double latCur; double lngCur; double altCur;
            while(MainApplication.shouldCollect && nextSimIdx < simDataL){
                if (osmNodeColor.equals(Color.red)){
                System.out.println("Booster");
                }else
                    System.out.println("Sustainer");
                if (osmNodeColor.equals(Color.green) && nextSimIdx > 50){return;}
                    try {
                    //Simulate receiving GPS data, recorded in sim ~20 times per second (like in simulation)
                    //nextSimIdx= gpsIndex*refreshRate/50;
                        if (nextSimIdx < simDataL){
                        simIdx= nextSimIdx;
                        latCur= simLat[simIdx];
                        lngCur= simLng[simIdx];
                        altCur= simAlt[simIdx];
                        lat[gpsIndex]= latCur;
                        lng[gpsIndex]= lngCur;
                        alt[gpsIndex]= altCur;
                        //MainApplication.plotTrajectory();

                        //MainApplication.trajectory.removePlot(0);


                    //Set labels...
                   // for (JLabel l: latLabels){l.setText("" + latCur);}
                    //for (JLabel l: lngLabels){l.setText("" + lngCur);}
                   // for (JLabel l: altLabels){l.setText("" + altCur);}

                    //Plot Trajectory...
                    if (gpsIndex > 1)
                    MainApplication.trajectory.addLinePlot("Rocket Trajectory", osmNodeColor, Arrays.copyOfRange(lat, gpsIndex-1, gpsIndex+1), Arrays.copyOfRange(lng, gpsIndex-1, gpsIndex+1), Arrays.copyOfRange(alt, gpsIndex-1, gpsIndex+1));
                    MainApplication.trajectory.setFixedBounds(0,MainApplication.minLat,MainApplication.maxLat);
                    MainApplication.trajectory.setFixedBounds(1,MainApplication.minLng,MainApplication.maxLng);
                    MainApplication.trajectory.setFixedBounds(2,MainApplication.minAlt,MainApplication.maxAlt);

                    //Plot GPS Body on OSM...
                    osmNode.setCoor(new LatLon(latCur,lngCur));
                    Main.contentPanePrivate.repaint();

                    gpsIndex++;


                    //System.out.println("running sim gps collector...");
                    //nextSimIdx= gpsIndex*refreshFactor;
                    if (nextSimIdx < simDataL) refreshTime=  (long) ((simTime[nextSimIdx]-simTime[simIdx]) * 1000);
                    //Thread.sleep(refreshRate);
                    Thread.sleep(refreshTime);
                        }
                    } catch (InterruptedException e) {}
                }
        }

    }
    */
}
