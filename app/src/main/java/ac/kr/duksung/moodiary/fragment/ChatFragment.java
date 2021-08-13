package ac.kr.duksung.moodiary.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import ac.kr.duksung.moodiary.ConnectedThread;
import ac.kr.duksung.moodiary.R;
import ac.kr.duksung.moodiary.TextClassification;
import ac.kr.duksung.moodiary.adapter.ChatAdapter;
import ac.kr.duksung.moodiary.domain.ChatItem;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

// 화면 설명 : 메인화면의 챗봇 화면
// Author : Soohyun, Last Modified : 2021.06.14
public class ChatFragment extends Fragment {
    public int sequence = 1; // 챗봇의 단계 처리를 위한 변수
    public ArrayList<ChatItem> chatList = new ArrayList<>(); ; // 챗봇 메세지 리스트
    RecyclerView rv_chat; // 챗봇 리사이클러뷰
    ChatAdapter adapter; // 챗봇 어댑터
    public EditText et_input; // 메세지 입력창
    ImageButton btn_push; // 전송 버튼

    Interpreter interpreter; // 모델 인터프리터
    BluetoothAdapter btAdapter; // 블루투스 통신을 위한 어댑터
    BluetoothSocket btSocket; // 블루투스 통신을 위한 소켓
    ConnectedThread connectedThread; // 소켓 통신을 위한 스레드
    private final static int REQUEST_ENABLE_BT = 1; // 블루투스 연결 번호
    private static String btAddress = "00:20:12:08:1D:EF"; // 장치의 MAC주소
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UDDI 값

    float maxEmotion = 0; // 최대 감정 정보(퍼센트)
    int maxIndex = -1; // 최대 감정 인덱스
    String[] emotion = {"공포", "놀람", "분노", "슬픔", "중립", "행복", "혐오"}; // 감정 정보
    String[] color = {"파란색", "노란색", "빨강", "주황색", "흰색", "흰색", "초록색"}; // 컬러테라피 정보

    public static String url; //노래재생을 위한 웹서버 url

    MediaPlayer player;
    //int position = 0; // 음악 다시 시작 기능을 위한 현재 재생 위치 확인 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        rv_chat = view.findViewById(R.id.rv_chat);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false); // 레이아웃 매니저
        adapter = new ChatAdapter(chatList, ChatFragment.this); // 챗봇 어댑터
        rv_chat.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_chat.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결
        et_input = view.findViewById(R.id.et_input);
        btn_push = view.findViewById(R.id.btn_push);

        init(); // 데이터 초기화

        // 전송 버튼 클릭시
        btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sequence == 1) { // 감정일기 쓰는 단계일 경우
                    String message = et_input.getText().toString().trim(); // 사용자가 입력한 메세지 가져옴
                    chatList.add(new ChatItem(1, message)); // 사용자가 입력한 메시지를 챗봇 메세지 리스트에 추가
                    chatList.add(new ChatItem(0, "감정을 분석 중입니다."));
                    adapter.notifyDataSetChanged();
                    et_input.setText("");

                    getEmotion(message);

                    /*
                    DisposableObserver<String> observer = new DisposableObserver<String>() {
                        @Override
                        public void onNext(@NonNull String s) {
                            // 데이터 전처리
                            TextClassification client = new TextClassification(getContext()); // 데이터 전처리 클래스 호출
                            List<String> tokenizeText = client.tokenize(message); // 토큰화된 텍스트
                            String[][] paddingText = client.padSequence(tokenizeText); // 패딩된 텍스트
                            float[][] dicText = client.jsonParsing(paddingText); // 정수화된 텍스트

                            getEmotionModel(message, dicText); // 감정 분석 모델 실행
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d("TEST", "Observer Error...");
                        }

                        @Override
                        public void onComplete() {
                            Log.d("TEST", "Observer Complete!");
                        }
                    };

                    Observable<String> observable = Observable.create(emitter -> {
                                emitter.onNext(Thread.currentThread().getName() + "\n: RxJava Observer Test");
                                emitter.onComplete();
                            }
                    );

                    observable.subscribeOn(Schedulers.io()).subscribe(observer); // io스레드에서 실행
                     */

                }
            }
        });

        return view;
    }

    // 챗봇 초기 세팅
    private void init(){
        chatList.add(new ChatItem(0,"어떤 것을 원하시나요?"));
        chatList.add(new ChatItem(5)); // 일기/조명 선택 옵션 보여주기
        et_input.setEnabled(false); // 메세지 입력창 사용 금지
    }

    // 감정 분석 모델 가져오기
    private void getEmotionModel(String message, float[][] paddingText) {

        FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder("modelSR").build();
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();
        FirebaseModelManager.getInstance().download(remoteModel, conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void v) {
                FirebaseModelManager.getInstance().getLatestModelFile(remoteModel).addOnCompleteListener(new OnCompleteListener<File>() {
                    @Override
                    public void onComplete(@NonNull Task<File> task) {
                        File modelFile = task.getResult();
                        if (modelFile != null) {
                            interpreter = new Interpreter(modelFile); // 인터프리터 생성
                            float[][] input = paddingText; // input 텍스트
                            float[][] output = new float[1][7]; // 모델 output 결과

                            if(interpreter != null) {
                                interpreter.run(input, output); // 모델 실행
                            } else {
                                interpreter = new Interpreter(modelFile); // 인터프리터 생성
                                interpreter.run(input, output); // 모델 실행
                            }

                            // 모델 결과값 가져온 후 최대 감정 뽑아내기
                            for (int i = 0; i < 7; i++) {
                                if(maxEmotion < output[0][i]) {
                                    maxEmotion = output[0][i];
                                    maxIndex = i;
                                }
                                System.out.println(i + " : " + output[0][i]);
                            }

                            interpreter.close(); // 인터프리터 종료

                            chatList.add(new ChatItem(0, "일기에서 가장 많이 느껴지는 감정은 " + emotion[maxIndex] + "입니다"));
                            chatList.add(new ChatItem(0, "당신을 위해 " + color[maxIndex] +" 조명을 틀어드릴게요"));
                            chatList.add(new ChatItem(2));
                            adapter.notifyDataSetChanged(); // 챗봇 메세지 리스트 갱신

                            et_input.setText(""); // 메세지 입력창 초기화
                            sequence++; // 다음 단계로 이동할 수 있도록 변수값 변경 (일기 입력이 완료된 단계라는 의미)
                            et_input.setEnabled(false); // 메세지 입력창 사용 금지
                        }

                        saveDairy(message); // 일기와 감정 정보 저장 메소드 실행
                    }
                });
            }
        });
    }

    // 일기 감정 분석 결과 가져오는 메소드
    public void getEmotion(String content) {

        // 사용자 입력 정보 JSON 형태로 변환
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("emotions", content);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // 서버에 데이터 전달
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:5000/predict", requestJsonObject, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) { // 데이터 전달 후 받은 응답

                try {
                    String result = response.getString("code"); // 응답 메시지 가져오기

                    // 응답 메시지에 따른 처리
                    if(result.equals("400"))
                        Toast.makeText(getContext(),"에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    if(result.equals("200")) {
                        String emotions = response.getString("result"); // 일기 감정 분석 결과값 가져오기
                        JSONArray jArray = new JSONArray(emotions);

                        System.out.println(emotions);

                        // 긍정, 부정 퍼센트 값 가져오기
                        JSONObject jObject = jArray.getJSONObject(0);
                        String[] first = jObject.getString("0").split(" ");
                        String[] second = jObject.getString("1").split(" ");
                        chatList.add(new ChatItem(0, "일기에서 가장 많이 보여지는 감정은\n" + first[0] + " " + first[1] + "%  "+ second[0] + " " + second[1] + "% 입니다"));

                        if(first[0].equals("긍정")) { // 긍정 감정인 경우
                            maxIndex = 4; // 최대 감정 뽑기
                        } else { // 부정 감정일 경우 부정 세부 감정의 퍼센트 값 가져오기
                            String chat = "부정 감정 중 많이 보여지는 감정은 \n";
                            JSONObject jObject_detail = jArray.getJSONObject(1);
                            for(int i = 0; i < 6; i++) {
                                String[] detail = jObject_detail.getString(String.valueOf(i)).split(" ");

                                if(i == 0)
                                    maxIndex = Arrays.asList(emotion).indexOf(detail[0]);

                                if(!detail[1].equals("0"))
                                    chat += detail[0] + " " + detail[1] +"%  ";
                            }
                            chat += "입니다";
                            chatList.add(new ChatItem(0, chat));
                        }

                        chatList.add(new ChatItem(0, "현재 감정에 도움이 되는 " + color[maxIndex] +" 조명을 틀어드릴게요"));
                        chatList.add(new ChatItem(2));
                        adapter.notifyDataSetChanged(); // 챗봇 메세지 리스트 갱신

                        et_input.setText(""); // 메세지 입력창 초기화
                        sequence++; // 다음 단계로 이동할 수 있도록 변수값 변경 (일기 입력이 완료된 단계라는 의미)
                        et_input.setEnabled(false); // 메세지 입력창 사용 금지*/

                        saveDairy(content); // 일기 저장
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() { // 데이터 전달 및 응답 실패시
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "네트워크 연결 오류", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObject);
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

    // 일기와 감정 정보 저장 메소드
    public void saveDairy(String content) {
        SharedPreferences auto = this.getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 자동로그인 데이터 저장되어있는 곳
        String user_id = auto.getString("ID",null); // 저장된 아이디 값, 없으면 null

        // 사용자 입력 정보 JSON 형태로 변환
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("user_id",user_id);
            requestJsonObject.put("content", content);
            requestJsonObject.put("emotion", emotion[maxIndex]);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // 서버에 데이터 전달
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:3000/diary/savediary", requestJsonObject, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) { // 데이터 전달 후 받은 응답

                try {
                    String result = response.getString("code"); // 응답 메시지 가져오기

                    // 응답 메시지에 따른 처리
                    if(result.equals("400"))
                        Toast.makeText(getContext(),"에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    if(result.equals("200")) {
                        System.out.println("감정 일기 저장 완료");
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() { // 데이터 전달 및 응답 실패시
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "네트워크 연결 오류", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObject);
    }

    // 타이머 설정하는 메소드
    public void setTimer() {
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() { public void run() {
            chatList.add(new ChatItem(0, "타이머를 설정해주세요"));
            chatList.add(new ChatItem(3));
            adapter.notifyDataSetChanged(); // 챗봇 메세지 리스트 갱신
        } }, 600); // 0.6초 딜레이 후 함수 실행
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
        //connectBT();
        playAudio();

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() { public void run() {
            chatList.add(new ChatItem(4, time));
            adapter.notifyDataSetChanged(); // 챗봇 메세지 리스트 갱신
        } }, 600); // 0.6초 딜레이 후 함수 실행
    }

    public void AudioUrl() {
        // 일기의 최대 감정에 따라 Audio Url 변경.
        switch (maxIndex) {
            case 0: // 공포
                url = "http://10.0.2.2:3000/music/fear";
                break;
            case 1: // 놀람
                url = "http://10.0.2.2:3000/music/surprise";
                break;
            case 2: // 분노
                url = "http://10.0.2.2:3000/music/anger";
                break;
            case 3: // 슬픔
                url = "http://10.0.2.2:3000/music/sad";
                break;
            case 4: // 중립
                url = "http://10.0.2.2:3000/music/happy";
                break;
            case 5: // 행복
                url = "http://10.0.2.2:3000/music/happy";
                break;
            case 6: // 혐오
                url = "http://10.0.2.2:3000/music/aversion";
                break;
        }
    }

    // 음악을 재생하는 메소드
    public void playAudio() {
        try {
            AudioUrl();

            player = new MediaPlayer();
            player.setDataSource(url);
            player.prepare();
            player.start();
            player.setLooping(true);

            Toast.makeText(getContext(), "재생 시작됨.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //음악 재생을 종료하는 메소드
    public void stopAudio() {
        if(player != null && player.isPlaying()){
            player.stop();

            Toast.makeText(getContext(), "중지됨.", Toast.LENGTH_SHORT).show();
        }
    }


    // 블루투스 통신 및 조명 서비스를 제공하는 메소드
    public void connectBT() {
        btAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터 할당

        if (!btAdapter.isEnabled()) {
            // 블루투스가 비활성화인 상태 -> 사용자에게 블루투스 사용 동의 요청
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            // 블루투스를 지원하며 활성 상태인 경우 -> 페어링된 장치가 있는지 확인
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices(); // 페어링된 장치 목록
            
            if (pairedDevices.size() > 0) {
                // 페어링된 장치가 있는 경우.
                boolean flag = true; // 소켓이 정상적으로 만들어지고 연결되어 있는지 확인하는 변수
                BluetoothDevice device = btAdapter.getRemoteDevice(btAddress); // 페어링된 장치
                
                try {
                    btSocket = createBluetoothSocket(device); // 소켓을 생성
                    btSocket.connect(); // 소켓과 연결
                } catch (IOException e) {
                    flag = false;
                    System.out.println("connected fail!");
                    Toast.makeText(getContext(),"어플을 종료하고 다시 실행해주세요",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                if(flag){
                    // 소켓과 정상적으로 연결되어 있는 경우
                    connectedThread = new ConnectedThread(btSocket); // 소켓과 통신할 스레드 생성
                    connectedThread.start(); // 스레드 시작
 
                    // 일기의 최대 감정에 따라 조명에 넘겨줄 데이터를 정의한 부분
                    switch (maxIndex) {
                        case 0: // 공포
                            connectedThread.write("1"); // 스레드를 통해 데이터 전송
                            break;
                        case 1: // 놀람
                            connectedThread.write("2");
                            break;
                        case 2: // 분노
                            connectedThread.write("3");
                            break;
                        case 3: // 슬픔
                            connectedThread.write("4");
                            break;
                        case 4: // 중립
                            connectedThread.write("5");
                            break;
                        case 5: // 행복
                            connectedThread.write("5");
                            break;
                        case 6: // 혐오
                            connectedThread.write("6");
                            break;
                    }
                }

            } else {
                // 페어링된 장치가 없는 경우.
                Toast.makeText(getContext(), "먼저 Bluetooth 설정에 들어가 페어링을 진행해 주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 블루투스 통신 소켓을 생성하는 메소드
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                System.out.println("Could not create Insecure RFComm Connection" + e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    // 블루투스로 연결된 조명을 끄는 메소드
    public void finishBT() {
        connectedThread.write("0"); // 스레드를 통해 데이터 전송
    }

    // 조명 켜기 선택시 실행되는 메소드
    public void checktodayLight() {
        SharedPreferences auto = this.getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 자동로그인 데이터 저장되어있는 곳
        String user_id = auto.getString("ID",null); // 저장된 아이디 값, 없으면 null

        // 사용자 입력 정보 JSON 형태로 변환
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("user_id",user_id);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // 서버에 데이터 전달

        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:3000/diary/todaydiary", requestJsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) { // 데이터 전달 후 받은 응답

                try {
                    String result = response.getString("code"); // 응답 메시지 가져오기

                    // 응답 메시지에 따른 처리
                    if(result.equals("400"))
                        Toast.makeText(getContext(),"에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    // 오늘 작성한 일기가 없는 경우 - 흰색 조명을 틀어줌
                    if(result.equals("204")) {
                        chatList.add(new ChatItem(0, "오늘 작성한 일기가 없으므로 흰색 조명을 틀어드릴게요"));
                        setTimer();
                        adapter.notifyDataSetChanged();
                        maxIndex = 4;
                        //connectBT();
                    }
                    // 오늘 작성한 일기가 있는 경우
                    if(result.equals("200")) {
                        String emotion = response.getString("emotion"); // 오늘 일기 감정
                        if(emotion.equals("공포")) { maxIndex = 0; }
                        else if(emotion.equals("놀람")) { maxIndex = 1; }
                        else if(emotion.equals("분노")) { maxIndex = 2; }
                        else if(emotion.equals("슬픔")) { maxIndex = 3; }
                        else if(emotion.equals("중립")) { maxIndex = 4; }
                        else if(emotion.equals("행복")) { maxIndex = 5; }
                        else if(emotion.equals("혐오")) { maxIndex = 6; }

                        chatList.add(new ChatItem(0,"오늘 감정에 도움이 되는 " + color[maxIndex] +" 조명을 틀어드릴게요"));
                        setTimer();
                        adapter.notifyDataSetChanged();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() { // 데이터 전달 및 응답 실패시
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "네트워크 연결 오류", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObject);
    }

    //오늘의 일기를 썼는지 확인하는 메소드
    public void checkTodayDiary() {
        SharedPreferences auto = this.getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 자동로그인 데이터 저장되어있는 곳
        String user_id = auto.getString("ID",null); // 저장된 아이디 값, 없으면 null

        // 사용자 입력 정보 JSON 형태로 변환
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("user_id",user_id);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // 서버에 데이터 전달
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:3000/diary/todaydiary", requestJsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) { // 데이터 전달 후 받은 응답

                try {
                    String result = response.getString("code"); // 응답 메시지 가져오기

                    // 응답 메시지에 따른 처리
                    if(result.equals("400"))
                        Toast.makeText(getContext(),"에러가 발생했습니다", Toast.LENGTH_SHORT).show();

                    // 오늘 작성한 일기가 없는 경우
                    if(result.equals("204")) {
                        chatList.add(new ChatItem(0, "오늘 하루에 대해 일기를 남겨볼까요?"));
                        adapter.notifyDataSetChanged();
                    }

                    // 오늘 작성한 일기가 있는 경우
                    if(result.equals("200")) {
                        chatList.add(new ChatItem(0,"오늘의 일기가 이미 작성되었어요!"));
                        chatList.add(new ChatItem(0,"모아보기에서 오늘의 일기를 삭제하면 일기를 다시 작성할 수 있어요!"));
                        adapter.notifyDataSetChanged();
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() { // 데이터 전달 및 응답 실패시
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "네트워크 연결 오류", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObject);
    }

}