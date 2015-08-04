package nu.pto.androidapp.base.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.LruCache;
import android.widget.ImageView;
import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.db.dao.ImageDao;
import nu.pto.androidapp.base.db.model.Image;

import java.lang.ref.WeakReference;
import java.sql.SQLException;

public class DbImageLoader {

    public LruCache<String, Bitmap> lruCache;

    private static DbImageLoader instance;

    private DbImageLoader() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }


    public static DbImageLoader getInstance() {
        if (instance == null) {
            instance = new DbImageLoader();
        }
        return instance;
    }


    public void load(Context context, String imageId, ImageView imageView) {
        load(context, imageId, imageView, imageView.getWidth(), imageView.getHeight());
    }

    public void load(Context context, String imageId, ImageView imageView, int reqWidth, int reqHeight) {

        if (imageId == null) {
            return;
        }

        final Bitmap bitmap = getInstance().getBitmapFromMemCache(imageId + "_" + reqHeight + "x" + reqHeight);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            new LoadImageTask(context, imageId, imageView, reqWidth, reqHeight).execute();
        }
    }

    class LoadImageTask extends AsyncTask<String, Void, Bitmap> {


        private final Context context;
        private final WeakReference<ImageView> imageViewReference;
        private final String imageId;
        private final ImageView imageView;

        private int reqWidth;
        private int reqHeight;

        public LoadImageTask(Context context, String imageId, ImageView imageView, int reqWidth, int reqHeight) {
            this.imageView = imageView;
            this.context = context;
            this.imageId = imageId;
            this.reqHeight = reqHeight;
            this.reqWidth = reqWidth;

            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... imageIds) {
            byte[] decodedString;
            try {
                ImageDao imageDao = ((BaseActivity) context).getDatabaseHelper().getImageDao();
                Image image = imageDao.getImageById(imageId);
                if (image == null) {
                    return null;
                }
                String base64 = image.getPhotoBase64();
                if (base64 == null || base64.isEmpty()) {
                    return null;
                }
                decodedString = Base64.decode(String.valueOf(base64), Base64.DEFAULT);

                BitmapFactory.Options options = new BitmapFactory.Options();

                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);


                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

//                if(options.inSampleSize < 2) {
//                    options.inSampleSize = 2;
//                }
                options.inPurgeable = true;
                options.inJustDecodeBounds = false;

                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (reqHeight == 0 || reqWidth == 0) {
                return inSampleSize;
            }

            if (height > reqHeight || width > reqWidth) {

                int heightRatio = Math.round((float) height / (float) reqHeight);
                int widthRatio = Math.round((float) width / (float) reqWidth);

                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }

            return inSampleSize;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                addBitmapToMemoryCache(imageId + "_" + reqHeight + "x" + reqHeight, bitmap);
//                ImageView imageView = imageViewReference.get();
                if (imageView != null) {

                    String imageTag = (String) imageView.getTag();
                    if (imageTag == null || imageId.equals(imageTag)) {
                        imageView.setTag(imageId);
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (key != null) {
            if (getBitmapFromMemCache(key) == null) {
                lruCache.put(key, bitmap);
            }
        }

    }

    public Bitmap getBitmapFromMemCache(String key) {
        if (key != null) {
            return lruCache.get(key);
        }
        return null;
    }


}
