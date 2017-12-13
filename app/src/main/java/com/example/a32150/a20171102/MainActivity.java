package com.example.a32150.a20171102;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.logging.LogRecord;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    ImageView iv, iv2;
    ProgressBar progressBar;
    TextView tv;
    int readSum;
    File imgFile;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView)findViewById(R.id.imageView);
        iv2 = (ImageView)findViewById(R.id.imageView2);
        handler = new Handler();

        //getImg();
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        tv = (TextView) findViewById(R.id.textView);

        imgFile = new File(getFilesDir()+File.separator+"Diamond.jpg");
    }

    public void click(View v) {
        progressBar.setVisibility(View.VISIBLE);
        readSum=0;

        getImg();

    }

    public void onClickShow(View v) {
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            iv2.setImageBitmap(myBitmap);
        }
    }

    public void onClickClear(View v) {
        if(imgFile.exists()){
           // Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            iv2.setImageBitmap(null);
        }
    }

    void getImg()   {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL("http://farm6.static.flickr.com/5329/7180527223_65e59a9f82_z.jpg");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    final int fullSize = conn.getContentLength();//圖檔大小
                    Log.d("NET", String.valueOf(fullSize));

                    InputStream is = conn.getInputStream();
                    //暫存陣列
                    ByteArrayOutputStream os = new ByteArrayOutputStream();

                    byte b[] = new byte[1024];
                    int size;

                    progressBar.setMax(fullSize);

                    while((size = is.read(b)) != -1)  {
                        os.write(b, 0, size);
                        readSum+=size;
                        Log.d("NET","" + readSum);
                        //runOnUiThread(new Runnable() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                int percent=(100 * readSum / fullSize);
                                progressBar.setProgress(readSum);
                                tv.setText(String.valueOf(percent)+"%" +"    " +String.valueOf(readSum)+"k");
                            }
                        });
                    }
                    byte result[] = os.toByteArray();
                    final Bitmap bmp = BitmapFactory.decodeByteArray(result, 0, result.length);
                    //存圖檔
                    FileOutputStream fos = new FileOutputStream(imgFile);
                    fos.write(result);
                    fos.close();
                    Log.d("NET", "Image Finish");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(bmp);
                            progressBar.setVisibility(View.INVISIBLE);
                            int i=0;
//                            while(i<fullSize)   {
//                                i+=1000;
//                                progressBar.setProgress(i);
//                                tv.setText(String.valueOf(i));
//                                Log.d("NET", "進度:"+String.valueOf(i));
//                            }
                        }
                    });
                    is.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
