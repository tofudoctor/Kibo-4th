package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.util.Log;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.android.gs.MessageType;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.objdetect.QRCodeDetector;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    private static final String TAG = "NeverCaresyoU";

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
        Log.i(TAG, "QRcode message" + map.get(data));
        return map.get(data);
    }

    private Point getPoint(int num) {
        // go to shoot target (num)
        return new Point(0, 0, 0);
    }

    private Quaternion getQuaternion(int num) {
        // give the quaternion that astrobee can face the target (num)
        return new Quaternion(0, 0, 0, 0);
    }

    private void moveToGoal(String endMes) {
        // move to goal and report mission completion
    }

    @Override
    protected void runPlan1(){
        String endMes = "";
        boolean scanned = false;
        List<Long> remain = new LinkedList<>();
        List<Integer> active = new LinkedList<>();
        api.startMission();
        remain = api.getTimeRemaining();
        api.moveTo(new Point(10.4f, -10, 5.17f));
        

        while(remain.get(1) > 90000) {
            active = api.getActiveTargets();
            int length = active.size();
            
            if ((! scanned) && length == 1) {
                scanned = true;
                endMes = scanQRcode();
            }

            for (int i = 0 ; i < length && remain.get(0) > 60000 && remain.get(1) > 90000 ; i++) { 
                p = getPoint(i);
                first = new Point(p.getX(), p.getY(), 5.17);
                q = getQuaternion(i);
                api.moveTo(first, q, true);
                api.moveTo(p, q, true);
                api.laserControl(true);
                api.takeTargetSnapshot(i);
                remain = api.getTimeRemaining();
                api.moveTo(first, q, true);
            }

            if (remain.get(1) > 90000) break;

            remain = api.getTimeRemaining();
        }

        if (! scanned) {
            endMes = scanQRcode();
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
