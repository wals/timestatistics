package info.walsli.timestatistics;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;

public class StatisticsView extends View {
    DisplayMetrics dm = this.getResources().getDisplayMetrics();
    int screenWidth = dm.widthPixels;
    int screenHeight = dm.heightPixels;
    float[][] location=new float[10][2];
    boolean showdetail=false;
    int pickedday=0;
    RectF rectf=new RectF(0,0,0,0);
    public StatisticsView(Context context,AttributeSet attr) {
        super(context,attr);

    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction()==MotionEvent.ACTION_DOWN)
        {
            if(showdetail)
            {
                showdetail=false;
                postInvalidate();
            }
            else
            {
                float x=event.getX();
                float y=event.getY();
                for(int i=0;i<10;i++)
                {
                    if((x-location[i][0]<30)&&(x-location[i][0]>-30)&&(y-location[i][1]<30)&&(y-location[i][1]>-30))
                    {
                        showdetail=true;
                        this.pickedday=i;
                        postInvalidate();
                    }
                }
            }
        }
        return true;

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!DBHelper.lock)
        {
            tempdraw(canvas);
        }
        else
        {
            Paint p=new Paint();
            p.setTextSize((float) (screenWidth/20));
            p.setTextAlign(Paint.Align.CENTER);
            p.setColor(Color.WHITE);
            RectF rectf1=new RectF(0,0,screenWidth,screenHeight);
            canvas.drawText("数据库忙碌，本页面暂不可用",(float)(screenWidth*0.5),getBaseLine(p,rectf1),p);
        }
    }
    public void tempdraw(Canvas canvas)
    {
        Paint p=new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
        p.setTextSize((float) (screenWidth/35.0));
        p.setSubpixelText(true);


        int dayNum=MyUtils.getDaysFrom20140715();
        DBManager mDBManager=new DBManager(MyApplication.getInstance());
        mDBManager.calculateTimeOfDays(dayNum);
        int dayAndSeond[][]=new int[10][2];
        HashMap<Integer,Integer> mHashMap=new HashMap<Integer,Integer>();
        Cursor c=mDBManager.query("select * from timeofdays");
        while(c.moveToNext())
        {
            if(dayNum-c.getInt(1)<10)
            {
                mHashMap.put(c.getInt(1),c.getInt(2));
            }
        }
        int maxSecond=0;
        for(int i=dayNum-9;i<=dayNum;i++)
        {
            if(mHashMap.containsKey(i))
            {
                dayAndSeond[i-dayNum+9][0]=i;
                dayAndSeond[i-dayNum+9][1]=mHashMap.get(i);
                if(dayAndSeond[i-dayNum+9][1]>maxSecond)
                {
                    maxSecond=dayAndSeond[i-dayNum+9][1];
                }
            }
            else
            {
                dayAndSeond[i-dayNum+9][0]=i;
                dayAndSeond[i-dayNum+9][1]=0;
            }
        }
        c.close();
        if(!showdetail)
        {
            int strokeWidth=(int)(Math.round(screenHeight/360.0));
            p.setStrokeWidth(strokeWidth);
            canvas.drawRect((float)(screenWidth*0.1-strokeWidth*0.5),(float)(screenHeight*0.2),(float)(screenWidth*0.1+strokeWidth*0.5),(float)(screenHeight*0.8),p);
            canvas.drawRect((float)(screenWidth*0.1),(float)(screenHeight*0.8-strokeWidth*0.5),(float)(screenWidth*0.9),(float)(screenHeight*0.8+strokeWidth*0.5),p);
            float pointerLength=(float) (screenWidth/72.0);
            canvas.drawLine((float)(screenWidth*0.1-pointerLength),(float)(screenHeight*0.2+pointerLength),(float)(screenWidth*0.1),(float)(screenHeight*0.2),p);
            canvas.drawLine((float)(screenWidth*0.1+pointerLength),(float)(screenHeight*0.2+pointerLength),(float)(screenWidth*0.1),(float)(screenHeight*0.2),p);
            canvas.drawLine((float)(screenWidth*0.9-pointerLength),(float)(screenHeight*0.8-pointerLength),(float)(screenWidth*0.9),(float)(screenHeight*0.8),p);
            canvas.drawLine((float)(screenWidth*0.9-pointerLength),(float)(screenHeight*0.8+pointerLength),(float)(screenWidth*0.9),(float)(screenHeight*0.8),p);
            p.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("使用时间",(float)(screenWidth*0.1),(float)(screenHeight*0.2-pointerLength*2),p);
            p.setTextAlign(Paint.Align.LEFT);
            rectf.set(0,(int) (screenHeight*0.8),screenWidth,(int) (screenHeight*0.8));
            canvas.drawText("日期",(float)(screenWidth*0.9+pointerLength),getBaseLine(p,rectf),p);


            float datum=(float)(screenHeight*0.55/(maxSecond/3600+1)/3600.0);
            for(int i=0;i<10;i++)
            {
                location[i][0]=(float)(screenWidth*(0.17272+0.07272*i));
                location[i][1]=(float)(0.8*screenHeight-dayAndSeond[i][1]*datum);
                canvas.drawCircle(location[i][0],location[i][1],(float)(screenWidth/108.0),p);
            }
            for(int i=0;i<9;i++)
            {
                canvas.drawLine(location[i][0],location[i][1],location[i+1][0],location[i+1][1],p);
            }
            p.setTextAlign(Paint.Align.CENTER);
            Time t=new Time();
            for(int i=0;i<10;i++)
            {
                t.set(1405353600000L + (long) dayAndSeond[i][0] * 86400000L);
                canvas.drawText(""+t.monthDay,(float)(screenWidth*(0.17272+0.07272*i)),(float)(screenHeight*0.8+pointerLength+screenWidth/35.0),p);
            }
            for(int i=1;i<=maxSecond/3600+1;i++)
            {
                float high=(float)(screenHeight*0.8-i*3600*datum);
                canvas.drawLine((float)(0.1*screenWidth),high,(float)(0.1*screenWidth+pointerLength),high,p);
                rectf.set(0,high,0,high);
                canvas.drawText(""+i+"h",(float)(0.1*screenWidth-pointerLength-screenWidth/35.0),getBaseLine(p,rectf),p);
            }
        }
        else
        {
            int strokeWidth=(int)(Math.round(screenHeight/360.0));
            p.setStrokeWidth(strokeWidth);
            p.setStyle(Paint.Style.STROKE);
            rectf.set((float) (screenWidth * 0.05), (float) (screenHeight * 0.5 - screenWidth * 0.45), (float) (screenWidth * 0.95), (float) (screenHeight * 0.5 + screenWidth * 0.45));
            canvas.drawArc(rectf, 0, 360, false, p);
            rectf.set((float)(screenWidth*0.25),(float)(screenHeight*0.5-screenWidth*0.25),(float)(screenWidth*0.75),(float)(screenHeight*0.5+screenWidth*0.25));
            canvas.drawArc(rectf,0,360,false, p);
            p.setStrokeWidth((int)(Math.round(screenHeight/108.0)));
            rectf.set((float)(screenWidth*0.25+strokeWidth),(float)(screenHeight*0.5-screenWidth*0.25+strokeWidth),(float)(screenWidth*0.75-strokeWidth),(float)(screenHeight*0.5+screenWidth*0.25-strokeWidth));
            for(int i=0;i<24;i++)
            {
                canvas.drawArc(rectf,(float) (i*15-0.5),1,false,p);
            }
            strokeWidth=(int)(Math.round(screenHeight/60.0));
            p.setStrokeWidth(strokeWidth);
            rectf.set((float) (screenWidth * 0.25 + strokeWidth * 0.5), (float) (screenHeight * 0.5 - screenWidth * 0.25 + strokeWidth * 0.5), (float) (screenWidth * 0.75 - strokeWidth * 0.5), (float) (screenHeight * 0.5 + screenWidth * 0.25 - strokeWidth * 0.5));
            for(int i=0;i<4;i++)
            {
                canvas.drawArc(rectf,(float) (i*90-1),2,false,p);
            }
            p.setStyle(Paint.Style.FILL);
            p.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("0", (float) (screenWidth * 0.5), (float) (screenHeight * 0.5 - screenWidth * 0.25 + screenWidth / 18), p);
            canvas.drawText("12",(float)(screenWidth*0.5),(float)(screenHeight*0.5+screenWidth*0.25-screenWidth/25),p);
            rectf.set(0,(float)(0.5*screenHeight),0,(float)(0.5*screenHeight));
            canvas.drawText("6",(float)(screenWidth*0.75-screenWidth/25),getBaseLine(p,rectf),p);
            canvas.drawText("18",(float)(screenWidth*0.25+screenWidth/20),getBaseLine(p,rectf),p);
            dayNum=dayAndSeond[pickedday][0];

            Cursor d=mDBManager.query("select * from timeinfo where datenum="+dayNum);

            int begin=0;
            int end=0;
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth((float)(0.2*screenWidth));
            rectf.set((float)(0.15*screenWidth),(float)(0.5*screenHeight-0.35*screenWidth),(float)(0.85*screenWidth),(float)(0.5*screenHeight+0.35*screenWidth));

            while(d.moveToNext())
            {
                if(end==d.getInt(2))
                {
                    end=d.getInt(3);
                }
                else
                {
                    canvas.drawArc(rectf,(float)(begin/240.0-90),(float)((end-begin)/240.0),false,p);
                    begin=d.getInt(2);
                    end=d.getInt(3);
                }
            }
            canvas.drawArc(rectf,(float)(begin/240.0-90),(float)((end-begin)/240.0),false,p);
            d.close();
            p.setStyle(Paint.Style.FILL);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize((float) (screenWidth/20.0));
            rectf.set(0,(float)(0.5*screenHeight-0.6*screenWidth),0,(float)(0.5*screenHeight-0.6*screenWidth));
            Time t=new Time();
            t.set(1405353600000L + (long) (dayNum * 86400000L));
            canvas.drawText(""+t.year+"年"+(t.month+1)+"月"+t.monthDay+"日详情",(float)(screenWidth*0.5),getBaseLine(p,rectf),p);
            p.setTextSize((float) (screenWidth/10.0));
            rectf.set((float)(0.5*screenWidth),(float)(0.5*screenHeight),(float)(0.5*screenWidth),(float)(0.5*screenHeight));

            int seconds=dayAndSeond[pickedday][1];
            String s="";
            if(seconds/3600<10)s+="0";
            s+=String.valueOf(seconds/3600);
            s+=":";
            long minutes=seconds%3600;
            if(minutes/60<10)s+="0";
            s+=String.valueOf(minutes/60);
            p.setTypeface(MyApplication.gettypeface());
            canvas.drawText(s,(float)(0.5*screenWidth),getBaseLine(p,rectf),p);
        }
        mDBManager.closeDB();
    }

    protected float getBaseLine(Paint p,RectF rectf)
    {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return rectf.top + (rectf.bottom - rectf.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
    }
}