// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;

import org.math.plot.Plot3DPanel;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.OpenFileAction;
import org.openstreetmap.josm.actions.OpenFileAction.OpenFileTask;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.GPSBodyNode;

public class GSApp implements ActionListener, ItemListener{
    static final String BUTTON_RUN= "button_run";
    static final String BUTTON_ENABLEPAYLOAD= "button_enablepayload";
    static final String BUTTON_SEPTEST= "button_septest";
    static final String BUTTON_OPENHZRD= "button_openhzrd";
    static final String BUTTON_GETHZRD= "button_gethzrd";
    static final String BUTTON_SELNODES= "button_selnodes";
    static final String BUTTON_OPENOSM= "button_openosm";

    private GSView view;
    private GSModel model;
    private XBeeThread xbeeThread;

    //private JOSMBridge josmBridge;
    private boolean osmOpened;
    private boolean nodesInitialized;
    private boolean shouldCollect;


    public static void main(String[] args){
        new GSApp();
    }

    public GSApp(){
        //this.josmBridge= new JOSMBridge();
        model= new GSModel();
        view= GSView.setup(model, new String[0]);
        view.addController(this);
    }


    public void initGPSNodes(){
        GPSObject[] objects= model.getObjects();
        //Initialize new nodes and set them as "Selected" (i.e., bigger)...
        for (GPSObject o: objects){
            o.osmNode= new GPSBodyNode(new LatLon(0,0), o.osmNodeColor);
            model.addSelectedNode(o.osmNode);
        }

        //Add nodes to view...
        view.addGPSNodes(objects);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd= e.getActionCommand();

        if (cmd.equals(BUTTON_SEPTEST)){
            if (!GSModel.isSim || osmReady()){
                /*
                String data= "<Z 100026000V200300X 54.1111-70.2222-80.9999Y 10.1234 50.4321-80.8888S10000000C0C21,21,17,16C24,22,21,22,21,22,24,22,25,19,18,20,23,24C45C36,38,37,38,40,42,44,45,43,41,35,34,36,35,34,33,32,33,36,35,35C30,31,31,29,30,29,28,29C12C7,8,3,1,0,5C15,15,14,14C8,8,4,4C2,1C12,12,11,11C0C45R0R0,1,1,0R0,3,6,7,8,9,12,16,18,19,19,10,2,0R0R1,4,5,14,17,18,19,20,20,19,18,17,16,15,13,12,11,10,5,4,1R4,13,14,14,10,9,6,4R5R7,8,10,10,8,7R10,16,16,10R15,16,16,15R17,17R17,18,18,17R23R23A400,4400,32000,400,-31536,10000,400,9200,5600,4000,800,1600,400,400D1,1,1,1,1,1,1,1,1,1,1,1,1,1W0,4,30,0,32,9,0,8,5,3,0,1,0,0>";
                parseXBeeData(data);
                */
                xbeeThread.send("get;");
            }

        }else if (cmd.equals(BUTTON_OPENHZRD)){
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String path= fc.getSelectedFile().toString();
                view.hazard.setHzrdFileName(path);
            }

        }else if (cmd.equals(BUTTON_GETHZRD)){
            try {

                //------SIMULATED HAZARD DETECTION------//
                //get uplink parameters...
                getHzrdParams();

                //send uplink to start hazard detection camera/alorithm
                HazardSimulator hzrdSim= new HazardSimulator(view.hazard.getBufferedHzrdImg());


                //Extract hazard data...

                HzrdGroup[] hazards= hzrdSim.getHazardGroups();
                //Adjust size...
                view.hazard.setHzrdDispPxls(GSModel.hzrdImgWidth,GSModel.hzrdImgHeight);

                model.setHazards(hazards);

                //-------------//

                //update hazards
                updateHazards();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }else if (cmd.equals(BUTTON_SELNODES)){
            GSView.ds.setSelected(model.getSelectedNodes());

        }else if (cmd.equals(BUTTON_OPENOSM)){
            //Ask to open OSM file...
            JFileChooser fc = OpenFileAction.createAndOpenFileChooser(true, true, null);
            if (fc == null)
                return;
            File[] files = fc.getSelectedFiles();
            OpenFileTask task = new OpenFileTask(Arrays.asList(files), fc.getFileFilter());
            task.setRecordHistory(true);
            Main.worker.submit(task);
            osmOpened=true;
        }
    }

    public boolean osmReady(){
        if (osmOpened){
            if (!nodesInitialized)
                initGPSNodes();
            return true;
        }else{
            JOptionPane.showMessageDialog(null, "Please open an OSM file before running data collection");
            return false;
        }

    }

    @Override
    public void itemStateChanged(ItemEvent ev) {
        // TODO Auto-generated method stub
        if (ev.getItemSelectable() instanceof JToggleButton){
            JToggleButton item= (JToggleButton) ev.getItemSelectable();
            if (item.equals(view.launch.runBtn)){
                if (ev.getStateChange()==ItemEvent.SELECTED){
                    if (!GSModel.isSim || osmReady()){
                        shouldCollect= true;

                        //Start receiving data...
                        xbeeThread= new XBeeThread(this, model.getObjects());
                        xbeeThread.start();

                        //Change button display...
                        item.setText("STOP");
                        item.setForeground(Color.red);
                    }
                }else if (ev.getStateChange()==ItemEvent.DESELECTED){
                    shouldCollect=false;
                    item.setText("RUN");
                    item.setForeground(Color.green);
                }

            }else if (item.equals(view.launch.enablePayload)){
                if(ev.getStateChange()==ItemEvent.SELECTED){
                    System.out.println("KILLSWITCH SELECTED");
                    item.setText("PAYLOAD EJECTION ENABLED");
                    item.setForeground(Color.red);

                    //send cmd to xbee...
                    xbeeThread.send("enable;");

                  } else if(ev.getStateChange()==ItemEvent.DESELECTED){
                    System.out.println("(Killswitch deselected)");
                    item.setText("Enable Payload Ejection");
                    item.setForeground(Color.black);

                    //send cmd to xbee...
                    xbeeThread.send("disable;");
                  }
            }

        }
    }

    public void getHzrdParams(){
        model.lowThresh= view.hazard.getLThresh();
        model.highThresh= view.hazard.getLThresh();

        model.patchRes= view.hazard.getPatchRes();
        model.nbrRad= view.hazard.getNbrRad();

    }

    public void updateLabels(){
        for (GPSObject ob: model.getObjects()){
            if (ob.gIdx!=0){
                for (JLabel l: ob.latLabels) l.setText("" + ob.lat[ob.gIdx-1]);
                for (JLabel l: ob.lngLabels) l.setText("" + ob.lng[ob.gIdx-1]);
                for (JLabel l: ob.altLabels) l.setText("" + ob.alt[ob.gIdx-1]);
            }
        }
    }


    public void updateTrajectory(){
        try{
            for (GPSObject ob: model.getObjects()){
                //Plotting sometimes crashes, so surround w try/catch
                if (ob.gIdx > 1){
                    //Plot...
                    Plot3DPanel plot= view.launch.getPlot();
                    plot.addLinePlot(
                            "Rocket Trajectory",
                            ob.osmNodeColor,
                            Arrays.copyOfRange(ob.lat, ob.gIdx-2, ob.gIdx),
                            Arrays.copyOfRange(ob.lng, ob.gIdx-2, ob.gIdx),
                            Arrays.copyOfRange(ob.alt, ob.gIdx-2, ob.gIdx));
                    //Adjust bounds to keep them constant...
                    plot.setFixedBounds(0,model.minLat,model.maxLat);
                    plot.setFixedBounds(1,model.minLng,model.maxLng);
                    plot.setFixedBounds(2,model.minAlt,model.maxAlt);
                }
            }
        }catch(Exception e){};

    }

    public void updateOSM(){
        for (GPSObject ob: model.getObjects()){
            if (ob.gIdx!=0){
                //Node in OSM Display...
                ob.osmNode.setCoor(new LatLon(ob.lat[ob.gIdx-1],ob.lng[ob.gIdx-1]));
            }
        }
        //Repaint...
        GSView.ds.addSelected(model.getSelectedNodes()); //set nodes selected to make bigger
        GSView.contentPanePrivate.repaint();
    }

    public void updateHazards(){
        HzrdGroup[] hazards= model.getHazards();
        HzrdGroup.getPxlOutline(hazards);    //get surrounding pxles of image...
        //model.setHazards(hazards); //save to model
        view.hazard.drawHazards(hazards);
        view.hazard.setHazardData(hazards);    }

    public void updateView(){
        updateLabels();
        view.launch.rSectionsApplet.drawSeparated(model.getSeparated());
        updateTrajectory();
        updateOSM();
        //if (model.newHzrdsReceived){
            updateHazards();
        //}

    }

    public void parseXBeeData(String data){
        //Parse data string (not including start/end tags)...

        final int nSections= 8;
        ArrayList<Double>  alt=    new ArrayList<Double>();
        ArrayList<Double>   lat=    new ArrayList<Double>();
        ArrayList<Double>  vel=    new ArrayList<Double>();
        ArrayList<Double>   lng=    new ArrayList<Double>();
        boolean[]           isSep=  new boolean[nSections];



        ArrayList<HzrdGroup> hzrds= new ArrayList<HzrdGroup>();
        int nHzrds;

        int i=1;
        System.out.println(data);

        if (data.charAt(i)=='Z'){ //incoming altitude measurements (5 characters each)...
            i+=1;
            while (!isTag(data.charAt(i))){   //data is an entry...
                alt.add(Double.parseDouble(data.substring(i, i+5)));
                i+=5;
            }
        }
        if (data.charAt(i)=='V'){ //incoming altitude measurements (3 characters each)...
            i+=1;
            while (!isTag(data.charAt(i))){   //data is an entry...
                vel.add(Double.parseDouble(data.substring(i, i+3)));
                i+=3;
            }
        }
        if (data.charAt(i)=='X'){ //incoming latitude measurements (8 characters each)...
            i+=1;
            while (!isTag(data.charAt(i))){   //data is an entry...
                lat.add(Double.parseDouble(data.substring(i,i+8)));
                i+=8;
            }
        }
        if (data.charAt(i)=='Y'){ //incoming longitude measurements (8 characters each)...
            i+=1;
            while (!isTag(data.charAt(i))){   //data is an entry...
                lng.add(Double.parseDouble(data.substring(i,i+8)));
                i+=8;
            }
        }
        if (data.charAt(i)=='S'){ //incoming separation status data
            i+=1;
            for (int k=0; k < nSections; k++){
                isSep[k]=bool(data.charAt(i+k));
            }
            i+=nSections;
        }

       while (data.charAt(i)=='C'){//incoming hazard column patches data...
           HzrdGroup h= new HzrdGroup();
           boolean start=true;
           while (start || data.charAt(i)==','){ start=false;
               i++;
               int k=0;
               char c= data.charAt(i+k);
               while (!isTag(c)){
                   k++; c= data.charAt(i+k);
               }
               //reached comma or end of array--> add substring to array..
               h.addCol(Integer.parseInt(data.substring(i,i+k)));
               i+=k;
           }
           //reached end of array--> add substring to array..
           hzrds.add(h);
       }

       nHzrds= hzrds.size();


       //get all row data...
       if (data.charAt(i)=='R'){    //incoming hazard column patches data...
           for (int hIdx=0; hIdx < nHzrds; hIdx++){
               boolean start=true;
               while (start || data.charAt(i)==','){ start=false;
                   i++;
                   int k=0;
                   char c= data.charAt(i+k);
                   while (!isTag(c)){
                       k++; c= data.charAt(i+k);
                   }
                   //reached comma or end of array--> add substring to array..
                   hzrds.get(hIdx).addRow(Integer.parseInt(data.substring(i,i+k)));
                   i+=k;
               }
           }
       }

       //get all area data..
       if (data.charAt(i)=='A'){ //incoming area data
           for (int hIdx=0; hIdx < nHzrds; hIdx++){
                   i++;
                   int k=0;
                   char c= data.charAt(i+k);
                   while (!isTag(c)){
                       k++; c= data.charAt(i+k);
                   }
                   //reached comma or end of array--> add substring to array..
                   hzrds.get(hIdx).area= (Integer.parseInt(data.substring(i,i+k)));
                   i+=k;
               }
           }

       //get all dist data..
       if (data.charAt(i)=='D'){ //incoming distance data...
           for (int hIdx=0; hIdx < nHzrds; hIdx++){
                   i++;
                   int k=0;
                   char c= data.charAt(i+k);
                   while (!isTag(c)){
                       k++; c= data.charAt(i+k);
                   }
                   //reached comma or end of array--> add substring to array..
                   hzrds.get(hIdx).dist= (Integer.parseInt(data.substring(i,i+k)));
                   i+=k;
           }
       }

       //get all weight data...
       if (data.charAt(i)=='W'){ //incoming distance data...
           for (int hIdx=0; hIdx < nHzrds; hIdx++){
                   i++;
                   int k=0;
                   char c= data.charAt(i+k);
                   while (!isTag(c)){
                       k++; c= data.charAt(i+k);
                   }
                   //reached comma or end of array--> add substring to array..
                   hzrds.get(hIdx).weight=  (Integer.parseInt(data.substring(i,i+k)));
                   i+=k;
           }
       }



        //Should be end of data stream. Check:
        if (data.charAt(i)!='>'){
            System.out.println("WARNING: DID NOT REACH END OF DATA");
        }

        //Position data...
        try{

            //will always be there:

            model.setSeparated(isSep);

            GPSObject booster=  model.getObjects()[0];
            booster.alt[booster.gIdx]= alt.get(0);
            booster.lat[booster.gIdx]= lat.get(0);
            booster.lng[booster.gIdx]= lng.get(0);
            booster.vel[booster.gIdx]= vel.get(0);
            booster.gIdx++;

            //may or may not be in data stream (in order):

            GPSObject sustainer=  model.getObjects()[1];
            sustainer.alt[sustainer.gIdx]= alt.get(1);
            sustainer.lat[sustainer.gIdx]= lat.get(1);
            sustainer.lng[sustainer.gIdx]= lng.get(1);
            sustainer.vel[sustainer.gIdx]= vel.get(1);
            sustainer.gIdx++;

            GPSObject emu1= model.getObjects()[2];
            emu1.lat[emu1.gIdx]= lat.get(2);
            emu1.lng[emu1.gIdx]= lng.get(2);
            emu1.gIdx++;

            GPSObject emu2= model.getObjects()[3];
            emu2.lat[emu2.gIdx]= lat.get(3);
            emu2.lng[emu2.gIdx]= lng.get(3);
            emu2.gIdx++;

            GPSObject emu3= model.getObjects()[4];
            emu3.lat[emu3.gIdx]= lat.get(4);
            emu3.lng[emu3.gIdx]= lng.get(4);
            emu3.gIdx++;

        }catch (Exception e){
            e.printStackTrace();
        }

        //Hazard Detection data...

        try{
            System.out.println(hzrds.toArray(new HzrdGroup[0]));
            model.setHazards(hzrds.toArray(new HzrdGroup[0]));
        }catch (Exception e){
            e.printStackTrace();

        }



        //DEBUG:
        System.out.println(alt.toString());
        System.out.println(lat.toString());
        System.out.println(lng.toString());
        System.out.println(vel.toString());
        System.out.println(Arrays.toString(isSep));
        for (int j=0; j < hzrds.size(); j++){
            System.out.println("Hazard " + j + ": Columns= " + hzrds.get(j).getCols().toString());
            System.out.println("Hazard " + j + ": Rows= " + hzrds.get(j).getRows().toString());
            System.out.println("----");
        }



        updateView();
    }

    public boolean bool(char c){
        if (c=='0'){
            return false;
        }
        return true;
    }


    public boolean isTag(char c){
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c == '>')
                || (c == ',');
    }

    public boolean shouldCollect(){
        return shouldCollect;
    }

}
