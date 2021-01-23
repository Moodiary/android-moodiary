package ac.kr.duksung.moodiary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

// 화면 설명 : 메인화면의 마이페이지 화면
// Author : Soohyun, Last Modified : 2021.01.21
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

        return view;
    }
}