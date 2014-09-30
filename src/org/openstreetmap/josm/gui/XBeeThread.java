// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.gui;

import org.apache.log4j.PropertyConfigurator;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.zigbee.ZNetRxResponse;
import com.rapplogic.xbee.api.zigbee.ZNetTxRequest;
import com.rapplogic.xbee.api.zigbee.ZNetTxStatusResponse;
import com.rapplogic.xbee.util.ByteUtils;



public class XBeeThread extends Thread {

    private final int simRefreshRate= 500; // rate at which data is collected (ms, multiple of 10);
    private final int simRefreshFactor= 10; // relates to data collection rate (# entries skipped each loop in real time)
    private long delayTime= 1000; //default delay time (ms);
    private double latCur;
    private double lngCur;
    private double altCur;

    private GSApp controller;
    private GPSObject[] gpsObjects;

    private XBee xbee;
    private XBeeAddress64 addr64;
    private String data="";
    private boolean receiving;

    public XBeeThread(GSApp controller, GPSObject[] gpsObjects){
        this.controller= controller;
        this.gpsObjects= gpsObjects;

        if (!GSModel.isSim){
            PropertyConfigurator.configure("log4j.properties");
            xbee = new XBee();
            try {
                xbee.open("/dev/tty.usbserial-AD025FLG", 9600);
                addr64 = new XBeeAddress64(0, 0x13, 0xa2, 0, 0x40, 0x91, 0x79, 0xa7);
            } catch (XBeeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    public void send(String data){
        int[] payload= new int[data.length()];
        for (int i=0; i < data.length(); i++){
            payload[i]= data.charAt(i);
        }
        ZNetTxRequest request = new ZNetTxRequest(addr64, payload);

        try {
            // send a request and wait up to 10 seconds for the response
            ZNetTxStatusResponse response = (ZNetTxStatusResponse) xbee.sendSynchronous(request, 10000);
            if (response.isSuccess()) {
                // packet was delivered successfully
                System.out.println("Success!");

            } else {
                    // packet was not delivered
                    System.out.println("Packet was not delivered");
            }

        } catch (XBeeTimeoutException e) {
            System.out.println("THERE WAS AN ERROR");
            // no response was received in the allotted time
        } catch (XBeeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int sIdxNext;
        while(controller.shouldCollect()){
            //Run Button clicked --> collect data for each object...
            if (!GSModel.isSim){
                try {
                    XBeeResponse response = xbee.getResponse();
                    //System.out.println(response.getApiId().toString());
                    //System.out.println(response.getClass().toString());
                    if (response.getApiId() == ApiId.ZNET_RX_RESPONSE) {

                        ZNetRxResponse ioSample = (ZNetRxResponse)response;

                            String packet= ByteUtils.toString(ioSample.getData());
                            //System.out.println(packet);
                            if (packet.charAt(0)=='<'){
                                if (receiving){
                                    System.out.println("ERROR: new packet before last data stream ended");
                                }
                                receiving=true;
                            }

                            if (receiving){
                                //System.out.println(packet);
                                data+=packet;
                                if (packet.charAt(packet.length()-1)=='>'){
                                    //end of data stream reached--> return data and reset...
                                    System.out.println(data);
                                    controller.parseXBeeData(data);
                                    receiving=false;
                                    data="";

                                }
                            }

                    }
                } catch (XBeeException e) {
                    // we timed out without a response
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }else{
                for (GPSObject ob: gpsObjects){
                    if ((ob instanceof SimGPSObject)){
                        System.out.println("IS A SIM");
                        SimGPSObject o= (SimGPSObject) ob;
                        //Collect data from simulation...
                        o.lat[o.gIdx]= o.simLat[o.sIdx];
                        o.lng[o.gIdx]= o.simLng[o.sIdx];
                        o.alt[o.gIdx]= o.simAlt[o.sIdx];


                        //Find next delay time...
                        sIdxNext= o.gIdx*simRefreshFactor;
                        if (sIdxNext < o.simDataL){
                            delayTime= (long) (1000* (o.simTime[sIdxNext]-o.simTime[o.sIdx]));
                        }
                        //Increment sim index...
                        o.sIdx= sIdxNext;
                    }

                    //Increment GPS array index...
                    ob.gIdx++;
                }

                //request view update...
                controller.updateView();

                //Delay...
                try {
                    Thread.sleep(delayTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
