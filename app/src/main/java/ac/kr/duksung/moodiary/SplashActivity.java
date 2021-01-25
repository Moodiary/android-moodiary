package ac.kr.duksung.moodiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

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
        isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        // 네트워크 연결 여부에 따른 동작
        if(isConnected)
            handler.postDelayed(new splashhandler(), 3000); // 3초 후 실행
        else
            Toast.makeText(getApplicationContext(), "네트워크가 연결되어 있지 않습니다", Toast.LENGTH_LONG).show();

    }

    // 스플래시 화면에서 3초 후 동작하는 부분
    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(SplashActivity.this, LoginActivity.class)); // 스플래시 화면에서 로그인화면으로 이동
            finish();
        }
    }
}