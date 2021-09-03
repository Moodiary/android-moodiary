package ac.kr.duksung.moodiary.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ac.kr.duksung.moodiary.fragment.ChatFragment;
import ac.kr.duksung.moodiary.domain.ChatItem;
import ac.kr.duksung.moodiary.R;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ChatItem> chatList = null;
    ChatFragment fragment;
    public CountDownTimer countDownTimer; // 남은 시간 알려주는 타이머

    public ChatAdapter(ArrayList<ChatItem> chatList, ChatFragment fragment){
        this.chatList = chatList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(type == 0){ // 디어 뷰일 경우
            view = inflater.inflate(R.layout.chat_item_chatbot,parent,false);
            return new ChatbotViewHolder(view);
        } else if(type == 1){ // 사용자 뷰일 경우
            view = inflater.inflate(R.layout.chat_item_user,parent,false);
            return new UserViewHolder(view);
        } else if(type == 2) { // 조명 뷰일 경우
            view = inflater.inflate(R.layout.chat_item_button_light, parent, false);
            return new LightViewHolder(view);
        } else if(type == 3) { // 타이머 설정 뷰일 경우
            view = inflater.inflate(R.layout.chat_item_button_time, parent, false);
            return new ButtonViewHolder(view);
        } else if(type == 4) { // 실시간 타이머 뷰일 경우
            view = inflater.inflate(R.layout.chat_item_timer, parent, false);
            return new TimerViewHolder(view);
        } else if(type == 5) { // 일기/조명 선택 뷰일 경우
            view = inflater.inflate(R.layout.chat_item_select, parent, false);
            return new SelectViewHolder(view);
        } else
            return null;
    }

    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof ChatbotViewHolder){
            ((ChatbotViewHolder)viewHolder).tv_chatbot.setText(chatList.get(position).getContent());
        } else if(viewHolder instanceof UserViewHolder){
            ((UserViewHolder)viewHolder).tv_user.setText(chatList.get(position).getContent());
        } else if (viewHolder instanceof TimerViewHolder) {
            long time = chatList.get(position).getTime(); // 설정된 타이머 값

            // 타이머 생성
            countDownTimer = new CountDownTimer(time,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    String time = hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
                    ((TimerViewHolder) viewHolder).tv_timer.setText(time);
                }

                @Override
                public void onFinish() {
                    fragment.finishBT(); // 조명 서비스 종료
                    fragment.stopAudio(); //음악 재생 종료
                    fragment.deleteButton();
                    fragment.chatList.add(new ChatItem(0, "타이머가 종료되었습니다"));
                }
            };
            countDownTimer.start(); // 타이머 시작
        }
    }

    // 리사이클러뷰 안의 전체 데이터 개수 리턴
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    // 리사이클러뷰의 아이템의 뷰 타입 리턴
    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getType();
    }

    // 뷰 홀더에 들어갈 아이템 세팅
    // 디어 뷰 홀더
    public class ChatbotViewHolder extends RecyclerView.ViewHolder{
        TextView tv_chatbot;

        public ChatbotViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_chatbot = itemView.findViewById(R.id.tv_chatbot);
        }
    }

    // 사용자 뷰 홀더
    public class UserViewHolder extends RecyclerView.ViewHolder{
        TextView tv_user;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_user = itemView.findViewById(R.id.tv_user);
        }
    }

    // 조명 설정 뷰 홀더
    public class LightViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView button1;
        TextView button2;

        public LightViewHolder(@NonNull View itemView) {
            super(itemView);
            button1 = itemView.findViewById(R.id.button1);
            button2 = itemView.findViewById(R.id.button2);

            button1.setOnClickListener(this);
            button2.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition(); // 리스트에서 위치

            switch (v.getId()) {
                case R.id.button1:
                    fragment.deleteButton();
                    fragment.userClick("좋아요");
                    fragment.setTimer();
                    break;
                case R.id.button2:
                    fragment.deleteButton();
                    fragment.userClick("싫어요");
                    fragment.chatList.add(new ChatItem(0, "오늘의 감정일기는 '모아보기'에서 꺼내볼 수 있어요"));
                    fragment.chatList.add(new ChatItem(0, "내일도 감정일기를 쓰면서 하루를 정리해보아요 :)"));
                    break;
            }
        }
    }

    // 타이머 설정 뷰 홀더
    public class ButtonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView button1;
        TextView button2;
        TextView button3;
        TextView button4;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            button1 = itemView.findViewById(R.id.button1);
            button2 = itemView.findViewById(R.id.button2);
            button3 = itemView.findViewById(R.id.button3);
            button4 = itemView.findViewById(R.id.button4);

            button1.setOnClickListener(this);
            button2.setOnClickListener(this);
            button3.setOnClickListener(this);
            button4.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition(); // 리스트에서 위치

            switch (v.getId()) {
                case R.id.button1:
                    fragment.deleteButton();
                    fragment.userClick("15분");
                    fragment.startTimer(15*60*1000);
                    break;
                case R.id.button2:
                    fragment.deleteButton();
                    fragment.userClick("30분");
                    fragment.startTimer(30*60*1000);
                    break;
                case R.id.button3:
                    fragment.deleteButton();
                    fragment.userClick("1시간");
                    fragment.startTimer(60*60*1000);
                    break;
                case R.id.button4:
                    fragment.showAlert();
                    break;
            }
        }
    }

    // 실시간 타이머 뷰 홀더
    public class TimerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_timer;
        AppCompatButton btn_finish;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_timer = itemView.findViewById(R.id.tv_timer);
            btn_finish = itemView.findViewById(R.id.btn_finish);

            btn_finish.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_finish:
                    countDownTimer.cancel(); // 타이머 종료
                    fragment.finishBT(); // 조명 서비스 종료
                    fragment.stopAudio(); //음악 재생 종료
                    fragment.deleteButton();
                    fragment.chatList.add(new ChatItem(0, "타이머가 종료되었습니다"));
            }
        }

    }

    // 일기/ 조명 선택 뷰 홀더
    public class SelectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView button1;
        TextView button2;

        public SelectViewHolder(@NonNull View itemView) {
            super(itemView);
            button1 = itemView.findViewById(R.id.button1);
            button2 = itemView.findViewById(R.id.button2);

            button1.setOnClickListener(this);
            button2.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition(); // 리스트에서 위치

            switch (v.getId()) {
                case R.id.button1:
                    fragment.deleteButton();
                    fragment.userClick("일기 쓰기");
                    fragment.checkTodayDiary();
                    fragment.et_input.setEnabled(true); // 메세지 입력창 활성화
                    break;
                case R.id.button2:
                    fragment.deleteButton();
                    fragment.userClick("조명 켜기");
                    fragment.checktodayLight();
                    break;
            }
        }
    }
}
