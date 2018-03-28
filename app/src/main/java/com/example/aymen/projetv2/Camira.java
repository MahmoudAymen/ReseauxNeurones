package com.example.aymen.projetv2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.OptionalDataException;
@SuppressLint({"SimpleDateFormat"})
public class Camira extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Camira";
    Bitmap bitmapDrawingPane;
    Bitmap bitmapMaster;
    Button btnAnalysis, btnPreProces;
    Canvas canvasDrawingPane, canvasMaster;
    Uri fileUri = null;
    boolean finalRectangle = false;
    ImageView imageDrawingPane, resultImage;
    TextView imageSource;
    Uri imageUri = null;
    CheckPoints startPoint;
    boolean Adjustcheck = true;

    private void AddImageToGallery(Uri paramUri) {
        Intent i = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        i.setData(paramUri);
        sendBroadcast(i);
    }
    private void AdjustImage() {
        try {
            resultImage= (ImageView) findViewById(R.id.resultImage);
            double d1 = new Date().getTime();//créer un variable et mettre dans la variable le  time de class date en android
            Log.d(TAG,"haniMechii");
            Bitmap localBitmap1 = ((BitmapDrawable) resultImage.getDrawable()).getBitmap();//créer un image de type bitmap et mettre dans l'image un autre image intericée sur l'interface graphique
            Log.d(TAG,"haniFoutBitmap");
            Mat localMat1 = new Mat();//instancer un variable de type mat
            Log.d(TAG,"haniFoutMat");
            Utils.bitmapToMat(localBitmap1, localMat1);//convertir un image to mat
            Log.d(TAG, "haniFoutUtils");
            PreProcessImage localPreProcessImage = new PreProcessImage();//constructeur par defaut
            Mat localMat2 = localPreProcessImage.Detect(localPreProcessImage.ResizeImage(localMat1));//applique un filtre sur un variable de type mat qui elle convertir  bitmaptoMat
            Log.d(TAG,"haniFoutFiltre");
            Bitmap localBitmap2 = Bitmap.createBitmap(localMat2.cols(), localMat2.rows(), Bitmap.Config.ARGB_8888);
            Log.d(TAG,"haniFoutBitmap2");
            Utils.matToBitmap(localMat2, localBitmap2);//convertir mat to image de class Utils en opencv
            Log.d(TAG, "haniFoutUtils2");
            int i = localBitmap2.getWidth();//largeur de l'image coleur
            int j = localBitmap2.getHeight();//hauteur de l'image couleur
            String str = String.valueOf(i) + "x" + String.valueOf(j);//taille de l'image
            Log.d(TAG,"haniFoutBitmapBechN5ouImage");
            resultImage.setImageBitmap(localBitmap2);//remplace l'image resultat par l'image filter
            Log.d(TAG, "haniFoutBitmap5atiteImage");
            double d2 = new Date().getTime() - d1;//calculuer le time de transformation des images en fct de leur variable origine
            Toast.makeText(this, "Image Resized Successfully to:" + str + " in " + d2 + " ms", Toast.LENGTH_LONG).show();
            return;
        } catch (Exception e) {

        }

    }

  /*  private void AdjustImage() {
        try {
            resultImage= (ImageView) findViewById(R.id.resultImage);
            double d1 = new Date().getTime();//créer un variable et mettre dans la variable le  time de class date en android
            Log.d(TAG,"haniMechii");
            Bitmap localBitmap1 = ((BitmapDrawable) resultImage.getDrawable()).getBitmap();//créer un image de type bitmap et mettre dans l'image un autre image intericée sur l'interface graphique
            Log.d(TAG,"haniFoutBitmap");
            Mat localMat1 = new Mat();//instancer un variable de type mat
            Log.d(TAG,"haniFoutMat");
            Utils.bitmapToMat(localBitmap1, localMat1);//convertir un image to mat
            Log.d(TAG, "haniFoutUtils");
            PreProcessImage localPreProcessImage = new PreProcessImage();//constructeur par defaut
            Mat localMat2 = localPreProcessImage.Detect(localMat1);//applique un filtre sur un variable de type mat qui elle convertir  bitmaptoMat
            Log.d(TAG,"haniFoutFiltre");
            Bitmap localBitmap2 = Bitmap.createBitmap(localMat2.cols(), localMat2.rows(), Bitmap.Config.ARGB_8888);
            Log.d(TAG,"haniFoutBitmap2");
            Utils.matToBitmap(localMat2, localBitmap2);//convertir mat to image de class Utils en opencv
            Log.d(TAG, "haniFoutUtils2");
            int i = localBitmap2.getWidth();//largeur de l'image coleur
            int j = localBitmap2.getHeight();//hauteur de l'image couleur
            String str = String.valueOf(i) + "x" + String.valueOf(j);//taille de l'image
            Log.d(TAG,"haniFoutBitmapBechN5ouImage");
            resultImage.setImageBitmap(localBitmap2);//remplace l'image resultat par l'image filter
            Log.d(TAG, "haniFoutBitmap5atiteImage");
            double d2 = new Date().getTime() - d1;//calculuer le time de transformation des images en fct de leur variable origine
            Toast.makeText(this, "Image Resized Successfully to:" + str + " in " + d2 + " ms", Toast.LENGTH_LONG).show();
            return;
        } catch (Exception e) {

        }

    }*/
    public int calculateInSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2) {
        int i = paramOptions.outHeight;
        int j = paramOptions.outWidth;
        int k = 1;
        if ((i > paramInt2) || (j > paramInt1)) {
            if (j > i)
                k = Math.round(i / paramInt2);
        } else
            return k;
        return Math.round(j / paramInt1);
    }
    public Bitmap decodeSampledBitmapFromUri(Uri paramUri, int paramInt1, int paramInt2) {
        try {
            BitmapFactory.Options localOptions = new BitmapFactory.Options();
            localOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(paramUri), null, localOptions);
            localOptions.inSampleSize = calculateInSampleSize(localOptions, paramInt1, paramInt2);
            localOptions.inJustDecodeBounds = false;
            Bitmap localBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(paramUri), null, localOptions);
            return localBitmap;
        } catch (FileNotFoundException localFileNotFoundException) {
            localFileNotFoundException.printStackTrace();
            Toast.makeText(getApplicationContext(), "ERROR DECODING: " + localFileNotFoundException.toString(), Toast.LENGTH_LONG).show();
        }
        return null;
    }


    private void LoadCamera() {
        try {
            Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            this.fileUri = Uri.fromFile(getCapturedImageFile());
            localIntent.putExtra("output", this.fileUri);
            startActivityForResult(localIntent, 1);
        } catch (ActivityNotFoundException localActivityNotFoundException) {
            Toast.makeText(Camira.this, "This device doesn't support Camera!", Toast.LENGTH_LONG).show();
        }
    }

    private void LoadGallery() {
        try {
            startActivityForResult(Intent.createChooser(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), "Select Image PLease"), 0);
            return;
        } catch (ActivityNotFoundException localActivityNotFoundException) {
            Toast.makeText(this, "No Content Found in the Gallery.", Toast.LENGTH_LONG).show();
        }
    }

    private File getCapturedImageFile() {
        File localFile = new File(Environment.getExternalStorageDirectory() + "/mSkinDoctor/mSkinCaptured");
        if ((!localFile.exists()) && (!localFile.mkdirs())) {
            Log.e(TAG, "Failed To Create Storage Directory");
            return null;
        }
        String str = new SimpleDateFormat("yyyyMM_ddHHmmss").format(new Date());
        return new File(localFile.getPath() + File.separator + "mSkinDoctor_" + str);
    }

    public AlertDialog.Builder getCameraOrGallery() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle("Please Choose Source");
        localBuilder.setIcon(R.drawable.logo);
        localBuilder.setCancelable(false);
        localBuilder.setItems(new String[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                switch (paramInt) {
                    case 0:
                        LoadCamera();
                        break;
                    case 1:
                        LoadGallery();
                        break;
                    default:
                        break;
                }
            }
        });
        return localBuilder;
    }



   protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        if ((paramInt1 == 0) && (paramInt2 == -1) && (paramIntent != null)) {
            Uri localUri2 = paramIntent.getData();
            this.imageUri = localUri2;
            File localFile2 = new File(String.valueOf(Uri.parse(localUri2.toString())));
            this.imageSource.setText("Image Name: " + localFile2.getName());
            try {
                Bitmap localBitmap2 = decodeSampledBitmapFromUri(localUri2, 640, 480);
                if (localBitmap2.getConfig() != null) ;
                for (Bitmap.Config localConfig2 = localBitmap2.getConfig(); ; localConfig2 = Bitmap.Config.ARGB_8888) {
                    this.bitmapMaster = Bitmap.createBitmap(localBitmap2.getWidth(), localBitmap2.getHeight(), localConfig2);
                    this.canvasMaster = new Canvas(this.bitmapMaster);
                    this.canvasMaster.drawBitmap(localBitmap2, 0.0F, 0.0F, null);
                    this.resultImage.setImageBitmap(this.bitmapMaster);
                    this.bitmapDrawingPane = Bitmap.createBitmap(localBitmap2.getWidth(), localBitmap2.getHeight(), localConfig2);
                    this.canvasDrawingPane = new Canvas(this.bitmapDrawingPane);
                    this.imageDrawingPane.setImageBitmap(this.bitmapDrawingPane);
                    return;
                }
            } catch (Exception localException2) {
                localException2.printStackTrace();
                return;
            }
        }
        if ((paramInt1 == 1) && (paramInt2 == -1)) {
            if (paramIntent == null) ;
            while (true) {
                try {
                    Uri localUri1 = this.fileUri;
                    this.imageUri = localUri1;
                    File localFile1 = new File(String.valueOf(Uri.parse(localUri1.toString())));
                    this.imageSource.setText("Image Name: " + localFile1.getName());
                    AddImageToGallery(localUri1);
                    Bitmap localBitmap1 = decodeSampledBitmapFromUri(localUri1, 640, 480);
                    if (localBitmap1.getConfig() == null)
                        break;
                    Bitmap.Config localConfig1 = localBitmap1.getConfig();
                    this.bitmapMaster = Bitmap.createBitmap(localBitmap1.getWidth(), localBitmap1.getHeight(), localConfig1);
                    this.canvasMaster = new Canvas(this.bitmapMaster);
                    this.canvasMaster.drawBitmap(localBitmap1, 0.0F, 0.0F, null);
                    this.resultImage.setImageBitmap(this.bitmapMaster);
                    this.bitmapDrawingPane = Bitmap.createBitmap(localBitmap1.getWidth(), localBitmap1.getHeight(), localConfig1);
                    this.canvasDrawingPane = new Canvas(this.bitmapDrawingPane);
                    this.imageDrawingPane.setImageBitmap(this.bitmapDrawingPane);
                    return;
                } catch (Exception localException1) {
                    localException1.printStackTrace();
                    return;
                }

            }
            Uri localUri1 = paramIntent.getData();
            Bitmap.Config localConfig1 = Bitmap.Config.ARGB_8888;

        }
       /* if (paramInt2 == 0) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }*/
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camira);
        this.imageSource = ((TextView) findViewById(R.id.ImageSourceUri));
        this.resultImage = ((ImageView) findViewById(R.id.resultImage));
        this.imageDrawingPane = ((ImageView) findViewById(R.id.drawingpaneImage));
        btnAnalysis= (Button) findViewById(R.id.buttonAnalysis);
        btnPreProces= (Button) findViewById(R.id.buttonResize);
        btnAnalysis.setOnClickListener(this);
        btnPreProces.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
           case R.id.buttonResize:
                getCameraOrGallery().create().show();
                break;
            case R.id.buttonAnalysis:
                if(Adjustcheck)
                {
                    AdjustImage();
                    Adjustcheck=false;
                }
                break;
            default:
                break;
        }

    }


    private class CheckPoints{
     int x;
     int y;
     CheckPoints(int a,int b)
     {
         this.x=a;
         this.y=b;
     }

    }
    }

