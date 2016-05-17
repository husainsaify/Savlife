package com.hackerkernel.blooddonar.network;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.hackerkernel.blooddonar.infrastructure.MyApplication;

/**
 * Volley class
 */
public class MyVolley {
    private static MyVolley mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    protected MyVolley() {
        //request queue
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());


        //Image loader
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            //create image cache
            int max = (int) (Runtime.getRuntime().maxMemory() / 1024 / 8);
            LruCache<String, Bitmap> cache = new LruCache<>(max);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static MyVolley getInstance() {
        if (mInstance == null) {
            mInstance = new MyVolley();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public static String handleVolleyError(VolleyError error) {
        String message = null;
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            message = "Bad network Connection";
        } else if (error instanceof AuthFailureError) {
            message = "Failed to perform a request";
        } else if (error instanceof ServerError) {
            message = "Server error";
        } else if (error instanceof NetworkError) {
            message = "Network error while performing a request";
        } else if (error instanceof ParseError) {
            message = "Server response could not be parsed";
        }
        return message;
    }
}
