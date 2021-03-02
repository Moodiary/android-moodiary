package ac.kr.duksung.moodiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ac.kr.duksung.moodiary.domain.MypageItem;
import ac.kr.duksung.moodiary.R;

public class MypageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<MypageItem> mypageList = null;
    private OnItemClickListener listener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MypageAdapter(ArrayList<MypageItem> mypageList){
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

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.mypage_switch);

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
