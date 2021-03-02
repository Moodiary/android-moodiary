package ac.kr.duksung.moodiary.domain;

public class MypageItem {
    private int type; // 뷰 타입 ( 일반 리스트 - 0, 알림 설정 - 1)
    private String text; // 마이페이지 아이템 텍스트

    public MypageItem(int type, String text) {
        this.type = type;
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}
