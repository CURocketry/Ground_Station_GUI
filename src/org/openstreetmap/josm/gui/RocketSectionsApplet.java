// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui;


/*
* Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
*   - Redistributions of source code must retain the above copyright
*     notice, this list of conditions and the following disclaimer.
*
*   - Redistributions in binary form must reproduce the above copyright
*     notice, this list of conditions and the following disclaimer in the
*     documentation and/or other materials provided with the distribution.
*
*   - Neither the name of Oracle or the names of its
*     contributors may be used to endorse or promote products derived
*     from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
* IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
* THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
* PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
* LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.JApplet;

/*
* This is like the FontDemo applet in volume 1, except that it
* uses the Java 2D APIs to define and render the graphics and text.
*/

public class RocketSectionsApplet extends JApplet{

 final static int maxCharHeight = 15;
 final static int minFontSize = 6;

 final static Color bg = Color.white;
 final static Color fg = Color.black;
 final static Color red = Color.red;
 final static Color blue = Color.blue;
 final static Color gray = new Color(255,0,0,25);
 final static Color white = Color.white;

 final static BasicStroke stroke = new BasicStroke(2.0f);
 final static BasicStroke wideStroke = new BasicStroke(8.0f);

 final static float dash1[] = {10.0f};
 final static BasicStroke dashed = new BasicStroke(1.0f,
                                                   BasicStroke.CAP_BUTT,
                                                   BasicStroke.JOIN_MITER,
                                                   10.0f, dash1, 0.0f);

 public int unitH;
 public int unitW;
 public int x;
 public int y;

 private GSModel model;
 public Color c;

 private GSView view;

 public int sepTestIdx=0;

 Dimension totalSize;
 FontMetrics fontMetrics;

 private boolean[] isSeparated= new boolean[8];

 @Override
 public void init() {
     //Initialize drawing colors
     setBackground(bg);
     setForeground(fg);
 }

 FontMetrics pickFont(Graphics2D g2,
                      String longString,
                      int xSpace) {
     boolean fontFits = false;
     Font font = g2.getFont();
     FontMetrics fontMetrics = g2.getFontMetrics();
     int size = font.getSize();
     String name = font.getName();
     int style = font.getStyle();

     while ( !fontFits ) {
         if ( (fontMetrics.getHeight() <= maxCharHeight)
              && (fontMetrics.stringWidth(longString) <= xSpace) ) {
             fontFits = true;
         }
         else {
             if ( size <= minFontSize ) {
                 fontFits = true;
             }
             else {
                 g2.setFont(font = new Font(name,
                                            style,
                                            --size));
                 fontMetrics = g2.getFontMetrics();
             }
         }
     }

     return fontMetrics;
 }

public void drawSeparated(boolean[] isSeparated){
    this.isSeparated= isSeparated;
    repaint();
}

 public void testSep(){
     if (sepTestIdx<isSeparated.length){
         isSeparated[sepTestIdx]= true;
         String[] sections= {"Booster","Sustainer","Recovery and Tracking","Payload","Nose Cone","EMU1","EMU2","EMU3"};
         LaunchTab.addLaunchMessage(sections[sepTestIdx++] + " has successfully separated...");
         repaint();
     }
 }


 public void drawLeftFin(Graphics2D g2){
     int x2Points[] = {x, x+unitW, x+unitW, x};
     int y2Points[] = {y, y, y-unitH, y};
     GeneralPath lFin = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                                            x2Points.length);
     lFin.moveTo (x2Points[0], y2Points[0]);
     for ( int index = 1; index < x2Points.length; index++ ) {
         lFin.lineTo(x2Points[index], y2Points[index]);
     }
     lFin.closePath();
     g2.setPaint(bg);
     g2.fill(lFin);
     g2.setPaint(c);
     g2.fill(lFin);
     g2.setPaint(fg);
     g2.draw(lFin);
 }

 public void drawRightFin(Graphics2D g2){
     int x2Points[] = {x, x+unitW, x, x};
     int y2Points[] = {y, y, y-unitH, y};
     GeneralPath lFin = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                                            x2Points.length);
     lFin.moveTo (x2Points[0], y2Points[0]);
     for ( int index = 1; index < x2Points.length; index++ ) {
         lFin.lineTo(x2Points[index], y2Points[index]);
     }
     lFin.closePath();
     g2.setPaint(bg);
     g2.fill(lFin);
     g2.setPaint(c);
     g2.fill(lFin);
     g2.setPaint(fg);
     g2.draw(lFin);
 }

 public void drawSection(Graphics2D g2, String name){
     Rectangle2D rect= new Rectangle2D.Double(x,y-2*unitH,2*unitW,2*unitH);
     g2.setPaint(bg);
     g2.fill(rect);
     g2.setPaint(c);
     g2.fill(rect);
     g2.setPaint(fg);
     g2.draw(rect);

     g2.drawString(name, x+unitW,y);
 }


 public void drawEMUs(Graphics2D g2){
     Color c1,c2,c3;
     c1 = isSeparated[GSModel.EMU1] ? gray:red;
     c2 = isSeparated[GSModel.EMU2] ? gray:red;
     c3 = isSeparated[GSModel.EMU3] ? gray:red;
     int w= unitW/2;
     int h= unitH;
     int d= h /4;
     int r= d /2;
     int s= r/2;
     int cx= x+ w/2;
     int cy= y-h/2-s-r;
     //draw rect outline...

     g2.setPaint(bg);
     g2.fill(new Rectangle2D.Double(x, y-3*h/2,w, h));
     g2.setPaint(fg);
     g2.draw(new Rectangle2D.Double(x, y-3*h/2,w, h));

     //draw EMUs indicators...
     g2.setPaint(c1);
     g2.fill(new Ellipse2D.Double(cx-r,cy-r,d,d));
     g2.setPaint(fg);
     g2.draw(new Ellipse2D.Double(cx-r,cy-r,d,d));
     cy= cy - d - s;
     g2.setPaint(c2);
     g2.fill(new Ellipse2D.Double(cx-r,cy-r,d,d));
     g2.setPaint(fg);
     g2.draw(new Ellipse2D.Double(cx-r,cy-r,d,d));
     cy= cy - d - s;
     g2.setPaint(c3);
     g2.fill(new Ellipse2D.Double(cx-r,cy-r,d,d));
     g2.setPaint(fg);
     g2.draw(new Ellipse2D.Double(cx-r,cy-r,d,d));

 }

 public void drawNoseCone(Graphics2D g2){
     int x2Points[] = {x, x+2*unitW, x+unitW, x};
     int y2Points[] = {y, y, y-unitH, y};
     GeneralPath lFin = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                                            x2Points.length);
     lFin.moveTo (x2Points[0], y2Points[0]);
     for ( int index = 1; index < x2Points.length; index++ ) {
         lFin.lineTo(x2Points[index], y2Points[index]);
     }
     lFin.closePath();
     g2.setPaint(bg);
     g2.fill(lFin);
     g2.setPaint(fg);
     g2.draw(lFin);
     g2.setPaint(c);
     g2.fill(lFin);
 }
 @Override
 public void paint(Graphics g) {
     //Setup...
     Graphics2D g2 = (Graphics2D) g;
     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

     fontMetrics = pickFont(g2, "Filled and Stroked GeneralPath",
             5*unitW);

     Dimension d = getSize();
     Math.min(2.1,12);
     unitH= d.height/10;
     unitW= d.width/6;


     x=0;
     y= d.height-unitH/2; //started from the bottom

     //Booster...
     c = (isSeparated[GSModel.BOOSTER]) ? gray:red;
     x=unitW;
     drawLeftFin(g2);
     x+=unitW;
     drawSection(g2,"Booster");
     x+=2*unitW;
     drawRightFin(g2);
     y-=2*unitH;

     //Sustainer...
     c = (isSeparated[GSModel.SUSTAINER]) ? gray:red;
     x=unitW;
     drawLeftFin(g2);
     x+=unitW;
     drawSection(g2,"Sustainer");
     x+=2*unitW;
     drawRightFin(g2);
     y-=2*unitH;

     //RANDT...
     c = (isSeparated[GSModel.RANDT]) ? gray:red;
     x=2*unitW;
     drawSection(g2,"Recovery & Tracking");
     y-=2*unitH;

     //Payload...
     c = (isSeparated[GSModel.PAYLOAD]) ? gray:red;
     x=2*unitW;
     drawSection(g2,"Payload");
     drawEMUs(g2);
     y-=2*unitH;

     //EMUS...


     //Nosecone...
     c = (isSeparated[GSModel.NOSECONE]) ? gray:red;
     drawNoseCone(g2);
 }

}

