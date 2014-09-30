// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui;

import java.awt.Color;
import java.util.ArrayList;

public class HzrdGroup implements Comparable{


    //public double area= Math.random()*100;    // [m^3] (test)
    public double area;
    public double dist= 50;     // [m] (test)
    public double weight= Math.random()*100;   // [%] (test)

    public Color c;

    private ArrayList<Integer>  x = new ArrayList<Integer>();
    private ArrayList<Integer>  y = new ArrayList<Integer>();
    private ArrayList<Integer> cols = new ArrayList<Integer>();
    private ArrayList<Integer> rows = new ArrayList<Integer>();


    public HzrdGroup(){
    }

    public void addYPxl(int pxl){

        y.add(pxl);
    }

    public void addXPxl(int pxl){
        x.add(pxl);
    }

    public void addCol(int col) {
        cols.add(col);
    }

    public void addRow(int row) {
        rows.add(row);
    }

    public ArrayList<Integer> getCols(){
        return cols;
    }

    public ArrayList<Integer> getRows(){
        return rows;
    }


    public int[] getYPxls(){
        /*//Old code for getting centers...
        if (y==null){
            y= new int[r.size()];
            for (int i=0; i<r.size(); i++){
                y[i]= r.get(i)*patchRes - patchRes/2; //get center pxl of patch
            }
            System.out.println("Y: " + Arrays.toString(y));
            System.out.println("R: " + r.toString());
            System.out.println("X: " + Arrays.toString(x));
            System.out.println("C: " + c.toString());
            System.out.println("-----");
        }*/
        return convertIntegers(y);
    }

    public int[] getXPxls(){
        /*//Old code for getting centers...
        if (x==null){
            x= new int[c.size()];
            for (int i=0; i<c.size(); i++){
                x[i]= c.get(i)*patchRes + patchRes/2; //get center pxl of patch
            }
        }
        */
        return convertIntegers(x);

    }

    public static int[] convertIntegers(ArrayList<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    @Override
    public int compareTo(Object arg0) {
        double w1= weight;
        double w2= ((HzrdGroup)arg0).weight;
        if (w1 < w2){
            return 1;
        }else if (w1 > w2){
            return -1;
        }else
            return 0;
    }


    public static void getPxlOutline(HzrdGroup[] hazards) {
        boolean itsGoingDown;
        int btmRowIdx, btmRow;
        int xPxl, yPxl, cCur, rCur;
        int rNext,cNext;
        for (HzrdGroup h: hazards){

            btmRowIdx= maxIdx(h.rows);
            btmRow= max(h.rows);

            //add first top-right corner...
            cCur= h.cols.get(0);
            rCur= h.rows.get(0);
            h.addXPxl(Math.min((cCur+1)*GSModel.patchRes,GSModel.hzrdImgWidth));
            h.addYPxl(rCur*GSModel.patchRes);

            //scan outline down...
            for (int i=1; i <= btmRowIdx; i++){
                cNext= h.cols.get(i);
                rNext= h.rows.get(i);

                //add non-patch corner..
                h.addXPxl(Math.min((cCur+1)*GSModel.patchRes,GSModel.hzrdImgWidth));
                h.addYPxl(rNext*GSModel.patchRes);

                //add patch top-right corner..
                h.addXPxl(Math.min((cNext+1)*GSModel.patchRes,GSModel.hzrdImgWidth));
                h.addYPxl(rNext*GSModel.patchRes);

                cCur= cNext;
                rCur= rNext;
            }

            //add bottom-right corner...
            h.addXPxl(Math.min((cCur+1)*GSModel.patchRes,GSModel.hzrdImgWidth));
            h.addYPxl((rCur+1)*GSModel.patchRes);

            //add bottom-left corner...
            if ((btmRowIdx+1) < h.cols.size() && h.rows.get(btmRowIdx+1)==btmRow){
                //there's another patch to the left...
                btmRowIdx++;
            }
            cCur=h.cols.get(btmRowIdx);
            rCur= h.rows.get(btmRowIdx);
            h.addXPxl(cCur*GSModel.patchRes);
            h.addYPxl(Math.min((rCur+1)*GSModel.patchRes,GSModel.hzrdImgHeight));

            //scan outline up...
            for (int i=btmRowIdx+1; i < h.getRows().size(); i++){
                cNext= h.cols.get(i);
                rNext= h.rows.get(i);

                //add non-patch corner..
                h.addXPxl(cCur*GSModel.patchRes);
                h.addYPxl((rNext+1)*GSModel.patchRes); //(NOTE: won't ever be bigger than height, since moving up past btm row)

                //add patch top-right corner..
                h.addXPxl(cNext*GSModel.patchRes);
                h.addYPxl((rNext+1)*GSModel.patchRes);
                cCur= cNext;
                rCur= rNext;
            }

            //add top-left corner...
            h.addXPxl(cCur*GSModel.patchRes);
            h.addYPxl(rCur*GSModel.patchRes);

            //wrap back around to top-right corner...
            cCur= h.cols.get(0);
            rCur= h.rows.get(0);
            h.addXPxl(Math.min((cCur+1)*GSModel.patchRes,GSModel.hzrdImgHeight));
            h.addYPxl(rCur*GSModel.patchRes);

            System.out.println("COLS: " + h.cols.toString());
            System.out.println("ROWS: " + h.rows.toString());
            System.out.println("X: " + h.x.toString());
            System.out.println("Y: " + h.y.toString());




        }

    }


    public static int maxIdx(ArrayList<Integer> array){
        int x=(int) Double.NEGATIVE_INFINITY;
        int idx=-1;
        for (int i=0; i<array.size(); i++){
            if (array.get(i)> x){
                x=array.get(i);
                idx=i;
            }
        }
        return idx;
    }

    public static int max(ArrayList<Integer> array){
        int x=(int) Double.NEGATIVE_INFINITY;
        for (int i=0; i<array.size(); i++){
            if (array.get(i)> x){
                x=array.get(i);
            }
        }
        return x;
    }


}


