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

public class ClockView extends View {
    private long seconds=0;
    private int hour=0;
    private int screenon_frequency=1;

    DisplayMetrics dm = this.getResources().getDisplayMetrics();
    int screenWidth = dm.widthPixels;
    int screenHeight = dm.heightPixels;
    RectF rectf1=new RectF(0,(int) (screenHeight/8.0),screenWidth,(int) (screenHeight/8.0));
    RectF rectf2=new RectF((int) (0.3*screenWidth),(int) (screenHeight/3.0-0.2*screenWidth),(int) (0.7*screenWidth),(int) (screenHeight/3.0+0.2*screenWidth));
    RectF rectf3=new RectF(0,(int) (screenHeight*0.55),screenWidth,(int) (screenHeight*0.55));
    RectF rectf4=new RectF(0,(int) (screenHeight*0.7),screenWidth,(int) (screenHeight*0.7));

    public ClockView(Context context,long seconds) {
        super(context);
        this.seconds=seconds;
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
    public void refreshclock(long s,int screenon_frequency,int hour)
    {
        this.seconds=s;
        this.screenon_frequency=screenon_frequency;
        this.hour=hour;
        postInvalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p=initPaint();
        if(seconds%60!=0)
        {
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth((float) 5.0);
            canvas.drawArc(rectf2,(seconds%60)*6-90,360-(seconds%60)*6,false, p);
        }
        //时刻
        String time="";
        if(seconds/3600<10)time+="0";
        time+=String.valueOf(seconds/3600);
        time+=":";
        long minutes=seconds%3600;
        if(minutes/60<10)time+="0";
        time+=String.valueOf(minutes/60);

        p.setStyle(Paint.Style.FILL);
        p.setSubpixelText(true);
        p.setTextSize((float) (screenWidth/10.0));
        p.setTypeface(MyApplication.gettypeface());

        p.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(time,(float) (screenWidth/2.0),getBaseLine(p,rectf2),p);
        p.setTypeface(Typeface.DEFAULT);

        p.setTextSize((float) (screenWidth/30.0));
        String topwords="今天我的手机被使用了";
        canvas.drawText(topwords,(float) (screenWidth/2.0),getBaseLine(p,rectf1),p);

        String screenon_frequency_string="使用次数  "+String.valueOf(screenon_frequency);
        canvas.drawText(screenon_frequency_string,(float) (screenWidth/2.0),getBaseLine(p,rectf3),p);

        String saying;
        if(seconds/3600>=2)
        {
            saying="你已经使用超过两个小时的时间，影响视力";
            canvas.drawText(saying,(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)-screenWidth/20.0),p);
            saying="不要只顾着低头社交，也许你抬起头就可以";
            canvas.drawText(saying,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
            saying="发现更多的美好，抬起头来动一动吧";
            canvas.drawText(saying,(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)+screenWidth/20.0),p);
        }
        else
        {
            switch(hour)
            {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    saying="把活着的每一天看作生命的最后一天";
                    canvas.drawText(saying,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 6:
                    saying="盛年不再来  一日难再晨";
                    canvas.drawText(saying,(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)-screenWidth/20.0),p);
                    saying="及时当自勉  岁月不待人";
                    canvas.drawText(saying,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 7:
                    saying="早餐要吃饱";
                    canvas.drawText(saying,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                    saying="完成工作的方法是爱惜每一分钟";
                    canvas.drawText(saying,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 12:
                    saying="午餐要吃好";
                    canvas.drawText(saying,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 13:
                case 14:
                case 15:
                case 16:
                    saying="普通人只想如何度过时间";
                    canvas.drawText(saying,(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)-screenWidth/20.0),p);
                    saying="有才能的人才能利用时间";
                    canvas.drawText(saying,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 17:
                case 18:
                    saying="晚餐要吃少";
                    canvas.drawText(saying,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 19:
                case 20:
                    saying="黑夜到临的时候";
                    canvas.drawText(saying,(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)-screenWidth/20.0),p);
                    saying="没有人能够把一角阳光继续保留";
                    canvas.drawText(saying,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 21:
                case 22:
                case 23:
                    saying="把活着的每一天看作生命的最后一天";
                    canvas.drawText(saying,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                default:
                    saying="不要为已消逝之年华叹息";
                    canvas.drawText(saying,(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)-screenWidth/20.0),p);
                    saying="须正视欲匆匆溜走的时光";
                    canvas.drawText(saying,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
            }
        }

    }

}