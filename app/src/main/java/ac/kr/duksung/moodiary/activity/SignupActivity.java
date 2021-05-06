package ac.kr.duksung.moodiary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Patterns;
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

import java.util.regex.Pattern;

import ac.kr.duksung.moodiary.R;

// 화면 설명 : 회원가입 화면
// Author : Soohyun, Last Modified : 2021.01.20
public class SignupActivity extends AppCompatActivity {
    Toolbar toolbar; // 상단바
    EditText et_name; // 이름 입력창
    EditText et_id; // 아이디 입력창
    EditText et_email; // 이메일 입력창
    EditText et_password; // 패스워드 입력창
    EditText et_identify_password; // 패스워드 확인 입력창
    Button btn_login; // 확인 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 상단바 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 삭제
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back버튼

        et_name = findViewById(R.id.et_name);
        et_id = findViewById(R.id.et_id);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_identify_password = findViewById(R.id.et_identify_password);
        btn_login = findViewById(R.id.btn_findpw);

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // 이메일 검증 패턴

        // 확인 버튼 클릭시
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 데이터를 가져오는 부분
                String name = et_name.getText().toString();
                String id = et_id.getText().toString();
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                String identify_password = et_identify_password.getText().toString();

                // 사용자 입력 정보 확인
                if(name.equals(""))
                    et_name.setError("이름을 입력하세요");
                if(id.equals(""))
                    et_id.setError("아이디를 입력하세요");
                if(email.equals(""))
                    et_email.setError("이메일을 입력하세요");
                if(!emailPattern.matcher(email).matches())
                    et_email.setError("이메일 형식이 아닙니다");
                if(password.equals(""))
                    et_password.setError("비밀번호를 입력하세요");
                if(identify_password.equals(""))
                    et_identify_password.setError("비밀번호 확인을 입력하세요");
                if(!identify_password.equals(password))
                    et_identify_password.setError("비밀번호와 다릅니다");

                // 사용자 입력 정보가 제대로 들어온 경우 -> 회원가입 메소드 실행
                if(!name.equals("") && !id.equals("") && !email.equals("") && !password.equals(""))
                    requestSignup(name, id, email, password);
            }
        });
    }

    // 회원가입 메소드
    public void requestSignup(String name, String id, String email, String password) {

        // 사용자 입력 정보 JSON 형태로 변환
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("user_id", id);
            requestJsonObject.put("user_pw", password);
            requestJsonObject.put("user_name", name);
            requestJsonObject.put("user_email", email);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);

        // 서버에 데이터 전달
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://172.30.1.21:3000/user/signup", requestJsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) { // 데이터 전달 후 받은 응답

                try {
                    String result = response.getString("code"); // 응답 메시지 가져오기

                    // 응답 메시지에 따른 처리
                    if(result.equals("400"))
                        Toast.makeText(getApplicationContext(),"에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    if(result.equals("204"))
                        Toast.makeText(getApplicationContext(),"아이디가 중복입니다", Toast.LENGTH_SHORT).show();
                    if(result.equals("203"))
                        Toast.makeText(getApplicationContext(),"이메일이 중복입니다", Toast.LENGTH_SHORT).show();
                    if(result.equals("200")) {
                        Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                        finish(); // 회원가입 완료 후 다시 로그인 화면으로 돌아감
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