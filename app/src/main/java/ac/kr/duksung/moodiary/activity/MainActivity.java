package ac.kr.duksung.moodiary.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ac.kr.duksung.moodiary.fragment.MypageFragment;
import ac.kr.duksung.moodiary.R;
import ac.kr.duksung.moodiary.fragment.ChatFragment;
import ac.kr.duksung.moodiary.fragment.CollectFragment;
import ac.kr.duksung.moodiary.fragment.StaticsFragment;

// 화면 설명 : 메인(홈) 화면
// Author : Soohyun, Last Modified : 2021.01.20
public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView; // 하단바
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    ChatFragment chatFragment; // 챗봇 화면
    CollectFragment collectFragment; // 모아보기 화면
    StaticsFragment staticsFragment; // 통계 화면
    MypageFragment mypageFragment; // 마이페이지 화면

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentManager = getSupportFragmentManager();
        chatFragment = new ChatFragment();

        // 메인화면의 첫 화면 설정
        chatFragment = new ChatFragment();
        fragmentManager.beginTransaction().replace(R.id.main_frame, chatFragment).commit();

        // 하단바 선택 시
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_chat:

                        if(chatFragment == null) { // 챗봇 프래그먼트가 없는 경우
                            chatFragment = new ChatFragment(); // 챗봇 프래그먼트 생성
                            fragmentManager.beginTransaction().add(R.id.main_frame, chatFragment).commit(); // 챗봇 화면 보여줌
                        }

                        // 챗봇 프래그먼트가 있는 경우 -> 챗봇 화면 보여주고 나머지 화면은 숨김
                        if(chatFragment != null) fragmentManager.beginTransaction().show(chatFragment).commit();
                        if(collectFragment != null) fragmentManager.beginTransaction().hide(collectFragment).commit();
                        if(staticsFragment != null) fragmentManager.beginTransaction().hide(staticsFragment).commit();
                        if(mypageFragment != null) fragmentManager.beginTransaction().hide(mypageFragment).commit();

                        break;
                    case R.id.menu_collect:

                        if(collectFragment == null) { // 모아보기 프래그먼트가 없는 경우
                            collectFragment = new CollectFragment(); // 모아보기 프래그먼트 생성
                            fragmentManager.beginTransaction().add(R.id.main_frame, collectFragment).commit(); // 모아보기 화면 보여줌
                        }

                        // 모아보기 프래그먼트가 있는 경우 -> 모아보기 화면 보여주고 나머지 화면은 숨김
                        if(chatFragment != null) fragmentManager.beginTransaction().hide(chatFragment).commit();
                        if(collectFragment != null) fragmentManager.beginTransaction().show(collectFragment).commit();
                        if(staticsFragment != null) fragmentManager.beginTransaction().hide(staticsFragment).commit();
                        if(mypageFragment != null) fragmentManager.beginTransaction().hide(mypageFragment).commit();

                        break;
                    case R.id.menu_statics:

                        if(staticsFragment == null) { // 통계 프래그먼트가 없는 경우
                            staticsFragment = new StaticsFragment(); // 통계 프래그먼트 생성
                            fragmentManager.beginTransaction().add(R.id.main_frame, staticsFragment).commit(); // 통계 화면 보여줌
                        }

                        // 통계 프래그먼트가 있는 경우 -> 통계 화면 보여주고 나머지 화면은 숨김
                        if(chatFragment != null) fragmentManager.beginTransaction().hide(chatFragment).commit();
                        if(collectFragment != null) fragmentManager.beginTransaction().hide(collectFragment).commit();
                        if(staticsFragment != null) fragmentManager.beginTransaction().show(staticsFragment).commit();
                        if(mypageFragment != null) fragmentManager.beginTransaction().hide(mypageFragment).commit();

                        break;
                    case R.id.menu_mypage:

                        if(mypageFragment == null) { // 마이페이지 프래그먼트가 없는 경우
                            mypageFragment = new MypageFragment(); // 마이페이지 프래그먼트 생성
                            fragmentManager.beginTransaction().add(R.id.main_frame, mypageFragment).commit(); // 마이페이지 화면 보여줌
                        }

                        // 마이페이지 프래그먼트가 있는 경우 -> 마이페이지지 화면 보여주고 나머지 화면은 숨김
                        if(chatFragment != null) fragmentManager.beginTransaction().hide(chatFragment).commit();
                        if(collectFragment != null) fragmentManager.beginTransaction().hide(collectFragment).commit();
                        if(staticsFragment != null) fragmentManager.beginTransaction().hide(staticsFragment).commit();
                        if(mypageFragment != null) fragmentManager.beginTransaction().show(mypageFragment).commit();

                        break;
                }
                return true;
            }
        });
    }
}