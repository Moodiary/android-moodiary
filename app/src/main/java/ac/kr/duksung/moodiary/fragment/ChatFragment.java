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
import android.widget.TextView;
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

import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ac.kr.duksung.moodiary.R;
import ac.kr.duksung.moodiary.adapter.ChatAdapter;
import ac.kr.duksung.moodiary.domain.ChatItem;
import scala.collection.Seq;

// 화면 설명 : 메인화면의 챗봇 화면
// Author : Soohyun, Last Modified : 2021.02.15
public class ChatFragment extends Fragment {
    public int sequence = 1; // 챗봇의 단계 처리를 위한 변수
    public ArrayList<ChatItem> chatList; // 챗봇 메세지 리스트
    ChatAdapter adapter;
    EditText et_input; // 메세지 입력창
    Button btn_push; // 전송 버튼
    Interpreter interpreter;

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
        TextView tv_timer = view.findViewById(R.id.tv_timer);

        // 전송 버튼 클릭시
        btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sequence == 1) { // 감정일기 쓰는 단계일 경우
                    String message = et_input.getText().toString(); // 사용자가 입력한 메세지 가져옴
                    chatList.add(new ChatItem(1, message)); // 사용자가 입력한 메시지를 챗봇 메세지 리스트에 추가

                    Test(message);
                    //getEmotionModel(); // 감정 분석 모델 가져오기

                    /*
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() { public void run() {
                        chatList.add(new ChatItem(0, "당신의 감정은 ~~~~ 하군요"));
                        chatList.add(new ChatItem(0, "당신의 감정에 도움이 되는 컬러테라피와 음악을 제공해드릴게요"));
                        chatList.add(new ChatItem(2, "조명", "사운드", "둘 다", "선택 안함", 1));
                        adapter.notifyDataSetChanged(); // 챗봇 메세지 리스트 갱신
                    } }, 600); // 0.6초 딜레이 후 함수 실행

                    et_input.setText(""); // 메세지 입력창 초기화
                    sequence++; // 다음 단계로 이동할 수 있도록 변수값 변경
                    et_input.setEnabled(false); // 메세지 입력창 사용 금지*/
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

    public void Test(String text) {

        //String text = "한국어를 처리하는 예시입니닼ㅋㅋㅋㅋㅋ #한국어";

        // Normalize
        CharSequence normalized = TwitterKoreanProcessorJava.normalize(text);
        System.out.println(normalized);
        // 한국어를 처리하는 예시입니다ㅋㅋ #한국어


        // Tokenize
        Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
        System.out.println(TwitterKoreanProcessorJava.tokensToJavaStringList(tokens));
        // [한국어, 를, 처리, 하는, 예시, 입니, 다, ㅋㅋ, #한국어]
        System.out.println(TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(tokens));
        // [한국어(Noun: 0, 3), 를(Josa: 3, 1),  (Space: 4, 1), 처리(Noun: 5, 2), 하는(Verb: 7, 2),  (Space: 9, 1), 예시(Noun: 10, 2), 입니(Adjective: 12, 2), 다(Eomi: 14, 1), ㅋㅋ(KoreanParticle: 15, 2),  (Space: 17, 1), #한국어(Hashtag: 18, 4)]


        // Stemming
        Seq<KoreanTokenizer.KoreanToken> stemmed = TwitterKoreanProcessorJava.stem(tokens);
        System.out.println(TwitterKoreanProcessorJava.tokensToJavaStringList(stemmed));
        // [한국어, 를, 처리, 하다, 예시, 이다, ㅋㅋ, #한국어]
        System.out.println(TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(stemmed));
        // [한국어(Noun: 0, 3), 를(Josa: 3, 1),  (Space: 4, 1), 처리(Noun: 5, 2), 하다(Verb: 7, 2),  (Space: 9, 1), 예시(Noun: 10, 2), 이다(Adjective: 12, 3), ㅋㅋ(KoreanParticle: 15, 2),  (Space: 17, 1), #한국어(Hashtag: 18, 4)]


        // Phrase extraction
        List<KoreanPhraseExtractor.KoreanPhrase> phrases = TwitterKoreanProcessorJava.extractPhrases(tokens, true, true);
        System.out.println(phrases);
        // [한국어(Noun: 0, 3), 처리(Noun: 5, 2), 처리하는 예시(Noun: 5, 7), 예시(Noun: 10, 2), #한국어(Hashtag: 18, 4)]

    }

        // 감정 분석 모델 가져오기
    private void getEmotionModel() {
        FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder("modelDY").build();
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();
        FirebaseModelManager.getInstance().download(remoteModel, conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        // Download complete. Depending on your app, you could enable
                        // the ML feature, or switch from the local model to the remote
                        // model, etc.
                        Toast.makeText(getContext(), "get model success", Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseModelManager.getInstance().getLatestModelFile(remoteModel).addOnCompleteListener(new OnCompleteListener<File>() {
                    @Override
                    public void onComplete(@NonNull Task<File> task) {
                        File modelFile = task.getResult();
                        if (modelFile != null) {
                            interpreter = new Interpreter(modelFile);
                        }
                    }
                });
    }

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
                chatList.add(new ChatItem(2, "15분", "30분", "1시간", "직접 입력", 2));
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
                chatList.add(new ChatItem(1, value_hour + "시간 " + value_min + "분"));
                dialog.dismiss();     //닫기

                Long hour = Long.parseLong(value_hour);
                Long minute = Long.parseLong(value_min);
                startTimer((hour*60 + minute)*60*1000);
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
            chatList.add(new ChatItem(3, time));
            adapter.notifyDataSetChanged(); // 챗봇 메세지 리스트 갱신
        } }, 600); // 0.6초 딜레이 후 함수 실행
    }

}