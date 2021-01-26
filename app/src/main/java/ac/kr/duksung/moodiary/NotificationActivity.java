package ac.kr.duksung.moodiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


// 화면 설명 : 마이페이지 안에 알림 설정 화면
// Author : Seungyeon, Last Modified : 2021.01.26
public class NotificationActivity extends AppCompatActivity {
    Toolbar toolbar; // 상단바
    Switch notification; //알림설정 switch

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // 상단바 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 삭제
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back버튼

        Intent Noti = getIntent();  //화면 전환

        //switch 설정
        notification = (Switch
                ) findViewById(R.id.notification);
        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked){
                    Toast.makeText(getApplicationContext(),"알림 ON", Toast.LENGTH_SHORT).show();  //on으로 체크될 경우
                }else{
                    Toast.makeText(getApplicationContext(),"알림 off", Toast.LENGTH_SHORT).show();  //off으로 체크될 경우
                }
            }
        });
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
