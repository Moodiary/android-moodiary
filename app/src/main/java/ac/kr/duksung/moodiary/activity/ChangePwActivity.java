package ac.kr.duksung.moodiary.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import ac.kr.duksung.moodiary.R;


// 화면 설명 : 마이페이지 안에 비밀번호 변경 화면
// Author : Seungyeon, Last Modified : 2021.01.25
// Author : Soohyun, Last Modified : 2021.02.08
public class ChangePwActivity extends AppCompatActivity {
    Toolbar toolbar; // 상단바
    EditText et_origin_password;
    EditText et_new_password;
    EditText et_new_password_check;
    Button btn_change;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);

        // 상단바 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 삭제
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back버튼

        et_origin_password = findViewById(R.id.et_origin_password);
        et_new_password = findViewById(R.id.et_new_password);
        et_new_password_check = findViewById(R.id.et_new_password_check);
        btn_change = findViewById(R.id.btn_change);

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 데이터를 가져오는 부분
                SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 자동로그인 데이터 저장되어있는 곳
                String id = auto.getString("ID",null);
                String before_pw = et_origin_password.getText().toString();
                String after_pw = et_new_password.getText().toString();
                String check_pw = et_new_password_check.getText().toString();

                // 사용자 입력 정보 확인
                if(before_pw.equals(""))
                    et_origin_password.setError("기존 비밀번호를 입력하세요");
                if(after_pw.equals(""))
                    et_new_password.setError("새로운 비밀번호를 입력하세요");
                if(check_pw.equals(""))
                    et_new_password_check.setError("비밀번호 확인을 입력하세요");
                if(!after_pw.equals(check_pw))
                    et_new_password_check.setError("새로운 비밀번호와 다릅니다");

                // 사용자 입력 정보가 제대로 들어온 경우 -> 비밀번호 변경 메소드 실행
                if(after_pw.equals(check_pw) && !before_pw.equals("") && !after_pw.equals("") && !check_pw.equals(""))
                    requestChangepw(id, before_pw, after_pw);
            }
        });

    }

    // 회원가입 메소드
    public void requestChangepw(String id, String before_pw, String after_pw) {

        // 사용자 입력 정보 JSON 형태로 변환
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("user_id", id);
            requestJsonObject.put("before_pw", before_pw);
            requestJsonObject.put("after_pw", after_pw);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(ChangePwActivity.this);

        // 서버에 데이터 전달
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://192.168.35.186:3000/user/changepw", requestJsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) { // 데이터 전달 후 받은 응답

                try {
                    String result = response.getString("code"); // 응답 메시지 가져오기

                    // 응답 메시지에 따른 처리
                    if(result.equals("400"))
                        Toast.makeText(getApplicationContext(),"에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    if(result.equals("204"))
                        Toast.makeText(getApplicationContext(),"기존 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    if(result.equals("200")) {
                        Toast.makeText(getApplicationContext(), "비밀번호 변경이 완료되었습니다", Toast.LENGTH_SHORT).show();
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
