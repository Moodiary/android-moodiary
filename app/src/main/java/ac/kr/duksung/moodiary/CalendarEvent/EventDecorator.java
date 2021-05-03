package ac.kr.duksung.moodiary.CalendarEvent;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;

// 화면 설명 : 일기 커스텀 자바클래스
// Author : Seungyeon, Last Modified : 2021.04.30

public class EventDecorator implements DayViewDecorator {

    private int color;
    private ArrayList<CalendarDay> dates;

    public EventDecorator(int color, ArrayList<CalendarDay> dates) {
        this.color = color;
        this.dates = new ArrayList<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(10, color));
    }
}
