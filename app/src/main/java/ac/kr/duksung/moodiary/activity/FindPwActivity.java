package ac.kr.duksung.moodiary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

// 화면 설명 : 비밀번호 찾기 화면
// Author : Soohyun, Last Modified : 2021.01.20
public class FindPwActivity extends AppCompatActivity {
    Toolbar toolbar; // 상단바
    EditText et_id; // 아이디 입력창
    EditText et_email; // 이메일 입력창
    Button btn_findpw; // 비밀번호찾기 버튼
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        et_id = findViewById(R.id.et_id);
        et_email = findViewById(R.id.et_email);
        btn_findpw = findViewById(R.id.btn_findpw);

        // 확인 버튼 클릭 시
        btn_findpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = et_id.getText().toString();
                String user_email = et_email.getText().toString();

                if(user_id.equals("")){
                    Toast.makeText(context, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                }else if(user_email.equals("")){
                    Toast.makeText(context, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                }else{
                    // node 서버에 보낼 값 Json 형태로 만들기
                    JSONObject requestJsonObject = new JSONObject();
                    try {
                        requestJsonObject.put("user_id", user_id);
                        requestJsonObject.put("user_email", user_email);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 2. RequestQueue 선언
                    RequestQueue requestQueue = Volley.newRequestQueue(FindPwActivity.this);

                    // 3. node 서버 IP와 받을 경로 수정 (http://192.168.99.83:3000/post)  하고,
                    JsonObjectRequest R_Object = new JsonObjectRequest(Request.Method.POST, "http://192.168.99.84:3000/user/findpw", requestJsonObject, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String result = response.getString("code"); // 응답 메시지 가져오기

                                // 응답 메시지에 따른 처리
                                if(result.equals("200")) {
                                    Toast.makeText(getApplicationContext(), "메일이 전송되었습니다", Toast.LENGTH_SHORT).show();
                                }
                                if(result.equals("204"))
                                    Toast.makeText(getApplicationContext(), "등록된 아이디가 없습니다.", Toast.LENGTH_SHORT).show();
                                if(result.equals("208"))
                                    Toast.makeText(getApplicationContext(), "이메일이 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                                if(result.equals("400"))
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

        // 상단바 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 삭제
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back버튼
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