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

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.QRCodeDetector;

import static org.opencv.core.CvType.CV_8UC4;

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
            new Point(11.453, -8.552, 5), // QRcode
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

    private static int area = 2, prevArea = 2;
    private static boolean moveArea = false;
    private static Long phaseRemainTime = Long.valueOf(0);
    private static String endMes = "";

    //Keep In Zone Co - ordinates

    private String scanQRcode() {
        Map<String, String> map = new HashMap<>();
        map.put("JEM", "STAY_AT_JEM");
        map.put("COLUMBUS", "GO_TO_COLUMBUS");
        map.put("RACK1", "CHECK_RACK_1");
        map.put("ASTROBEE", "I_AM_HERE");
        map.put("INTBALL", "LOOKING_FORWARD_TO_SEE_YOU");
        map.put("BLANK", "NO_PROBLEM");

        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "init fail");
            return "";
        }

        MatOfPoint point = new MatOfPoint();
        QRCodeDetector detector = new QRCodeDetector();
        Mat subpicture = api.getMatNavCam().submat(400, 600, 300, 700);
        api.saveMatImage(subpicture, "cropped.png");

        String data = detector.detectAndDecode(subpicture, point);
        return map.get(data);
    }

    private  String gotoQRcode() {
//        Long start = api.getTimeRemaining().get(1);

        api.flashlightControlFront(0.05f);

        String res = scanQRcode();


        return res;
    }

    private void shootTarget(int target) {
        Point p = P[5];
        Quaternion q = quaternion[0];

        if (target == 0) return;
        Long start = api.getTimeRemaining().get(1);

        boolean moveArea = false;

//        if (target == 4 && area != 3) {
//            // if (phase == 3) break;
//
//            p = getPoint(9);
//            moveArea = true;
//            prevArea = area;
//            area = 3;
//        }
        if (target == 1 || target == 2 && area != 1) {
            p = getPoint(8);
            prevArea = area;
            moveArea = true;
            area = 1;
        }
        else if ((target == 3 || target == 4 || target == 5 || target == 6) && area != 2) {
//                if (target == 5 && phase == 3) break;

            moveArea = true;
            p = getPoint(10);
            prevArea = area;
            area = 2;
        }

        if (moveArea) {
            api.moveTo(p, q, false);
            moveArea = false;
            Log.i(TAG, "moveArea time cost: " + (start - api.getTimeRemaining().get(1)));
        }

        p = getPoint(target);
        q = getQuaternion(target);
        api.moveTo(p, q, false);
        api.laserControl(true);
        phaseRemainTime = api.getTimeRemaining().get(0);
        api.takeTargetSnapshot(target);
//                api.saveMatImage(api.getMatNavCam(), shootTime + ".png");

        if (api.getTimeRemaining().get(1) < 30000) {
            moveToGoal(endMes);
            return;
        }

        if (area == 1) {

            p = getPoint(8);
            api.moveTo(p, q, false);
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
            api.moveTo(p, q, false);
            Log.i(TAG, "back to 3, 4, 5, 6 midpoint");

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
//        else {
//            p = getPoint(9);
//            api.moveTo(p, q, false);
//
////                    if (api.getTimeRemaining().get(1) >= 50000) {
////                        if (i + 1 != length) {
////                            if (active.get(i + 1) == 1 || active.get(i + 1) == 2) {
////                                p = getPoint(8);
////                                api.moveTo(p, q, true);
////                                area = 1;
////                            }
////                            else if (active.get(i + 1) == 3 || active.get(i + 1) == 5 || active.get(i + 1) == 6) {
////                                p = getPoint(10);
////                                api.moveTo(p, q, true);
////                                area = 2;
////                            }
////                            else {
////                                area = 3;
////                            }
////                        }
////                    }
////                    else break;
//
//        }
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
        // api.moveTo(P[11], q, falsse);
        api.moveTo(P[10], q, false);

        api.reportMissionCompletion(endMes);
    }

    @Override
    protected void runPlan1(){
        int pre_target = 3, cur_target = 3;
        // score:
        // 1 :30
        // 2 :20
        // 3 :40
        // 4 :20
        // 5 :30
        // 6 :30
        Map<Integer, Integer> points = new HashMap<>();
        points.put(1, 30);
        points.put(2, 20);
        points.put(3, 40);
        points.put(4, 20);
        points.put(5, 30);
        points.put(6, 30);

        Map<Integer, Integer> pointArea = new HashMap<>();
        pointArea.put(1, 1);
        pointArea.put(2, 1);
        pointArea.put(3, 2);
        pointArea.put(4, 2);
        pointArea.put(5, 2);
        pointArea.put(6, 2);

        Map<Integer, Integer> moveTime = new HashMap<>();
        moveTime.put(1, 25000);
        moveTime.put(2, 21000);
        moveTime.put(3, 27000);
        moveTime.put(4, 34000);
        moveTime.put(5, 23000);
        moveTime.put(6, 19000);



        // 1 for target 1, 2
        // 2 for target 3, 5, 6
        // 3 for target 4
        List<Integer> active = new LinkedList<>();
        api.startMission();
        int phase = 1;
        int shootTime = 1;
        Point p = P[6];
        int length = 0;

        Long startShooting, startMoveArea = new Long(0);
        Quaternion q = quaternion[6];
        Long start = api.getTimeRemaining().get(1);
        api.moveTo(p, q, false);
        endMes = gotoQRcode();
        Log.i(TAG, "scan QRcode time cost: " + (start - api.getTimeRemaining().get(1)));

        while(api.getTimeRemaining().get(1) > 10000) {  // T2 to goal : 34080ms

            Long minus = phaseRemainTime - api.getTimeRemaining().get(0);

            if (minus >= 0) {
                Log.i(TAG, "wait" + phase);
                continue;
            }

            active = api.getActiveTargets();

            length = active.size();

            int first, second;

            if (length == 2) {
                int costTime = 0;
                int isMoved = 0;
                int sameArea = 0;
                first = active.get(0);
                second = active.get(1);
                if (pointArea.get(first) != area) {
                    if (pointArea.get(first) == pointArea.get(second)) {
                        sameArea = 1;
                    }
                    else {
                        first = active.get(1);
                        second = active.get(0);
                    }

                    isMoved++;
                }

                if (pointArea.get(first) != pointArea.get(second)) isMoved++;

                if ((moveTime.get(first) * 2 + moveTime.get(second) + ((isMoved - 1) + sameArea) * 24000) < (moveTime.get(second) * 2 + moveTime.get(first) + isMoved * 24000)){
                    costTime = moveTime.get(first) * 2 + moveTime.get(second) + ((isMoved - 1) + sameArea) * 24000;
                }
                else {
                    costTime = moveTime.get(second) * 2 + moveTime.get(first) + isMoved * 24000;
                    first = active.get(1);
                    second = active.get(0);
                }
                Long misson_cost = new Long (costTime) - api.getTimeRemaining().get(1);
                Long phase_cost = new Long(costTime) - api.getTimeRemaining().get(0);
                if (costTime > api.getTimeRemaining().get(1)) {
                    break;
                }
                else if (costTime > api.getTimeRemaining().get(0)) {
                    first = points.get(active.get(0)) > points.get(active.get(1)) ? active.get(0) : active.get(1);
                    second = 0;

                    if (area != pointArea.get(first)) {
                        isMoved = 1;
                    }
                    else {
                        isMoved = 0;
                    }

                    if (isMoved * 24000 + moveTime.get(first) > api.getTimeRemaining().get(0)) {
                        continue;
                    }
                }
                else {
                }

        }
        else {
            first = active.get(0);
            second = 0;
        }

            Log.i(TAG, "shooting target: " + first);

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


//                if (api.getTimeRemaining().get(1) <= 60000 || length == 2) break;

            Long startshooting = api.getTimeRemaining().get(1);
            phaseRemainTime = api.getTimeRemaining().get(0);

            shootTarget(first);
            Log.i(TAG, "shoot target" + first + " time cost: " + (startshooting - api.getTimeRemaining().get(1)));

            startshooting = api.getTimeRemaining().get(1);
            shootTarget(second);
            Log.i(TAG, "shoot target" + second + " time cost: " + (startshooting - api.getTimeRemaining().get(1)));


            phase++;

        }


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
