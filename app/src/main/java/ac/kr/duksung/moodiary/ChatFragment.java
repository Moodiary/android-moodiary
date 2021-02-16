package ac.kr.duksung.moodiary;

import android.os.Bundle;

import androidx.constraintlayout.solver.ArrayLinkedVariables;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

// 화면 설명 : 메인화면의 챗봇 화면
// Author : Soohyun, Last Modified : 2021.02.15
public class ChatFragment extends Fragment {
    public int sequence = 1; // 챗봇의 단계 처리를 위한 변수
    private ArrayList<ChatItem> chatList; // 챗봇 메세지 리스트
    ChatAdapter adapter;
    EditText et_input; // 메세지 입력창
    Button btn_push; // 전송 버튼

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        initData(); // 데이터 초기화

        RecyclerView rv_chat = view.findViewById(R.id.rv_chat); // 리사이클러뷰
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false); // 레이아웃 매니저
        adapter = new ChatAdapter(chatList, ChatFragment.this); // 챗봇 어댑터
        rv_chat.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_chat.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결
        et_input = view.findViewById(R.id.et_input);
        btn_push = view.findViewById(R.id.btn_push);

        // 전송 버튼 클릭시
        btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sequence == 1) { // 감정일기 쓰는 단계일 경우
                    String message = et_input.getText().toString(); // 사용자가 입력한 메세지 가져옴
                    chatList.add(new ChatItem(1, message)); // 사용자가 입력한 메시지를 챗봇 메세지 리스트에 추가

                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() { public void run() {
                        chatList.add(new ChatItem(0, "당신의 감정은 ~~~~ 하군요"));
                        chatList.add(new ChatItem(0, "당신의 감정에 도움이 되는 컬러테라피와 음악을 제공해드릴게요"));
                        chatList.add(new ChatItem(2, "조명", "사운드", "둘 다", "선택 안함", 1));
                        adapter.notifyDataSetChanged(); // 챗봇 메세지 리스트 갱신
                    } }, 600); // 0.6초 딜레이 후 함수 실행

                    et_input.setText(""); // 메세지 입력창 초기화
                    sequence++; // 다음 단계로 이동할 수 있도록 변수값 변경
                    et_input.setEnabled(false); // 메세지 입력창 사용 금지
                } else if (sequence == 3) { // 타이머 설정 단계일 경우
                    String message = et_input.getText().toString(); // 사용자가 입력한 메세지 가져옴
                    chatList.add(new ChatItem(1, message)); // 사용자가 입력한 메시지를 챗봇 메세지 리스트에 추가
                }
            }
        });
        return view;
    }

    // 챗봇 초기 세팅
    private void initData(){
        chatList = new ArrayList<>();
        chatList.add(new ChatItem(0,"오늘 하루는 어떠셨나요?"));
    }

    // 타이머 설정하는 메소드
    public void setTimer() {
        sequence++; // 타이머 설정 단계로 이동할 수 있도록 변수값 변경

        if(sequence == 3) { // 타이머 설정 단계일 경우
            chatList.add(new ChatItem(0, "타이머를 설정해주세요"));
            chatList.add(new ChatItem(2, "15분", "30분", "1시간", "직접 입력", 2));
            adapter.notifyDataSetChanged();
        }
    }
}