package com.example.administrator.phoneinfo;

/**
 * Created by Administrator on 2017/6/1.
 */


        import android.graphics.Bitmap;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Typeface;
        import android.graphics.Bitmap.Config;
        import android.graphics.Paint.Align;
        import android.text.Layout;
        import android.text.StaticLayout;
        import android.text.TextPaint;
        import android.util.Log;

/**
 * 进行添加为照片添加水印图片和文字 帮助类
 */
public class WaterStampUtils {
    /**
     * 进行添加水印图片和文字
     *
     * @param src
     * @param waterMak
     * @return
     */
    public static Bitmap createBitmapWithMarker(Bitmap src, Bitmap waterMak, String title) {
        if (src == null) {
            return src;
        }
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();
        int ww = waterMak.getWidth();
        int wh = waterMak.getHeight();
        Log.i("jiangqq", "w = " + w + ",h = " + h + ",ww = " + ww + ",wh = "
                + wh);
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);
        // 在src的右下角添加水印
        Paint paint = new Paint();
        //paint.setAlpha(100);
        mCanvas.drawBitmap(waterMak, w - ww - 5, h - wh - 5, paint);

        // 开始加入文字

        StaticLayout layout_tx=null;
        if (null != title) {
            //Paint textPaint = new Paint();
            TextPaint textPaint = new TextPaint();

            textPaint.setColor(Color.GREEN);
            //textPaint.setTextSize(96);


            textPaint.setTextSize(Math.min(w/36,h/36));

            String familyName = "宋体";
            Typeface typeface = Typeface.create(familyName,
                    Typeface.BOLD);
            textPaint.setTypeface(typeface);
            //textPaint.setTextAlign(Align.CENTER);
            //mCanvas.drawText(title, w / 2, 25, textPaint);
            //mCanvas.drawText(title, w/40, h*39/40, textPaint);
            layout_tx = new StaticLayout(title,textPaint, w*3/4, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);

        }

        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        if(h>w){
            mCanvas.translate(w/40,h*3/4);
        }
        else{
            mCanvas.translate(w/40,h*2/3);
        }

        layout_tx.draw(mCanvas);

        mCanvas.restore();
        return newBitmap;
    }

    public static Bitmap createBitmapWithNoMarker(Bitmap src, String title) {
        if (src == null) {
            return src;
        }
        // 获取原始图片与水印图片的宽与高
        int w = src.getWidth();
        int h = src.getHeight();
        //int ww = waterMak.getWidth();
        //int wh = waterMak.getHeight();
        //Log.i("jiangqq", "w = " + w + ",h = " + h + ",ww = " + ww + ",wh = "
        //        + wh);
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        // 往位图中开始画入src原始图片
        mCanvas.drawBitmap(src, 0, 0, null);
        // 在src的右下角添加水印
        //Paint paint = new Paint();
        //paint.setAlpha(100);
        //mCanvas.drawBitmap(waterMak, w - ww - 5, h - wh - 5, paint);

        // 开始加入文字

        StaticLayout layout_tx=null;
        if (null != title) {
            //Paint textPaint = new Paint();
            TextPaint textPaint = new TextPaint();

            textPaint.setColor(Color.GREEN);
            //textPaint.setTextSize(96);


            textPaint.setTextSize(Math.min(w/36,h/36));

            String familyName = "宋体";
            Typeface typeface = Typeface.create(familyName,
                    Typeface.BOLD);
            textPaint.setTypeface(typeface);
            //textPaint.setTextAlign(Align.CENTER);
            //mCanvas.drawText(title, w / 2, 25, textPaint);
            //mCanvas.drawText(title, w/40, h*39/40, textPaint);
            layout_tx = new StaticLayout(title,textPaint, w*3/4, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);

        }

        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        if(h>w){
            mCanvas.translate(w/40,h*3/4);
        }
        else{
            mCanvas.translate(w/40,h*2/3);
        }

        layout_tx.draw(mCanvas);

        mCanvas.restore();
        return newBitmap;
    }

}

