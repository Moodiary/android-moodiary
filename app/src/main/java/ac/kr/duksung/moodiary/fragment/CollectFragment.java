package ac.kr.duksung.moodiary.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import ac.kr.duksung.moodiary.R;

// 화면 설명 : 메인화면의 모아보기 화면
// Author : Soohyun, Last Modified : 2021.04.09
public class CollectFragment extends Fragment {

    CalendarView diary_calendar; // 달력
    TextView diary_content; // 일기내용



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, container, false);

        diary_calendar = view.findViewById(R.id.diary_calendar);
        diary_content = view.findViewById(R.id.diary_content);

        diary_calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){

            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                diary_content.setVisibility(View.VISIBLE);
            }
        });
        return inflater.inflate(R.layout.fragment_collect, container, false);
    }



}