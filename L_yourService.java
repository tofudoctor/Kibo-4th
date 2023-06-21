package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.util.Log;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import java.util.HashMap;
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
    private static String endMes = "";

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
        Mat picture = api.getMatNavCam();

        String data = detector.detectAndDecode(picture, point);
        Log.i(TAG, "QRcode message" + map.get(data));
        return map.get(data);
    }

    private String photo_name(int x) {
        String res = "";

        res += "photo";
        res += Integer.toString(x);
        res += ".png";

        return res;
    }

    private void show_point_log(Point p) {
        Log.i(TAG, String.valueOf(p.getX()) + String.valueOf(p.getY()) + String.valueOf(p.getZ()));
    }


    @Override
    protected void runPlan1(){
        String position;
        api.startMission();
        double[] posP3 = new double[6]; //for storing the co-ordinates of P3
        //Astrobee 1 ft cube  = 0.3048 meter per side, half approx = 0.16 m, diagonally half length = 0.22 m
        //the below 7 arrays constitute the position of P1-1 to P2-3
        double[] posX = {9.815, 11.2746, 10.612, 10.71, 10.51, 10.764, 11.355, 11.369, 11.143}; //4th value 10.30 ....10.25 + 0.22 = 10.47
        double[] posY = {-9.806, -9.92284, -9.0709, -7.7, -6.7185, -7.9756, -8.9929, -8.5518, -6.7607};
        double[] posZ = {4.293, 5.2988, 4.48, 4.48, 5.1804, 5.3393, 4.7818, 4.48, 4.9654}; //2nd value and 6th value = 5.55 KIZ_lim = 5.6 - 0.22 = 5.42
        float[] quarX = {1, 0, 0, 0.5f, 0, 0, -0.5f};
        float[] quarY = {0, 0, 0, 0.5f, 0.707f, 0, -0.5f};
        float[] quarZ = {0, -0.707f, -0.707f, -0.5f, 0, -1, -0.5f};
        float[] quarW = {0, 0.707f, 0.707f, 0.5f, 0.707f, 0, 0.5f};
        Point point = new Point(10.4f, -10, 4.4f);
        Point pointz = new Point(10.4f, -10, 5.16f);
        Point[] P = {
                new Point(11.2746f , -9.92284f , 5.2988f),
                new Point(posX[2], posY[2], posZ[2]),
                new Point(posX[3], posY[3], posZ[3]),
                new Point(posX[4], posY[4], posZ[4]),
                new Point(posX[5], posY[5], posZ[5]),
                new Point(posX[6], posY[6], posZ[6]),
                new Point(posX[7], posY[7], posZ[7]),
                new Point(posX[1], posY[1], 5.30f),
                new Point(posX[2], posY[2], 5.30f),
                new Point(posX[3], posY[3], 5.30f),
                new Point(posX[4], posY[4], 5.30f),
                new Point(posX[5], posY[5], 5.30f),
                new Point(posX[6], posY[6], 5.30f),
                new Point(posX[7], posY[7], 5.30f),
                new Point(posX[8], posY[8], posZ[8]),
                new Point(posX[8], posY[8], 5.30f),
        };

        Quaternion[] quaternion = {
                new Quaternion(0, 0, -0.707f, 0.707f),
                new Quaternion(quarX[1], quarY[1], quarZ[1], quarW[1]),
                new Quaternion(quarX[2], quarY[2], quarZ[2], quarW[2]),
                new Quaternion(quarX[3], quarY[3], quarZ[3], quarW[3]),
                new Quaternion(quarX[4], quarY[4], quarZ[4], quarW[4]),
                new Quaternion(quarX[5], quarY[5], quarZ[5], quarW[5]),
                new Quaternion(quarX[6], quarY[6], quarZ[6], quarW[6])
        };
        Quaternion test = new Quaternion(0,-1,0,0);

        // T2 = P1 + Q3
        // T4 = P3 + Q5
        // T6 = P5 + Q0 (opposite)
        // QRcode = P6 + Q3(left) || Q4(opposite)
        // T3(?) = P7 + Q5 (small)
        // T5 = P4 + Q6 (not in middle)

        Log.i(TAG, "arrive start z");

        Point p;
        Quaternion q;
        // T1 :
        // P = 1
        // Q = (0, 0, -0.707f, 0.707f)

        p = P[1 + 7];
        q = quaternion[0];


        api.moveTo(p, q, true);
        show_point_log(p);
        Log.i(TAG, "arrive T1 z");

        p = P[0];
        api.moveTo(p, q, true);
        show_point_log(p);


        Log.i(TAG, "arrive T1");
        api.laserControl(true);
        api.flashlightControlFront(0.05f);

        // take active target snapshots
        api.takeTargetSnapshot(1);

        api.saveMatImage(api.getMatNavCam(), photo_name(1));

        p = P[1 + 7];
        api.moveTo(p, q, true);
        Log.i(TAG, "back to T1 z");

//        // T2 :
//        // P = 1
//        // Q = 3
//        // T2 = P1 + Q3
//        p = P[1 + 7];
//        q = quaternion[3];// new Quaternion(0,-1,0,0);
//
//        api.moveTo(p, q, true);
//        show_point_log(p);
//        Log.i(TAG, "arrive T2 z");
//
//        p = new Point(10.463, -9.173, posZ[2]);
//        api.moveTo(p, q, true);
//        show_point_log(p);
//        Log.i(TAG, "arrive T2");
//        api.laserControl(true);
//        api.flashlightControlFront(0.05f);
//
//        // take active target snapshots
//        api.takeTargetSnapshot(2);
//
//        api.laserControl(false);
//        api.saveMatImage(api.getMatNavCam(), photo_name(2));
//
//
//        p = new Point(p.getX(), p.getY(), 5.17);
//        api.moveTo(p, q, true);
//        show_point_log(p);
//        Log.i(TAG, "back to T2 z");

        // T6 :
        // P = 6
        // Q = (0, 0, 0, 1)
//        p = P[6 + 7];
//        q = new Quaternion(0, 0, 0, 1);// new Quaternion(1,0,0,0);
//
//
//        api.moveTo(p, q, true);
//        show_point_log(p);
//        Log.i(TAG, "arrive T6 z");
//
//        p = new Point(11.307, -9.018, 4.931);
//        api.moveTo(p, q, true);
//        show_point_log(p);
//        Log.i(TAG, "arrive T6");
//        api.laserControl(true);
//        api.flashlightControlFront(0.05f);
//
//        // take active target snapshots
//        api.takeTargetSnapshot(6);
//
//        api.saveMatImage(api.getMatNavCam(), photo_name(6));
//
//
//        p = P[6 + 7];
//        api.moveTo(p, q, true);
//        show_point_log(p);
//        Log.i(TAG, "back to T6 z");

        // QRcode :
        // P = 7
        // Q = (0.707f, 0, -0.707f, 0)
        p = P[6 + 7];
        q = new Quaternion(0.707f, 0, -0.707f, 0);
        // q = quaternion[4];// new Quaternion(0,-1,0,0);

        api.moveTo(p, q, true);
        show_point_log(p);

        p = new Point(11.453, -8.552, 4.48);
        api.moveTo(p, q, true);

        api.flashlightControlFront(0.05f);
        String endMes = scanQRcode();


        p = new Point(p.getX(), p.getY(), 5.30);
        api.moveTo(p, q, true);
        show_point_log(p);
        Log.i(TAG, "back to QR z");

        // T3 :
        // P = 7(z-axis = 5.30) -> 3
        // Q = (0, 0.707f, 0, 0.707f)
//        p = P[6 + 7];
//        q = new Quaternion(0, 0.707f, 0, 0.707f);
//
//        api.moveTo(p, q, true);
//        show_point_log(p);
//        Log.i(TAG, "arrive T3 z");
//
//        p = new Point(10.709, -7.706, 4.487);
//        api.moveTo(p, q, true);
//        show_point_log(p);
//        Log.i(TAG, "arrive T3");
//        api.laserControl(true);
//        api.flashlightControlFront(0.05f);
//
//        // take active target snapshots
//        api.takeTargetSnapshot(3);
//
//        api.laserControl(false);
//        api.saveMatImage(api.getMatNavCam(), photo_name(3));
//
//        p = P[6 + 7];
//        api.moveTo(p, q, true);
//        show_point_log(p);
//        Log.i(TAG, "back to T3 z");



        // T5 :
        // P = 4 + ( -0.35, 0, 0 )
        // Q = 6 (not in middle)
        p = P[6 + 7];
        q = quaternion[6];// new Quaternion(0,-1,0,0);


        api.moveTo(p, q, true);
        show_point_log(p);
        Log.i(TAG, "arrive T5 z");

        p = new Point(11.037, -7.902, 5.312);
        api.moveTo(p, q, true);
        show_point_log(p);


        Log.i(TAG, "arrive T5");
        api.laserControl(true);
        api.flashlightControlFront(0.05f);

        // take active target snapshots
        api.takeTargetSnapshot(5);

        api.saveMatImage(api.getMatNavCam(), photo_name(5));

        p = P[6 + 7];
        api.moveTo(p, q, true);
        Log.i(TAG, "back to T5 z");

        // T4 :
        // P = 4
        // Q = 5

        p = P[6 + 7];
        q = new Quaternion(0.707f, 0, -0.707f, 0);
        // q = quaternion[4];// new Quaternion(0,-1,0,0);

        api.moveTo(p, q, true);


        p = P[3 + 7];
        q = quaternion[5];// new Quaternion(0,0,-1,0);


        api.moveTo(p, q, true);
        show_point_log(p);
        Log.i(TAG, "arrive T4 z");

        p = new Point(10.485, -6.615, 5.17);
        api.moveTo(p, q, true);
        show_point_log(p);
        Log.i(TAG, "arrive T4");
        api.laserControl(true);
        api.flashlightControlFront(0.05f);

        // take active target snapshots
        api.takeTargetSnapshot(4);

        api.saveMatImage(api.getMatNavCam(), photo_name(4));

        p = P[3 + 7];
        api.moveTo(p, q, true);
        show_point_log(p);
        Log.i(TAG, "back to T4 z");


        api.notifyGoingToGoal();
        api.moveTo(P[15], test, true);
        Log.i(TAG, "arrive goal z");
        api.moveTo(P[14], test, true);
        Log.i(TAG, "arrive goal");

        api.reportMissionCompletion(endMes);
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
