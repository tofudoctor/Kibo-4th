package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.util.Log;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import java.lang.annotation.Target;
import java.sql.Time;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.android.gs.MessageType;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import tf2_msgs.LookupTransformAction;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.objdetect.QRCodeDetector;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    private static final String TAG = "NCU";

    private static final Point[] P = {
            new Point(11.225, -9.923, 5.469), // target 1
            new Point(10.463, -9.173, 4.48), // target 2
            new Point(10.71, -7.75, 4.48), // target 3
            new Point(10.485, -6.615, 5.17), // target 4
            new Point(11.037, -7.902, 5.312), // target 5
            new Point(11.307, -9.038, 4.931), // target 6
            new Point(11.453, -8.552, 4.48), // QRcode
            new Point(10.463, -9.173, 5.25f), // mid point (1, 2)
            new Point(10.51, -6.7185, 5.25f), // mid point (4, goal)
            new Point(11.453, -8.552, 5.25f), // mid point (3, 5, 6, QRcode)
            new Point(11.143, -6.7607, 4.9654), // goal point
            new Point(11.143, -6.7607, 5.25f), // goal point (z-axis = 5.30)
    };

    private static final Quaternion[] quaternion = {
            new Quaternion(0, 0, -0.707f, 0.707f), // Target 1
            new Quaternion(0.5f, 0.5f, -0.5f, 0.5f), // Target 2
            new Quaternion(0, 0.707f, 0, 0.707f), // Target 3
            new Quaternion(0, 0, -1, 0), // Target 4
            new Quaternion(-0.5f, -0.5f, -0.5f, 0.5f), // Target 5
            new Quaternion(0, 0, 0, 1), // Target 6
            new Quaternion(0.707f, 0, -0.707f, 0) // QRcode
    };

    //Keep In Zone Co - ordinates

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
        Mat picture = api.getMatNavCam();
        Mat subpicture = picture.submat(400, 650, 550, 800);
        api.saveMatImage(subpicture, "cropped.png");

        String data = detector.detectAndDecode(subpicture, point);
        return map.get(data);
    }

    private  String gotoQRcode() {
        Quaternion q = quaternion[6];
        api.moveTo(P[9], q, true);
        api.moveTo(P[6], q, true);

        api.flashlightControlFront(0.05f);

        api.saveMatImage(api.getMatNavCam(), "QRcode.png");
        String res = scanQRcode();
        api.moveTo(P[9], q, true);
        return res;
    }

    private Point getPoint(int num) {
        // go to shoot target (num)
        return P[num - 1];
    }

    private Quaternion getQuaternion(int num) {
        // give the quaternion that astrobee can face the target (num)
        return quaternion[num - 1];
    }

    private void moveToGoal(String endMes) {
        // move to goal and report mission completion
        Quaternion q = new Quaternion(0, 0, 0, 1);

        api.notifyGoingToGoal();
        api.moveTo(P[11], q, true);
        Log.i(TAG, "arrive goal z");
        api.moveTo(P[10], q, true);
        Log.i(TAG, "arrive goal");

        api.reportMissionCompletion(endMes);
        Log.i(TAG, "mission complete");
    }

    @Override
    protected void runPlan1(){
        // score:
        // 1 :30
        // 2 :20
        // 3 :40
        // 4 :20
        // 5 :30
        // 6 :30
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 30);
        map.put(2, 20);
        map.put(3, 40);
        map.put(4, 20);
        map.put(5, 30);
        map.put(6, 30);

        String endMes = "";
        boolean scanned = false;
        int area = 2;
        // 1 for target 1, 2
        // 2 for target 3, 5, 6
        // 3 for target 4
        List<Integer> active = new LinkedList<>();
        api.startMission();
        int phase = 1;
        int shootTime = 1;
        Point p = P[9];
        int target;
        int length = 0;
        Long phaseRemainTime = Long.valueOf(0);
        Quaternion q = new Quaternion(0, 0, 0, 1);
        api.moveTo(p, q, true);

        while(api.getTimeRemaining().get(1) > 60000 && shootTime < 4) {  // T2 to goal : 34080ms
            active = api.getActiveTargets();
            target = active.get(0);
            length = active.size();
            Long minus = phaseRemainTime - api.getTimeRemaining().get(0);
            Log.i(TAG, "phaseRemainTime: " + phaseRemainTime);
            Log.i(TAG, "nowPhaseRemainTime: " + api.getTimeRemaining().get(0));
            Log.i(TAG, "minus: " + minus.toString());

            if (minus > 0 ) {
                Log.i(TAG, "phaseRemainTime: " + phaseRemainTime);
                Log.i(TAG, "nowPhaseRemainTime: " + api.getTimeRemaining().get(0));
                continue;
            }



            phaseRemainTime = Long.valueOf(0);
            Log.i(TAG, "now target: " + active);
            // int first = active.get(0);

            if (length == 2) {
                if (active.get(0) == 4) {
                    target = active.get(1);
                }
                else if (active.get(1) == 4) {
                    target = active.get(0);
                }
                else target = map.get(active.get(0)) > map.get(active.get(1)) ? active.get(0) : active.get(1);
            }

            if ((! scanned) && target == 4 && phase == 3) {
                scanned = true;
                endMes = gotoQRcode();
            }

            Log.i(TAG, "shooting target: " + target);
//            if (length == 2) {
//                int first = active.get(0);
//                int second = active.get(1);
//
//                if (area == 1 && ((second == 1 || second == 2) || first == 4)) {
//                    active.clear();
//                    active.add(second);
//                    active.add(first);
//                }
//                else if (area == 2 && ((second == 3 || second == 5 || second == 6) || first == 4)) {
//                    active.clear();
//                    active.add(second);
//                    active.add(first);
//                }
//                else if (area == 3 && second == 4) {
//                    active.clear();
//                    active.add(second);
//                    active.add(first);
//                }
//            }

            if (target == 4 && area != 3) {
                // if (phase == 3) break;

                p = getPoint(9);
                area = 3;
            }
            else if (target == 1 || target == 2 && area != 1) {
                p = getPoint(8);
                area = 1;
            }
            else if ((target == 3 || target == 5 || target == 6) && area != 2) {
//                if (target == 5 && phase == 3) break;

                p = getPoint(10);
                area = 2;
            }

            api.moveTo(p, q, true);

                Log.i(TAG, "shooting target: " + target);

                p = getPoint(target);
                q = getQuaternion(target);
                api.moveTo(p, q, true);
                api.laserControl(true);
                Log.i(TAG, "target remain time :" + api.getTimeRemaining().get(0));
                Log.i(TAG, "activating targets :" + api.getActiveTargets());
                api.takeTargetSnapshot(target);
                api.saveMatImage(api.getMatNavCam(), shootTime + ".png");
                shootTime++;

                if (area == 1) {

                    p = getPoint(8);
                    api.moveTo(p, q, true);
                    Log.i(TAG, "back to 2 midpoint");


//                    if (i + 1 != length) {
//                        if (active.get(i + 1) == 4) {
//                            p = getPoint(9);
//                            api.moveTo(p, q, true);
//                            area = 3;
//                        }
//                        else if (active.get(i + 1) == 3 || active.get(i + 1) == 5 || active.get(i + 1) == 6) {
//                            p = getPoint(10);
//                            api.moveTo(p, q, true);
//                            area = 2;
//                        }
//                        else {
//                            area = 1;
//                        }
//                    }

                }
                else if (area == 2) {
                    p = getPoint(10);
                    api.moveTo(p, q, true);
                    Log.i(TAG, "back to 3, 5, 6 midpoint");
                    area = 2;

//                    if (i + 1 != length) {
//                        if (active.get(i + 1) == 4) {
//                            p = getPoint(9);
//                            api.moveTo(p, q, true);
//                            area = 3;
//                        }
//                        else if (active.get(i + 1) == 1 || active.get(i + 1) == 2) {
//                            p = getPoint(8);
//                            api.moveTo(p, q, true);
//                            area = 1;
//                        }
//                        else {
//                            area = 2;
//                        }
//                    }
                }
                else {
                    p = getPoint(9);
                    api.moveTo(p, q, true);
                    Log.i(TAG, "back to 4 midpoint");

//                    if (api.getTimeRemaining().get(1) >= 50000) {
//                        if (i + 1 != length) {
//                            if (active.get(i + 1) == 1 || active.get(i + 1) == 2) {
//                                p = getPoint(8);
//                                api.moveTo(p, q, true);
//                                area = 1;
//                            }
//                            else if (active.get(i + 1) == 3 || active.get(i + 1) == 5 || active.get(i + 1) == 6) {
//                                p = getPoint(10);
//                                api.moveTo(p, q, true);
//                                area = 2;
//                            }
//                            else {
//                                area = 3;
//                            }
//                        }
//                    }
//                    else break;

                }
//                if (api.getTimeRemaining().get(1) <= 60000 || length == 2) break;
            if (length == 2 && (! scanned) && phase != 3) {
                scanned = true;
                endMes = gotoQRcode();
                area = 2;
            }

            phaseRemainTime = api.getTimeRemaining().get(0);
            phase++;

        }

        Log.i(TAG, "time remain to goal: " + api.getTimeRemaining().toString());

        moveToGoal(endMes);
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
