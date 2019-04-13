package ib.module.calendar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

/**
 * TODO
 */
public class CalendarAdapter extends FragmentStatePagerAdapter {
    private int countMonths;
    private int startYear;
    private int year;
    private int month;
    private boolean isFirstRun = true;

    private ArrayList<Object> viewParams;

    public CalendarAdapter(FragmentManager fm, int months, int startYear, int year, int month, boolean isFirstRun) {
        super(fm);
        countMonths         = months;
        this.year           = year;
        this.startYear      = startYear;
        this.month          = month;
        viewParams          = new ArrayList<>();
        this.isFirstRun     = isFirstRun;
    }

    public CalendarAdapter addViewParam(int index, Object v) {
        viewParams.add(index, v);

        return this;
    }

    @Override
    public Fragment getItem(int i) {
        int monthPosition  = i + 1;
        int calculatedYear = 0;
        int calculatedMonth = 0;

        if(isFirstRun) {
            isFirstRun = false;
            calculatedMonth     = month;
            calculatedYear      = year;
        }
        else {
            calculatedYear = startYear + i / CalendarView.MONTHS_PER_YEAR;
            calculatedMonth = monthPosition - ((calculatedYear - startYear) * CalendarView.MONTHS_PER_YEAR);
        }

        Log.d(getClass().toString(), "getItem = " + Integer.toString(monthPosition));
        Log.d(getClass().toString(), "calculatedMonth = " + Integer.toString(calculatedMonth));

        return CalendarFragment.newInstance(i, calculatedMonth, calculatedYear, viewParams);
    }

    @Override
    public int getCount() {
        return countMonths;
    }
}
