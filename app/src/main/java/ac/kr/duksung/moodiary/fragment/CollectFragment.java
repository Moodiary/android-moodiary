package ac.kr.duksung.moodiary.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import ac.kr.duksung.moodiary.R;

// 화면 설명 : 메인화면의 모아보기 화면
// Author : Soohyun, Last Modified : 2021.04.09
public class CollectFragment extends Fragment {
    TextView diary_emotion; // 일기 감정
    TextView diary_content; // 일기내용
    AppCompatButton deleteButton; // 삭제 버튼
    ArrayList<String> contentList = new ArrayList<>(); // 일기내용 리스트
    ArrayList<String> emotionList = new ArrayList<>(); // 일기감정 리스트
    ArrayList<String> createdList = new ArrayList<>(); // 일기작성날짜 리스트
    ArrayList<CalendarDay> dates_pleasure = new ArrayList<>(); //일정표시 리스트
    ArrayList<CalendarDay> dates_sadness = new ArrayList<>(); //일정표시 리스트
    ArrayList<CalendarDay> dates_surprised = new ArrayList<>(); //일정표시 리스트
    ArrayList<CalendarDay> dates_anger = new ArrayList<>(); //일정표시 리스트
    ArrayList<CalendarDay> dates_fear = new ArrayList<>(); //일정표시 리스트
    ArrayList<CalendarDay> dates_aversion = new ArrayList<>(); //일정표시 리스트
    ArrayList<CalendarDay> dates_neutrality = new ArrayList<>(); //일정표시 리스트
    MaterialCalendarView diary_calendar; // 캘린더 뷰
    EventDecorator eventDecorator_pleasure;
    EventDecorator eventDecorator_sadness;
    EventDecorator eventDecorator_surprised;
    EventDecorator eventDecorator_anger;
    EventDecorator eventDecorator_fear;
    EventDecorator eventDecorator_aversion;
    EventDecorator eventDecorator_neutrality;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, container, false);

        diary_calendar = view.findViewById(R.id.diary_calendar);
        diary_emotion = view.findViewById(R.id.diary_emotion);
        diary_content = view.findViewById(R.id.diary_content);
        deleteButton = view.findViewById(R.id.btn_delete);

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
                            deleteButton.setVisibility(View.VISIBLE);
                            break;
                        }else {
                            deleteButton.setVisibility(View.GONE);
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
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:3000/diary/collect", requestJsonObject, new Response.Listener<JSONObject>() {

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

        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:3000/diary/deletediary", requestJsonObject, new Response.Listener<JSONObject>() {


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
                        dates_anger.clear();
                        dates_pleasure.clear();
                        dates_sadness.clear();
                        dates_surprised.clear();
                        dates_fear.clear();
                        dates_aversion.clear();
                        dates_neutrality.clear();
                        diary_calendar.removeDecorator(eventDecorator_anger);
                        diary_calendar.removeDecorator(eventDecorator_pleasure);
                        diary_calendar.removeDecorator(eventDecorator_sadness);
                        diary_calendar.removeDecorator(eventDecorator_surprised);
                        diary_calendar.removeDecorator(eventDecorator_fear);
                        diary_calendar.removeDecorator(eventDecorator_aversion);
                        diary_calendar.removeDecorator(eventDecorator_neutrality);
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

        // 일기있는 날짜에 아이콘 표시
        try {
            for(int i=0; i<createdList.size(); i++) {
                Date createdDate = format.parse(createdList.get(i));
                //dates.add(CalendarDay.from(createdDate));   //dates리스트에 CalendarDay형식으로 날짜추가

                if ("공포".equals(emotionList.get(i))) {
                    dates_fear.add(CalendarDay.from(createdDate));
                } else if ("놀람".equals(emotionList.get(i))){
                    dates_surprised.add(CalendarDay.from(createdDate));
                } else if ("분노".equals(emotionList.get(i))){
                    dates_anger.add(CalendarDay.from(createdDate));
                }else if ("슬픔".equals(emotionList.get(i))){
                    dates_sadness.add(CalendarDay.from(createdDate));
                }else if ("중립".equals(emotionList.get(i))){
                    dates_neutrality.add(CalendarDay.from(createdDate));
                }else if ("행복".equals(emotionList.get(i))){
                    dates_pleasure.add(CalendarDay.from(createdDate));
                }else if ("혐오".equals(emotionList.get(i))){
                    dates_aversion.add(CalendarDay.from(createdDate));
                }
            }

            eventDecorator_fear = new EventDecorator(getResources().getDrawable(R.drawable.fear), dates_fear);
            eventDecorator_surprised = new EventDecorator(getResources().getDrawable(R.drawable.surprised), dates_surprised);
            eventDecorator_anger = new EventDecorator(getResources().getDrawable(R.drawable.anger), dates_anger);
            eventDecorator_sadness = new EventDecorator(getResources().getDrawable(R.drawable.sadness), dates_sadness);
            eventDecorator_neutrality = new EventDecorator(getResources().getDrawable(R.drawable.neutrality), dates_neutrality);
            eventDecorator_pleasure = new EventDecorator(getResources().getDrawable(R.drawable.pleasure), dates_pleasure);
            eventDecorator_aversion = new EventDecorator(getResources().getDrawable(R.drawable.aversion), dates_aversion);
            diary_calendar.addDecorator(eventDecorator_fear);
            diary_calendar.addDecorator(eventDecorator_surprised);
            diary_calendar.addDecorator(eventDecorator_anger);
            diary_calendar.addDecorator(eventDecorator_sadness);
            diary_calendar.addDecorator(eventDecorator_neutrality);
            diary_calendar.addDecorator(eventDecorator_pleasure);
            diary_calendar.addDecorator(eventDecorator_aversion);

            //eventDecorator = new EventDecorator(Color.RED, dates);
            
            //eventDecorator = new EventDecorator(eventIcon, dates);
            //diary_calendar.addDecorator(eventDecorator);  //일정에 점찍기

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}