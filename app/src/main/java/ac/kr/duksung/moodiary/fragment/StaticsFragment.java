package ac.kr.duksung.moodiary.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ac.kr.duksung.moodiary.R;

// 화면 설명 : 메인화면의 통계화면
// Author : Soohyun, Last Modified : 2021.04.09
public class StaticsFragment extends Fragment {
    TextView tv_start; // 시작 날짜 텍스트뷰
    TextView tv_end; // 끝 날짜 텍스트뷰
    String startDate = ""; // 시작 날짜 데이터
    String endDate = ""; // 끝 날짜 데이터
    PieChart emotion_chart; // 감정 통계 원형 그래프
    int[] colors = {Color.parseColor("#F8F8D9"), Color.parseColor("#FFAA66"), Color.parseColor("#FFEA61"), Color.parseColor("#FF7B5A"),
                    Color.parseColor("#B2DEF2"), Color.parseColor("#C3E0A3"), Color.parseColor("#F3E9BF")}; // 그래프 컬러(행복,슬픔,놀람,분노,공포,혐오,중립)

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statics, container, false);

        tv_start = view.findViewById(R.id.tv_start);
        tv_end = view.findViewById(R.id.tv_end);
        emotion_chart = view.findViewById(R.id.emotion_chart);

        getDate(); // 시작&끝 날짜 초기화 메소드 실행

        // 시작 날짜 클릭시
        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        startDate = year + "-" + (month + 1) + "-" + day; // 시작날짜 재설정
                        tv_start.setText(startDate);
                        requestStatics(startDate, endDate); // 통계 데이터 가져오는 메소드 실행
                    }
                }, 2021, 1, 1);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                positiveButton.setTextColor(Color.parseColor("#000000"));
                positiveButton.setBackgroundColor(Color.TRANSPARENT);

                negativeButton.setTextColor(Color.parseColor("#000000"));
                negativeButton.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        // 끝 날짜 클릭시
        tv_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        endDate = year + "-" + (month + 1) + "-" + day; // 끝날짜 재설정
                        tv_end.setText(year + "-" + (month + 1) + "-" + day);
                        requestStatics(startDate, endDate); // 통계 데이터 가져오는 메소드 실행
                    }
                }, 2021, 1, 1);
                dialog.show();

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                positiveButton.setTextColor(Color.parseColor("#000000"));
                positiveButton.setBackgroundColor(Color.TRANSPARENT);

                negativeButton.setTextColor(Color.parseColor("#000000"));
                negativeButton.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        return view;
    }

    // 시작&끝 날짜 초기화 메소드
    public void getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // 날짜 데이터 형식

        // 오늘 날짜 기준 한달전 날짜 가져오기
        Calendar mon = Calendar.getInstance();
        mon.add(Calendar.MONTH , -1);
        startDate = dateFormat.format(mon.getTime());

        // 오늘 날짜 가져오기
        Date today = new Date();
        endDate = dateFormat.format(today);

        // 시작&끝 날짜 세팅
        tv_start.setText(startDate);
        tv_end.setText(endDate);

        requestStatics(startDate, endDate); // 통계 데이터 가져오는 메소드 실행
    }

    // 통계 데이터 가져오는 메소드
    public void requestStatics(String startDate, String endDate) {

        SharedPreferences auto = this.getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 자동로그인 데이터 저장되어있는 곳
        String user_id = auto.getString("ID",null); // 저장된 아이디 값, 없으면 null

        // 사용자 입력 정보 JSON 형태로 변환
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("user_id", user_id);
            requestJsonObject.put("start", startDate);
            requestJsonObject.put("end", endDate);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // 서버에 데이터 전달
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://172.20.18.162:3000/diary/statics", requestJsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) { // 데이터 전달 후 받은 응답

                try {
                    String result = response.getString("code"); // 응답 메시지 가져오기
                    int pleasure = Integer.parseInt(response.getString("pleasure")); // 행복 횟수
                    int sadness = Integer.parseInt(response.getString("sadness")); // 슬픔 횟수
                    int surprised = Integer.parseInt(response.getString("surprised")); // 놀람 횟수
                    int anger = Integer.parseInt(response.getString("anger")); // 분노 횟수
                    int fear = Integer.parseInt(response.getString("fear")); // 공포 횟수
                    int aversion = Integer.parseInt(response.getString("aversion")); // 혐오 횟수
                    int neutrality = Integer.parseInt(response.getString("neutrality")); // 중립 횟수


                    // 응답 메시지에 따른 처리
                    if(result.equals("400"))
                        Toast.makeText(getContext(),"에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    if(result.equals("204")) {
                        Toast.makeText(getContext(),"저장된 일기가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    if(result.equals("200")) {
                        setGraph(pleasure, sadness, surprised, anger, fear, aversion, neutrality); // 감정 통계 그래프 메소드 실행
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() { // 데이터 전달 및 응답 실패시
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "네트워크 연결 오류", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObject);
    }

    // 감정 통계 그래프를 그리는 메소드
    public void setGraph(int pleasure, int sadness, int surprised, int anger, int fear, int aversion, int neutrality ) {

        // 그래프 설정 및 데이터와 연결하는 부분
        emotion_chart.setUsePercentValues(true);
        emotion_chart.getDescription().setEnabled(false); // 그래프에서 description 삭제
        emotion_chart.setExtraOffsets(10, 10, 10, 10); // 그래프 바탕 여백 설정

        emotion_chart.setDragDecelerationFrictionCoef(0.9f); // 그래프 드래그 했을 때 드래그 되는 크기 설정

        emotion_chart.setDrawHoleEnabled(false); // 그래프 가운데 Hole 삭제
        emotion_chart.setHoleColor(Color.WHITE); // 그래프 가운데 Hole 컬러
        emotion_chart.setTransparentCircleRadius(10f); // Hole의 크기

        emotion_chart.setEntryLabelColor(Color.BLACK); // 라벨 텍스트 컬러

        // 감정 데이터 리스트
        ArrayList<PieEntry> emotion = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        if(pleasure != 0) {
            emotion.add(new PieEntry(pleasure, "행복"));
            colors.add(Color.parseColor("#F8F8D9"));
        }
        if(sadness != 0) {
            emotion.add(new PieEntry(sadness, "슬픔"));
            colors.add(Color.parseColor("#FFAA66"));
        }
        if(surprised != 0) {
            emotion.add(new PieEntry(surprised, "놀람"));
            colors.add(Color.parseColor("#FFEA61"));
        }
        if(anger != 0) {
            emotion.add(new PieEntry(anger, "분노"));
            colors.add(Color.parseColor("#FF7B5A"));
        }
        if(fear != 0) {
            emotion.add(new PieEntry(fear, "공포"));
            colors.add(Color.parseColor("#B2DEF2"));
        }
        if(aversion != 0) {
            emotion.add(new PieEntry(aversion, "혐오"));
            colors.add(Color.parseColor("#C3E0A3"));

        }
        if(neutrality != 0) {
            emotion.add(new PieEntry(neutrality, "중립"));
            colors.add(Color.parseColor("#F3E9BF"));
        }

        PieDataSet dataSet = new PieDataSet(emotion, ""); // 데이터의 카테고리
        dataSet.setSliceSpace(3); // 그래프의 데이터 사이 간격
        dataSet.setColors(colors); // 그래프 색상 테마
        dataSet.setValueFormatter(new MyValueFormatter());

        PieData data = new PieData(dataSet); // 데이터를 담는 그릇
        data.setValueTextSize(15); // 데이터 텍스트 크기
        data.setValueTextColor(Color.BLACK); // 데이터 텍스트 컬러
        emotion_chart.setData(data); // 그래프에 데이터 할당

        emotion_chart.invalidate(); // 차트 갱신
    }
}

class MyValueFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public MyValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        // write your logic here
        return mFormat.format(value) + " %"; // e.g. append a dollar-sign
    }
}