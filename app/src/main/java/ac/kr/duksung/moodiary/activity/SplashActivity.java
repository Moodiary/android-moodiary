package ac.kr.duksung.moodiary.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import ac.kr.duksung.moodiary.R;

// 화면 설명 : 앱 실행시 첫 화면
// Author : Soohyun, Last Modified : 2021.01.20
public class SplashActivity extends AppCompatActivity {
    boolean isConnected;
    ConnectivityManager connectivityManager;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        handler = new Handler();

        // 네트워크 연결 확인
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // 네트워크 연결 여부에 따른 동작
        /*
        if(networkInfo != null){
            int type = networkInfo.getType();
            if(type == ConnectivityManager.TYPE_MOBILE || type == ConnectivityManager.TYPE_WIFI){ // 3G, LTE, 와이파이로 연결된 경우 앱 실행
                handler.postDelayed(new splashhandler(), 3000); // 3초 후 실행
            }
        } else {
            Toast.makeText(getApplicationContext(), "네트워크가 연결되어 있지 않습니다", Toast.LENGTH_LONG).show();
        }*/

        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                // 네트워크를 사용할 준비가 되었을 때
                handler.postDelayed(new splashhandler(), 3000);
            }

            @Override
            public void onLost(@NonNull Network network) {
                // 네트워크가 끊겼을 때
                Toast.makeText(getApplicationContext(), "네트워크가 연결되어 있지 않습니다", Toast.LENGTH_LONG).show();
            }
        });

    }

    // 스플래시 화면에서 3초 후 동작하는 부분
    private class splashhandler implements Runnable{
        public void run(){
            SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 자동로그인 데이터 저장되어있는 곳
            String loginId = auto.getString("ID",null); // 저장된 아이디 값, 없으면 null
            String loginPw = auto.getString("PW",null); // 저장된 비밀번호 값, 없으면 null

            if(loginId == null && loginPw == null) { // 자동로그인 데이터가 없는 경우
                startActivity(new Intent(SplashActivity.this, LoginActivity.class)); // 스플래시 화면에서 로그인화면으로 이동
                finish();
            } else if (loginId != null && loginPw != null) { // 자동로그인 데이터가 있는 경우
                startActivity(new Intent(SplashActivity.this, MainActivity.class)); // 스플래시 화면에서 메인화면으로 이동
                finish();
            }
        }
    }
}