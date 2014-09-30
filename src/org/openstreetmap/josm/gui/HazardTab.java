// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class HazardTab extends JPanel{
    private GSModel model;

    private HazardApplet hzrdDisp;
    private HazardDataDisp hzrdDataDisp;

    public JButton openHzrdFileBtn;
    public JButton getHzrdBtn;

    private JSpinner lThreshSpinner;
    private JSpinner hThreshSpinner;
    private JSpinner resSpinner;
    private JSpinner nbrSpinner;
    private JTextField fNameField;

    public HazardTab(GSModel model){
        this.model= model;
        setLayout(new BorderLayout());

        //-I. Hazard Objects Display...
        JPanel hzrdObjectsPanel= new JPanel(new BorderLayout());
        hzrdDisp= new HazardApplet(); hzrdDisp.init();
        hzrdDataDisp= new HazardDataDisp(hzrdDisp);
        //Add to panel...
        hzrdObjectsPanel.add(hzrdDisp, BorderLayout.CENTER);
        hzrdObjectsPanel.add(hzrdDataDisp, BorderLayout.EAST);


        //-II. Hazard Controls Panel...
        JPanel hazardControls= new JPanel(new GridLayout(5,2));

        Float[] lParams= {GSModel.lowThresh, 0f, 500f, 1f};
        Float[] hParams= {GSModel.highThresh, 0f, 500f, 1f};

        SpinnerNumberModel lowThreshModel= new SpinnerNumberModel(lParams[0],lParams[1],lParams[2],lParams[3]);
        SpinnerNumberModel highThreshModel= new SpinnerNumberModel(hParams[0],hParams[1],hParams[2],hParams[3]);

        SpinnerNumberModel resModel = new SpinnerNumberModel(GSModel.patchRes,1,200,1);
        SpinnerNumberModel neighborModel = new SpinnerNumberModel(GSModel.nbrRad,0,100,1);

        lThreshSpinner= new JSpinner(lowThreshModel);
        hThreshSpinner= new JSpinner(highThreshModel);
        resSpinner = new JSpinner(resModel);
        nbrSpinner = new JSpinner(neighborModel);

        //---A. File Name Panel...
        JPanel fNamePanel= new JPanel(new BorderLayout());
        fNameField= new JTextField(GSModel.fName);
        openHzrdFileBtn= new JButton("Open Test File");
        //Add to panel...
        fNamePanel.add(new JLabel("Hazard Cam File: "),BorderLayout.WEST);
        fNamePanel.add(fNameField, BorderLayout.CENTER);
        fNamePanel.add(openHzrdFileBtn,BorderLayout.EAST);

        getHzrdBtn= new JButton("Get Hazards");


        //Add to panel...
        hazardControls.add(new JLabel("Low Threshold (To Connect Edges): "));
        hazardControls.add(lThreshSpinner);
        hazardControls.add(new JLabel("High Threshold (To Detect Edges:) "));
        hazardControls.add(hThreshSpinner);
        hazardControls.add(new JLabel("Patch Resolution (# Pixels): "));
        hazardControls.add(resSpinner);
        hazardControls.add(new JLabel("Neighbor Radius: "));
        hazardControls.add(nbrSpinner);
        hazardControls.add(fNamePanel);
        hazardControls.add(getHzrdBtn);

        //Add to panel...
        add(hzrdObjectsPanel,BorderLayout.CENTER);
        add(hazardControls, BorderLayout.SOUTH);

    }

    public void drawHazards(HzrdGroup[] hzrdGroups){
        hzrdDisp.drawHazards(hzrdGroups);
    }

    public void setHzrdFileName(String name){
        fNameField.setText(name);
    }

    public Float getLThresh(){
        return (Float) lThreshSpinner.getValue();
    }

    public Float getHThresh(){
        return (Float) hThreshSpinner.getValue();
    }

    public Integer getPatchRes(){
        return (Integer) resSpinner.getValue();
    }

    public Integer getNbrRad(){
        return (Integer) nbrSpinner.getValue();
    }

    //only for simulations...
    public BufferedImage getBufferedHzrdImg() throws IOException{
        String fName= fNameField.getText();
        File imgFile= new File(fName);
        return ImageIO.read(imgFile);
    }

    public void setHazardData(HzrdGroup[] hazards) {
        hzrdDataDisp.setData(hazards);
    }

    public void setHzrdDispPxls(int hzrdImgWidth, int hzrdImgHeight) {
        hzrdDisp.setPxls(hzrdImgWidth, hzrdImgHeight);
    }

}
