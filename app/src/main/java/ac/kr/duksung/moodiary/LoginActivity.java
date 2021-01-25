package ac.kr.duksung.moodiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// 화면 설명 : 로그인 화면
// Author : Soohyun, Last Modified : 2021.01.20
public class LoginActivity extends AppCompatActivity {
    EditText et_id; // 아이디 입력창
    EditText et_password; // 비밀번호 입력창
    Button btn_login; // 로그인 버튼
    TextView tv_find_id; // 아이디 찾기
    TextView tv_find_password; // 비밀번호 찾기
    TextView tv_signup; // 회원가입

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
                startActivity(new Intent(LoginActivity.this, MainActivity.class)); // 메인 화면으로 이동
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