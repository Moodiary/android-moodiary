package ac.kr.duksung.moodiary;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

// 화면 설명 : 메인화면의 마이페이지 화면
// Author : Soohyun, Last Modified : 2021.01.21
// Author : Seungyeon, Last Modified : 2021.01.26(리스트 클릭시 액티비티 연결)
public class MypageFragment extends Fragment {
    String[] mypage_list = {"알림 설정", "비밀번호 변경", "로그아웃", "회원 탈퇴"};
    ListView lv_mypage;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        lv_mypage = view.findViewById(R.id.lv_mypage);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mypage_list);
        lv_mypage.setAdapter(adapter);

        lv_mypage.setOnItemClickListener(new AdapterView.OnItemClickListener() {    //리스트에 액티비티 연결
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0 :
                        Intent Noti = new Intent(getActivity(),NotificationActivity.class);
                        startActivity(Noti);
                        break;
                    case 1 :
                        Intent Change = new Intent(getActivity(),ChangePwActivity.class);
                        startActivity(Change);
                        break;
                    default:
                        break;
                }
            }
        });

        return view;
    }
}