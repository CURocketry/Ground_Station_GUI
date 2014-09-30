// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class HazardDataDisp extends JPanel{

    public HazardApplet applet;
    public Color[] oldColors;

    public final Color hover= new Color(181,230,255);
    public final Color polyFaded= new Color(50,50,50);
    public final Color polyHover= new Color(255,36,36);
    public final Color bg= new Color(238,238,238);

    public int nGroups;
    public JPanel MainHazards;
    public double[] mainArea= new double[5];
    public double[] mainDist= new double[5];
    public double[] mainWeight= new double[5];

    public HzrdGroup[] pGroups;
    public final JPanel[] mainPlaceholders= new JPanel[5];
    public JLabel[] mainTitles= new JLabel[5];
    public JLabel[][] mainLabels= new JLabel[5][3];
    private JTable table;
    private JScrollPane scrollPane;

    public HazardDataDisp(final HazardApplet applet){
        setLayout(new BorderLayout());
        this.applet= applet;
        JLabel title= new JLabel("Hazard Data", JLabel.CENTER);
        title.setFont(GSView.italic24Font);
        add(title,BorderLayout.NORTH);

        MainHazards= new JPanel(new GridLayout(5,1));
        add(MainHazards,BorderLayout.CENTER);

        //format mainPlaceholders...
        for (int i=0; i < mainPlaceholders.length; i++){
            //Create ith main object panel placeholder ...
            mainPlaceholders[i]=new JPanel(new BorderLayout());
            final JPanel p=mainPlaceholders[i];
            mainTitles[i]= new JLabel("Hazard "+(i+1), JLabel.CENTER);
            mainTitles[i].setFont(new Font("Arial", Font.BOLD, 18));
            p.add(mainTitles[i],BorderLayout.NORTH);

            final JPanel dataGrid= new JPanel(new GridLayout(2,3));
            JLabel areaLabel= new JLabel("Area (m^3)   ", JLabel.CENTER); areaLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            JLabel distLabel= new JLabel("Distance (m)   ", JLabel.CENTER); distLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            JLabel weightLabel= new JLabel("Weight (%)", JLabel.CENTER); weightLabel.setFont(new Font("Arial", Font.ITALIC, 14));

            dataGrid.add(areaLabel);
            dataGrid.add(distLabel);
            dataGrid.add(weightLabel);

            for (int j= 0; j < 3; j++){
                mainLabels[i][j]= new JLabel("", JLabel.CENTER);
            }
            dataGrid.add(mainLabels[i][0]); //area label
            dataGrid.add(mainLabels[i][1]); //dist label
            dataGrid.add(mainLabels[i][2]); //weight label
            p.add(dataGrid,BorderLayout.CENTER);

            mainPlaceholders[i].addMouseListener(new MouseListener() {
                @Override
                public void mouseExited(MouseEvent e) {
                    p.setBackground(bg);
                    dataGrid.setBackground(bg);

                    for (int i=0; i < oldColors.length; i++){
                        pGroups[i].c= oldColors[i];
                    }
                    applet.repaint();

                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    p.setBackground(hover);
                    dataGrid.setBackground(hover);

                    //find highlighted idx...
                    boolean found=false; int k=0; int hIdx= 0;
                    while (k < mainPlaceholders.length && !found){
                        if (mainPlaceholders[k]==p){
                            hIdx=k;
                            found=true;
                        }
                        k++;
                    }
                    oldColors= new Color[pGroups.length];
                    for (int i=0; i < pGroups.length; i++){
                        oldColors[i]= pGroups[i].c;
                        if (i==hIdx){
                            pGroups[i].c= polyHover;
                        }
                        else{
                            pGroups[i].c= polyFaded;
                        }

                    }
                    applet.repaint();

                }
                @Override
                public void mouseClicked(MouseEvent arg0) {
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    // TODO Auto-generated method stub

                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    // TODO Auto-generated method stub

                }
            });


            //System.out.println("R: " + dataGrid.getBackground().getRed() + " G: " + dataGrid.getBackground().getGreen() + " B: " + dataGrid.getBackground().getBlue() );

        }

        //Create the scroll pane and add the table to it.





        JTable table= new JTable();
        table.setPreferredScrollableViewportSize(new Dimension(300, 125));
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane,BorderLayout.SOUTH);

    }

    //PRECONDITION: poly is sorted by descending weight
    public void setData(HzrdGroup[] pGroups){
        this.pGroups= pGroups;

        MainHazards.removeAll();


        //Sort area groups (this should be done on the beaglebone (i.e., in hazard simulator), but is put here now for simplicity...
        Arrays.sort(pGroups);



        for (int i=0; i < Math.min(pGroups.length,5); i++){
                HzrdGroup g= pGroups[i];
                mainTitles[i].setForeground(g.c);
                mainLabels[i][0].setText(String.format("%.2f%n",g.area));
                mainLabels[i][1].setText(String.format("%.2f%n",g.dist));
                mainLabels[i][2].setText(String.format("%.2f%n",g.weight));
                System.out.println(g.weight);
                MainHazards.add(mainPlaceholders[i]);
        }
       // MainHazards.add(new JButton("TESTING..."));
        //panel.add(MainHazards,BorderLayout.CENTER);


        if (pGroups.length > 5){
            //Add remaining polies to table...

            String[] columnNames = {"Hazard #",
                    "Area (m^3)",
                    "Distance (m)",
                    "Weight (%)",};

            String[][] data = new String[pGroups.length][4];
            for (int j= 0; j < pGroups.length; j++){
                HzrdGroup g= pGroups[j];
                data[j][0] = "" + (j+1);
                data[j][1] = String.format("%.2f%n",g.area);
                data[j][2] = String.format("%.2f%n",g.dist);
                data[j][3] = String.format("%.2f%n",g.weight);
            }


            table = new JTable(data, columnNames);
            table.setPreferredScrollableViewportSize(new Dimension(280, 125));
            table.setFillsViewportHeight(true);
            scrollPane = new JScrollPane(table);
            remove(2);
            add(scrollPane,BorderLayout.SOUTH);

            //Add the scroll pane to this panel.
            //panel.remove(scrollPane);
            //panel.add(scrollPane,BorderLayout.SOUTH);


           // panel.add(table,BorderLayout.SOUTH);

        }
        validate();
        repaint();


    }



}
