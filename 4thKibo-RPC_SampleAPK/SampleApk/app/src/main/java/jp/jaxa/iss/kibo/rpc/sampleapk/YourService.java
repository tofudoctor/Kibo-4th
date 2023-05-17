package jp.jaxa.iss.kibo.rpc.sampleapk;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import java.util.List;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.android.gs.MessageType;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import org.opencv.core.Mat;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    private static final String TAG = "KiboFiendFyre";

    int flag_obstacle = 0;//keeping track of each obstacle crossed

    //Keep In Zone Co - ordinates

    private double KIZ[][][] = {
            {
                    {10.3, 11.55}, {-10.2, -6}, {4.32, 5.57}
            },
            {
                    {9.5, 10.5}, {-10.5, -9.6}, {4.02, 4.8}
            }
    };

    //Keep Out Zone Co-ordinates
    private double KOZ[][][] = {
            {
                    {10.783, 11.071}, {-9.8899, -9.6929}, {4.8385, 5.0665}
            },
            {
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


    @Override
    protected void runPlan1(){
        String position;
        api.startMission();
        double[] posP3 = new double[6]; //for storing the co-ordinates of P3
        //Astrobee 1 ft cube  = 0.3048 meter per side, half approx = 0.16 m, diagonally half length = 0.22 m
        //the below 7 arrays constitute the position of P1-1 to P2-3
        double[] posX = {9.815, 11.2746, 10.612, 10.71, 10.51, 11.114, 11.355, 11.369, 11.143}; //4th value 10.30 ....10.25 + 0.22 = 10.47
        double[] posY = {-9.806, -9.92284, -9.0709, -7.7, -6.7185, -7.9756, -8.9929, -8.5518, -6.7607};
        double[] posZ = {4.293, 5.2988, 4.48, 4.48, 5.1804, 5.3393, 4.7818, 4.48, 4.9654}; //2nd value and 6th value = 5.55 KIZ_lim = 5.6 - 0.22 = 5.42
        float[] quarX = {1, 0, 0, 0.5f, 0, 0, -0.5f};
        float[] quarY = {0, 0, 0, 0.5f, 0.707f, 0, -0.5f};
        float[] quarZ = {0, -0.707f, -0.707f, -0.5f, 0, -1, -0.5f};
        float[] quarW = {0, 0.707f, 0.707f, 0.5f, 0.707f, 0, 0.5f};
        Point point = new Point(10.4f, -10, 4.4f);
        Point firstPoint = new Point(posX[1], posY[0], posZ[0]);
        Point secondPoint = new Point(posX[1], posY[1], posZ[0]);
        Point thirdPoint = new Point(posX[1], posY[1], posZ[1]);
        Quaternion quaternion[] = {
                new Quaternion(quarX[0],quarY[0],quarZ[0],quarW[0]),
                new Quaternion(quarX[1],quarY[1],quarZ[1],quarW[1]),
                new Quaternion(quarX[2],quarY[2],quarZ[2],quarW[2]),
                new Quaternion(quarX[3],quarY[3],quarZ[3],quarW[3]),
                new Quaternion(quarX[4],quarY[4],quarZ[4],quarW[4]),
                new Quaternion(quarX[5],quarY[5],quarZ[5],quarW[5]),
                new Quaternion(quarX[6],quarY[6],quarZ[6],quarW[6])
        };

        Quaternion test = new Quaternion(0,-1,0,0);



        api.moveTo(point, test, true);
        api.moveTo(firstPoint, test, true);

        api.moveTo(secondPoint, test, true);
        api.moveTo(thirdPoint, test, true);

        api.saveMatImage(api.getMatNavCam(), "test.png");
        api.reportMissionCompletion("");

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
