package ac.kr.duksung.moodiary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ac.kr.duksung.moodiary.R;


// 화면 설명 : 마이페이지 안에 알림 설정 화면
// Author : Seungyeon Last Modified : 2021.01.27
public class NotificationActivity extends AppCompatActivity {
    Toolbar toolbar; // 상단바
    Switch notification; //알림설정 switch
    TimePicker timePicker; // 알림 시간 설정
    Button btn_finish; // 알림 설정 완료 버튼

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

        notification = (Switch) findViewById(R.id.notification);
        timePicker = findViewById(R.id.timePicker);
        btn_finish = findViewById(R.id.btn_finish);

        // 처음 화면 설정 - 추후 수정 필요
        timePicker.setVisibility(View.INVISIBLE);
        btn_finish.setVisibility(View.INVISIBLE);

        //switch 설정
        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if (isChecked){
                    Toast.makeText(getApplicationContext(),"알림 ON", Toast.LENGTH_SHORT).show();  //on으로 체크될 경우
                    // 뷰에서 보이게 설정
                    timePicker.setVisibility(View.VISIBLE);
                    btn_finish.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(),"알림 off", Toast.LENGTH_SHORT).show();  //off으로 체크될 경우
                    // 뷰에서 안보이게 설정
                    timePicker.setVisibility(View.INVISIBLE);
                    btn_finish.setVisibility(View.INVISIBLE);
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
