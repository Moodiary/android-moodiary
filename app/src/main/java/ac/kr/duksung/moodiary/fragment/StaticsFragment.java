package ac.kr.duksung.moodiary.fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import ac.kr.duksung.moodiary.R;

// 화면 설명 : 메인화면의 통계화면
// Author : Soohyun, Last Modified : 2021.03.09
public class StaticsFragment extends Fragment {
    TextView tv_start; // 시작 날짜
    TextView tv_end; // 끝 날짜
    PieChart emotion_chart; // 감정 통계 원형 그래프

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statics, container, false);

        tv_start = view.findViewById(R.id.tv_start);
        tv_end = view.findViewById(R.id.tv_end);
        emotion_chart = view.findViewById(R.id.emotion_chart);

        // 시작 날짜 클릭시
        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        tv_start.setText(year + "/" + (month+1) + "/" + day);
                    }
                }, 2021, 1, 1);
                dialog.show();
            }
        });

        // 끝 날짜 클릭시
        tv_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        tv_end.setText(year + "/" + (month+1) + "/" + day);
                    }
                }, 2021, 1, 1);
                dialog.show();
            }
        });


        // 그래프 설정 및 데이터와 연결하는 부분
        emotion_chart.setUsePercentValues(true);
        emotion_chart.getDescription().setEnabled(false); // 그래프에서 description 삭제
        emotion_chart.setExtraOffsets(10,10,10,10); // 그래프 바탕 여백 설정

        emotion_chart.setDragDecelerationFrictionCoef(0.9f); // 그래프 드래그 했을 때 드래그 되는 크기 설정

        emotion_chart.setDrawHoleEnabled(false); // 그래프 가운데 Hole 삭제
        emotion_chart.setHoleColor(Color.WHITE); // 그래프 가운데 Hole 컬러
        emotion_chart.setTransparentCircleRadius(10f); // Hole의 크기

        // 감정 데이터 리스트
        ArrayList<PieEntry> emotion = new ArrayList<PieEntry>();
        emotion.add(new PieEntry(34f,"기쁨"));
        emotion.add(new PieEntry(23f,"슬픔"));
        emotion.add(new PieEntry(14f,"놀람"));
        emotion.add(new PieEntry(35f,"분노"));
        emotion.add(new PieEntry(40f,"공포"));
        emotion.add(new PieEntry(40f,"혐오"));
        emotion.add(new PieEntry(34f,"중립"));

        PieDataSet dataSet = new PieDataSet(emotion,"Emotions"); // 데이터의 카테고리
        dataSet.setSliceSpace(3); // 그래프의 데이터 사이 간격
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS); // 그래프 색상 테마

        PieData data = new PieData(dataSet); // 데이터를 담는 그릇
        data.setValueTextSize(15); // 데이터 텍스트 크기
        data.setValueTextColor(Color.YELLOW); // 데이터 텍스트 컬러
        emotion_chart.setData(data); // 그래프에 데이터 할당

        return view;
    }
}