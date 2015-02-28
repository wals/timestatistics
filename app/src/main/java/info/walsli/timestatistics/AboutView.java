package info.walsli.timestatistics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;

public class AboutView extends View {
    String version="";
    DisplayMetrics dm = this.getResources().getDisplayMetrics();
    int screenWidth = dm.widthPixels;
    int screenHeight = dm.heightPixels;
    RectF rectf1=new RectF(0,(int) (screenHeight/8.0),screenWidth,(int) (screenHeight/8.0));
    RectF rectf2=new RectF((int) (0.3*screenWidth),(int) (screenHeight/3.0-0.2*screenWidth),(int) (0.7*screenWidth),(int) (screenHeight/3.0+0.2*screenWidth));
    RectF rectf3=new RectF(0,(int) (screenHeight*0.55),screenWidth,(int) (screenHeight*0.55));
    RectF rectf4=new RectF(0,(int) (screenHeight*0.7),screenWidth,(int) (screenHeight*0.7));

    public AboutView(Context context,String version) {
        super(context);
        this.version=version;
        postInvalidate();
    }
    private float getBaseLine(Paint p,RectF rectf)
    {
        FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return rectf.top + (rectf.bottom - rectf.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
    }
    private Paint initPaint()
    {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setColor(Color.WHITE);
        return p;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p=initPaint();
        p.setStyle(Paint.Style.FILL);
        p.setSubpixelText(true);
        p.setTextSize((float) (screenWidth/10.0));
        p.setTypeface(MyApplication.gettypeface());
        p.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(""+MyTime.getDayNum()+"天",(float) (screenWidth/2.0),getBaseLine(p,rectf2),p);
        p.setTypeface(Typeface.DEFAULT);

        p.setTextSize((float) (screenWidth/30.0));
        String topwords="魅时间已经走过了";
        canvas.drawText(topwords,(float) (screenWidth/2.0),getBaseLine(p,rectf1),p);


        canvas.drawText("当前版本  v "+version,(float) (screenWidth/2.0),getBaseLine(p,rectf3),p);


        canvas.drawText("walsli",(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)-screenWidth/20.0),p);
        canvas.drawText("Miss U",(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)+screenWidth/20.0),p);


    }

}