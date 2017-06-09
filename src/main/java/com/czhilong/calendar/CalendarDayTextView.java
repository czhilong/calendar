package com.czhilong.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 项目名：  MyApplication
 * 包名：    com.czhilong.calendar
 * 文件名：  CalendarDayTextView
 * 创建者：  czhilong
 * 创建时间： 2017/5/10 2:09
 * 描述：TODO
 */

public class CalendarDayTextView extends TextView {
    boolean isToday=false;
    private Paint paint=new Paint();

    public CalendarDayTextView(Context context) {
        super(context);
    }

    public CalendarDayTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl();
    }

    public CalendarDayTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl();
    }

    private void initControl() {
        paint.setStyle(Paint.Style.STROKE);//进行描边
        paint.setStrokeWidth(8);//设置画笔宽度
        paint.setColor(Color.RED);//设置画笔颜色
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isToday){//判断是否进行红色圆圈绘制包围
            canvas.translate(getWidth()/2,getHeight()/2);
            canvas.drawCircle(0,0,getWidth()/2-5,paint);
        }

    }
}
