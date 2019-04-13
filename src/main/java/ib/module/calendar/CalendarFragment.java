package ib.module.calendar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * TODO
 */
public class CalendarFragment extends Fragment {
    private static final String ARG_MONTH = "month";
    private static final String ARG_YEAR = "year";
    private static final String ARG_HIDE_MONTH_YEAR_TITLE = "hide_month_year"; //скрывать строку месяц-год
    private static final String ARG_SHOW_BOTTOM_PANEL = "show_bottom_panel"; //всегда показывать панель событий
    private static final String ARG_REAL_POSITION = "real_pos";
    private static final String ARG_MONTH_NAME = "month_name";
    public static final String DAY_NUM = "day_num";
    public static final String DAY_NUM_SELECTED = "day_num_selected";
    public static final String DAY_NUM_CURRENT = "day_num_current";
    public static final String DAY_HAS_EVENT_INDICATOR = "day_num_has_event";
    public static final String TAG_BOTTOM_PANEL = "bottom_panel";
    private int mCurrentMonth;
    private int mCurrentYear;
    private int mRealPos;
    private boolean mIsHiddenMonthYearTitle;
    private boolean mIsAlwaysShowBottomPanel;

    private LinearLayout mBottomPanel;

    private OnMonthInteractionListener mListener;
    private ArrayList<View>  views;

    public CalendarFragment() {
        // Required empty public constructor
    }


    public static CalendarFragment newInstance(int realPosition, int monthValue, int year, ArrayList<Object> viewParams) {

        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MONTH, monthValue);
        args.putInt(ARG_YEAR, year);
        args.putBoolean(ARG_HIDE_MONTH_YEAR_TITLE, (Boolean) viewParams.get(CalendarView.VIEW_PARAM_HIDE_MONTH_YEAR));
        args.putBoolean(ARG_SHOW_BOTTOM_PANEL, (Boolean) viewParams.get(CalendarView.VIEW_PARAM_ALWAYS_SHOW_BOTTOM_PANEL));
        args.putInt(ARG_REAL_POSITION, realPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentMonth       = getArguments().getInt(ARG_MONTH);
            mCurrentYear        = getArguments().getInt(ARG_YEAR);
            mRealPos        = getArguments().getInt(ARG_REAL_POSITION);
            mIsHiddenMonthYearTitle = getArguments().getBoolean(ARG_HIDE_MONTH_YEAR_TITLE);
            mIsAlwaysShowBottomPanel = getArguments().getBoolean(ARG_SHOW_BOTTOM_PANEL);
        }
    }

    public int getRealPosition(){
        return mRealPos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.ib_module_fragment_calendar, container, false);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mCurrentYear);
        calendar.set(Calendar.MONTH, mCurrentMonth - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);


        int calendarCurrentDay   = getCurrentDayNumber();
        int calendarCurrentMonth = getCurrentMonthNumber();
        int calendarCurrentYear  = CalendarView.getCurrentYear();

        int startWeekDayIndex   = 0;
        int startDayCounter     = 1;
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int limitDayMonth  = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if(firstDayOfWeek == 0) {
            startWeekDayIndex = 5;
        }
        else if(firstDayOfWeek == 1) {
            startWeekDayIndex = 6;
        }
        else if(firstDayOfWeek >= 2 && firstDayOfWeek <= 6) {
            startWeekDayIndex = firstDayOfWeek - 2;
        }

        //logDebug("calendarCurrentMonth is " + Integer.toString(calendarCurrentMonth));

        TextView monthWithYearTextView = layout.findViewWithTag("month_year");
        monthWithYearTextView.setText(getMonthName(mCurrentMonth) + " " + Integer.toString(mCurrentYear));
        LinearLayout calendarBaseLayout = layout.findViewWithTag("my_calendar");
        CalendarDayClickListener dayClickListener = new CalendarDayClickListener(getContext(), mListener, mCurrentMonth, mCurrentYear);
        //Пробросить нижнюю панель
        mBottomPanel = calendarBaseLayout.findViewWithTag(CalendarFragment.TAG_BOTTOM_PANEL);
        if(mIsAlwaysShowBottomPanel) {
            mBottomPanel.setVisibility(View.VISIBLE);
        }
        else
        {
            mBottomPanel.setVisibility(View.GONE);
        }

        dayClickListener.setBottomPanel(mBottomPanel, mIsAlwaysShowBottomPanel);

        if(mIsHiddenMonthYearTitle) {
            layout.findViewWithTag("month_year_title").setVisibility(View.GONE);
        }
        views = new ArrayList<>();
        views.add(CalendarView.VIEW_MONTH_YEAR, monthWithYearTextView);

        boolean isFirstWeek = true;
        /**
         * Перебор недель (могут попасться лишние, по тегу отрезаем)
         */
        for(int i = 0; i < calendarBaseLayout.getChildCount(); i++) {
            if(calendarBaseLayout.getChildAt(i).getTag() != null) {
                String viewTag = calendarBaseLayout.getChildAt(i).getTag().toString();
                //logDebug("view week line tag is " + viewTag);
                /**
                 * Попали в строку недели
                 */
                if(viewTag.equals("week_line")) {
                    LinearLayout weekLine = (LinearLayout) calendarBaseLayout.getChildAt(i);
                    int weekDayIndex = 0;
                    //logDebug("weekDayIndex is " + Integer.toString(weekDayIndex));
                    /**
                     * Перебор дней
                     */
                    for(int n = 0; n < weekLine.getChildCount(); n++) {
                        if(weekLine.getChildAt(n).getTag() != null) {
                            RelativeLayout dayContainer = (RelativeLayout) weekLine.getChildAt(n);
                            if(startDayCounter >= 1 && startDayCounter <= 31) {
                                views.add(startDayCounter, dayContainer);
                            }

                            String weekLineItemTag = dayContainer.getTag().toString();
                            //logDebug("view week ITEM tag is " + weekLineItemTag);
                            //Этот отображаем когда все дни
                            TextView dayText = weekLine.getChildAt(n).findViewWithTag(DAY_NUM);
                            //Этот вью отображаем когда выделен день
                            TextView dayTextSelected = weekLine.getChildAt(n).findViewWithTag(DAY_NUM_SELECTED);
                            //Этот вью отображаем когда текущий месяц и текущий день, то есть он должен стать dayText.ом
                            TextView dayTextCurrent = weekLine.getChildAt(n).findViewWithTag(DAY_NUM_CURRENT);
                            Boolean isVisibleDay = true;
                            if(isFirstWeek && weekDayIndex < startWeekDayIndex) {
                                dayText.setText("");
                                dayTextSelected.setText("");
                                dayTextCurrent.setText("");
                                isVisibleDay = false;
                            }
                            else if(isFirstWeek && weekDayIndex >= startWeekDayIndex) {
                                dayText.setText(Integer.toString(startDayCounter));
                                dayTextSelected.setText(Integer.toString(startDayCounter));
                                dayTextCurrent.setText(Integer.toString(startDayCounter));
                                startDayCounter++;
                            }
                            else {
                                if(startDayCounter <= limitDayMonth) {
                                    dayText.setText(Integer.toString(startDayCounter));
                                    dayTextSelected.setText(Integer.toString(startDayCounter));
                                    dayTextCurrent.setText(Integer.toString(startDayCounter));
                                    startDayCounter++;
                                }
                                else {
                                    dayText.setText("");
                                    dayTextSelected.setText("");
                                    dayTextCurrent.setText("");
                                    isVisibleDay = false;
                                }
                            }
                            /**
                             * Подсветим текущий день если месяц и день ==
                             */
                            if(calendarCurrentDay == startDayCounter-1
                                    && calendarCurrentYear == mCurrentYear
                                    && mCurrentMonth == calendarCurrentMonth) {
                                dayContainer.removeView(dayText);
                                dayTextCurrent.setTag(DAY_NUM);
                                CalendarFragment.highLightDay(dayTextCurrent, dayTextSelected);

                                dayClickListener.setPrevSelectedDayText(dayTextCurrent);
                                dayClickListener.setPrevSelectedDayTextSelected(dayTextSelected);
                            }
                            else {
                                dayContainer.removeView(dayTextCurrent);
                            }

                            if(isVisibleDay) {
                                dayContainer.setOnClickListener(dayClickListener);
                            }
                            weekDayIndex++;
                        }
                    }
                    isFirstWeek = false;
                }

            }
        }

        //mListener.onCalendarCreateView(views, mCurrentMonth, mCurrentYear);


        return layout;
    }

    /**
     *
     * @param index
     * @return
     */
    private String getMonthName(final int index)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLLL");
        GregorianCalendar c = new GregorianCalendar(1900, index-1, 1);

        return dateFormat.format(c.getTime());
    }

    private int getCurrentDayNumber(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("d");
        String format =  dateFormat.format(GregorianCalendar.getInstance().getTime());

        return Integer.parseInt(format);
    }

    private int getCurrentMonthNumber(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("M");
        String format =  dateFormat.format(GregorianCalendar.getInstance().getTime());

        return Integer.parseInt(format);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment parentFrag = getParentFragment();
        if(parentFrag != null) {
            if(parentFrag instanceof OnMonthInteractionListener) {
                mListener = (OnMonthInteractionListener) parentFrag;
                //logDebug("onAttach to " + mListener.getClass().toString());
            }
            else {
                throw new RuntimeException(context.toString()
                        + " must implement OnMonthInteractionListener");
            }
        }
        else {
            if (context instanceof OnMonthInteractionListener) {
                mListener = (OnMonthInteractionListener) context;
                //logDebug("onAttach to " + mListener.getClass().toString());
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnMonthInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnMonthInteractionListener {
        void onCalendarMonthChange(ArrayList<View> views, int selectedMonth, int selectedYear);
        void onCalendarDayClick(RelativeLayout dayView, TextView selectedDayView, int selectedMonth, int selectedYear);
        void onCalendarBottomPanelShow(ScrollView bottomPanel, RelativeLayout dayView, TextView selectedDayView, int selectedMonth, int selectedYear);
        void onCalendarBottomPanelHide(ScrollView bottomPanel, RelativeLayout dayView, TextView selectedDayView, int selectedMonth, int selectedYear);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getUserVisibleHint()){
            mListener.onCalendarMonthChange(views, mCurrentMonth, mCurrentYear);
        }
    }

    public void onPageSelected(){
        if(getUserVisibleHint()){
            mListener.onCalendarMonthChange(views, mCurrentMonth, mCurrentYear);
        }
    }

    /**
     * Подсветить день
     * @param dayContainer
     */
    public static void highLightDay(RelativeLayout dayContainer){
        TextView dayView = dayContainer.findViewWithTag(CalendarFragment.DAY_NUM);
        TextView dayViewSelected = dayContainer.findViewWithTag(CalendarFragment.DAY_NUM_SELECTED);

        if(dayViewSelected.getVisibility() == View.GONE) {
            dayViewSelected.setVisibility(View.VISIBLE);
            dayView.setVisibility(View.GONE);
        }
    }

    
    public static void highLightDay(TextView dayView, TextView dayViewSelected){
        if(dayViewSelected.getVisibility() == View.GONE) {
            dayViewSelected.setVisibility(View.VISIBLE);
            dayView.setVisibility(View.GONE);
        }
    }

    protected void logDebug(String msg) {
        Log.d(getClass().getName(), msg);
    }
}
