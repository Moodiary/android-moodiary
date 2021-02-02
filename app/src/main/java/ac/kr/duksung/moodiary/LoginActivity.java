package ac.kr.duksung.moodiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// 화면 설명 : 로그인 화면
// Author : Soohyun, Last Modified : 2021.01.20
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
        btn_login = findViewById(R.id.btn_login);
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
                    Toast.makeText(context, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
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

                    // 3. node 서버 IP와 받을 경로 수정 (http://192.168.99.83:3000/post)  하고,
                    JsonObjectRequest R_Object = new JsonObjectRequest(Request.Method.POST, "http://192.168.99.83:3000/user/login", requestJsonObject, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            JSONArray J_JsonArray = new JSONArray();
                            try {
                                J_JsonArray = response.getJSONArray("results");
                                JSONObject dataObj = J_JsonArray.getJSONObject(0);
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