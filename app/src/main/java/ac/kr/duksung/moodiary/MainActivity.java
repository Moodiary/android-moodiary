package ac.kr.duksung.moodiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.SurfaceControl;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        collectFragment = new CollectFragment();
        staticsFragment = new StaticsFragment();
        mypageFragment = new MypageFragment();

        // 메인화면의 첫 화면 설정
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_frame, chatFragment);
        transaction.commit();

        // 하단바 선택 시
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                transaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {
                    case R.id.menu_chat:
                        transaction.replace(R.id.main_frame, chatFragment); // 챗봇 화면으로 전환
                        transaction.commit();
                        break;
                    case R.id.menu_collect:
                        transaction.replace(R.id.main_frame, collectFragment); // 모아보기 화면으로 전환
                        transaction.commit();
                        break;
                    case R.id.menu_statics:
                        transaction.replace(R.id.main_frame, staticsFragment); // 통계 화면으로 전환
                        transaction.commit();
                        break;
                    case R.id.menu_mypage:
                        transaction.replace(R.id.main_frame, mypageFragment); // 마이페이지 화면으로 전환
                        transaction.commit();
                        break;
                }
                return true;
            }
        });
    }
}