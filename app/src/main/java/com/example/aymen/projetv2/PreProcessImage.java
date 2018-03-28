package com.example.aymen.projetv2;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aymen on 27/03/2017.
 */
public class PreProcessImage {
    Mat kernel;
    Mat Mclose;
    Moments moments;
    private List<MatOfPoint> contours;
    double M00,M01,M10;
    int poX,poY;
    double mu20,mu02,mu11,mu00;
    double N,N1,A;
    private static final String TAG="PreProcessImage";
    Mat mRgba,resizeM,shape1,shape2,mBlur,MSegm,mHSL,mGray,Mthreshold;
    private List<Mat> HSLlsit;
   public Mat ApplyGaussianFilter(Mat paramMat)
    {
        Mat Mclose = new Mat();

        try
        {
            kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20.0D, 20.0D));
           Imgproc.medianBlur(paramMat, paramMat, 5);
            Imgproc.morphologyEx(paramMat, paramMat, Imgproc.MORPH_CLOSE, kernel);
            Imgproc.GaussianBlur(paramMat, Mclose, new Size(3, 3), 15);
            /*Log.d(TAG, "Pr1");
            contours=new ArrayList<MatOfPoint>();
            Log.d(TAG,"Pr2");
            moments=new Moments();
            Log.d(TAG,"Pr3");
            moments=Imgproc.moments(contours.get(0));
            Log.d(TAG,"Pr4");
            M00=moments.get_m00();
            Log.d(TAG,"Pr5");
            M01=moments.get_m01();
            Log.d(TAG,"Pr6");
            M10=moments.get_m10();
            Log.d(TAG,"Pr7");
            this.poX = (int) (this.M10 / this.M00);
            Log.d(TAG,"Pr8");
            this.poY= (int) (this.M01 / this.M00);
            Log.d(TAG,"Pr9");
            this.mu20 = this.moments.get_mu20();
            Log.d(TAG,"Pr10");
            this.mu02 = this.moments.get_mu02();
            Log.d(TAG,"Pr11");
            this.mu11 = this.moments.get_mu11();
            Log.d(TAG,"Pr12");
            this.mu00 = moments.get_m00();
            Log.d(TAG,"Pr13");
            N=2*(mu20+mu02-Math.sqrt(Math.pow(mu20-mu02,2)+4*Math.pow(mu11,2))/mu00);
            Log.d(TAG,"Pr13");
            N1=2*(mu20+mu02-Math.sqrt(Math.pow(mu20-mu02,2)+4*Math.pow(mu11,2))/mu00);
            Log.d(TAG,"Pr14");
            A=N/N1;
            Log.d(TAG,"Pr15");*/
            return paramMat;
        }
        catch (Exception localException)
        {
        }
        return paramMat;
    }
    public Mat Detect(Mat mat)
    {
        Log.i("lesion", "start IP 1");
        mRgba=mat;
        resizeM=new Mat(mRgba.width(),mRgba.height(),mRgba.type());
        Imgproc.resize(mRgba,resizeM,resizeM.size(),0.0D,0.0D,2);
        mRgba.copyTo(shape1);
        mRgba.copyTo(shape2);
        Imgproc.cvtColor(this.resizeM, this.resizeM, 1);
        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5.0D, 5.0D));
        Imgproc.medianBlur(this.resizeM, this.mBlur, 5);
        Imgproc.pyrMeanShiftFiltering(this.mBlur, this.MSegm, 20.0D, 30.0D);
        Imgproc.cvtColor(this.MSegm, this.mHSL, 3);
        Core.split(mHSL, HSLlsit);
        this.HSLlsit.get(1).convertTo(this.mGray, CvType.CV_8UC1);
        Imgproc.threshold(this.mGray, this.Mthreshold, 0.0D, 255.0D, 8);
        Imgproc.morphologyEx(this.Mthreshold, this.Mclose, 3, this.kernel, new Point(1.0D, 1.0D), 1);
        return  Mthreshold;
    }
   public Mat BitmapToMat(Bitmap paramBitmap)
    {
        Mat localMat = new Mat();
        Utils.bitmapToMat(paramBitmap, localMat);
        return localMat;
    }
    public Bitmap MattoBitmap(Mat paramMat)
    {
        Bitmap localBitmap = Bitmap.createBitmap(paramMat.cols(), paramMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(paramMat, localBitmap);
        return localBitmap;
    }
    public Mat ResizeImage(Mat paramMat)
    {
        Mat localMat = new Mat();
        try
        {
            Imgproc.resize(paramMat, localMat, new Size(640.0D, 480.0D));
            return localMat;
        }
        catch (Exception localException)
        {
        }
        return paramMat;
    }




}
