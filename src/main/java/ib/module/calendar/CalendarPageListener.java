package ib.module.calendar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class CalendarPageListener extends ViewPager.SimpleOnPageChangeListener {
    FragmentManager fm;
    public CalendarPageListener(FragmentManager fm) {
        this.fm = fm;
    }

    @Override
    public void onPageSelected(int position) {
        CalendarFragment calendarFragment;
        for(Fragment fragment : fm.getFragments()) {
            calendarFragment = (CalendarFragment) fragment;
            if(calendarFragment.getRealPosition() == position) {
                calendarFragment.setUserVisibleHint(true);
                calendarFragment.onPageSelected();
                break;
            }
        }
    }
}
