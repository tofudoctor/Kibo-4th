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
    private float down = 5.25f;
    private Quaternion q;
    private Point p;
    private String QRmes;
    private Point[] P = {
            new Point(10.4f    , -10      , 4.4f),       // start(0)
            new Point(11.225f  , -9.923f  , 5.469f),     //v
            new Point(10.463f  , -9.173f  , 4.48f),      //v
            new Point(10.71f   , -7.75f   , 4.48f),      //v
            new Point(10.485f  , -6.615f  , 5.17f),      //v
            new Point(11.037f  , -7.902f  , 5.312f),     //v
            new Point(11.307f  , -9.038f  , 4.931f),     //v
            new Point(11.443f  , -8.5518   , 5),          // QR code(7)
            new Point(11.143f  , -6.7607f , 4.48f),      // goal(8)
            new Point(10.4f    , -10      , down),         // startz(9 = 0 + 9)
            new Point(11.225f  , -9.923f  , down),
            new Point(10.463f  , -9.173f  , down),         // T2 z
            new Point(10.71f   , -7.75f   , down),
            new Point(10.485f  , -6.615f  , down),
            new Point(11.037f  , -7.902f  , down),
            new Point(11.307f  , -9.038f  , down),
            new Point(11.443f  , -8.5518f  , down),        // QR codez(16 = 7 + 9)
            new Point(11.143f  , -6.7607f , down),         // goalz(17 = 8 + 9)

    };
    private Quaternion[] quaternion = {
            new Quaternion(0, 0, 0, 0),                   // start
            new Quaternion(0, 0, -0.707f, 0.707f),
            new Quaternion(0.5f, 0.5f, -0.5f, 0.5f),
            new Quaternion(0, 0.707f, 0, 0.707f),
            new Quaternion(0, 0, -1, 0),
            new Quaternion(-0.5f, -0.5f, -0.5f, 0.5f),
            new Quaternion(0, 0, 0, 1),
            new Quaternion(0.707f,0,-0.707f,0)            // QR code
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
        Mat subpicture = api.getMatNavCam().submat(400, 600, 300, 700);
        api.saveMatImage(subpicture, photo_name(1000000));

        String data = detector.detectAndDecode(subpicture, point);
        return map.get(data);
    }

    private void gotoStart(){
        p = P[7];
        q = quaternion[7];
        api.moveTo(p, q, false);
        Log.i(TAG, "arrive QR");
        api.flashlightControlFront(0.05f);
        api.saveMatImage(api.getMatNavCam(), photo_name(103));
        QRmes = scanQRcode();


        p = P[16];
        api.moveTo(p, q, false);
        Log.i(TAG, "back to QR z");
    }

    private void gotoTarget(int pre, int cur){ // T1 ~ T6
        q = quaternion[cur];


        if(pre <= 2 && cur <= 2){
            p = P[cur];
            api.moveTo(p, q, false);
            api.laserControl(true);
            api.takeTargetSnapshot(cur);
            api.saveMatImage(api.getMatNavCam(), photo_name(cur));
            Log.i(TAG, "arrive " + Integer.toString(cur));


            p = P[11];
            api.moveTo(p, q, false);
            Log.i(TAG, "arrivez2 " + Integer.toString(cur));
        }
        else if(pre > 2 && cur > 2){
            p = P[cur];
            api.moveTo(p, q, false);
            api.laserControl(true);
            api.takeTargetSnapshot(cur);
            api.saveMatImage(api.getMatNavCam(), photo_name(cur));
            Log.i(TAG, "arrive " + Integer.toString(cur));


            p = P[16];
            api.moveTo(p, q, false);
            Log.i(TAG, "arrivez2 " + Integer.toString(cur));
        }
        else{
            if(cur <= 2){
                p = P[11];
                api.moveTo(p, q, false);
                Log.i(TAG, "arrivez " + Integer.toString(cur));


                p = P[cur];
                api.moveTo(p, q, false);
                api.laserControl(true);
                api.takeTargetSnapshot(cur);
                api.saveMatImage(api.getMatNavCam(), photo_name(cur));
                Log.i(TAG, "arrive " + Integer.toString(cur));


                p = P[11];
                api.moveTo(p, q, false);
                Log.i(TAG, "arrivez2 " + Integer.toString(cur));
            }
            else{
                p = P[16];
                api.moveTo(p, q, false);
                Log.i(TAG, "arrivez " + Integer.toString(cur));

                p = P[cur];
                api.moveTo(p, q, false);
                api.laserControl(true);
                api.takeTargetSnapshot(cur);
                api.saveMatImage(api.getMatNavCam(), photo_name(cur));
                Log.i(TAG, "arrive " + Integer.toString(cur));


                p = P[16];
                api.moveTo(p, q, false);
                Log.i(TAG, "arrivez2 " + Integer.toString(cur));
            }
        }
    }

    private void gotoQR(){
        p = P[16];
        q = quaternion[7];


        api.moveTo(p, q, false);
        Log.i(TAG, "arrive QR z");

        p = P[7];
        api.moveTo(p, q, false);
        Log.i(TAG, "arrive QR");
        api.flashlightControlFront(0.05f);
        api.saveMatImage(api.getMatNavCam(), photo_name(103));
        QRmes = scanQRcode();


        p = P[16];
        api.moveTo(p, q, false);
        Log.i(TAG, "back to QR z");
    }

    private void gotoGoal(){
        api.notifyGoingToGoal();
        p = P[17];
        api.moveTo(p, q, false);
        Log.i(TAG, "arrive goal z ");


        p = P[8];
        api.moveTo(p, q, false);
        Log.i(TAG, "arrive goal");
        api.reportMissionCompletion(QRmes);

    }


    @Override
    protected void runPlan1(){
        api.startMission();


        gotoStart();
        int pre_target = 3, cur_target = 3;

        while(true) {
            List<Long> MissiontimeRemaining = api.getTimeRemaining();
            Log.i(TAG, "Mission: " + Long.toString(MissiontimeRemaining.get(1)));
            if (MissiontimeRemaining.get(1) < 60000) break;

            List<Integer> targets = api.getActiveTargets();

            for (int i = 0; i < targets.size(); i++) {
                List<Long> ActivetimeRemaining = api.getTimeRemaining();
                Log.i(TAG, "Active: " + Long.toString(ActivetimeRemaining.get(0)));
                if (ActivetimeRemaining.get(1) < 35000) break;

                cur_target = targets.get(i);

                gotoTarget(pre_target, cur_target);

                pre_target = cur_target;

            }
        }

        gotoGoal();
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
