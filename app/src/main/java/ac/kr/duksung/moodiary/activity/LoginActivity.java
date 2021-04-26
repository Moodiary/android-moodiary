package ac.kr.duksung.moodiary.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

// 화면 설명 : 로그인 화면
// Author : Soohyun, Last Modified : 2021.03.10
public class LoginActivity extends AppCompatActivity {
    EditText et_id; // 아이디 입력창
    EditText et_password; // 비밀번호 입력창
    Button btn_login; // 로그인 버튼
    TextView tv_find_id; // 아이디 찾기
    TextView tv_find_password; // 비밀번호 찾기
    TextView tv_signup; // 회원가입
    private boolean seq;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = findViewById(R.id.et_id);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_findpw);
        tv_find_id = findViewById(R.id.tv_find_id);
        tv_find_password = findViewById(R.id.tv_find_password);
        tv_signup = findViewById(R.id.tv_signup);

        // 로그인 버튼 클릭 시
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = et_id.getText().toString();
                String user_pw = et_password.getText().toString();

                if(user_id.equals("")){
                    Toast.makeText(context, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                }else if(user_pw.equals("")){
                    Toast.makeText(context, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                }else{
                    // node 서버에 보낼 값 Json 형태로 만들기
                    JSONObject requestJsonObject = new JSONObject();
                    try {
                        requestJsonObject.put("user_id", user_id);
                        requestJsonObject.put("user_pw", user_pw);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 2. RequestQueue 선언
                    RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

                    // 3. node 서버 IP와 받을 경로 수정 (http://192.168.99.83:3000/post)  하고
                    JsonObjectRequest R_Object = new JsonObjectRequest(Request.Method.POST, "http://192.168.0.6:3000/user/login", requestJsonObject, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String result = response.getString("code"); // 응답 메시지 가져오기
                                String name = response.getString("name"); // 응답 메시지 중 이름 데이터 가져오기
                                String email = response.getString("email"); // 응답 메시지 중 이메일 데이터 가져오기

                                // 응답 메시지에 따른 처리
                                if(result.equals("200")) {
                                    SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 자동로그인 데이터가 저장되어있는 곳
                                    SharedPreferences.Editor editor = auto.edit();
                                    editor.putString("ID", user_id); // 아이디 값 저장
                                    editor.putString("PW", user_pw); // 비밀번호 값 저장
                                    editor.putString("Name", name); // 이름 값 저장
                                    editor.putString("Email", email); // 이메일 값 저장
                                    editor.putString("Noti", "false"); // 알림 설정 여부 저장
                                    editor.commit(); // 변경사항 저장

                                    Toast.makeText(getApplicationContext(), "환영합니다!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class)); // 메인 화면으로 이동
                                    finish();
                                }
                                if(result.equals("204"))
                                    Toast.makeText(getApplicationContext(), "ID 또는 비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                                if(result.equals("208"))
                                    Toast.makeText(getApplicationContext(), "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                                if(result.equals("404"))
                                    Toast.makeText(getApplicationContext(), "에러 발생했습니다.", Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "네트워크 연결 오류.", Toast.LENGTH_SHORT).show();
                            Log.i("VolleyError", "Volley Error in receiv");
                        }
                    });
                    requestQueue.add(R_Object);
                }
            }
        });

        // 아이디 찾기 클릭 시
        tv_find_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, FindIdActivity.class)); // 아이디 찾기 화면으로 이동
            }
        });

        // 비밀번호 찾기 클릭 시
        tv_find_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, FindPwActivity.class)); // 비밀번호 찾기 화면으로 이동
            }
        });

        // 회원가입 클릭 시
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class)); // 회원가입 화면으로 이동
            }
        });



    }

}