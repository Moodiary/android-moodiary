package ac.kr.duksung.moodiary;

public class ChatItem {
    private int type; // 뷰 타입 (챗봇 - 0, 사용자 - 1, 버튼 - 2)
    private String content; // 내용

    public ChatItem(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
