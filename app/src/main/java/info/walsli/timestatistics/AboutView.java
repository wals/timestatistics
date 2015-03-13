package info.walsli.timestatistics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AboutView extends View {
    int screenWidth;
    int screenHeight;
    RectF rectf1;
    RectF rectf2;
    RectF rectf3;
    RectF rectf4;
    String version="";

    int password=0;
    public void setVersion(String version)
    {
        this.version=version;
    }
    public AboutView(Context context,AttributeSet attrs) {
        super(context,attrs);
        this.version="2.31FTL";
        postInvalidate();
    }
    @Override
    public void onLayout(boolean changed,int left,int top,int right,int buttom)
    {
        super.onLayout(changed,left,top,right,buttom);
        screenHeight=getHeight();
        screenWidth=getWidth();
        rectf1=new RectF(0,(int) (screenHeight/8.0),screenWidth,(int) (screenHeight/8.0));
        rectf2=new RectF((int) (0.3*screenWidth),(int) (screenHeight/3.0-0.2*screenWidth),(int) (0.7*screenWidth),(int) (screenHeight/3.0+0.2*screenWidth));
        rectf3=new RectF(0,(int) (screenHeight*0.55),screenWidth,(int) (screenHeight*0.55));
        rectf4=new RectF(0,(int) (screenHeight*0.7),screenWidth,(int) (screenHeight*0.7));
    }
    private int getAreaNum(MotionEvent event)
    {
        int areaNum=1;
        if(event.getY()/screenHeight>0.66)
        {
            areaNum+=6;
        }
        else if(event.getY()/screenHeight>0.33)
        {
            areaNum+=3;
        }
        if(event.getX()/screenWidth>0.66)
        {
            areaNum+=2;
        }
        else if(event.getX()/screenWidth>0.33)
        {
            areaNum+=1;
        }
        return areaNum;
    }
    private Paint initPaint()
    {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
        p.setSubpixelText(true);
        p.setTextSize((float) (screenWidth/10.0));
        p.setTextAlign(Paint.Align.CENTER);
        return p;
    }
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event)
    {
        if(event.getAction()==MotionEvent.ACTION_DOWN)//password=666422=monica
        {
            int areaNum=getAreaNum(event);
            switch(password)
            {
                case 0:
                case 1:
                case 2:
                    if(areaNum==6)
                    {
                        password++;
                    }
                    else
                    {
                        password=0;
                    }
                    break;
                case 3:
                    if(areaNum==4)
                    {
                        password++;
                    }
                    else
                    {
                        password=0;
                    }
                    break;
                case 4:
                    if(areaNum==2)
                    {
                        password++;
                    }
                    else
                    {
                        password=0;
                    }
                    break;
                case 5:
                    if(areaNum==2)
                    {
                        lovetomonica();//TODO
                        password=0;
                    }
                    else
                    {
                        password=0;
                    }
                    break;
            }
            return true;
        }
        return false;
    }
    private void lovetomonica()
    {

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p=initPaint();
        canvas.drawText(""+MyUtils.getDaysFrom20140715()+"天",(float) (screenWidth/2.0),getBaseLine(p,rectf2),p);
        p.setTypeface(Typeface.DEFAULT);

        p.setTextSize((float) (screenWidth/30.0));
        String topwords="魅时间已经走过了";
        canvas.drawText(topwords,(float) (screenWidth/2.0),getBaseLine(p,rectf1),p);


        canvas.drawText("当前版本  v "+version,(float) (screenWidth/2.0),getBaseLine(p,rectf3),p);


        canvas.drawText("walsli",(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)-screenWidth/20.0),p);
        canvas.drawText("Live long and prosper",(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)+screenWidth/20.0),p);


    }
    protected float getBaseLine(Paint p,RectF rectf)
    {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return rectf.top + (rectf.bottom - rectf.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
    }
}