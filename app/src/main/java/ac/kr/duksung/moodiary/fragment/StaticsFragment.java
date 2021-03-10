package ac.kr.duksung.moodiary.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ac.kr.duksung.moodiary.R;

// 화면 설명 : 메인화면의 통계화면
// Author : Soohyun, Last Modified : 2021.01.20
public class StaticsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statics, container, false);
    }
}