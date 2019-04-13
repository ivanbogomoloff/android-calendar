# android-calendar
Android calendar module / Модуль календаря для android OS

![alt text](https://github.com/ivanbogomoloff/android-calendar/raw/master/calendar.png)

```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support:support-v4:28.0.0'
}
```

# How to install / Как установить

1. Clone module into your root project dir
2. Add implementation to build.gradle
```
implementation project(':calendar')
```
3. *For usage in Activity* :
```java
public class MainActivity extends AppCompatActivity implements CalendarFragment.OnMonthInteractionListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendar);
        calendarView.initialize(2017, 2021, getSupportFragmentManager());
    }
}
```
4. *For usage in Fragment*:
```java
public class FragmentCalendar extends Fragment implements
        CalendarFragment.OnMonthInteractionListener,
        View.OnClickListener {
@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        ib.module.calendar.CalendarView calendar = view.findViewById(R.id.calendar);
        int currentYear = calendar.getCurrentYear();
        calendar.alwaysShowBottomPanel(); //always show bottom panel
        calendar.hideMonthYearTitle(); //hide month year title
        calendar.initialize(currentYear - 1, currentYear + 2, getChildFragmentManager());
        return view;
    }
}
```
5. *Implement CalendarFragment.OnMonthInteractionListener*:
```java
    //This method invoke only when user see current month
    @Override
    public void onCalendarMonthChange(ArrayList<View> views, int selectedMonth, int selectedYear) {
        Log.d("onCalendarMonthChange selected month from 1 to 12", Integer.toString(selectedMonth));
        Log.d("onCalendarMonthChange selected year in YYYY format like 2019...", Integer.toString(selectedYear));
    }
```
6. *Other callbacks*:
```java
@Override
    public void onCalendarDayClick(RelativeLayout dayView, TextView selectedDayView, int selectedMonth, int selectedYear) {

    }
    //When alwaysShowBottomPanel is false.
    @Override
    public void onCalendarBottomPanelShow(ScrollView bottomPanel, RelativeLayout dayView, TextView selectedDayView, int selectedMonth, int selectedYear) {
        /**
         * Client code must implement own method for render items in bottom panel like bottomPanel.addView(youtItem);
         * calendarView.renderBottomPanelItems only for example!
         */
        calendarView.renderBottomPanelItems(bottomPanel, selectedDayView.getText().toString());
        Log.d(getClass().toString(), "onCalendarBottomPanelShow");
    }
    //When alwaysShowBottomPanel is false.
    @Override
    public void onCalendarBottomPanelHide(ScrollView bottomPanel, RelativeLayout dayView, TextView selectedDayView, int selectedMonth, int selectedYear) {
      //do something
    }
```