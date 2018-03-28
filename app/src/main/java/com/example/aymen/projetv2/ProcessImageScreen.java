package com.example.aymen.projetv2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.ml.CvSVM;
import org.opencv.ml.CvSVMParams;

import java.util.ArrayList;

public class ProcessImageScreen extends AppCompatActivity {
    ImageView resultImage;
    TextView textView;
    Button btn1, btn2;
    String imageName;
    private static final String TAG = "ProcessImageScreen";
    boolean featuresExtracted = false;
    PreProcessImage ppi;
    String[] GlobalFeaturesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_image_screen);
        resultImage = (ImageView) findViewById(R.id.resultImage);
        textView = (TextView) findViewById(R.id.ImageSourceUri);
        btn1 = (Button) findViewById(R.id.btnExtractFeatures);
        btn2 = (Button) findViewById(R.id.btnClassification);
        Log.d(TAG, "v");
        Bundle b = getIntent().getExtras();
        imageName = b.getString("ImageName");
        byte[] arrayOfByte =b.getByteArray("ImageData");
        Bitmap bm = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length);
        resultImage.setImageBitmap(bm);
        textView.setText("Image Name:" + imageName);
        Log.d(TAG, "v1");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!featuresExtracted) {
                    Bitmap bitmap = ((BitmapDrawable) resultImage.getDrawable()).getBitmap();
                    Mat mat = new PreProcessImage().BitmapToMat(bitmap);
                    FindAndStoreFeatures(mat);
                    featuresExtracted = true;
                    return;
                }
                Toast.makeText(ProcessImageScreen.this, "Features Already Extracted.", Toast.LENGTH_LONG).show();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if(featuresExtracted)
                    {
                        new SVMclassifierBackgroundTask().execute(new Void[0]);
                        return;
                    }
                    Toast.makeText(ProcessImageScreen.this, "Please Extract Features First.", Toast.LENGTH_LONG).show();
                    return;

                }
                catch (Exception e)
                {
                    Toast.makeText(ProcessImageScreen.this, "Error Calculating Diameter.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void FindAndStoreFeatures(Mat mat) {
        double d1 = 0.0D;
        int i = 0;
        double d2 = new Date().getTime();
        Mat localMat1 = new Mat();
        ArrayList localArrayList = new ArrayList();
        Imgproc.cvtColor(mat, localMat1, 6);
        Imgproc.findContours(localMat1, localArrayList, new Mat(), 0, 1);
        for (int j = 0; ; j++) {
            int k = localArrayList.size();
            if (j >= k) {
                Scalar localScalar = new Scalar(0.0D, 0.0D, 255.0D);
                Imgproc.drawContours(mat, localArrayList, i, localScalar, 2);
                MatOfPoint2f localMatOfPoint2f = new MatOfPoint2f();
                ((MatOfPoint) localArrayList.get(i)).convertTo(localMatOfPoint2f, CvType.CV_32FC2);
                RotatedRect localRotatedRect = Imgproc.fitEllipse(localMatOfPoint2f);
                double d3 = localRotatedRect.size.height;
                double d4 = localRotatedRect.size.width;
                Point localPoint = localRotatedRect.center;
                double d5 = localRotatedRect.angle;
                double d6 = Math.sqrt(4.0D * d1 / 3.141592653589793D);
                Core.circle(mat, localPoint, 5, new Scalar(0.0D, 0.0D, 0.0D));
                Core.ellipse(mat, localRotatedRect, new Scalar(0.0D, 255.0D, 0.0D));
                double d7 = Imgproc.arcLength(localMatOfPoint2f, true);
                Moments localMoments = Imgproc.moments((Mat) localArrayList.get(i));
                double d8 = Math.sqrt((localMoments.get_m20() - localMoments.get_m02()) * (localMoments.get_m20() - localMoments.get_m02()) + 4.0D * localMoments.get_m11() * localMoments.get_m11());
                double d9 = (d8 + (localMoments.get_m20() + localMoments.get_m02())) / (localMoments.get_m20() + localMoments.get_m02() - d8);
                Mat localMat2 = (Mat) localArrayList.get(i);
                MatOfDouble localMatOfDouble1 = new MatOfDouble();
                MatOfDouble localMatOfDouble2 = new MatOfDouble();
                Core.meanStdDev(localMat2, localMatOfDouble1, localMatOfDouble2);
                double d10 = localMatOfDouble1.toArray()[0];
                double d11 = localMatOfDouble2.toArray()[0];
                double d12 = Core.norm(localMat2, 2);
                double d13 = Core.norm(localMat2, 4);
                DecimalFormat localDecimalFormat = new DecimalFormat("##");
                this.GlobalFeaturesArray = new String[]{localDecimalFormat.format(d1), localDecimalFormat.format(d7), localDecimalFormat.format(d6), localDecimalFormat.format(d3), localDecimalFormat.format(d4), localDecimalFormat.format(d5), localDecimalFormat.format(d9), localDecimalFormat.format(d10), localDecimalFormat.format(d11), localDecimalFormat.format(d12), localDecimalFormat.format(d13)};
                Bitmap localBitmap = new PreProcessImage().MattoBitmap(mat);
                this.resultImage.setImageBitmap(localBitmap);
                double d14 = new Date().getTime() - d2;
                Toast.makeText(this, "Featuers Stored Successfullly in " + d14 + " ms", Toast.LENGTH_LONG).show();
                return;
            }
            double d15 = Imgproc.contourArea((Mat) localArrayList.get(j));
            if (d15 <= d1)
                continue;
            d1 = d15;
            i = j;

        }
    }
    private Mat CreateFeatureVectorForTraining()
    {
        Mat localMat = new Mat(70, 11, CvType.CV_32FC1);
        AssetManager localAssetManager = getAssets();
        while (true)
        {
            int i;
            int j;
            try
            {
                BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localAssetManager.open("mSkinDoctor_Features.txt")));
                i = 0;
                String str = localBufferedReader.readLine();
                if (str != null)
                    continue;
                localBufferedReader.close();
               // return localMat;
                String[] arrayOfString = str.split(" ");
                j = 0;
               // break ;
                double[] arrayOfDouble = new double[1];
                arrayOfDouble[0] = Integer.parseInt(arrayOfString[j]);
                localMat.put(i, j, arrayOfDouble);
                j++;
            }
            catch (Exception localException)
            {
                Toast.makeText(this, "Feature Vector Not Created", Toast.LENGTH_LONG).show();
                return localMat;
            }
            if (j <= 10)
                continue;
            i++;
        }

    }
    private void AddImageToGallery(Uri paramUri)
    {
        Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        localIntent.setData(paramUri);
        sendBroadcast(localIntent);
    }
    @SuppressLint({"SimpleDateFormat"})
    private File getResultImageFile()
    {
        File localFile = new File(Environment.getExternalStorageDirectory() + "/mSkinDoctor/mSkinResults");
        if ((!localFile.exists()) && (!localFile.mkdirs()))
            Log.e(this.TAG, "Failed To Create Storage Directory");
        String str = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(localFile.getPath() + File.separator + "mSkinDoctor_" + str + ".jpg");
    }

    public AlertDialog.Builder getDialogSaveDiscard(int paramInt)
    {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle("Confirmation");
        localBuilder.setMessage("Do you want to Save the Results?");
        localBuilder.setIcon(R.drawable.logo);
        localBuilder.setCancelable(true);
        localBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @SuppressLint({"SimpleDateFormat"})
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                File localFile = ProcessImageScreen.this.getResultImageFile();
                String str1 = localFile.getName();
                SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String str2 = localSimpleDateFormat.format(new Date());
                Scalar localScalar1 = new Scalar(255.0D, 255.0D, 255.0D);
                String  str3 = "Malignant Melanoma (Cancer)";
                Scalar localScalar2 = new Scalar(255.0D, 0.0D, 0.0D);
                while (true)
                {
                    Bitmap localBitmap1 = ((BitmapDrawable)ProcessImageScreen.this.resultImage.getDrawable()).getBitmap();
                    try
                    {
                        Mat localMat = new PreProcessImage().BitmapToMat(localBitmap1);
                        Core.putText(localMat, "Image: " + str1, new Point(10.0D, 395.0D), 1, 1.0D, localScalar1);
                        Core.putText(localMat, "Result: " + str3, new Point(10.0D, 410.0D), 1, 1.0D, localScalar2);
                        Core.putText(localMat, "Powered By: Aleem Technologies", new Point(10.0D, 425.0D), 1, 1.0D, localScalar1);
                        Core.putText(localMat, "Results Generated By: mSkin Doctor", new Point(10.0D, 440.0D), 1, 1.0D, localScalar1);
                        Core.putText(localMat, "Result Saved: " + str2, new Point(10.0D, 455.0D), 1, 1.0D, localScalar1);
                        Bitmap localBitmap2 = new PreProcessImage().MattoBitmap(localMat);
                        FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
                        localBitmap2.compress(Bitmap.CompressFormat.JPEG, 90, localFileOutputStream);
                        localFileOutputStream.flush();
                        localFileOutputStream.close();
                        ProcessImageScreen.this.getDialogToHome("Result Saved", "The Result has been stored in the history.").create().show();
                        Uri localUri = Uri.fromFile(localFile);
                        ProcessImageScreen.this.AddImageToGallery(localUri);
                        str3 = "Non-Malignant Melanoma (Non Cancer)";
                        localScalar2 = new Scalar(0.0D, 255.0D, 0.0D);
                    }
                    catch (Exception localException)
                    {
                    }
                }

            }
        });
        localBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                ProcessImageScreen.this.getDialogToHome("Result Discarded", "The Result has been discarded.").create().show();
            }
        });
        return localBuilder;
    }
    public AlertDialog.Builder getDialogToHome(String paramString1, String paramString2)
    {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle(paramString1);
        localBuilder.setMessage(paramString2);
        localBuilder.setIcon(R.drawable.logo);
        localBuilder.setCancelable(false);
        localBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent localIntent = new Intent(ProcessImageScreen.this, MainActivity.class);
                localIntent.setFlags(268468224);
                ProcessImageScreen.this.startActivity(localIntent);
                ProcessImageScreen.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });
        return localBuilder;
    }
    public AlertDialog.Builder getDialogOk()
    {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle("Process Canceled");
        localBuilder.setMessage("Classification Process stoped, Please Try Again.");
        localBuilder.setIcon(R.drawable.logo);
        localBuilder.setCancelable(false);
        localBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                new ProcessImageScreen.SVMclassifierBackgroundTask().execute(new Void[0]);
            }
        });
        return localBuilder;
    }
    private class SVMclassifierBackgroundTask extends AsyncTask<Void, Void, Void>
    {
        Date clockAfter;
        Date clockBefore;
        private ProgressDialog dialog;
        double endTime;
        int exception;
        double startTime;
        int testResult;
        double totalTime;
        private SVMclassifierBackgroundTask()
        {
        }


        @Override
        protected Void doInBackground(Void... params) {
            File localFile1 = new File(Environment.getExternalStorageDirectory() + "/mSkinDoctor/mSkinFiles");
            if (!localFile1.exists())
                localFile1.mkdirs();
            File localFile2 = new File(localFile1.getPath() + File.separator + "mSkinDoctor_SVM.txt");
            Mat localMat1 = ProcessImageScreen.this.CreateFeatureVectorForTraining();
            Mat localMat2 = new Mat(localMat1.rows(), 1, CvType.CV_32FC1);
            localMat2.rowRange(0, 44).setTo(new Scalar(1.0D));
            localMat2.rowRange(45, localMat1.rows()).setTo(new Scalar(0.0D));
            CvSVMParams localCvSVMParams = new CvSVMParams();
            localCvSVMParams.set_svm_type(100);
            localCvSVMParams.set_kernel_type(0);
            localCvSVMParams.set_C(10.0D);
            localCvSVMParams.set_degree(0.0D);
            localCvSVMParams.set_gamma(0.0D);
            localCvSVMParams.set_coef0(0.0D);
            localCvSVMParams.set_nu(0.5D);
            localCvSVMParams.set_p(0.0D);
            localCvSVMParams.set_term_crit(new TermCriteria(1, 10000000, 1.0E-006D));
            CvSVM localCvSVM = new CvSVM();
            try
            {
                String str = ProcessImageScreen.this.getSharedPreferences("mSkinDoctor_pref", 0).getString("TestTrain", "train");
                if (str.contains("train"))
                {
                    localCvSVM.train(localMat1, localMat2, new Mat(), new Mat(0, 0, CvType.CV_8UC1), localCvSVMParams);
                    localCvSVM.save(localFile2.toString());
                }
                while (true)
                {
                   Mat localMat3 = new Mat(1, 11, CvType.CV_32FC1);
                  int  i = 0;
                    if (i <= 10)
                        break;
                    this.testResult = (int)localCvSVM.predict(localMat3);
                    localCvSVM.load(localFile2.toString());
                }
            }
            catch (Exception localException)
            {
                while (true)
                {
                    Mat localMat3 = new Mat(1, 11, CvType.CV_32FC1);
                    int i=0;
                    if (this.dialog.isShowing())
                        this.dialog.dismiss();
                    this.exception = 1;
                    double[] arrayOfDouble = new double[1];
                    arrayOfDouble[0] = Integer.parseInt(ProcessImageScreen.this.GlobalFeaturesArray[i]);
                    localMat3.put(1, i, arrayOfDouble);
                    i++;
                }
            }
             return null;
        }

        protected void onPostExecute(Void paramVoid)
        {
            while (true)
            {
                AlertDialog.Builder localBuilder;
                try
                {
                    if (!this.dialog.isShowing())
                        continue;
                    this.dialog.dismiss();
                    if (this.exception != 1)
                        continue;
                    ProcessImageScreen.this.getDialogOk().create().show();
                    this.clockAfter = new Date();
                    this.endTime = this.clockAfter.getTime();
                    this.totalTime = (this.endTime - this.startTime);
                    localBuilder = new AlertDialog.Builder(ProcessImageScreen.this);
                    localBuilder.setTitle("Image Classified in " + this.totalTime + " ms");
                    if (this.testResult == 1)
                    {
                        localBuilder.setMessage("The input Image is Malignant Melanoma(Cancer).");
                        localBuilder.setIcon(R.drawable.logo);
                        localBuilder.setCancelable(false);
                        localBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface paramDialogInterface, int paramInt)
                            {
                                ProcessImageScreen.this.getDialogSaveDiscard(ProcessImageScreen.SVMclassifierBackgroundTask.this.testResult).create().show();
                            }
                        });
                        localBuilder.create().show();
                        return;
                    }
                }
                catch (Exception localException)
                {
                    localException.printStackTrace();
                    return;
                }
                localBuilder.setMessage("The input Image is Non-Malignant Melanoma(Not Cancer).");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog = new ProgressDialog(ProcessImageScreen.this);
            this.dialog.setTitle("Classification of Image");
            this.dialog.setIcon(R.drawable.logo);
            this.dialog.setMessage("Image Classification in Progress, Please Wait...");
            this.dialog.setCancelable(false);
            this.dialog.show();
            this.clockBefore = new Date();
            this.startTime = this.clockBefore.getTime();
        }
    }
}
