package ac.kr.duksung.moodiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

// 화면 설명 : 메인화면의 마이페이지 화면
// Author : Soohyun, Last Modified : 2021.02.08
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
                    case 0 : // 알림 설정
                        Intent Noti = new Intent(getActivity(),NotificationActivity.class);
                        startActivity(Noti); // 알림 설정 화면으로 이동
                        break;
                    case 1 : // 비밀번호 변경
                        Intent Change = new Intent(getActivity(),ChangePwActivity.class);
                        startActivity(Change); // 비밀번호 변경 화면으로 이동                        \
                        break;
                    case 2: // 로그아웃
                        SharedPreferences auto = getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE); // 자동로그인 데이터가 저장되어있는 곳
                        SharedPreferences.Editor editor = auto.edit();
                        editor.clear(); // autoLogin에 저장되어 있는 정보 삭제
                        editor.commit();

                        Intent Login = new Intent(getActivity(),LoginActivity.class);
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
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, "http://192.168.99.84:3000/user/deleteuser", requestJsonObject, new Response.Listener<JSONObject>() {

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