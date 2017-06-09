package com.czhilong.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 项目名：  MyApplication
 * 包名：    com.czhilong.calendar
 * 文件名：  NewCalendar
 * 创建者：  czhilong
 * 创建时间： 2017/5/10 0:01
 * 描述：TODO
 */

public class NewCalendar extends LinearLayout {
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView textDate;
    private GridView grid;

    private Calendar currDate=Calendar.getInstance();//获取当前日期
    private String displayFormat;//主标题显示格式
    public NewCalendarListener listener;//定义回调函数，用于使用该类调用回调
    private int currMon;

    public NewCalendar(Context context) {
        super(context);
    }

    public NewCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context,attrs);
    }

    public NewCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context,attrs);
    }

    /**
     * 主函数，用于处理控件所有逻辑
     * @param context
     * @param attrs
     */
    private void initControl(Context context,AttributeSet attrs){
        bindControl(context);
        bindControlEvent();

        TypedArray ta=getContext().obtainStyledAttributes(attrs,R.styleable.NewCalendar);
        try {
            String format=ta.getString(R.styleable.NewCalendar_dateFormat);
            displayFormat=format;
            if(displayFormat==null){
                displayFormat="MMM yyyy";
            }
        }finally {
            ta.recycle();
        }
        renderCalendar();
    }

    /**
     * 给控件定义监听，该方法主要给 前一个月和后一个月设置监听
     */
    private void bindControlEvent() {
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                currDate.add(Calendar.MONTH,-1);
                currMon=currDate.get(Calendar.MONTH);
                renderCalendar();
            }
        });

        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                currDate.add(Calendar.MONTH,1);
                currMon=currDate.get(Calendar.MONTH);
                renderCalendar();
            }

        });
    }

    private void bindControl(Context context) {
        LayoutInflater inflater=LayoutInflater.from(context);
        inflater.inflate(R.layout.calendar_view,this);

        btnPrev= (ImageView) findViewById(R.id.btn_prev);
        btnNext= (ImageView) findViewById(R.id.btn_next);
        textDate= (TextView) findViewById(R.id.text_date);
        grid= (GridView) findViewById(R.id.calendar_grid);

        currMon=currDate.get(Calendar.MONTH);
    }


    private void renderCalendar() {
        SimpleDateFormat sdf=new SimpleDateFormat(displayFormat);
        textDate.setText(sdf.format(currDate.getTime()));

        ArrayList<Date> cells=new ArrayList<Date>();
        Calendar calendar= (Calendar) currDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH,1);//设置当前月份的某天，设置为1号
        int prevDays=calendar.get(Calendar.DAY_OF_WEEK)-1;//因周天的置为1，周一为2，故此值可以表示1号当属周几时前方有几天，类举：当为周天时，prevDays=0，即前方没有日期，当为周一时，prevDays=1，表示前方还有一个
        calendar.add(Calendar.DAY_OF_MONTH,-prevDays);

        int maxCellCount=5*7;
        int extendCellCount=6*7;
        Date nextDay=new Date();

        //获取最后一个cells看是否为本月
        boolean isCurrMonth=false;
        //按5行先行排列
        while(cells.size()<maxCellCount){
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH,1);
            //在判断下一行是否为当前月份，为true表示还需增加一行，为false表示已排满无需增加一行进行显示
            if(calendar.get(Calendar.MONTH)==currDate.get(Calendar.MONTH)){
                isCurrMonth=true;
            }else{
                isCurrMonth=false;
            }
        }
        //根据isCurrMonth判断是否还需添加一行
        if(isCurrMonth){
            while(cells.size()<extendCellCount){
                cells.add(calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH,1);
            }
        }

        grid.setAdapter(new CalendarAdapter(getContext(),cells));
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l) {
                if(listener==null){
                    return false;
                }else{
                    //对每个节点进行设置监听并传一个值
                    listener.onItemLongPress((Date) parent.getItemAtPosition(position));
                    return true;
                }
            }
        });
    }

    private class CalendarAdapter extends ArrayAdapter<Date>{
        LayoutInflater inflater;
        public CalendarAdapter(Context context,  ArrayList<Date> days) {
            super(context, R.layout.calender_text_day, days);
            inflater=LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Date date=getItem(position);
            if(convertView==null){
                convertView= inflater.inflate( R.layout.calender_text_day,parent,false);
            }
            int day= date.getDate();
            ((TextView)convertView).setText(String.valueOf(day));

            //针对本月进行绘制，本月日期变黑，非当月变灰
            Date now=new Date();
            boolean isTheSameMonth=false;
            //判断是否为当月
            if(date.getMonth()==now.getMonth()&&date.getYear()==now.getYear()){
                isTheSameMonth=true;
            }
            if(isTheSameMonth){
                ((TextView)convertView).setTextColor(Color.BLACK);
            }else{
                ((TextView)convertView).setTextColor(Color.GRAY);
            }

            //针对调整月份后，判断是否属于当前月份进行变黑和变灰
            int month=date.getMonth();
            if(isTheSameMonth&&currMon!=month){
                ((TextView)convertView).setTextColor(Color.GRAY);
            }

            if(!isTheSameMonth&&currMon==month){
                ((TextView)convertView).setTextColor(Color.BLACK);
            }

            //当天日期进行红色圆圈标注，使用CalendarDayTextView组件，设置isToday属性即可
            if(now.getDate()==date.getDate()&&now.getMonth()==date.getMonth()&&now.getYear()==date.getYear()){
                ((TextView)convertView).setTextColor(Color.RED);
                ((CalendarDayTextView)convertView).isToday=true;
            }
            return convertView;
        }
    }

    //添加接口用于调用该类进行回调
    public interface NewCalendarListener{
        void onItemLongPress(Date day);
    }
}
