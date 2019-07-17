package com.roman.fightnet.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.roman.fightnet.R;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static com.roman.fightnet.IConstants.storage;


public class GalleryImageAdapter extends BaseAdapter {
    private Context mContext;
    private final List<String> urls;

    public GalleryImageAdapter(Context context, List<String> urls) {
        mContext = context;
        this.urls = urls;
    }

    public int getCount() {
        return urls.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }


    public View getView(int index, View view, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(mContext);
        new DownLoadImageTask(imageView).execute(urls.get(index) + storage.getFacebookToken());
        imageView.setLayoutParams(new Gallery.LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    public static class DownLoadImageTask extends AsyncTask<String,Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }

}