// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class RecoveryTab extends JPanel{
    private GSModel model;

    public JButton selNodesBtn;
    public JButton openOsmBtn;



    public RecoveryTab(GSModel model,JPanel josmMap){
        this.model= model;
        setLayout(new BorderLayout());

        //-I. JOSM Map Display
        //-II. GPS Data Display...
        JPanel gpsDataDisp= new JPanel(new BorderLayout());

        JLabel gpsDataDispTitle= new JLabel("GPS Coordinates",JLabel.CENTER);
        gpsDataDispTitle.setFont(GSView.italic24Font);

        //---A. Data Table...
        JPanel gpsDataTable= new JPanel(new GridLayout(6,3));
        ((JComponent) gpsDataTable).setBorder(new EmptyBorder(10, 10, 10, 10));

        //Add all GPS object lat/long data to panel...
        gpsDataTable.add(new JLabel(""));
        gpsDataTable.add(GSView.customJLabel("Latitude", JLabel.CENTER, GSView.bold16Font));
        gpsDataTable.add(GSView.customJLabel("Longitude", JLabel.CENTER, GSView.bold16Font));

        for (GPSObject o: model.getObjects()){
            gpsDataTable.add(GSView.customJLabel2(o.name + ":", JLabel.RIGHT, GSView.bold16Font,o.osmNodeColor));
            gpsDataTable.add(o.latLabels.get(o.newLatLabelIdx()));
            gpsDataTable.add(o.lngLabels.get(o.newLngLabelIdx()));
        }
        //Add fake EMU data for now...
        gpsDataTable.add(GSView.customJLabel2("EMU #1:", JLabel.RIGHT, GSView.bold16Font, Color.blue));   gpsDataTable.add(new JLabel("---",JLabel.CENTER));  gpsDataTable.add(new JLabel("---",JLabel.CENTER));
        gpsDataTable.add(GSView.customJLabel2("EMU #2:", JLabel.RIGHT, GSView.bold16Font, Color.blue));   gpsDataTable.add(new JLabel("---",JLabel.CENTER));  gpsDataTable.add(new JLabel("---",JLabel.CENTER));
        gpsDataTable.add(GSView.customJLabel2("EMU #3:", JLabel.RIGHT, GSView.bold16Font, Color.blue));   gpsDataTable.add(new JLabel("---",JLabel.CENTER));  gpsDataTable.add(new JLabel("---",JLabel.CENTER));

        //-----1. Buttons Section
        JPanel btnsPanel= new JPanel(new GridLayout(2,1));
        openOsmBtn= new JButton("Open OSM");
        selNodesBtn= new JButton("Select Nodes");

        btnsPanel.add(openOsmBtn); btnsPanel.add(selNodesBtn);

        //Add to panel...
        gpsDataDisp.add(gpsDataDispTitle, BorderLayout.NORTH);
        gpsDataDisp.add(gpsDataTable, BorderLayout.CENTER);
        gpsDataDisp.add(btnsPanel, BorderLayout.SOUTH);

        //Add to panel...
        add(josmMap, BorderLayout.CENTER);
        add(gpsDataDisp, BorderLayout.EAST);

    }
}
