package ib.module.calendar;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * TODO
 */
public class CalendarView extends ViewPager {
    /**
     * Months constants
     */
    public enum MONTH {
        January(1), February(2), March(3),
        April(4), May(5), June(6),
        July(7), August(8), September(9),
        October(10), November(11), December(12);

        private int val;

        MONTH(int v){
            this.val = v;
        }

        public int getVal(){
            return val;
        }
    }

    final public static int MONTHS_PER_YEAR = 12;
    final public static int VIEW_MONTH_YEAR = 0;

    final public static int VIEW_PARAM_HIDE_MONTH_YEAR = 0; //скрывать месяц-год в строке перед дням неделе 1 - да
    final public static int VIEW_PARAM_ALWAYS_SHOW_BOTTOM_PANEL = 1; // всегда показывать нижнюю панель

    final public static String VIEW_TAG_DAY_NUM_EVENT = "day_num_has_event";
    final public static String VIEW_TAG_DAY_NUM_TEXT = CalendarFragment.DAY_NUM;

    private  Integer startYear;
    private  Integer endYear;
    /**
     * @example 2018-2019 = 19 - 18 = 1 * 12 = 12 months
     */
    private  Integer monthsCount;

    private  MONTH month;
    private  Integer year;
    private  Integer date;

    private boolean hideMonthYearTitle;
    private boolean isAlwaysShowBottomPanel = false;

    private  CalendarAdapter adapter;
    private  FragmentManager fragmentManager;
    private  boolean isFirstRun = true;

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        logDebug("CalendarView(Context context, AttributeSet attrs)");
    }

    public void setMonth(MONTH month) {
        this.month = month;
    }


    public void setYear(int year){
        this.year = year;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void hideMonthYearTitle(){
        hideMonthYearTitle = true;
    }

    public void showMonthYearTitle(){
        hideMonthYearTitle = false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        logDebug("onLayout");
        super.onLayout(changed, l, t, r, b);
        logDebug("onLayout end");
    }

    public static int getCurrentDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("d");
        String format =  dateFormat.format(GregorianCalendar.getInstance().getTime());

        return Integer.parseInt(format);
    }

    public static int getCurrentYear(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("Y");
        String format =  dateFormat.format(GregorianCalendar.getInstance().getTime());

        return Integer.parseInt(format);
    }

    private int getCurrentMonth(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("M");
        String format =  dateFormat.format(GregorianCalendar.getInstance().getTime());

        return Integer.parseInt(format);
    }

    public Integer getDate() {
        return date;
    }

    public void alwaysShowBottomPanel(){
        isAlwaysShowBottomPanel = true;
    }

    /**
     * @example initialize(2018, 2020, getSupportFragmentManager())
     *
     * @param fromYear
     * @param toYear
     * @param fm
     */
    public void initialize(int fromYear, int toYear, FragmentManager fm) {
        if(isFirstRun) {
            isFirstRun = false;
        }
        if(year == null) {
            year = CalendarView.getCurrentYear();
        }
        if(month == null) {
            month = MONTH.values()[getCurrentMonth()-1];
        }
        if(date == null) {
            date = CalendarView.getCurrentDate();
        }
        logDebug("initialize start");
        startYear           = fromYear;
        endYear             = toYear;
        fragmentManager     = fm;
        monthsCount         = calculateMonthsCount();
        adapter             = new CalendarAdapter(fragmentManager, monthsCount, startYear, year, month.getVal(), date, isFirstRun);
        adapter.addViewParam(CalendarView.VIEW_PARAM_HIDE_MONTH_YEAR, hideMonthYearTitle);
        adapter.addViewParam(CalendarView.VIEW_PARAM_ALWAYS_SHOW_BOTTOM_PANEL, isAlwaysShowBottomPanel);

        setAdapter(adapter);
        setCurrentItem(month.getVal());
        addOnPageChangeListener(new CalendarPageListener(fragmentManager));
        logDebug("initialize end");
    }

    /**
     * Пример как постротить события.
     * Клиентский код должен сам реализовать это метод в своем классе.
     * !!!!!!!
     * Это лишь пример.
     * TODO в release версии здесь должен быть Exception
     * @param bottomPanel
     */
    public void renderBottomPanelItems(ScrollView bottomPanel, String dayNumberString){
        //throw new Exception("Not for client code");
        for(int i = 0; i < bottomPanel.getChildCount();i++){
            if(bottomPanel.getChildAt(i) != null) {
                bottomPanel.removeView(bottomPanel.getChildAt(i));
            }
        }

        int numberDays = Integer.parseInt(dayNumberString) / 2;
        if(numberDays == 0) {
            numberDays = 2;
        }

        View layout = inflate(getContext(), R.layout.ib_module_calendar_bottom_panel_layout, null);
        for(int i = 0; i <= numberDays; i++) {
            View item = inflate(getContext(), R.layout.ib_module_calendar_bottom_panel_item, null);
            item = (LinearLayout) item;
            TextView eventDateTime = item.findViewWithTag("event_datetime");
            eventDateTime.setText(eventDateTime.getText() + " - " + Integer.toString(i));
            ((LinearLayout) layout).addView(item);
        }

        bottomPanel.addView(layout);
    }


    @Override
    public void setCurrentItem(int item) {
        /**
         * В item придет месяц, от 1 до 12.
         * В ViewPager диапазон по месяцам от 0 до N месяцев
         * Переводим месяц в диапазон
         */
        int deltaMonths    = (year - startYear) * MONTHS_PER_YEAR;
        int pagePosition = (deltaMonths + item) - 1;
        logDebug("page position is " + Integer.toString(pagePosition));

        super.setCurrentItem(pagePosition);
    }

    protected int calculateMonthsCount() {
        int delta = endYear - startYear;
        if(delta <= 0) {
            String msg = "invalid startYear and endYear. EndYear must be greater than startYear";
            logError(msg);
            throw new RuntimeException(msg);
        }

        return delta * MONTHS_PER_YEAR;
    }

    private void logError(String msg) {
        Log.e(getClass().toString(), msg);
    }

    private void logDebug(String msg) {
        Log.d(getClass().toString(), msg);
    }
}
