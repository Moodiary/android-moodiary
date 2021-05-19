package ac.kr.duksung.moodiary.domain;

public class ChatItem {
    private int type; // 뷰 타입 (디어 - 0, 사용자 - 1, 조명 - 2, 타이머 설정 - 3, 실시간 타이머 - 4, 일기/조명 선택 - 5)
    private String content; // 채팅 내용
    private long time; // 남은 시간

    // 디어, 사용자 채팅 생성자
    public ChatItem(int type, String content) {
        this.type = type;
        this.content = content;
    }

    // 조명 , 타이머, 일기/조명 선택 설정 생성자
    public ChatItem(int type) {
        this.type = type;
        this.content = null;
    }

    // 실시간 타이머 생성자
    public ChatItem(int type, long time) {
        this.type = type;
        this.content = null;
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public long getTime() {
        return time;
    }
}
