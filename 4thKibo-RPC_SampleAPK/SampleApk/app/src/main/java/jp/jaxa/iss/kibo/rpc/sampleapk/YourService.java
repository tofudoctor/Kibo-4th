package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.util.Log;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import java.util.List;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.android.gs.MessageType;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import java.util.Map;
import java.util.HashMap;

import org.opencv.core.Mat;

import org.opencv.core.MatOfPoint;
import org.opencv.objdetect.QRCodeDetector;


/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    private static final String TAG = "KiboFiendFyre";

    int flag_obstacle = 0;//keeping track of each obstacle crossed

    //Keep In Zone Co - ordinates

    private double[][][] KIZ = {
            {
                    {10.3, 11.55}, {-10.2, -6}, {4.32, 5.57}
            },
            {
                    {9.5, 10.5}, {-10.5, -9.6}, {4.02, 4.8}
            }
    };

    //Keep Out Zone Co-ordinates
    private double[][][] KOZ = {
            {
                    // 000    001        010       011
                    {10.783, 11.071}, {-9.8899, -9.6929}, {4.8385, 5.0665}
            },
            {
                    // 100    101         110       111
                    {10.8652, 10.9628}, {-9.0734, -8.7314}, {4.3861, 4.6401}
            },
            {
                    {10.185, 11.665}, {-8.3826, -8.2826}, {4.1475, 4.6725}
            },
            {
                    {10.7955, 11.3525}, {-8.0635, -7.7305}, {5.1055, 5.1305}
            },
            {
                    {10.563, 10.709}, {-7.1449, -6.8099}, {4.6544, 4.8164}
            }

    };

    private String photo_name(int x) {
        String res = "";

        res += "photo";
        res += Integer.toString(x);
        res += ".png";

        return res;
    }


    private String scanQRcode() {
        Map<String, String> map = new HashMap<>();
        map.put("JEM", "STAY_AT_JEM");
        map.put("COLUMBUS", "GO_TO_COLUMBUS");
        map.put("RACK1", "CHECK_RACK_1");
        map.put("ASTROBEE", "I_AM_HERE");
        map.put("INTBALL", "LOOKING_FORWARD_TO_SEE_YOU");
        map.put("BLANK", "NO_PROBLEM");

        MatOfPoint point = new MatOfPoint();
        QRCodeDetector detector = new QRCodeDetector();

        String data = detector.detectAndDecode(api.getMatNavCam(), point);
        return map.get(data);
    }

    @Override
    protected void runPlan1(){
        api.startMission();
        //Astrobee 1 ft cube  = 0.3048 meter per side, half approx = 0.16 m, diagonally half length = 0.22 m
        //the below 7 arrays constitute the position of P1-1 to P2-3
        double[] posX = {9.815, 11.2746, 10.612, 10.71, 10.51, 11.114, 11.355, 11.369, 11.143}; //4th value 10.30 ....10.25 + 0.22 = 10.47
        double[] posY = {-9.806, -9.92284, -9.0709, -7.7, -6.7185, -7.9756, -8.9929, -8.5518, -6.7607};
        double[] posZ = {4.293, 5.2988, 4.48, 4.48, 5.1804, 5.3393, 4.7818, 4.48, 4.9654}; //2nd value and 6th value = 5.55 KIZ_lim = 5.6 - 0.22 = 5.42
        float[] quarX = {1, 0, 0, 0.5f, 0, 0, -0.5f};
        float[] quarY = {0, 0, 0, 0.5f, 0.707f, 0, -0.5f};
        float[] quarZ = {0, -0.707f, -0.707f, -0.5f, 0, -1, -0.5f};
        float[] quarW = {0, 0.707f, 0.707f, 0.5f, 0.707f, 0, 0.5f};
        float down = 5.25f;
        Point[] P = {
                new Point(10.4f    , -10       , 4.4f),       // start(0)
                new Point(11.2746f , -9.92284f , 5.2988f),    //v
                new Point(10.612f  , -9.0709f  , 4.48f),      //v
                new Point(10.71f   , -7.7f     , 4.48f),      //v
                new Point(10.51f   , -6.7185f  , 5.1804f),    //v
                new Point(11.114f  , -7.9756f  , 5.3393f),    //v
                new Point(11.355f  , -8.9929f  , 4.7818f),    //v
                new Point(11.369f  , -8.5518   , 4.7818f),    // QR code(7)
                new Point(11.143f  , -6.7607f  , 4.48f),      // goal(8)
                new Point(10.4f    , -10       , down),         // startz(9 = 0 + 9)
                new Point(11.2746f , -9.92284f , down),
                new Point(10.612f  , -9.0709f  , down),
                new Point(10.71f   , -7.7f     , down),
                new Point(10.51f   , -6.7185f  , down),
                new Point(11.114f  , -7.9756f  , down),
                new Point(11.355f  , -8.9929f  , down),
                new Point(11.369f  , -8.5518   , down),         // QR codez(16 = 7 + 9)
                new Point(11.143f  , -6.7607f  , down),         // goalz(17 = 8 + 9)

        };
        Quaternion[] quaternion = {
                new Quaternion(0, 0, 0, 0),                   // start
                new Quaternion(0, 0, -0.707f, 0.707f),
                new Quaternion(0.5f, 0.5f, -0.5f, 0.5f),
                new Quaternion(0, 0.707f, 0, 0.707f),
                new Quaternion(0, 0, -1, 0),
                new Quaternion(-0.5f, -0.5f, -0.5f, 0.5f),
                new Quaternion(0, 0, 0, 1),
                new Quaternion(0.707f,0,-0.707f,0)            // QR code
        };

        // T1(?) = P7 + Q5 (small)
        // T2 = P1 + Q3
        // T3(?) = P2 (crash)
        // T4 = P3 + Q5
        // T5 = P4 + Q6 (not in middle)
        // T6 = P5 + Q0 (opposite)
        // QRcode = P6 + Q3(left) || Q4(opposite)




        Quaternion q = quaternion[0];
        Point p = P[0];

        api.moveTo(P[9], q, true);
        Log.i(TAG, "arrive start z");

        // QRcode
        p = P[16];
        q = quaternion[7];


        api.moveTo(p, q, true);
        Log.i(TAG, "arrive QR z");

        p = P[7];
        api.moveTo(p, q, true);
        Log.i(TAG, "arrive QR");
        api.flashlightControlFront(0.05f);
        api.saveMatImage(api.getMatNavCam(), photo_name(103));
        String mes = scanQRcode();


        p = P[16];
        api.moveTo(p, q, true);
        Log.i(TAG, "back to QR z");

//        for(int i = 1;i <= 8;i++){
//            q = quaternion[i];
//
//            if(i <= 2){
//                p = P[11];
//                api.moveTo(p, q, true);
//                Log.i(TAG, "arrivez " + Integer.toString(i));
//
//
//                p = P[i];
//                api.moveTo(p, q, true);
//                api.saveMatImage(api.getMatNavCam(), photo_name(i));
//                Log.i(TAG, "arrive " + Integer.toString(i));
//
//
//                p = P[11];
//                api.moveTo(p, q, true);
//                Log.i(TAG, "arrivez2 " + Integer.toString(i));
//            }
//            else if(i == 8 || i == 4){
//                p = P[13];
//                api.moveTo(p, q, true);
//                Log.i(TAG, "arrivez " + Integer.toString(i));
//
//
//                p = P[i];
//                api.moveTo(p, q, true);
//                api.saveMatImage(api.getMatNavCam(), photo_name(i));
//                Log.i(TAG, "arrive " + Integer.toString(i));
//
//
//                p = P[13];
//                api.moveTo(p, q, true);
//                Log.i(TAG, "arrivez2 " + Integer.toString(i));
//            }
//            else{
//                p = P[16];
//                api.moveTo(p, q, true);
//                Log.i(TAG, "arrivez " + Integer.toString(i));
//
//                p = P[i];
//                api.moveTo(p, q, true);
//                api.saveMatImage(api.getMatNavCam(), photo_name(i));
//                Log.i(TAG, "arrive " + Integer.toString(i));
//
//
//                p = P[16];
//                api.moveTo(p, q, true);
//                Log.i(TAG, "arrivez2 " + Integer.toString(i));
//            }
//
//
//        }


        api.notifyGoingToGoal();
        p = P[13];
        api.moveTo(p, q, true);
        Log.i(TAG, "arrivez " + Integer.toString(8));


        p = P[8];
        api.moveTo(p, q, true);
        Log.i(TAG, "arrive goal");
        api.reportMissionCompletion(mes);


        Log.i(TAG, "mission complete");



    }

    @Override
    protected void runPlan2(){
        // write here your plan 2
    }

    @Override
    protected void runPlan3(){
        // write here your plan 3
    }



}
