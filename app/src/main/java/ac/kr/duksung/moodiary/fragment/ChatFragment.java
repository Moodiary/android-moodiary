package ac.kr.duksung.moodiary.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.phrase_extractor.KoreanPhraseExtractor;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

import ac.kr.duksung.moodiary.R;
import ac.kr.duksung.moodiary.TextClassification;
import ac.kr.duksung.moodiary.adapter.ChatAdapter;
import ac.kr.duksung.moodiary.domain.ChatItem;
import scala.collection.Seq;

// 화면 설명 : 메인화면의 챗봇 화면
// Author : Soohyun, Last Modified : 2021.04.01
public class ChatFragment extends Fragment {
    public int sequence = 1; // 챗봇의 단계 처리를 위한 변수
    public ArrayList<ChatItem> chatList; // 챗봇 메세지 리스트
    ChatAdapter adapter; // 챗봇 어댑터
    EditText et_input; // 메세지 입력창
    Button btn_push; // 전송 버튼

    Interpreter interpreter; // 모델 인터프리터

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

                    // 데이터 전처리
                    TextClassification client = new TextClassification(getContext()); // 데이터 전처리 클래스 호출
                    List<String> tokenizeText = client.tokenize(message); // 토큰화된 텍스트
                    List<Float> dicText = client.jsonParsing(tokenizeText); // 정수화된 텍스트
                    float[] paddingText = client.padSequence(dicText); // 패딩된 텍스트

                    getEmotionModel(); // 감정 분석 모델 가져오기

                    float[][] input = {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 727, 2, 304, 122, 1816, 39, 26600, 2}};
                    float[][] output = new float[1][7];
                    if(interpreter != null) {
                        interpreter.run(input, output);
                    }
                    for (int i = 0; i < 7; i++) {
                        chatList.add(new ChatItem(0,i + ": " + output[0][i]));
                    }
                    adapter.notifyDataSetChanged();

                    /*
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() { public void run() {
                        chatList.add(new ChatItem(0, "일기에서 가장 많이 느껴지는 감정은 ~~입니다"));
                        chatList.add(new ChatItem(0, "당신을 위해 ~~색 조명을 틀어드릴게요"));
                        chatList.add(new ChatItem(2));
                        adapter.notifyDataSetChanged(); // 챗봇 메세지 리스트 갱신
                    } }, 600); // 0.6초 딜레이 후 함수 실행

                    et_input.setText(""); // 메세지 입력창 초기화
                    sequence++; // 다음 단계로 이동할 수 있도록 변수값 변경 (일기 입력이 완료된 단계라는 의미)
                    et_input.setEnabled(false); // 메세지 입력창 사용 금지
                     */
                } else if(sequence == 3) { // 컬러테라피가 끝난 후 의견을 입력받는 단계
                    String message = et_input.getText().toString(); // 사용자가 입력한 메세지 가져옴
                    chatList.add(new ChatItem(1, message)); // 사용자가 입력한 메시지를 챗봇 메세지 리스트에 추가
                    chatList.add(new ChatItem(0, "의견을 남겨주셔서 감사합니다 :)"));
                    rv_chat.scrollToPosition(chatList.size()-1); // 뷰 스크롤 가장 아래로 위
                    et_input.setText(""); // 메세지 입력창 초기화
                    adapter.notifyDataSetChanged();
                }
            }
        });
        return view;
    }

    // 챗봇 초기 세팅
    private void initData(){
        chatList = new ArrayList<>();
        chatList.add(new ChatItem(0,"오늘 하루에 대해 일기를 남겨볼까요?"));
    }


    // 감정 분석 모델 가져오기
    private void getEmotionModel() {
            FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder("modelSR").build();
            FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();
            FirebaseModelManager.getInstance().download(remoteModel, conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void v) {
                    Toast.makeText(getContext(), "get model success", Toast.LENGTH_SHORT).show();
                    FirebaseModelManager.getInstance().getLatestModelFile(remoteModel).addOnCompleteListener(new OnCompleteListener<File>() {
                        @Override
                        public void onComplete(@NonNull Task<File> task) {
                            File modelFile = task.getResult();
                            if (modelFile != null) {
                                interpreter = new Interpreter(modelFile);
                                Toast.makeText(getContext(), "get interpreter success", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
    }

        /*
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder().requireWifi().build();
        FirebaseModelDownloader.getInstance()
                .getModel("modelDY", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        Toast.makeText(getContext(), "get model success", Toast.LENGTH_SHORT).show();
                        File modelFile = model.getFile();
                        if (modelFile != null) {
                            interpreter = new Interpreter(modelFile);
                            Toast.makeText(getContext(), "get interpreter success", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
         */

    // 버튼 뷰 삭제
    public void deleteButton() {
        chatList.remove(chatList.size()-1);
        adapter.notifyDataSetChanged();
    }

    // 사용자가 버튼 클릭시 -> 선택된 버튼에 따라 텍스트 생성
    public void userClick(String text) {
        chatList.add(new ChatItem(1, text));
        adapter.notifyDataSetChanged();
    }

    // 타이머 설정하는 메소드
    public void setTimer() {
        if(sequence == 2) { // 타이머 설정 단계일 경우
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() { public void run() {
                chatList.add(new ChatItem(0, "타이머를 설정해주세요"));
                chatList.add(new ChatItem(3));
                adapter.notifyDataSetChanged(); // 챗봇 메세지 리스트 갱신
            } }, 600); // 0.6초 딜레이 후 함수 실행
        }
    }

    // 타이머 팝업창 메소드
    public void showAlert() {
        AlertDialog.Builder time_dialog = new AlertDialog.Builder(getActivity());

        time_dialog.setTitle("시간 직접 입력");
        time_dialog.setMessage("시간을 입력해주세요.");

        // 뷰와 다이얼로그 연결
        LayoutInflater inflater = getLayoutInflater();
        View timeView = inflater.inflate(R.layout.time_dialog, null);
        time_dialog.setView(timeView);

        EditText time_hour = timeView.findViewById(R.id.et_hour);
        EditText time_min = timeView.findViewById(R.id.et_minute);

        // 확인 버튼 설정
        time_dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value_hour = time_hour.getText().toString();
                String value_min = time_min.getText().toString();

                if(value_hour.equals("") && value_min.equals("")) {
                    Toast.makeText(getContext(), "시간을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if(value_hour.equals("") && !value_min.equals("")) {
                    chatList.remove(chatList.size()-1);
                    chatList.add(new ChatItem(1, value_min + "분"));
                    dialog.dismiss(); // 팝업창 닫기

                    Long minute = Long.parseLong(value_min);
                    startTimer((minute)*60*1000);
                } else if(!value_hour.equals("") && value_min.equals("")) {
                    chatList.remove(chatList.size()-1);
                    chatList.add(new ChatItem(1, value_hour + "시간"));
                    dialog.dismiss(); // 팝업창 닫기

                    Long hour = Long.parseLong(value_hour);
                    startTimer((hour*60)*60*1000);
                } else {
                    chatList.remove(chatList.size()-1);
                    chatList.add(new ChatItem(1, value_hour + "시간 " + value_min + "분"));
                    dialog.dismiss(); // 팝업창 닫기

                    Long hour = Long.parseLong(value_hour);
                    Long minute = Long.parseLong(value_min);
                    startTimer((hour*60 + minute)*60*1000);
                }

            }
        });

        // 취소 버튼 설정
        time_dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });

        // 창 띄우기
        time_dialog.show();
    }

    // 타이머 실행 메소드
    public void startTimer(long time) {
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() { public void run() {
            chatList.add(new ChatItem(4, time));
            adapter.notifyDataSetChanged(); // 챗봇 메세지 리스트 갱신
        } }, 600); // 0.6초 딜레이 후 함수 실행
    }

    // 조명 서비스 후 의견을 입력받는 메소드
    public void Comment() {
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() { public void run() {
            chatList.add(new ChatItem(0, "타이머가 종료되었습니다"));
            chatList.add(new ChatItem(0, "~~색 조명이 당신의 감정에 도움이 되셨나요?"));
            chatList.add(new ChatItem(0, "의견을 남겨주세요"));
            et_input.setEnabled(true); // 메세지 입력창 사용 허용
            adapter.notifyDataSetChanged(); // 챗봇 메세지 리스트 갱신
            adapter.countDownTimer.cancel();
        } }, 600); // 0.6초 딜레이 후 함수 실행
    }

}