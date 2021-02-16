package ac.kr.duksung.moodiary;

public class ChatItem {
    private int type; // 뷰 타입 (챗봇 - 0, 사용자 - 1, 버튼 - 2)
    private String content; // 내용
    private String btn_text1; // 버튼 텍스트1
    private String btn_text2; // 버튼 텍스트2
    private String btn_text3; // 버튼 텍스트3
    private String btn_text4; // 버튼 텍스트4

    public ChatItem(int type, String text1, String text2, String text3, String text4) {
        this.type = type;
        this.content = null;
        this.btn_text1 = text1;
        this.btn_text2 = text2;
        this.btn_text3 = text3;
        this.btn_text4 = text4;
    }

    public ChatItem(int type, String content) {
        this.type = type;
        this.content = content;
        this.btn_text1 = null;
        this.btn_text2 = null;
        this.btn_text3 = null;
        this.btn_text4 = null;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getBtn_text1() {
        return btn_text1;
    }

    public String getBtn_text2() {
        return btn_text2;
    }

    public String getBtn_text3() {
        return btn_text3;
    }

    public String getBtn_text4() {
        return btn_text4;
    }
}
