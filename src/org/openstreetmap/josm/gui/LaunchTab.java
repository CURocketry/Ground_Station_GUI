// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

import org.math.plot.Plot3DPanel;

public class LaunchTab extends JPanel{

    private GSModel model;
    private Plot3DPanel plot = new Plot3DPanel();
    public RocketSectionsApplet rSectionsApplet;

    public JToggleButton runBtn;
    public JToggleButton enablePayload;
    public JButton sepTestBtn= new JButton("Get XBee Packet");

    public static JTextArea messagesText;


    public LaunchTab(GSModel model){
        this.model= model;

        setLayout(new GridLayout(1,2));

        //-I. Main Panel:

        //---A. Rocket Sections:
        rSectionsApplet= new RocketSectionsApplet(); rSectionsApplet.init();

        //---B. Controls and Plot:
        JPanel controlsAndPlot= new JPanel(new GridLayout(2,1));

        //-----1. Controls:
        JPanel controls= new JPanel(new GridLayout(1,2));

        //-------i. Left controls:
        JPanel leftControls= new JPanel(new GridLayout(2,1));
        ((JComponent) leftControls).setBorder(new EmptyBorder(5, 5, 5, 5));


        //---------a. Controls Buttons Panel:
        JPanel controlsBtns= new JPanel(new GridLayout(3,1));
        //Initialize Buttons...
        runBtn= new JToggleButton("RUN"); runBtn.setForeground(Color.green);
        enablePayload= new JToggleButton("Enable Payload Ejection");
        sepTestBtn= new JButton("Test Section Separation");
        //Add buttons to panel...
        controlsBtns.add(runBtn); controlsBtns.add(enablePayload); controlsBtns.add(sepTestBtn);

        //---------b. GPS Data Panel:
        JPanel gpsData= new JPanel(new GridLayout(0,1+model.launchTabObjects.length));
        //rrayList<JPanel> gpsDataCols= new ArrayList<JPanel>();
        JPanel rowNames= new JPanel(new GridLayout(5,1));
        rowNames.add(new JLabel(""));
        rowNames.add(new JLabel(" Altitude (ft):"));
        rowNames.add(new JLabel(" Velocity (ft/s):"));
        rowNames.add(new JLabel(" Latitude:"));
        rowNames.add(new JLabel(" Longitude:"));
        gpsData.add(rowNames);
        for (GPSObject o: model.launchTabObjects){
            JPanel col= new JPanel(new GridLayout(5,1));
            col.add(GSView.customJLabel2(o.name,JLabel.CENTER,GSView.bold16Font,o.osmNodeColor));
            col.add(o.altLabels.get(o.newAltLabelIdx()));
            col.add(new JLabel("...", JLabel.CENTER)); //dummy velocity label
            col.add(o.latLabels.get(o.newLatLabelIdx()));
            col.add(o.lngLabels.get(o.newLngLabelIdx()));
            gpsData.add(col);
        }

        //Add to Panel...
        leftControls.add(controlsBtns); leftControls.add(gpsData);


        //-------ii. Messages Panel:
        JPanel controlsMsgs= new JPanel(new BorderLayout());
        ((JComponent) controlsMsgs).setBorder(new EmptyBorder(5, 5, 5, 5));
        messagesText= new JTextArea();
        messagesText.setLineWrap(true);
        messagesText.setWrapStyleWord(true);
        messagesText.setEditable(false);
        //Add to Panel...
        controlsMsgs.add(new JLabel("Messages / Warnings",JLabel.CENTER),BorderLayout.NORTH);
        controlsMsgs.add(messagesText, BorderLayout.CENTER);

        //Add to Panel...
        controls.add(leftControls); controls.add(controlsMsgs);

        //-----2. Plot:
        //fix plot bounds...


        //Add to panel...
        controlsAndPlot.add(controls); controlsAndPlot.add(plot);

        //Add to panel...
        add(rSectionsApplet);
        add(controlsAndPlot);

    }

    public Plot3DPanel getPlot(){
        return plot;
    }

    public static void addLaunchMessage(String text){
        messagesText.setText(messagesText.getText() + "- " + text + System.getProperty("line.separator") + System.getProperty("line.separator"));
        messagesText.setCaretPosition(messagesText.getDocument().getLength()); //locks scroll at bottom

    }


}
