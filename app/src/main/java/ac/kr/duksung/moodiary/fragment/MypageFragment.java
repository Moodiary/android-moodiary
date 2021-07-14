package ac.kr.duksung.moodiary.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ac.kr.duksung.moodiary.R;
import ac.kr.duksung.moodiary.activity.ChangePwActivity;
import ac.kr.duksung.moodiary.activity.LoginActivity;
import ac.kr.duksung.moodiary.adapter.MypageAdapter;
import ac.kr.duksung.moodiary.domain.MypageItem;

// 화면 설명 : 메인화면의 마이페이지 화면
// Author : Soohyun, Last Modified : 2021.03.10
// Author : Seungyeon, Last Modified : 2021.01.26(리스트 클릭시 액티비티 연결)
public class MypageFragment extends Fragment {
    TextView tv_user_name; // 사용자 이름
    TextView tv_user_id; // 사용자 아이디
    TextView tv_user_email; // 사용자 이메일
    ArrayList<MypageItem> mypageList = new ArrayList<>(); // 마이페이지 메세지 리스트
    RecyclerView rv_mypage; // 마이페이지 리사이클러뷰
    MypageAdapter adapter; // 마이페이지 어댑터

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        tv_user_name = view.findViewById(R.id.tv_user_name);
        tv_user_id = view.findViewById(R.id.tv_user_id);
        tv_user_email = view.findViewById(R.id.tv_user_email);

        // 사용자 기본 정보 세팅
        SharedPreferences auto = getContext().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        tv_user_name.setText(auto.getString("Name",null) + "님");
        tv_user_id.setText(auto.getString("ID",null));
        tv_user_email.setText(auto.getString("Email",null));

        // 데이터 초기화
        mypageList.add(new MypageItem(1, "알림 설정"));
        mypageList.add(new MypageItem(0, "비밀번호 변경"));
        mypageList.add(new MypageItem(0, "로그아웃"));
        mypageList.add(new MypageItem(0, "회원 탈퇴"));

        rv_mypage = view.findViewById(R.id.rv_mypage);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false); // 레이아웃 매니저
        adapter = new MypageAdapter(getContext(), mypageList);
        rv_mypage.setLayoutManager(manager); // 리사이클러뷰와 레이아웃 매니저 연결
        rv_mypage.setAdapter(adapter); // 리사이클러뷰와 어댑터 연결

        // 리사이클러뷰 아이템 클릭시
        adapter.setOnItemClickListener(new MypageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (position) {
                    case 0: // 알림 설정
                        break;
                    case 1: // 비밀번호 변경
                        Intent Change = new Intent(getActivity(), ChangePwActivity.class);
                        startActivity(Change); // 비밀번호 변경 화면으로 이동
                        break;
                    case 2: // 로그아웃
                        SharedPreferences auto = getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 자동로그인 데이터가 저장되어있는 곳
                        SharedPreferences.Editor editor = auto.edit();
                        editor.clear(); // autoLogin에 저장되어 있는 정보 삭제
                        editor.commit();

                        Intent Login = new Intent(getActivity(), LoginActivity.class);
                        startActivity(Login); // 로그인 화면으로 이동
                        getActivity().finish();
                        break;
                    case 3: // 회원탈퇴
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("회원탈퇴");
                        builder.setMessage("정말 탈퇴하시겠습니까?");
                        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences auto = getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 자동로그인 데이터 저장되어있는 곳
                                String id = auto.getString("ID",null);

                                requestDeleteuser(id); // 회원탈퇴 메소드 실행
                            }
                        });
                        builder.setNegativeButton("아니오", null);
                        builder.show();
                        break;
                    default:
                        break;
                }
            }
        });

        return view;
    }

    // 회원탈퇴 메소드
    public void requestDeleteuser(String id) {

        // 사용자 입력 정보 JSON 형태로 변환
        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("user_id", id);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // 서버에 데이터 전달

        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://172.30.1.18:3000/user/deleteuser", requestJsonObject, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) { // 데이터 전달 후 받은 응답

                try {
                    String result = response.getString("code"); // 응답 메시지 가져오기

                    // 응답 메시지에 따른 처리
                    if(result.equals("400"))
                        Toast.makeText(getContext(),"에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    if(result.equals("200")) {
                        Toast.makeText(getContext(), "회원탈퇴가 완료되었습니다", Toast.LENGTH_SHORT).show();
                        Intent Login = new Intent(getActivity(),LoginActivity.class);
                        startActivity(Login); // 로그인 화면으로 이동
                        getActivity().finish();
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