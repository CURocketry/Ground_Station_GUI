package org.openstreetmap.josm.gui;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


/**This class is used to simulate the hazard detection algorithm that will eventually be translated into python
 *  onto the beagle bone. It splits an image into a number of square patches (determined by patchRes), and marks each patch
 *  that contains a white pixel (getRGB()==-1), which represents an edge as determined through the Canny Edge Detector).
 *  In addition, it organizes the marked patches into "n" groups, all of which are returned by the "getHazardGroups" and later
 *  used to plot "n" polygons based on the midpoints of the patches within each group.
 *
 * @author Steven Dourmashkin
 *
 */
public class HazardSimulator{

    private BufferedImage image;

    public static int patchRes;     //The resolution of each patch (in pixels)
    public static int nNeighbors;   //The number of neighboring patches that could be included in one group of patches

    public static ArrayList<HzrdGroup> pGroups; //An array where each element is a group of marked patches

    /** The main hazard gathering function, which returns an Array of "PatchGroup"s,
     * a class that essentially stores the x and y coordinates of a given group of marked patches.
     */

    public HazardSimulator(BufferedImage image){
        this.image= image;
    }

    public HzrdGroup[] getHazardGroups() throws IOException {

        //---------------------------------------------//
        //-------------CANNY EDGE DETECTOR-------------//
        //---------------------------------------------//

        //Create the Canny Edge Detector
        CannyEdgeDetector detector = new CannyEdgeDetector();

        //Adjust its parameters as desired
        detector.setLowThreshold(GSModel.lowThresh);
        detector.setHighThreshold(GSModel.highThresh);

        //Apply it to an image
          detector.setSourceImage(image);
        detector.process();
        BufferedImage edges = detector.getEdgesImage(); //edges is the edge-detected image

      //---------------------------------------------//
      //-------Get Hazards as Polygon Objects--------//
      //---------------------------------------------//

        //Get pixel size of image...
        int  width = edges.getWidth();
        int height = edges.getHeight();

        //Set model pixels for now (will eventually be hardcoded)
        GSModel.hzrdImgHeight= height;
        GSModel.hzrdImgWidth= width;

        //Set patch resolution and # neighbors from GUI...
        patchRes=  GSModel.patchRes;
        nNeighbors= GSModel.nbrRad;
        int nPatchRows= (int) Math.ceil(width/patchRes); //# patch rows
        int nPatchCols= (int) Math.ceil(width/patchRes); //# patch columns

        //Create array lists, where each element is a HashMap corresponding to a group of marked (markedPatches)
        //or possible (possible) patches that could belong to the given group. The keys of the HashMap are the marked/possible
        //patch row number, and the elements corresponding to a key is an arraylist of columns corresponding to a given row.
        //This organization is useful later on, where the outsides of each row are passed as the polygon vertices.
        ArrayList<HashMap<Integer,ArrayList<Integer>>> markedPatches= new ArrayList<HashMap<Integer,ArrayList<Integer>>>();
        ArrayList<HashMap<Integer,ArrayList<Integer>>> possible= new ArrayList<HashMap<Integer,ArrayList<Integer>>>();


        int r1= 0; int c1=0; //row/column pixel of first pixel of patch
        int r2,c2,r1temp, c1temp; //row/column pixel of last pixel of patch


        boolean isMarked, isPossible;

        //For each row in patches...
        for (int rPatch=0; rPatch < nPatchRows; rPatch++){

            r1= rPatch*patchRes;    //get starting row pixel

            //For each column in patches...
            for (int cPatch=0; cPatch < nPatchCols; cPatch++){

                //Check if patch has edge marking (i.e., getRGB=-1, white)...
                isMarked=false;
                c1= cPatch*patchRes;
                r2= Math.min(r1+patchRes,height);
                c2= Math.min(c1+patchRes,width);

                //Check within patch (NOTE: This can be done more efficiently using matrix code)
                r1temp= r1;
                while (!isMarked && r1temp < r2){
                    c1temp=c1;
                    while (!isMarked && c1temp < c2){
                        if (edges.getRGB(c1temp, r1temp) == -1){
                            isMarked=true;
                        }
                        c1temp++;
                    }
                    r1temp++;
                }

                if (isMarked){
                //Patch is marked. First, check if its in the "possible" array
                //(i.e., if it should belong to a previously marked group)

                    isPossible= false;
                    for (int i=0; i < possible.size(); i++){
                        if ( possible.get(i).get(rPatch) != null){
                            if (possible.get(i).get(rPatch).contains(cPatch)){
                                //Patch had previously been marked as a possible -->
                                //Add it to the corresponding existing marked group...

                                isPossible=true;
                                //check if no row rPatch found.
                                if (!markedPatches.get(i).containsKey(rPatch)){
                                    //Patch roww not marked yet--> add new row...
                                    markedPatches.get(i).put(rPatch,new ArrayList<Integer>());
                                }
                                //Add patch column to array...
                                markedPatches.get(i).get(rPatch).add(cPatch); //add column to row key

                                //Add neighbors to possible...
                                HashMap<Integer, ArrayList<Integer>> h= possible.get(i);
                                for (int rp= Math.max(rPatch-nNeighbors,0); rp <= Math.min(rPatch+nNeighbors,nPatchRows); rp++){
                                    //Add new arraylist at row key if it doesn't exist...
                                    if (!h.containsKey(rp)){
                                        h.put(rp, new ArrayList<Integer>());
                                    }
                                    //Add columns to row...
                                    for (int cp= Math.max(cPatch-nNeighbors,0); cp < Math.min(cPatch+nNeighbors+1,nPatchCols); cp++){
                                        h.get(rp).add(cp);
                                    }
                                }
                            }
                        }
                    }


                    if (!isPossible){
                        //Patch not marked as possible... make new marked group and add...
                        HashMap<Integer,ArrayList<Integer>> hMatch= new HashMap<Integer,ArrayList<Integer>>();
                        ArrayList<Integer> c= new ArrayList<Integer>();
                        c.add(cPatch);
                        hMatch.put(rPatch, c);
                        markedPatches.add(hMatch);

                        //Add right/down neighbors to possible...
                        HashMap<Integer, ArrayList<Integer>> hPoss= new HashMap<Integer, ArrayList<Integer>>();
                        for (int rp= Math.max(rPatch-nNeighbors,0); rp <  Math.min(rPatch+nNeighbors,nPatchRows); rp++){
                            hPoss.put(rp, new ArrayList<Integer>());
                            for (int cp= Math.max(cPatch-nNeighbors,0); cp < Math.min(cPatch+nNeighbors+1,nPatchCols); cp++){
                                hPoss.get(rp).add(cp);
                            }
                        }

                        //Add HashMap hPoss (group of possible patches that could belong to corresonding matched group)
                        possible.add(hPoss);

                    }
                }
            }

        }


        //check if any possibles match (i.e., should combine group)...
        int r_h1;
        ArrayList<Integer> cols_h1,cols_h2;
        for (int i=0; i < possible.size(); i++){
            for (int j=0; j < possible.size(); j++){
                if (i!=j){
                    HashMap<Integer,ArrayList<Integer>> m1= markedPatches.get(i);
                    HashMap<Integer,ArrayList<Integer>> p2= possible.get(j);

                    Set<Integer> keys1= m1.keySet();
                    Set<Integer> keys2= p2.keySet();

                    Iterator<Integer> it1= keys1.iterator();

                    boolean match= false;
                    while (it1.hasNext() && !match){
                        r_h1= it1.next();
                        if (keys2.contains(r_h1)){
                            //they both contain same row, so check columns
                            cols_h1=m1.get(r_h1);
                            cols_h2=p2.get(r_h1);
                            int colsIdx_h1=0;
                            while (colsIdx_h1 < cols_h1.size() && !match){
                                if (cols_h2.contains(cols_h1.get(colsIdx_h1))){
                                    //WE HAVE A MATCH!
                                    match=true;

                                    //add hashmaps together...
                                    HashMap<Integer,ArrayList<Integer>> possCombinedMap= combineMaps(possible.get(i),p2);
                                    HashMap<Integer,ArrayList<Integer>> markedCombinedMap= combineMaps(m1,markedPatches.get(j));

                                    possible.remove(i); possible.add(i,possCombinedMap); possible.remove(j);
                                    markedPatches.remove(i); markedPatches.add(i,markedCombinedMap); markedPatches.remove(j);

                                    /*

                                    HashMap<Integer,ArrayList<Integer>> possCombinedMap= new HashMap<Integer,ArrayList<Integer>>();
                                    possCombinedMap.putAll(h1);
                                    possCombinedMap.putAll(h2);

                                    HashMap<Integer,ArrayList<Integer>> markedCombinedMap= new HashMap<Integer,ArrayList<Integer>>();
                                    markedCombinedMap.putAll(markedPatches.get(i));
                                    markedCombinedMap.putAll(markedPatches.get(j));

                                    possible.remove(i); possible.add(i,possCombinedMap); possible.remove(j);
                                    markedPatches.remove(i); markedPatches.add(i,markedCombinedMap); markedPatches.remove(j);

                                    */
                                }
                                colsIdx_h1++;
                            }

                        }
                    }
                }

            }

        }


        //Get Polygon data from each marked group...
        pGroups= new ArrayList<HzrdGroup>();

        HashMap<Integer,ArrayList<Integer>> marks;
        for (int i=0; i<markedPatches.size(); i++){
            marks= markedPatches.get(i); //get marked patch group...
            Set<Integer> keySet= marks.keySet();
            Integer[] keys= keySet.toArray(new Integer[keySet.size()]); //get rows
            Arrays.sort(keys);


            HzrdGroup p= new HzrdGroup(); //create new patch group to be sent to GUI
                                                    //this group only contains the outer patches of marked group
                                                    //2* since each patch gives top-right/bot-left coords. +3 b/c 1st elem recorded twice, and last row once
            int rCur=0;
            ArrayList<Integer> cCurs;
            int cMax=-1; int cMin=-1;

            //add top/bottom of maximums of each row (and calculate simulated area while we're at it)...
            //NOTE: assume only pxl area for now...
            int k;
            double area=0;
            double pxlArea= Math.pow(patchRes, 2);
            for (k=0; k < keys.length; k++){
                rCur= keys[k];
                cCurs= marks.get(rCur);
                area+= pxlArea* (max(cCurs)-min(cCurs)+1);
                //only add if column is different than the last or last row (to reduce packet size)...
                if (max(cCurs)!=cMax || k == keys.length-1){
                    cMax= max(cCurs);
                    //add center pixel...
                    p.addRow(rCur);
                    p.addCol(cMax);
                }

            }
            p.area=(int) area;
            double dist= 1; //don't worry about distance algorithm for now
            p.dist= dist;
            p.weight= area*dist; //don't worry about ratio for now

            //go back up and add minimum column centers for each row...
            cMin= cMax; //set min to last maximum...
            for(k=keys.length-1; k >= 0; k--){
                rCur= keys[k];
                cCurs= marks.get(rCur);
                //only add if column is different than the last (to reduce packet size)...
                if (min(cCurs)!=cMin || (k==0 && min(cCurs)!=max(cCurs))){ //if not in same row as previous or if first row and not first...
                    cMin= min(cCurs);
                    //add center pixel...
                    p.addRow(rCur);
                    p.addCol(cMin);
                }
            }

            pGroups.add(p);
        }

        //Normalize weights...
        double sum= 0;
        for (HzrdGroup g:pGroups)
            sum+=g.weight;
        for (HzrdGroup g:pGroups)
            g.weight= g.weight/sum * 100; //weight in percentage


        //add image to sample jframe...
        /*
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(edges)));
        frame.pack();
        frame.setVisible(true);
        */

        HzrdGroup[] pGroupsArray= pGroups.toArray(new HzrdGroup[0]);

        /*

        for (int i=0; i < pGroupsArray.length; i++){
            HzrdGroup g= pGroupsArray[i];

            System.out.println("----------");
            System.out.println("Cols: ");
            System.out.println(g.getCols().toString());
            System.out.println("rows: ");
            System.out.println(g.getRows().toString());

            System.out.println("Area: ");


            System.out.println(g.area);

            System.out.println("Dist: ");

            System.out.println(g.dist);

            System.out.println("Weight: ");
            System.out.println(g.weight);

        }

*/

        return pGroupsArray;


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

    public static int min(ArrayList<Integer> array){
        int x=(int) Double.POSITIVE_INFINITY;
        for (int i=0; i<array.size(); i++){
            if (array.get(i) <  x){
                x=array.get(i);
            }
        }
        return x;
    }



    public static int max(int[] array){
        int x=(int) Double.NEGATIVE_INFINITY;
        for (int i=0; i<array.length; i++){
            if (array[i]> x){
                x=array[i];
            }
        }
        return x;
    }

    public static int min(int[] array){
        int x=(int) Double.POSITIVE_INFINITY;
        for (int i=0; i<array.length; i++){
            if (array[i] <  x){
                x=array[i];
            }
        }
        return x;
    }


    public static HashMap<Integer, ArrayList<Integer>> combineMaps(HashMap<Integer, ArrayList<Integer>> map1, HashMap<Integer, ArrayList<Integer>> map2){
        HashMap<Integer, ArrayList<Integer>> map3 = new HashMap<Integer, ArrayList<Integer>>();
        map3.putAll(map1);
        for(Integer key : map2.keySet()) {
            ArrayList<Integer> list2 = map2.get(key);
            ArrayList<Integer> list3 = map3.get(key);
            if(list3 != null) {
                list3.addAll(list2);
            } else {
                map3.put(key,list2);
            }
        }
        return map3;
    }
}
