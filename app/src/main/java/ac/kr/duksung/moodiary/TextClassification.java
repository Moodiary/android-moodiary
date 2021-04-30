package ac.kr.duksung.moodiary;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import scala.collection.Seq;

// 화면 설명 : 데이터 전처리 클래스
// Author : Soohyun, Last Modified : 2021.04.01
public final class TextClassification {
    private Context context;
    private String filename = "word_dict_sr.json";
    private int maxlen = 10;
    private HashMap vocabData;

    // 생성자
    public TextClassification(Context context) {
        this.context = context;
    }

    // 입력받은 텍스트를 토큰화하는 메소드
    public List<String> tokenize(String text) {

        CharSequence normalized = TwitterKoreanProcessorJava.normalize(text); // Normalize

        Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized); // Tokenize
        List<String> tokenizeText = TwitterKoreanProcessorJava.tokensToJavaStringList(tokens); // 토큰화된 텍스트
        System.out.println("Tokenize: " + tokenizeText);

        return tokenizeText;
    }

    // 정수화된 텍스트를 패딩하는 메소드
    public String[][] padSequence(List<String> tokenizeText) {
        String paddingText[][] = new String[1][maxlen]; // 패딩된 텍스트

        if (tokenizeText.size() >= maxlen) { // maxlen 보다 같거나 긴 경우
            for(int i=0; i<maxlen; i++) {
                paddingText[0][i] = tokenizeText.get(i);
            }
        } else if (tokenizeText.size() < maxlen) { // maxlen보다 작은 경우
            for(int i=0; i<maxlen; i++) { // maxlen 크기에 맞춰 나머지는 0으로 채우기
                if(i >= tokenizeText.size()) {
                    paddingText[0][i] = "";
                } else {
                    paddingText[0][i] = tokenizeText.get(i);
                }
            }
        }

        System.out.print("padding: [");
        for(int i=0; i<maxlen; i++) {
            System.out.print(paddingText[0][i] + ", ");
        }
        System.out.print("]\n");

        return paddingText;
    }

    // 토큰화된 텍스트를 정수화하는 메소드
    public float[][] jsonParsing(String[][] paddingText) {
        String json = ""; // json 파일의 전체 내용
        //List<Float> dicText = new ArrayList<>(); // 정수화된 텍스트
        float[][] dicText = new float[1][maxlen];

        try {
            AssetManager assetManager = context.getAssets(); // assets폴더의 파일을 가져오기 위해 창고관리자 얻어오기
            InputStream is = assetManager.open(filename); // json 파일을 읽는 입력스트림
            int fileSize = is.available(); // json 파일 크기

            byte[] buffer = new byte[fileSize]; // 바이트 단위로 출력하기 위한 스트림
            is.read(buffer); // 파일 읽기
            is.close(); // 파일 닫기

            json = new String(buffer, "UTF-8"); // UTF-8으로 인코딩하여 String으로 json 파일 전체 내용 저장

            // 토큰화된 텍스트를 정수화하는 부분
            try {
                JSONObject jsonObject = new JSONObject(json); // json 내용을 jsonObject로 가져옴
                // 토큰화된 텍스트를 정수로 변환
                for(int i=0; i<maxlen; i++) {
                    if(jsonObject.has(paddingText[0][i])) { // 키(토큰화된 텍스트)값이 있는지 확인 - 있으면 true, 없으면 false 반환
                        Float dic = (float)jsonObject.getDouble(paddingText[0][i]); // 변환된 정수값 가져오기
                        dicText[0][i] = dic; // 정수화된 텍스트를 추가
                    } else {
                        dicText[0][i] = 0; // 단어사전에 해당하는 값이 없으면 0으로 할당
                    }
                }
            } catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception", e);
            }

            System.out.print("word_dict: [");
            for(int i=0; i<maxlen; i++) {
                System.out.print(dicText[0][i] + ", ");
            }
            System.out.print("]\n");

            return dicText;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
