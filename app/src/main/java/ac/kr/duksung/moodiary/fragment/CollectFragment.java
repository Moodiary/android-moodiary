package ac.kr.duksung.moodiary.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ac.kr.duksung.moodiary.CalendarEvent.EventDecorator;
import ac.kr.duksung.moodiary.CalendarEvent.SundayDecorator;
import ac.kr.duksung.moodiary.R;

// 화면 설명 : 메인화면의 모아보기 화면
// Author : Soohyun, Last Modified : 2021.04.09
public class CollectFragment extends Fragment {

    CalendarView diary_calendar; // 달력
    TextView diary_emotion; // 일기 감정
    TextView diary_content; // 일기내용
    ArrayList<String> contentList = new ArrayList<>(); // 일기내용 리스트
    ArrayList<String> emotionList = new ArrayList<>(); // 일기감정 리스트
    ArrayList<String> createdList = new ArrayList<>(); // 일기작성날짜 리스트
    ArrayList<CalendarDay> dates = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, container, false);

        MaterialCalendarView diary_calendar = view.findViewById(R.id.diary_calendar);
        //diary_calendar = view.findViewById(R.id.diary_calendar);
        diary_emotion = view.findViewById(R.id.diary_emotion);
        diary_content = view.findViewById(R.id.diary_content);

        requestCollect(); // 일기 데이터 메소드 실행
        diary_calendar.setOnDateChangedListener(new OnDateSelectedListener(){
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); // 날짜 형식
                Date selectedDate = date.getDate(); // 선택된 날짜
                Date createdDate = null; // createdList의 특정 날짜

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
            }
        });

        class ApiSimulator extends AsyncTask<Void, Void, ArrayList<CalendarDay>> {

            @Override
            protected ArrayList<CalendarDay> doInBackground(@NonNull Void... voids) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Calendar calendar = Calendar.getInstance();



                /*특정날짜 달력에 점표시해주는곳*/
                /*월은 0이 1월 년,일은 그대로*/
                //string 문자열인 Time_Result 을 받아와서 ,를 기준으로 짜르고 string을 int 로 변환
                for (int i = 0; i < createdList.size(); i++) {


                    //이부분에서 day를 선언하면 초기 값에 오늘 날짜 데이터 들어간다.
                    //오늘 날짜 데이터를 첫 번째 인자로 넣기 때문에 데이터가 하나씩 밀려 마지막 데이터는 표시되지 않고, 오늘 날짜 데이터가 표시 됨.
                    // day선언 주석처리

                    //                CalendarDay day = CalendarDay.from(calendar);
                    //                Log.e("데이터 확인","day"+day);
                    String[] time = createdList.get(i).split(",");

                    int year = Integer.parseInt(time[0]);
                    int month = Integer.parseInt(time[1]);
                    int dayy = Integer.parseInt(time[2]);

                    //선언문을 아래와 같은 위치에 선언
                    //먼저 .set 으로 데이터를 설정한 다음 CalendarDay day = CalendarDay.from(calendar); 선언해주면 첫 번째 인자로 새로 정렬한 데이터를 넣어 줌.
                    calendar.set(year, month - 1, dayy);
                    CalendarDay day = CalendarDay.from(calendar);
                    dates.add(day);

                }
                return dates;
            }

            @Override
            protected void onPostExecute(@NonNull ArrayList<CalendarDay> calendarDays) {
                super.onPostExecute(calendarDays);

                /*if (isFinishing()) {
                    return;
                }*/

                diary_calendar.addDecorator(new EventDecorator(Color.RED, calendarDays));
            }


        }

        //일기 커스텀뷰
        diary_calendar.setSelectedDate(CalendarDay.today());
        //diary_calendar.addDecorator(new EventDecorator(Color.GREEN, CalendarDay));
        diary_calendar.addDecorator(new SundayDecorator());

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

        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://192.168.0.6:3000/diary/collect", requestJsonObject, new Response.Listener<JSONObject>() {


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
                    break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}