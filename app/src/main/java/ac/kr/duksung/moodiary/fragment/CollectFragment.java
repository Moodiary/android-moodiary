package ac.kr.duksung.moodiary.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ac.kr.duksung.moodiary.R;

// 화면 설명 : 메인화면의 모아보기 화면
// Author : Soohyun, Last Modified : 2021.04.09
public class CollectFragment extends Fragment {

    CalendarView diary_calendar; // 달력
    TextView diary_content; // 일기내용
    ArrayList<String> contentList = new ArrayList<>(); // 일기내용 리스트
    ArrayList<String> emotionList = new ArrayList<>(); // 일기감정 리스트
    ArrayList<String> createdList = new ArrayList<>(); // 일기작성날짜 리스트

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, container, false);

        diary_calendar = view.findViewById(R.id.diary_calendar);
        diary_content = view.findViewById(R.id.diary_content);

        requestCollect(); // 일기 데이터 메소드 실행

        diary_calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){

            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                diary_content.setVisibility(View.VISIBLE);
            }
        });

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
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://192.168.1.185:3000/diary/collect", requestJsonObject, new Response.Listener<JSONObject>() {

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
                        Toast.makeText(getContext(), "일기 모아보기입니다.", Toast.LENGTH_SHORT).show();
                        for(int i=0; i<contentList.size(); i++) {
                            System.out.println(contentList.get(i) + " / " + emotionList.get(i) + " / " + createdList.get(i));
                        }
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


}