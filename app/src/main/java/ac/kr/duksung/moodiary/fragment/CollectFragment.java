package ac.kr.duksung.moodiary.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ac.kr.duksung.moodiary.CalendarEvent.EventDecorator;
import ac.kr.duksung.moodiary.CalendarEvent.SaturdayDecorator;
import ac.kr.duksung.moodiary.CalendarEvent.SundayDecorator;
import ac.kr.duksung.moodiary.R;
import ac.kr.duksung.moodiary.domain.ChatItem;

// 화면 설명 : 메인화면의 모아보기 화면
// Author : Soohyun, Last Modified : 2021.04.09
public class CollectFragment extends Fragment {
    TextView diary_emotion; // 일기 감정
    TextView diary_content; // 일기내용
    Button deleteButton; //삭제 버튼
    ArrayList<String> contentList = new ArrayList<>(); // 일기내용 리스트
    ArrayList<String> emotionList = new ArrayList<>(); // 일기감정 리스트
    ArrayList<String> createdList = new ArrayList<>(); // 일기작성날짜 리스트
    ArrayList<CalendarDay> dates = new ArrayList<>(); //일정표시 리스트
    MaterialCalendarView diary_calendar; // 캘린더 뷰
    EventDecorator eventDecorator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, container, false);

        diary_calendar = view.findViewById(R.id.diary_calendar);
        diary_emotion = view.findViewById(R.id.diary_emotion);
        diary_content = view.findViewById(R.id.diary_content);
        deleteButton = view.findViewById(R.id.deleteButton);

        deleteButton.setVisibility(View.GONE);

        requestCollect(); // 일기 데이터 메소드 실행

        diary_calendar.setOnDateChangedListener(new OnDateSelectedListener(){
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); // 날짜 형식
                Date selectedDate = date.getDate(); // 선택된 날짜
                Date createdDate = null; // createdList의 특정 날짜

                String created_at = format.format(selectedDate); // 삭제할 날짜

                // 초기화
                diary_emotion.setText("");
                diary_content.setText("");

                try {
                    // 선택된 날짜와 날짜 리스트의 날짜가 같으면 일기 정보 표시
                    for(int i=0; i<createdList.size(); i++) {
                        createdDate = format.parse(createdList.get(i));

                        if(createdDate.equals(selectedDate)) {
                            diary_emotion.setText(emotionList.get(i));
                            diary_content.setText(contentList.get(i));
                            break;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //삭제 버튼 클릭했을 때
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deletediary(created_at);
                        deleteButton.setVisibility(View.GONE);
                    }
                });
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); // 날짜 형식
                Date now = new Date(); // 현재 날짜
                String nowStr = format.format(now);

                deletediary(nowStr);
                deleteButton.setVisibility(View.GONE);
            }
        });


        //일기 커스텀뷰
        diary_calendar.setSelectedDate(CalendarDay.today());
        diary_calendar.addDecorator(new SundayDecorator());
        diary_calendar.addDecorator(new SaturdayDecorator());

        return view;
    }

    // 일기 데이터 가져오는 메소드
    public void requestCollect() {

        SharedPreferences auto = this.getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 자동로그인 데이터 저장되어있는 곳
        String user_id = auto.getString("ID",null); // 저장된 아이디 값, 없으면 null

        // 사용자 입력 정보 JSON 형태로 변환
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("user_id", user_id);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // 서버에 데이터 전달

        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://172.20.18.162:3000/diary/collect", requestJsonObject, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) { // 데이터 전달 후 받은 응답

                try {
                    String result = response.getString("code"); // 응답 메시지 가져오기
                    String jArray = response.getString("jArray"); // 일기 데이터 가져오기

                    JSONArray resultList = new JSONArray(jArray); // 일기 데이터  JSONArray로 변환
                    for(int i = 0 ; i<resultList.length(); i++){
                        JSONObject jsonObject = resultList.getJSONObject(i); // JSONArray에서 JSONObject 하나씩 가져오기
                        String content = jsonObject.getString("content"); // 일기 내용 데이터
                        String emotion = jsonObject.getString("emotion"); // 일기 감정 데이터
                        String created_at = jsonObject.getString("created_at"); // 일기 작성 날짜 데이터

                        // 각각 리스트에 데이터 저장
                        contentList.add(content);
                        emotionList.add(emotion);
                        createdList.add(created_at);
                    }

                    // 응답 메시지에 따른 처리
                    if(result.equals("400"))
                        Toast.makeText(getContext(),"에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    if(result.equals("200")) {
                        for(int i=0; i<contentList.size(); i++) {
                            System.out.println(contentList.get(i) + " / " + emotionList.get(i) + " / " + createdList.get(i));
                        }
                        init(); // 일기 초기화 메소드 실행
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

    // 일기 삭제하는 메소드
    public void deletediary(String created_at) {

        SharedPreferences auto = this.getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 자동로그인 데이터 저장되어있는 곳
        String user_id = auto.getString("ID",null); // 저장된 아이디 값, 없으면 null

        // 사용자 입력 정보 JSON 형태로 변환
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("user_id", user_id);
            requestJsonObject.put("created_at", created_at);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // 서버에 데이터 전달
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://172.20.18.162:3000/diary/deletediary", requestJsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) { // 데이터 전달 후 받은 응답
                try {
                    String result = response.getString("code"); // 응답 메시지 가져오기

                    // 응답 메시지에 따른 처리
                    if(result.equals("400"))
                        Toast.makeText(getContext(),"에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    if(result.equals("200")) {
                        Toast.makeText(getContext(),"일기가 삭제되었습니다", Toast.LENGTH_SHORT).show();

                        // 캘린더의 모든 데이터 초기화
                        contentList.clear();
                        emotionList.clear();
                        createdList.clear();
                        dates.clear();
                        diary_calendar.removeDecorator(eventDecorator);
                        diary_content.setText("");
                        diary_emotion.setText("");

                        requestCollect(); // 일기 데이터 메소드 실행
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

    // 현재 날짜 일기 초기화 메소드
    public void init() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); // 날짜 형식
        Date now = new Date(); // 현재 날짜
        String nowStr = format.format(now);

        try {
            Date nowDate = format.parse(nowStr);

            for(int i=0; i<createdList.size(); i++) {
                Date createdDate = format.parse(createdList.get(i));

                if(createdDate.equals(nowDate)) {
                    diary_emotion.setText(emotionList.get(i));
                    diary_content.setText(contentList.get(i));
                    deleteButton.setVisibility(View.VISIBLE);
                    break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 일기있는 날짜에 빨간 점 표시
        try {
            for(int i=0; i<createdList.size(); i++) {
                Date createdDate = format.parse(createdList.get(i));
                dates.add(CalendarDay.from(createdDate));   //dates리스트에 CalendarDay형식으로 날짜추가
            }

            eventDecorator = new EventDecorator(Color.RED, dates);
            diary_calendar.addDecorator(eventDecorator);  //일정에 점찍기

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}