package info.walsli.timestatistics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;


public class MainView extends View {
    private long seconds=0;
    private int hour=0;
    private int screenon_frequency=1;
    int screenWidth;
    int screenHeight;
    RectF rectf1;
    RectF rectf2;
    RectF rectf3;
    RectF rectf4;


    public MainView(Context context, AttributeSet attr) {
        super(context,attr);
    }
    public void setTime(long s,int screenon_frequency,int hour)
    {
        this.seconds=s;
        this.screenon_frequency=screenon_frequency;
        this.hour=hour;
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
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setColor(Color.WHITE);
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
        canvas.drawText(ConstantField.MAINVIEW_TOP_STRING,(float) (screenWidth/2.0),getBaseLine(p,rectf1),p);

        String screenon_frequency_string="使用次数  "+String.valueOf(screenon_frequency);
        canvas.drawText(screenon_frequency_string,(float) (screenWidth/2.0),getBaseLine(p,rectf3),p);

        if(hour==-1)
        {
            canvas.drawText(ConstantField.MAINVIEW_DATA_RESTORE_STRING_1,(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)-screenWidth/20.0),p);
            canvas.drawText(ConstantField.MAINVIEW_DATA_RESTORE_STRING_2,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
        }
        else if(seconds/3600>=2)
        {
            canvas.drawText(ConstantField.MAINVIEW_PROTECT_EYESIGHT_STRING_1,(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)-screenWidth/20.0),p);
            canvas.drawText(ConstantField.MAINVIEW_PROTECT_EYESIGHT_STRING_2,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
            canvas.drawText(ConstantField.MAINVIEW_PROTECT_EYESIGHT_STRING_3,(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)+screenWidth/20.0),p);
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
                    canvas.drawText(ConstantField.MainView_SAYINGS_1,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 6:
                    canvas.drawText(ConstantField.MainView_SAYINGS_2_1,(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)-screenWidth/20.0),p);
                    canvas.drawText(ConstantField.MainView_SAYINGS_2_2,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 7:
                    canvas.drawText(ConstantField.MainView_SAYINGS_3,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                    canvas.drawText(ConstantField.MainView_SAYINGS_4,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 12:
                    canvas.drawText(ConstantField.MainView_SAYINGS_5,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 13:
                case 14:
                case 15:
                case 16:
                    canvas.drawText(ConstantField.MainView_SAYINGS_6_1,(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)-screenWidth/20.0),p);
                    canvas.drawText(ConstantField.MainView_SAYINGS_6_2,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 17:
                case 18:
                    canvas.drawText(ConstantField.MainView_SAYINGS_7,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 19:
                case 20:
                    canvas.drawText(ConstantField.MainView_SAYINGS_8_1,(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)-screenWidth/20.0),p);
                    canvas.drawText(ConstantField.MainView_SAYINGS_8_2,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                case 21:
                case 22:
                case 23:
                    canvas.drawText(ConstantField.MainView_SAYINGS_9,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
                default:
                    canvas.drawText(ConstantField.MainView_SAYINGS_10_1,(float) (screenWidth/2.0),(float) (getBaseLine(p,rectf4)-screenWidth/20.0),p);
                    canvas.drawText(ConstantField.MainView_SAYINGS_10_2,(float) (screenWidth/2.0),getBaseLine(p,rectf4),p);
                    break;
            }
        }

    }
    protected float getBaseLine(Paint p,RectF rectf)
    {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return rectf.top + (rectf.bottom - rectf.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
    }

}