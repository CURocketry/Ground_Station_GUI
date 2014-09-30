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
import java.util.ArrayList;

import javax.swing.JApplet;

/*
 * This is like the FontDemo applet in volume 1, except that it
 * uses the Java 2D APIs to define and render the graphics and text.
 */

public class HazardApplet extends JApplet {
    final static int maxCharHeight = 15;
    final static int minFontSize = 6;

    final static Color bg = Color.white;
    final static Color fg = Color.black;
    final static Color red = Color.red;
    final static Color white = Color.white;

    final static BasicStroke stroke = new BasicStroke(2.0f);
    final static BasicStroke wideStroke = new BasicStroke(8.0f);

    final static float dash1[] = {10.0f};
    final static BasicStroke dashed = new BasicStroke(1.0f,
                                                      BasicStroke.CAP_BUTT,
                                                      BasicStroke.JOIN_MITER,
                                                      10.0f, dash1, 0.0f);

    static ArrayList<Circle> circ= new ArrayList<Circle>();
    static ArrayList<Rect> rect= new ArrayList<Rect>();
    static ArrayList<Poly> poly= new ArrayList<Poly>();
    static HzrdGroup[] hazards= new HzrdGroup[0];

    //public static int wPxls;
    //public static int hPxls;

    public int wPxls;
    public int hPxls;


    static double ratioWH;
    static int W,H, CX, CY; //width, height, and center (x,y) of image display

    Dimension totalSize;
    FontMetrics fontMetrics;

    @Override
    public void init() {
        //Initialize default pixel size...
        setPxls(GSModel.hzrdImgWidth,GSModel.hzrdImgHeight);
        //Initialize drawing colors
        setBackground(bg);
        setForeground(fg);
    }

    public void setPxls(int wPxls, int hPxls){
        this.wPxls=wPxls;
        this.hPxls=hPxls;
        ratioWH=  (double) wPxls/hPxls;
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

    public Color randomColor(){
        //Randomly assign R,G, or B to vary...

        int cStart=50; int cEnd=255;
        int R,G,B;


        ArrayList<Integer> vals= new ArrayList<Integer>();
        vals.add(cStart); vals.add(cEnd); vals.add((int) ( Math.random()*(cEnd-cStart) + cStart ));

        int i= (int) Math.floor(Math.random()*3);
        R= vals.get(i);
        vals.remove(i);

        int j= (int) Math.floor(Math.random()*2);
        G= vals.get(j);
        vals.remove(j);

        B= vals.get(0);

        return new Color(R,G,B);

    }


    public void drawHazards(HzrdGroup[] hazards){

        /*
        //re-initialize...
        circ= new ArrayList<Circle>();
        rect= new ArrayList<Rect>();
        poly= new ArrayList<Poly>();
        */
        this.hazards= hazards;

        //assign random color to each element...
        for (HzrdGroup g: hazards){
            g.c= randomColor();
        }

        /*

        //int cIdx=0;
        for (PatchGroup p:  ){
            if (p.getXPxls().length==1){
                //only a single patch (start/end coords are same)...
                circ.add(new Circle(p.getXPxls()[0],p.getYPxls()[0],x(p.getPatchRes()/4)-x(0)));
            }else{
                poly.add(new Poly(p.getXPxls(),p.getYPxls(),randomColor()));

                if (cIdx >= colors.length)
                    cIdx=0;
                poly.add(new Poly(p.getXPxls(),p.getYPxls(),colors[cIdx++]));

            }

        }
*/

        validate();
        repaint();

    }

    public void drawRandomHazards(){

        //re-initialize...
        circ= new ArrayList<Circle>();
        rect= new ArrayList<Rect>();
        poly= new ArrayList<Poly>();

        //Random simulated data...


        //Random circ/rect/poly for now...
        int cx, cy, px,py,r,w,h,px1,px2,px3,px4,py1,py2,py3,py4;
        cx=     (int) (wPxls*Math.random());
        px=     (int) (wPxls*Math.random()) ;
        px1=   (int) (wPxls*Math.random());
        px2=    (int) (wPxls*Math.random());
        px3=    (int) (wPxls*Math.random());
        px4=    (int) (wPxls*Math.random());

        cy= (int) (hPxls*Math.random());
        py= (int) (hPxls*Math.random());
        py1= (int) (hPxls*Math.random());
                py2=(int) (hPxls*Math.random());
                py3=(int) (hPxls*Math.random());
                py4=(int) (hPxls*Math.random());

                r= Math.min(Math.min((int) (H/8*Math.random()), y(hPxls-cy)/2-(CX-W/2)),x(wPxls-cx)/2-(CY-H/2));
                w= (int) Math.min((W/4*Math.random()),x(wPxls-px)-(CX-W/2));
                h= (int) Math.min((H/4*Math.random()), y(hPxls-py)-(CY-H/2));
                circ.add(new Circle(cx,cy,r));


                rect.add(new Rect(px, py, w, h));
                //poly.add(new Poly(new int[]{px1, px2, px3, px4},new int[]{py1, py2, py3, py4}));



     repaint();

    }

    //Converts image pxl position to gui position...
    public int[] x(int[] xPxls){
        int l= xPxls.length;
        int[] x= new int[l];
        for (int i=0; i < l; i++){
            x[i]= (int) Math.min(Math.max((CX-W/2)+((double) xPxls[i])/wPxls*W,CX-W/2),CX+W/2);
        }
        return x;
    }

    public int[] y(int[] yPxls){
        int l= yPxls.length;
        int[] y = new int[l];
        for (int i=0; i < l; i++){
            y[i]= (int)  Math.min(Math.max((CY-H/2)+((double) yPxls[i])/hPxls*H,CY-H/2),CY+H/2);
        }
        return y;
    }


    public int x(int xPxl){

        return (int) Math.min(Math.max((CX-W/2)+((double) xPxl)/wPxls*W,CX-W/2),CX+W/2);
    }
    public int y(int yPxl){
        return (int)  Math.min(Math.max((CY-H/2)+((double) yPxl)/hPxls*H,CY-H/2),CY+H/2);
    }

    @Override
    public void paint(Graphics g) {
        //Setup...
        //System.out.println("DRAWING HAZARD");
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension d = getSize();
        W= (int) Math.min(d.width, ratioWH*d.height)*9/10;
        H=(int) (W/ratioWH);


        CX= d.width/2;
        CY= d.height/2;

        //clear background...
        g2.setPaint(bg);
        g.fillRect(0, 0, d.width, d.height);

        /*//Set up test objects...
        circ.add(new Ellipse2D.Double(CX-50,CY-50,100,100));
        rect.add(new Rectangle2D.Double(CX-20,CY-40,40,80));

        int x3Points[] = {x(0), x(0)+W/4, x(0)+3*W/16, x(0)+W/8};
        int y3Points[] = {y(0), y(0), y(0)+H/3, y(0)+H/3};
        GeneralPath poly1 = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                                                    x3Points.length);
        poly1.moveTo(x3Points[0], y3Points[0]);
        for ( int index = 1; index < x3Points.length; index++ ) {
            poly1.lineTo(x3Points[index], y3Points[index]);
        }
        poly1.closePath();
        poly.add(poly1);
        */

        //Draw objects...

        g2.setPaint(red);
        for (Circle c: circ){
            g2.fill(new Ellipse2D.Double(x(c.x),y(c.y),c.r,c.r));
        }


        g2.setPaint(Color.green);
        for (Rect r: rect){
            g2.fill(new Rectangle2D.Double(x(r.x),y(r.y),r.w,r.h));
        }

        //Color[] c= {Color.red, Color.orange, Color.yellow, Color.green, Color.blue, Color.cyan, Color.magenta, Color.pink};

        for (HzrdGroup group: hazards){
            /*
            //Random color...
            if (cIdx >= c.length)
                cIdx=0;
            g2.setPaint(c[cIdx++]);
            */
            g2.setPaint(group.c);

            int[] xPxls= group.getXPxls();
            int[] yPxls= group.getYPxls();

            GeneralPath poly1 = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                    xPxls.length);
            poly1.moveTo(x(xPxls[0]), y(yPxls[0]));
            for ( int index = 1; index < xPxls.length; index++ ) {
                poly1.lineTo(x(xPxls[index]), y(yPxls[index]));
            }
            poly1.closePath();
            g2.fill(poly1);
        }

        g2.setPaint(fg);
        //g2.draw3DRect(x(0), y(0), x(wPxls), y(hPxls),false);
        //Ellipse2D x= (new Ellipse2D.Double(CX-50,CY-50,100,100));

        /*
        int gridWidth = d.width / 6;
        int gridHeight = d.height / 2;

        fontMetrics = pickFont(g2, "Filled and Stroked GeneralPath",
                               gridWidth);

        Color fg3D = Color.lightGray;

        g2.setPaint(fg3D);
        g2.draw3DRect(0, 0, d.width - 1, d.height - 1, true);
        g2.draw3DRect(3, 3, d.width - 7, d.height - 7, false);
        g2.setPaint(fg);

        int x = 5;
        int y = 7;
        int rectWidth = gridWidth - 2*x;
        int stringY = gridHeight - 3 - fontMetrics.getDescent();
        int rectHeight = stringY - fontMetrics.getMaxAscent() - y - 2;

        // draw Line2D.Double
        g2.draw(new Line2D.Double(x, y+rectHeight-1, x + rectWidth, y));
        g2.drawString("Line2D", x, stringY);
        x += gridWidth;

        // draw Rectangle2D.Double
        g2.setStroke(stroke);
        g2.draw(new Rectangle2D.Double(x, y, rectWidth, rectHeight));
        g2.drawString("Rectangle2D", x, stringY);
        x += gridWidth;

        // draw  RoundRectangle2D.Double
        g2.setStroke(dashed);
        g2.draw(new RoundRectangle2D.Double(x, y, rectWidth,
                                            rectHeight, 10, 10));
        g2.drawString("RoundRectangle2D", x, stringY);
        x += gridWidth;

        // draw Arc2D.Double
        g2.setStroke(wideStroke);
        g2.draw(new Arc2D.Double(x, y, rectWidth, rectHeight, 90,
                                 135, Arc2D.OPEN));
        g2.drawString("Arc2D", x, stringY);
        x += gridWidth;

        // draw Ellipse2D.Double
        g2.setStroke(stroke);
        g2.draw(new Ellipse2D.Double(x, y, 20, 20));
        g2.drawString("Ellipse2D", x, stringY);
        x += gridWidth;

        // draw GeneralPath (polygon)
        int x1Points[] = {x, x+rectWidth, x, x+rectWidth};
        int y1Points[] = {y, y+rectHeight, y+rectHeight, y};
        GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                                              x1Points.length);
        polygon.moveTo(x1Points[0], y1Points[0]);
        for ( int index = 1; index < x1Points.length; index++ ) {
            polygon.lineTo(x1Points[index], y1Points[index]);
        };
        polygon.closePath();

        g2.draw(polygon);
        g2.drawString("GeneralPath", x, stringY);

        // NEW ROW
        x = 5;
        y += gridHeight;
        stringY += gridHeight;

        // draw GeneralPath (polyline)

        int x2Points[] = {x, x+rectWidth, x, x+rectWidth};
        int y2Points[] = {y, y+rectHeight, y+rectHeight, y};
        GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                                               x2Points.length);
        polyline.moveTo (x2Points[0], y2Points[0]);
        for ( int index = 1; index < x2Points.length; index++ ) {
            polyline.lineTo(x2Points[index], y2Points[index]);
        };

        g2.draw(polyline);
        g2.drawString("GeneralPath (open)", x, stringY);
        x += gridWidth;

        // fill Rectangle2D.Double (red)
        g2.setPaint(red);
        g2.fill(new Rectangle2D.Double(x, y, rectWidth, rectHeight));
        g2.setPaint(fg);
        g2.drawString("Filled Rectangle2D", x, stringY);
        x += gridWidth;

        // fill RoundRectangle2D.Double
        GradientPaint redtowhite = new GradientPaint(x,y,red,x+rectWidth, y,white);
        g2.setPaint(redtowhite);
        g2.fill(new RoundRectangle2D.Double(x, y, rectWidth,
                                            rectHeight, 10, 10));
        g2.setPaint(fg);
        g2.drawString("Filled RoundRectangle2D", x, stringY);
        x += gridWidth;

        // fill Arc2D
        g2.setPaint(red);
        g2.fill(new Arc2D.Double(x, y, rectWidth, rectHeight, 90,
                                 135, Arc2D.OPEN));
        g2.setPaint(fg);
        g2.drawString("Filled Arc2D", x, stringY);
        x += gridWidth;

        // fill Ellipse2D.Double
        redtowhite = new GradientPaint(x,y,red,x+rectWidth, y,white);
        g2.setPaint(redtowhite);
        g2.fill (new Ellipse2D.Double(x, y, rectWidth, rectHeight));
        g2.setPaint(fg);
        g2.drawString("Filled Ellipse2D", x, stringY);
        x += gridWidth;



        // fill and stroke GeneralPath
        int x3Points[] = {x, x+rectWidth, x, x+rectWidth};
        int y3Points[] = {y, y+rectHeight, y+rectHeight, y};
        GeneralPath filledPolygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                                                    x3Points.length);
        filledPolygon.moveTo(x3Points[0], y3Points[0]);
        for ( int index = 1; index < x3Points.length; index++ ) {
            filledPolygon.lineTo(x3Points[index], y3Points[index]);
        };
        filledPolygon.closePath();
        g2.setPaint(red);
        g2.fill(filledPolygon);
        g2.setPaint(fg);
        g2.draw(filledPolygon);
        g2.drawString("Filled and Stroked GeneralPath", x, stringY);
        */

        //Draw outer box...
        g2.setPaint(fg);
        g2.draw3DRect(x(0), y(0),W,H, true);
    }

    public class Circle{
        public int x, y, r;
        public Circle(int x,int y,int r){
            this.x=x;
            this.y=y;
            this.r=r;
        }
    }

    public class Poly{
        public int[] x, y;
        public Color c;
        public Poly(int[] x, int[] y, Color c){
            this.x=x;
            this.y=y;
            this.c=c;
        }

    }

    public class Rect{
        public int x,y,w,h;
        public Rect(int x, int y, int w, int h){
            this.x=x;
            this.y=y;
            this.w=w;
            this.h=h;
        }
    }

}
