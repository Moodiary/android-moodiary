package ac.kr.duksung.moodiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ChatItem> chatList = null;
    ChatFragment fragment;

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

        if(type == 0){ // 챗봇 뷰일 경우
            view = inflater.inflate(R.layout.chat_item_chatbot,parent,false);
            return new ChatbotViewHolder(view);
        } else if(type == 1){ // 사용자 뷰일 경우
            view = inflater.inflate(R.layout.chat_item_user,parent,false);
            return new UserViewHolder(view);
        } else if(type == 2) { // 버튼 뷰일 경우
            view = inflater.inflate(R.layout.chat_item_button, parent, false);
            return new ButtonViewHolder(view);
        }else
            return null;
    }

    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof ChatbotViewHolder){
            ((ChatbotViewHolder)viewHolder).tv_chatbot.setText(chatList.get(position).getContent());
        } else if(viewHolder instanceof UserViewHolder){
            ((UserViewHolder)viewHolder).tv_user.setText(chatList.get(position).getContent());
        } else if(viewHolder instanceof ButtonViewHolder) {
            ((ButtonViewHolder) viewHolder).button1.setText(chatList.get(position).getBtn_text1());
            ((ButtonViewHolder) viewHolder).button2.setText(chatList.get(position).getBtn_text2());
            ((ButtonViewHolder) viewHolder).button3.setText(chatList.get(position).getBtn_text3());
            ((ButtonViewHolder) viewHolder).button4.setText(chatList.get(position).getBtn_text4());
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
    // 챗봇 뷰 홀더
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

    // 버튼 뷰 홀더
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
            int type = chatList.get(pos).getBtn_type(); // 버튼 뷰의 타입 값

            switch (v.getId()) {
                case R.id.button1:
                    if(type == 1) {
                        Toast.makeText(v.getContext(), "조명", Toast.LENGTH_SHORT).show();
                        fragment.setTimer();
                    }
                    else if (type == 2)
                        Toast.makeText(v.getContext(),"15분", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button2:
                    if(type == 1) {
                        Toast.makeText(v.getContext(), "사운드", Toast.LENGTH_SHORT).show();
                        fragment.setTimer();
                    }
                    else if (type == 2)
                        Toast.makeText(v.getContext(),"30분", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button3:
                    if(type == 1) {
                        Toast.makeText(v.getContext(), "둘 다", Toast.LENGTH_SHORT).show();
                        fragment.setTimer();
                    }
                    else if (type == 2)
                        Toast.makeText(v.getContext(),"1시간", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button4:
                    if(type == 1)
                        Toast.makeText(v.getContext(),"선택 안함", Toast.LENGTH_SHORT).show();
                    else if (type == 2) {
                        Toast.makeText(v.getContext(), "직접 입력", Toast.LENGTH_SHORT).show();
                        fragment.et_input.setEnabled(true);
                    }
                    break;
            }
        }
    }
}
