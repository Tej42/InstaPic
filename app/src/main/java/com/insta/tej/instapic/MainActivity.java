package com.insta.tej.instapic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    ArrayList<Bitmap> bitmaps=new ArrayList<Bitmap>();
    ImageAdapter imageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageAdapter=new ImageAdapter(this);
        new InstaTask().execute();
        GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(imageAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView imageView=(ImageView)view;
                imageView.setLayoutParams(new GridView.LayoutParams(600,600));
                imageView.setElevation(5);
                imageAdapter.notifyDataSetChanged();
            }
        });
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return bitmaps.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                    imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageBitmap(bitmaps.get(position));
            return imageView;
        }
    }

    class InstaTask extends AsyncTask<String,String,ArrayList<Bitmap>>{

        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmapArrayList) {
            super.onPostExecute(bitmapArrayList);
            bitmaps=bitmapArrayList;
            imageAdapter.notifyDataSetChanged();

        }

        @Override
        protected ArrayList<Bitmap> doInBackground(String... params) {
            URL url1 = null;
            ArrayList<Bitmap> bitmapArrayList=new ArrayList<Bitmap>();
            String url="https://api.instagram.com/v1/tags/selfie/media/recent?access_token=1710139555.1fb234f.b651087078d74b0897db395023781604";
            try {
                url1=new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            URLConnection uc = null;
            try {
                uc = url1.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(
                        uc.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String line;
            try {
                while ((line = in.readLine()) != null) {
                    JSONObject ob = new JSONObject(line);

                    JSONArray dataArray = ob.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {


                        JSONObject jo = (JSONObject) dataArray.get(i);
                        JSONObject nja = (JSONObject) jo.getJSONObject("images");
                        if(i%3==0){
                            JSONObject bigJO= (JSONObject)nja.getJSONObject("standard_resolution");
                            String imageURL=(String)bigJO.getString("url");
                           bitmapArrayList.add(loadImage(imageURL));
                        }
                        else{
                            JSONObject smallJO= (JSONObject)nja.getJSONObject("low_resolution");
                            String imageURL=(String)smallJO.getString("url");
                            bitmapArrayList.add(loadImage(imageURL));
                        }

                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return bitmapArrayList;
        }
        private Bitmap loadImage(String url){
            Bitmap bitmap=null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

    }

}
