package info.walsli.timestatistics;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import java.util.Date;

public class StatisticsView extends View {
    Paint p=new Paint();
    int a[][];
    float[][] location=new float[10][2];
    boolean showdetail=false;
    int pickedday=0;
    public StatisticsView(Context context,int a[][]) {
        super(context);
        this.a=a;
        p.setAntiAlias(true);
        p.setDither(true);
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
        p.setTextSize(30);
        p.setSubpixelText(true);
        postInvalidate();
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
        if(true)//(MyLogic.getModel()==3)
        {
            drawmx3(canvas);
        }
        else
        {
            drawmx2(canvas);
        }
    }
    public void drawmx3(Canvas canvas)
    {
        p.setTextSize(30);
        if(!showdetail)
        {
            p.setStrokeWidth(5);
            p.setStyle(Paint.Style.FILL);
            canvas.drawRect(100, 1397, 980, 1403, p);
            canvas.drawRect(97, 300, 103, 1400, p);
            canvas.drawLine(85, 315, 100, 300, p);
            canvas.drawLine(115, 315, 100, 300, p);
            canvas.drawLine(965, 1385, 980, 1400, p);
            canvas.drawLine(965, 1415, 980, 1400, p);
            canvas.drawText("日期",1000,1415,p);
            canvas.drawText("使用时间",45,270,p);
            int maxy=0;
            for(int i=0;i<10;i++)
            {
                if(a[i][1]>maxy)maxy=a[i][1];
            }
            if(maxy%3600!=0)
            {
                maxy=maxy-maxy%3600+3600;
            }
            float standard=(float) (1000.0/maxy);
            for(int k=1;k<maxy/3600+1;k++)
            {
                canvas.drawRect(100,1400-k*3600*standard,108, 1400-k*3600*standard+5, p);
                canvas.drawText(String.valueOf(k)+"h",k>9?35:50,1415-k*3600*standard,p);
            }


            for(int i=0;i<10;i++)
            {
                Date d=new Date();
                d.setTime(1405353600000L+(long)a[i][0]*86400000L);

                canvas.drawCircle(180+80*i,1400-a[i][1]*standard, 10, p);
                canvas.drawText(String.valueOf(d.getDate()),170+80*i,1450,p);
                location[i][0]=180+80*i;
                location[i][1]=1400-a[i][1]*standard;
            }
            for(int i=0;i<9;i++)
            {
                canvas.drawLine(location[i][0], location[i][1], location[i+1][0], location[i+1][1], p);
            }
        }
        else
        {
            p.setStrokeWidth(10);
            p.setStyle(Paint.Style.FILL);
            canvas.drawLine(540,620,540,600, p);
            canvas.drawLine(240,900,260,900, p);
            canvas.drawLine(820,900,840,900, p);
            canvas.drawLine(540,1180,540,1200, p);

            canvas.drawText("0",535,650, p);
            canvas.drawText("18",270,910, p);
            canvas.drawText("12",525,1170, p);
            canvas.drawText("6",800,910, p);

            p.setStrokeWidth(5);
            p.setStyle(Paint.Style.STROKE);
            canvas.drawArc(new RectF(40,400,1040,1400),0,360,false, p);
            canvas.drawArc(new RectF(240,600,840,1200),0,360,false, p);
            RectF rectf1=new RectF(245,605,835,1195);
            p.setStrokeWidth(10);
            for(int i=0;i<24;i++)
            {
                canvas.drawArc(rectf1,(float) (i*15-0.5),1,false,p);
            }
            p.setStrokeWidth(200);
            RectF rectf2=new RectF(140,500,940,1300);

            DBManager dbManager=new DBManager(MyApplication.getInstance());
            Cursor c=dbManager.query("select * from timeinfo where datenum="+String.valueOf(a[pickedday][0]));

            int begin=0;
            int end=0;
            while(c.moveToNext())
            {
                if(end==c.getInt(2))
                {
                    end=c.getInt(3);
                }
                else
                {
                    canvas.drawArc(rectf2,(float)(begin/240.0-90),(float)((end-begin)/240.0),false,p);
                    begin=c.getInt(2);
                    end=c.getInt(3);
                }
            }
            canvas.drawArc(rectf2,(float)(begin/240.0-90),(float)((end-begin)/240.0),false,p);
            c.close();
            dbManager.closeDB();
            p.setTypeface(MyApplication.gettypeface());
            p.setTextSize(140);
            String time="";
            int seconds=a[pickedday][1];
            if(seconds/3600<10)time+="0";
            time+=String.valueOf(seconds/3600);
            time+=":";
            long minutes=seconds%3600;
            if(minutes/60<10)time+="0";
            time+=String.valueOf(minutes/60);
            p.setStyle(Paint.Style.FILL);
            canvas.drawText(time,367,950,p);
            p.setTypeface(Typeface.DEFAULT);
            p.setTextSize(45);
            Date d=new Date();
            d.setTime(1405353600000L+(long)a[pickedday][0]*86400000L);
            String date=String.valueOf(1900+d.getYear())+"年"+String.valueOf(1+d.getMonth())+"月"+String.valueOf(d.getDate())+"日详情";
            canvas.drawText(date,350,200,p);
            p.setTextSize(30);
            p.setStyle(Paint.Style.STROKE);
        }
        p.setStrokeWidth(5);
    }
    public void drawmx2(Canvas canvas)
    {
        p.setTextSize(25);
        if(!showdetail)
        {
            p.setStrokeWidth(4);
            p.setStyle(Paint.Style.FILL);
            canvas.drawRect(80, 1038, 720, 1042, p);
            canvas.drawRect(78, 200, 82, 1040, p);

            canvas.drawLine(68, 212, 80, 200, p);
            canvas.drawLine(92, 212, 80, 200, p);
            canvas.drawLine(708, 1028, 720, 1040, p);
            canvas.drawLine(708, 1052, 720, 1040, p);

            canvas.drawText("日期",730,1050,p);
            canvas.drawText("使用时间",30,180,p);

            int maxy=0;
            for(int i=0;i<10;i++)
            {
                if(a[i][1]>maxy)maxy=a[i][1];
            }
            if(maxy%3600!=0)
            {
                maxy=maxy-maxy%3600+3600;
            }
            float standard=(float) (800.0/maxy);
            for(int k=1;k<maxy/3600+1;k++)
            {
                canvas.drawRect(80,1040-k*3600*standard,88,1040-k*3600*standard+4, p);
                canvas.drawText(String.valueOf(k)+"h",k>9?38:50,1050-k*3600*standard,p);
            }


            for(int i=0;i<10;i++)
            {
                Date d=new Date();
                d.setTime(1405353600000L+(long)a[i][0]*86400000L);

                canvas.drawCircle(140+60*i,1040-a[i][1]*standard, 8, p);
                canvas.drawText(String.valueOf(d.getDate()),130+60*i,1070,p);
                location[i][0]=140+60*i;
                location[i][1]=1040-a[i][1]*standard;
            }
            for(int i=0;i<9;i++)
            {
                canvas.drawLine(location[i][0], location[i][1], location[i+1][0], location[i+1][1], p);
            }
        }
        else
        {
            p.setStrokeWidth(8);
            p.setStyle(Paint.Style.FILL);
            canvas.drawLine(400,440,400,460, p);
            canvas.drawLine(400,820,400,840, p);
            canvas.drawLine(200,640,220,640, p);
            canvas.drawLine(580,640,600,640, p);


            canvas.drawText("0",395,485, p);
            canvas.drawText("12",390,805, p);
            canvas.drawText("6",560,650, p);
            canvas.drawText("18",230,650, p);

            p.setStrokeWidth(5);
            p.setStyle(Paint.Style.STROKE);
            canvas.drawArc(new RectF(50,290,750,990),0,360,false, p);
            canvas.drawArc(new RectF(200,440,600,840),0,360,false, p);
            RectF rectf1=new RectF(205,445,595,835);
            p.setStrokeWidth(10);
            for(int i=0;i<24;i++)
            {
                canvas.drawArc(rectf1,(float) (i*15-0.5),1,false,p);
            }

            p.setStrokeWidth(150);
            RectF rectf2=new RectF(125,365,675,915);
            DBManager dbManager=new DBManager(MyApplication.getInstance());
            Cursor c=dbManager.query("select * from timeinfo where datenum="+String.valueOf(a[pickedday][0]));
            dbManager.closeDB();
            int begin=0;
            int end=0;
            while(c.moveToNext())
            {
                if(end==c.getInt(2))
                {
                    end=c.getInt(3);
                }
                else
                {
                    canvas.drawArc(rectf2,(float)(begin/240.0-90),(float)((end-begin)/240.0),false,p);
                    begin=c.getInt(2);
                    end=c.getInt(3);
                }
            }
            canvas.drawArc(rectf2,(float)(begin/240.0-90),(float)((end-begin)/240.0),false,p);
            c.close();
            p.setTypeface(MyApplication.gettypeface());
            p.setTextSize(100);
            String time="";
            int seconds=a[pickedday][1];
            if(seconds/3600<10)time+="0";
            time+=String.valueOf(seconds/3600);
            time+=":";
            long minutes=seconds%3600;
            if(minutes/60<10)time+="0";
            time+=String.valueOf(minutes/60);
            p.setStyle(Paint.Style.FILL);
            canvas.drawText(time,280,680,p);
            p.setTypeface(Typeface.DEFAULT);
            p.setTextSize(40);
            Date d=new Date();
            d.setTime(1405353600000L+(long)a[pickedday][0]*86400000L);
            String date=String.valueOf(1900+d.getYear())+"年"+String.valueOf(1+d.getMonth())+"月"+String.valueOf(d.getDate())+"日详情";
            canvas.drawText(date,230,180,p);
            p.setTextSize(30);
            p.setStyle(Paint.Style.STROKE);
        }
        p.setStrokeWidth(5);
    }
}