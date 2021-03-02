package ac.kr.duksung.moodiary.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ac.kr.duksung.moodiary.domain.MypageItem;
import ac.kr.duksung.moodiary.R;

public class MypageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    private ArrayList<MypageItem> mypageList = null;
    private OnItemClickListener listener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MypageAdapter(Context context, ArrayList<MypageItem> mypageList){
        this.context = context;
        this.mypageList = mypageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(type == 0){ // 일반 리스트 뷰일 경우
            view = inflater.inflate(R.layout.mypage_item_text, parent,false);
            return new MypageAdapter.ListViewHolder(view);
        } else if(type == 1){ // 알림 설정 뷰일 경우
            view = inflater.inflate(R.layout.mypage_item_switch, parent,false);
            return new MypageAdapter.AlarmViewHolder(view);
        } else
            return null;
    }

    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof ListViewHolder){
            ((ListViewHolder)viewHolder).text.setText(mypageList.get(position).getText());
        } else if(viewHolder instanceof AlarmViewHolder){
            ((AlarmViewHolder)viewHolder).text.setText(mypageList.get(position).getText());

            // 알림 설정 여부 값 가져오기
            SharedPreferences auto = context.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 알림 설정 데이터가 저장되어있는 곳
            String noti = auto.getString("Noti",null);

            // 알림 설정 값에 따라 초기화
            if(noti.equals("true")) { // 알림 설정 ON인 경우
                ((AlarmViewHolder)viewHolder).noti.setChecked(true);
            } else { // 알림 설정 OFF인 경우
                ((AlarmViewHolder)viewHolder).noti.setChecked(false);
            }

            // 알림 설정 스위치 값 변경시
            ((AlarmViewHolder) viewHolder).noti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) { // 알림 설정 ON인 경우
                        SharedPreferences auto = context.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 알림 설정 데이터가 저장되어있는 곳
                        SharedPreferences.Editor editor = auto.edit();
                        editor.putString("Noti", "true").apply(); // 알림 설정 값 변경
                        Toast.makeText(buttonView.getContext(),"알림 ON", Toast.LENGTH_SHORT).show();
                    }
                    else { // 알림 설정 OFF인 경우
                        SharedPreferences auto = context.getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 알림 설정 데이터가 저장되어있는 곳
                        SharedPreferences.Editor editor = auto.edit();
                        editor.putString("Noti", "false").apply(); // 알림 설정 값 변경
                        Toast.makeText(buttonView.getContext(),"알림 OFF", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // 리사이클러뷰 안의 전체 데이터 개수 리턴
    @Override
    public int getItemCount() {
        return mypageList.size();
    }

    // 리사이클러뷰의 아이템의 뷰 타입 리턴
    @Override
    public int getItemViewType(int position) {
        return mypageList.get(position).getType();
    }

    // 일반 리스트 뷰 홀더
    public class ListViewHolder extends RecyclerView.ViewHolder{
        TextView text;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.mypage_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        if(listener !=null){
                            listener.onItemClick(v,position);
                        }
                    }
                }
            });
        }
    }

    // 알림 설정 뷰 홀더
    public class AlarmViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        Switch noti;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.mypage_switch);
            noti = itemView.findViewById(R.id.mypage_notification);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        if(listener !=null){
                            listener.onItemClick(v,position);
                        }
                    }
                }
            });
        }
    }
}
