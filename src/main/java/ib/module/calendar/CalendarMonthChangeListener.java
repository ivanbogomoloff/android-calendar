package ib.module.calendar;

import android.support.v4.view.ViewPager;
import android.util.Log;

/**
 * TODO
 */
public class CalendarMonthChangeListener extends ViewPager.SimpleOnPageChangeListener {
    @Override
    public void onPageSelected(int realPosition) {
        Log.d(getClass().getName().toString(), "realPosition is " + Integer.toString(realPosition));
    }
}
