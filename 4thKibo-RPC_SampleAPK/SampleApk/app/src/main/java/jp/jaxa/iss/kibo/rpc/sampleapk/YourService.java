package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.util.Log;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import org.opencv.core.Mat;

import org.opencv.core.MatOfPoint;
import org.opencv.objdetect.QRCodeDetector;


/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    private static final String TAG = "abcde";
    private float down = 5.25f;
    private Quaternion q;
    private Point p;
    private String QRmes;
    private Long MissiontimeRemaining;
    private Map<Integer, Integer> value = new HashMap<Integer, Integer>();
    private Point[] P = {
            new Point(10.4f    , -10      , 4.4f),       // start(0)
            new Point(11.225f  , -9.923f  , 5.469f),     //v
            new Point(10.463f  , -9.173f  , 4.48f),      //v
            new Point(10.71f   , -7.75f   , 4.48f),      //v
            new Point(10.485f  , -6.615f  , 5.17f),      //v
            new Point(11.037f  , -7.902f  , 5.312f),     //v
            new Point(11.307f  , -9.038f  , 4.931f),     //v
            new Point(11.453, -8.552, 4.9f),          // QR code(7)
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

    private void nomove(){
        try{
            Long a = api.getTimeRemaining().get(0);
            if(a > 100) return;
            Thread.sleep(a);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }


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

        Map<Integer, String> nmap = new HashMap<>();
        nmap.put(0, "JEM");
        nmap.put(1,"COLUMBUS");
        nmap.put(2, "RACK1");
        nmap.put(3, "ASTROBEE");
        nmap.put(4, "INTBALL");
        nmap.put(5, "BLANK");

        MatOfPoint point = new MatOfPoint();
        QRCodeDetector detector = new QRCodeDetector();
        api.flashlightControlFront(0.05f);
        Mat subpicture = api.getMatNavCam().submat(400, 600, 300, 700);
        api.saveMatImage(subpicture, photo_name(1000000));

        String data = detector.detectAndDecode(subpicture, point);
        if(data.isEmpty()){
            Random r = new Random();
            int tmp = r.nextInt(6);
            data = nmap.get(tmp);
        }

        return map.get(data);
    }

    private boolean fastmove(int pre, int cur){
        // short cut

        q = quaternion[cur];
        Long a = api.getTimeRemaining().get(0);
        Long b = api.getTimeRemaining().get(1);
//        a = a - 1000L;
//        b = b - 1000L;

        if((pre == 1 && cur == 4) || (pre == 4 && cur == 1)){
            if(a >= 44000 || b >= 44000){
                p = P[cur];
                api.moveTo(p, q, false);
                api.laserControl(true);
                api.takeTargetSnapshot(cur);
                api.saveMatImage(api.getMatNavCam(), photo_name(cur));
                Log.i(TAG, "arrive " + Integer.toString(cur));
            }
            else{
                nomove();
            }
            return true;
        }
        else if((pre == 1 && cur == 5) || (pre == 5 && cur == 1)){
            if(a >= 36000 || b >= 36000){
                p = P[cur];
                api.moveTo(p, q, false);
                api.laserControl(true);
                api.takeTargetSnapshot(cur);
                api.saveMatImage(api.getMatNavCam(), photo_name(cur));
                Log.i(TAG, "arrive " + Integer.toString(cur));
            }
            else{
                nomove();
            }
            return true;
        }
        else if((pre == 1 && cur == 6) || (pre == 6 && cur == 1)){
            if(a >= 27000 || b >= 27000){
                p = P[cur];
                api.moveTo(p, q, false);
                api.laserControl(true);
                api.takeTargetSnapshot(cur);
                api.saveMatImage(api.getMatNavCam(), photo_name(cur));
                Log.i(TAG, "arrive " + Integer.toString(cur));
            }
            else{
                nomove();
            }
            return true;
        }
        else if((pre == 2 && cur == 6) || (pre == 6 && cur == 2)){
            if(a >= 36000 || b >= 36000){
                p = P[cur];
                api.moveTo(p, q, false);
                api.laserControl(true);
                api.takeTargetSnapshot(cur);
                api.saveMatImage(api.getMatNavCam(), photo_name(cur));
                Log.i(TAG, "arrive " + Integer.toString(cur));
            }
            else{
                nomove();
            }
            return true;
        }
        else if((pre == 4 && cur == 5) || (pre == 5 && cur == 4)){
            if(a >= 31000 || b >= 31000){
                p = P[cur];
                api.moveTo(p, q, false);
                api.laserControl(true);
                api.takeTargetSnapshot(cur);
                api.saveMatImage(api.getMatNavCam(), photo_name(cur));
                Log.i(TAG, "arrive " + Integer.toString(cur));
            }
            else{
                nomove();
            }
            return true;
        }
        else if((pre == 4 && cur == 6) || (pre == 6 && cur == 4)){
            if(a >= 40000 || b >= 40000){
                p = P[cur];
                api.moveTo(p, q, false);
                api.laserControl(true);
                api.takeTargetSnapshot(cur);
                api.saveMatImage(api.getMatNavCam(), photo_name(cur));
                Log.i(TAG, "arrive " + Integer.toString(cur));
            }
            else{
                nomove();
            }
            return true;
        }
        else if((pre == 5 && cur == 6) || (pre == 6 && cur == 5)){
            if(a >= 28000 || b >= 28000){
                p = P[cur];
                api.moveTo(p, q, false);
                api.laserControl(true);
                api.takeTargetSnapshot(cur);
                api.saveMatImage(api.getMatNavCam(), photo_name(cur));
                Log.i(TAG, "arrive " + Integer.toString(cur));
            }
            else{
                nomove();
            }
            return true;
        }

        return false;
    }

    private void gotoStart(){
        // move to QR z
        p = P[7];
        q = quaternion[7];
        api.moveTo(p, q, false);
        Log.i(TAG, "arrive QR");
        api.saveMatImage(api.getMatNavCam(), photo_name(103));
        QRmes = scanQRcode();

    }

    private boolean gotoTarget(int pre, int cur){ // T1 ~ T6

        if(fastmove(pre, cur)){
            return false;
        }
        if(moveArea(pre, cur)){
            nomove();
            return false;
        }
        Long a = api.getTimeRemaining().get(0);
        Long b = api.getTimeRemaining().get(1);
//        a = a - 1000L;
//        b = b - 1000L;
        Log.i(TAG, "actGT: " + a);

//        if(cur == 1 && a >= 25000 && b >= 25000) return false;
//        else if(cur == 2 && a >= 21000 && b >= 21000) return false;
//        else if(cur == 3 && a >= 27000 && b >= 27000) return false;
//        else if(cur == 4 && a >= 35000 && b >= 35000) return false;
//        else if(cur == 5 && a >= 21000 && b >= 21000) return false;
//        else if(cur == 6 && a >= 19000 && b >= 19000) return false;

        q = quaternion[cur];
        p = P[cur];
        api.moveTo(p, q, false);
        api.laserControl(true);
        api.takeTargetSnapshot(cur);
        api.saveMatImage(api.getMatNavCam(), photo_name(cur));
        Log.i(TAG, "arrive " + Integer.toString(cur));

        return true;
    }

    private void gotoGoal(){
        api.notifyGoingToGoal();
        if(api.getTimeRemaining().get(1) < 30000){
            api.reportMissionCompletion(QRmes);
        }
        else{
            p = P[17];
            api.moveTo(p, q, false);
            Log.i(TAG, "arrive goal z ");
            api.reportMissionCompletion(QRmes);

//            p = P[8];
//            api.moveTo(p, q, false);
//            Log.i(TAG, "arrive goal z ");
//            api.reportMissionCompletion(QRmes);
        }
    }

    private void goto_Goal(int pre){
        api.notifyGoingToGoal();
        Long b = api.getTimeRemaining().get(1);
//        b = b - 1000L;

        if(b >= 40000 && pre == 1){
            p = P[17];
            api.moveTo(p, q, false);
            Log.i(TAG, "arrive goal z ");
            api.reportMissionCompletion(QRmes);
        }
        else if(b >= 36000 && pre == 2){
            p = P[17];
            api.moveTo(p, q, false);
            Log.i(TAG, "arrive goal z ");
            api.reportMissionCompletion(QRmes);
        }
        else if(b >= 24000 && pre == 3){
            p = P[8];
            api.moveTo(p, q, false);
            Log.i(TAG, "arrive goal z ");
            api.reportMissionCompletion(QRmes);
        }
        else if(b >= 23000 && pre == 4){
            p = P[8];
            api.moveTo(p, q, false);
            Log.i(TAG, "arrive goal z ");
            api.reportMissionCompletion(QRmes);
        }
        else if(b >= 25000 && pre == 5){
            p = P[17];
            api.moveTo(p, q, false);
            Log.i(TAG, "arrive goal z ");
            api.reportMissionCompletion(QRmes);
        }
        else if(b >= 70000 && pre == 6){
            moveArea(pre, pre);
            p = P[17];
            api.moveTo(p, q, false);
            Log.i(TAG, "arrive goal z ");
            api.reportMissionCompletion(QRmes);
        }
        else{
            api.reportMissionCompletion(QRmes);
        }
    }

    private boolean moveArea(int pre, int cur){
        // move from target to midpoint 1(p[11]) or 2(p[16))

        q = quaternion[cur];
        Long a = api.getTimeRemaining().get(0);
        Long b = api.getTimeRemaining().get(1);
//        a = a - 1000L;
//        b = b - 1000L;
        Log.i(TAG, "actmA: " + a);
                ;
        if(pre <= 2 && cur <= 2){
            if(pre == 1){
                if(cur == 2 && (a < 46000 || b < 46000)) {
                    Log.i(TAG, "actmA: " + a + "> 46000");
                    return true;
                }
            }
            else if(pre == 2){
                if(cur == 1 && (a < 46000 || b < 46000)) return true;
            }
            else{
                Log.i(TAG, "actmA: " + a + "< 46000");
                p = P[11];
                api.moveTo(p, q, false);
                Log.i(TAG, "arrive area 1");
            }


        }
        else if(pre > 2 && cur > 2){
            if(pre == 3){
                if(cur == 4 && (a < 62000 || b < 62000)) return true;
                else if(cur == 5 && (a < 48000 || b < 48000)) return true;
                else if(cur == 6 && (a < 46000 || b < 46000)) return true;
            }
            else if(pre == 4){
                if(cur == 3 && (a < 62000 || b < 62000)) return true;
            }
            else if(pre == 5){
                if(cur == 3 && (a < 48000 || b < 48000)) return true;
            }
            else if(pre == 6){
                if(cur == 3 && (a < 46000 || b < 46000)) return true;
            }
            else{
                p = P[16];
                api.moveTo(p, q, false);
                Log.i(TAG, "arrive area 2");
            }


        }
        else if(pre <= 2 && cur > 2){
            if(pre == 1){
                if(cur == 3 && (a < 76000 || b < 76000)) return true;
            }
            else if(pre == 2){
                if(cur == 3 && (a < 72000 || b < 72000)) return true;
                else if(cur == 4 && (a < 80000 || b < 80000)) return true;
                else if(cur == 5 && (a < 66000 || b < 66000)) return true;
            }
            else{
                p = P[11];
                api.moveTo(p, q, false);
                Log.i(TAG, "arrive area 1");

                p = P[16];
                api.moveTo(p, q, false);
                Log.i(TAG, "arrive area 2");
            }



        }
        else if(pre > 2 && cur <= 2) {
            if(pre == 3){
                if(cur == 1 && (a < 76000 || b < 76000)) return true;
                else if(cur == 2 && (a < 72000 || b < 72000)) return true;
            }
            else if(pre == 4){
                if(cur == 2 && (a < 80000 || b < 80000)) return true;
            }
            else if(pre == 5){
                if(cur == 2 && (a < 66000 || b < 66000)) return true;
            }
            else{
                p = P[16];
                api.moveTo(p, q, false);
                Log.i(TAG, "arrive area 2");


                p = P[11];
                api.moveTo(p, q, false);
                Log.i(TAG, "arrive area 1");
            }



        }

        return false;
    }



    @Override
    protected void runPlan1(){
        // init
        value.put(1, 30);
        value.put(2, 20);
        value.put(3, 40);
        value.put(4, 20);
        value.put(5, 30);
        value.put(6, 30);
        api.startMission();
        gotoStart();
        Log.i(TAG, "QR: " + QRmes);
        int pre_target = 3, cur_target = 3, phase = 1;

//        // to 4
//        cur_target = 4;
//        q = quaternion[0];
//        p = P[cur_target];
//        api.moveTo(p, q, false);
//        api.laserControl(true);
//        api.takeTargetSnapshot(cur_target);
//
//        cur_target = 1;
//        q = quaternion[0];
//        p = P[cur_target];
//        api.moveTo(p, q, false);
//        api.laserControl(true);
//        api.takeTargetSnapshot(cur_target);
//
//        cur_target = 4;
//        p = P[cur_target];
//        for(int i = 0;i < 7;i++){
//            q = quaternion[i];
//            api.moveTo(p, q, false);
//        }



        while(true) {
            List<Integer> targets_unsort = api.getActiveTargets();
            List<Integer> targets = new ArrayList<>(); // new ArrayList<>();

            // sort target by value(score)
            if(targets_unsort.size() == 2){
                if(value.get(targets_unsort.get(0)) < value.get(targets_unsort.get(1))){
                    targets.add(targets_unsort.get(1));
                    targets.add(targets_unsort.get(0));
                }
                else{
                    targets.add(targets_unsort.get(0));
                    targets.add(targets_unsort.get(1));
                }
            }
            else{
                targets.add(targets_unsort.get(0));
            }



            if(phase == 1){  // pahse 1 is special
                cur_target = targets.get(0);
                if(targets.size() == 1){
                    q = quaternion[cur_target];
                    if(cur_target == 1 || cur_target == 2){
                        p = P[11];
                        api.moveTo(p, q, false);
                        Log.i(TAG, "arrive 11");
                    }
                    p = P[cur_target];
                    api.moveTo(p, q, false);
                    api.laserControl(true);
                    api.takeTargetSnapshot(cur_target);
                    api.saveMatImage(api.getMatNavCam(), photo_name(cur_target));
                    Log.i(TAG, "arrive " + Integer.toString(cur_target));

                    pre_target = cur_target;
                }
                else{
                    q = quaternion[cur_target];
                    if(cur_target == 1 || cur_target == 2){
                        p = P[11];
                        api.moveTo(p, q, false);
                        Log.i(TAG, "arrive 11");
                    }
                    p = P[cur_target];
                    api.moveTo(p, q, false);
                    api.laserControl(true);
                    api.takeTargetSnapshot(cur_target);
                    api.saveMatImage(api.getMatNavCam(), photo_name(cur_target));
                    Log.i(TAG, "arrive " + Integer.toString(cur_target));

                    pre_target = cur_target;
                    cur_target = targets.get(1);

                    if(gotoTarget(pre_target, cur_target)){
                        pre_target = cur_target;
                    }
                }
            }
            else{ // other phases
                for (int i = 0; i < targets.size(); i++) {

                    cur_target = targets.get(i);

                    if(gotoTarget(pre_target, cur_target)){
                        pre_target = cur_target;
                    }



                    // for break
                    // all time
                    if(targets.size() == 2){
                        Long a = api.getTimeRemaining().get(0), b = api.getTimeRemaining().get(1);;

                        if((pre_target == 1 && cur_target == 2) || pre_target == 2 || pre_target == 1){
                            if(a < 46000 || b < 46000){
                                if(b < 46000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 1 && cur_target == 3) || pre_target == 3 || pre_target == 1){
                            if(a < 76000 || b < 76000){
                                if(b < 76000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 1 && cur_target == 4) || pre_target == 4 || pre_target == 1){
                            if(a < 43000 || b < 43000){
                                if(b < 43000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 1 && cur_target == 5) || pre_target == 5 || pre_target == 1){
                            if(a < 35000 || b < 35000){
                                if(b < 35000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 1 && cur_target == 6) || pre_target == 6 || pre_target == 1){
                            if(a < 26000 || b < 26000){
                                if(b < 26000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 2 && cur_target == 3) || pre_target == 3 || pre_target == 2){
                            if(a < 72000 || b < 72000){
                                if(b < 72000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 2 && cur_target == 4) || pre_target == 4 || pre_target == 2){
                            if(a < 80000 || b < 80000){
                                if(b < 80000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 2 && cur_target == 5) || pre_target == 5 || pre_target == 2){
                            if(a < 66000 || b < 66000){
                                if(b < 66000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 2 && cur_target == 6) || pre_target == 6 || pre_target == 2){
                            if(a < 35000 || b < 35000){
                                if(b < 35000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 3 && cur_target == 4) || pre_target == 4 || pre_target == 3){
                            if(a < 62000 || b < 62000){
                                if(b < 62000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 3 && cur_target == 5) || pre_target == 5 || pre_target == 3){
                            if(a < 48000 || b < 48000){
                                if(b < 48000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 3 && cur_target == 6) || pre_target == 6 || pre_target == 3){
                            if(a < 46000 || b < 46000){
                                if(b < 46000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 4 && cur_target == 5) || pre_target == 5 || pre_target == 4){
                            if(a < 30000 || b < 30000){
                                if(b < 30000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 4 && cur_target == 6) || pre_target == 6 || pre_target == 4){
                            if(a < 39000 || b < 39000){
                                if(b < 39000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                        else if((pre_target == 5 && cur_target == 6) || pre_target == 6 || pre_target == 5){
                            if(a < 27000 || b < 27000){
                                if(b < 27000){
                                    goto_Goal(pre_target);
                                }
                                else{
                                    nomove();
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            phase++;

            // break while
            MissiontimeRemaining = api.getTimeRemaining().get(1);
            Log.i(TAG, "Mission: " + Long.toString(MissiontimeRemaining));


            Long a = api.getTimeRemaining().get(0), b = api.getTimeRemaining().get(1);;

            if((pre_target == 1 && cur_target == 2) || pre_target == 2 || pre_target == 1){
                if(a < 46000 || b < 46000){
                    if(b < 46000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 1 && cur_target == 3) || pre_target == 3 || pre_target == 1){
                if(a < 76000 || b < 76000){
                    if(b < 76000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 1 && cur_target == 4) || pre_target == 4 || pre_target == 1){
                if(a < 43000 || b < 43000){
                    if(b < 43000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 1 && cur_target == 5) || pre_target == 5 || pre_target == 1){
                if(a < 35000 || b < 35000){
                    if(b < 35000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 1 && cur_target == 6) || pre_target == 6 || pre_target == 1){
                if(a < 26000 || b < 26000){
                    if(b < 26000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 2 && cur_target == 3) || pre_target == 3 || pre_target == 2){
                if(a < 72000 || b < 72000){
                    if(b < 72000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 2 && cur_target == 4) || pre_target == 4 || pre_target == 2){
                if(a < 80000 || b < 80000){
                    if(b < 80000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 2 && cur_target == 5) || pre_target == 5 || pre_target == 2){
                if(a < 66000 || b < 66000){
                    if(b < 66000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 2 && cur_target == 6) || pre_target == 6 || pre_target == 2){
                if(a < 35000 || b < 35000){
                    if(b < 35000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 3 && cur_target == 4) || pre_target == 4 || pre_target == 3){
                if(a < 62000 || b < 62000){
                    if(b < 62000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 3 && cur_target == 5) || pre_target == 5 || pre_target == 3){
                if(a < 48000 || b < 48000){
                    if(b < 48000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 3 && cur_target == 6) || pre_target == 6 || pre_target == 3){
                if(a < 46000 || b < 46000){
                    if(b < 46000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 4 && cur_target == 5) || pre_target == 5 || pre_target == 4){
                if(a < 30000 || b < 30000){
                    if(b < 30000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 4 && cur_target == 6) || pre_target == 6 || pre_target == 4){
                if(a < 39000 || b < 39000){
                    if(b < 39000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
            else if((pre_target == 5 && cur_target == 6) || pre_target == 6 || pre_target == 5){
                if(a < 27000 || b < 27000){
                    if(b < 27000){
                        goto_Goal(pre_target);
                    }
                    else{
                        nomove();
                        break;
                    }
                }
            }
        }

        // gotoGoal();
        goto_Goal(pre_target);
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
