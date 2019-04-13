package ib.module.calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class CalendarDayClickListener implements View.OnClickListener  {
    private Context ctx;
    private CalendarFragment.OnMonthInteractionListener activity;
    private int month;
    private int year;
    private TextView prevSelectedDayText;
    private TextView prevSelectedDayTextSelected;
    private LinearLayout bottomPanel;
    private Boolean alwaysShowBottomPanel;

    public CalendarDayClickListener(Context context, CalendarFragment.OnMonthInteractionListener listener, int m, int y) {
        ctx = context;
        activity = listener;
        month = m;
        year = y;
    }

    public void setBottomPanel(LinearLayout bottomPanel) {
        this.bottomPanel = bottomPanel;
    }

    public void setBottomPanel(LinearLayout bottomPanel, Boolean alwaysShow) {
        this.bottomPanel = bottomPanel;
        alwaysShowBottomPanel = alwaysShow;
    }

    public void setPrevSelectedDayText(TextView prevSelectedDayText) {
        this.prevSelectedDayText = prevSelectedDayText;
    }

    public void setPrevSelectedDayTextSelected(TextView prevSelectedDayTextSelected) {
        this.prevSelectedDayTextSelected = prevSelectedDayTextSelected;
    }

    @Override
    public void onClick(View v) {
        RelativeLayout dayContainer = (RelativeLayout) v;
        TextView dayView = dayContainer.findViewWithTag(CalendarFragment.DAY_NUM);
        TextView dayViewSelected = dayContainer.findViewWithTag(CalendarFragment.DAY_NUM_SELECTED);

        CalendarFragment.highLightDay(dayView, dayViewSelected);

        if(prevSelectedDayText != null) {
            if(prevSelectedDayText.getText() != dayView.getText()) {
                prevSelectedDayText.setVisibility(View.VISIBLE);
                prevSelectedDayTextSelected.setVisibility(View.GONE);
            }
        }

        prevSelectedDayText = dayView;
        prevSelectedDayTextSelected = dayViewSelected;

        /**
         * Управление показом и скрытием нижний панели
         */
        TextView eventIndicator = dayContainer.findViewWithTag(CalendarFragment.DAY_HAS_EVENT_INDICATOR);
        ScrollView bottomPanelScroll = bottomPanel.findViewWithTag("bottom_panel_scroll_view");
        /**
         * Если настроено всегда отображать нижнюю панель.
         */
        if(alwaysShowBottomPanel)
        {
            activity.onCalendarBottomPanelShow(bottomPanelScroll, dayContainer, prevSelectedDayTextSelected, month, year);
        }
        else {
            if(eventIndicator.getVisibility() == View.VISIBLE) {
                bottomPanel.setVisibility(View.VISIBLE);
                activity.onCalendarBottomPanelShow(bottomPanelScroll, dayContainer, prevSelectedDayTextSelected, month, year);
            }
            else {
                activity.onCalendarBottomPanelHide(bottomPanelScroll, dayContainer, prevSelectedDayTextSelected, month, year);
                bottomPanel.setVisibility(View.GONE);
            }
        }

        activity.onCalendarDayClick(dayContainer, prevSelectedDayTextSelected, month, year);
    }
}
