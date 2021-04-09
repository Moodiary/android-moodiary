package ac.kr.duksung.moodiary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import ac.kr.duksung.moodiary.R;

// 화면 설명 : 아이디 찾기 화면
// Author : Soohyun, Last Modified : 2021.01.20
public class FindIdActivity extends AppCompatActivity {
    Toolbar toolbar; // 상단바
    EditText et_email; // 이메일 입력창
    Button btn_login; // 확인 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        // 상단바 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 삭제
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back버튼

        et_email = findViewById(R.id.et_email);
        btn_login = findViewById(R.id.btn_findid);

        // 확인 버튼 클릭시
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();

                if(email.equals("")) // 이메일이 입력되지 않은 경우
                    et_email.setError("이메일을 입력하세요");
                else { // 이메일이 입력된 경우 -> 아이디 찾기 메소드 실행
                    requestFindId(email);
                }
            }
        });
    }

    // 아이디 찾기 메소드
    public void requestFindId(String email) {

        // 사용자 입력 정보 JSON 형태로 변환
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("user_email", email);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(FindIdActivity.this);

        // 서버에 데이터 전달
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://192.168.35.186:3000/user/findid", requestJsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) { // 데이터 전달 후 받은 응답

                try {
                    String result = response.getString("code"); // 응답 메시지 가져오기

                    // 응답 메시지에 따른 처리
                    if(result.equals("400"))
                        Toast.makeText(getApplicationContext(),"에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    if(result.equals("204"))
                        Toast.makeText(getApplicationContext(),"등록되지 않은 이메일입니다", Toast.LENGTH_SHORT).show();;
                    if(result.equals("200")) {
                        Toast.makeText(getApplicationContext(), "메일이 전송되었습니다", Toast.LENGTH_SHORT).show();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() { // 데이터 전달 및 응답 실패시
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "네트워크 연결 오류", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObject);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //상단바의 back키 눌렀을 때
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}