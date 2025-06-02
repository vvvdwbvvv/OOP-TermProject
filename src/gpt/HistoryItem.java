package gpt;

/**
 * 簡單的 POJO，用來記錄對話的單筆訊息：角色(role) 與 內容(content)
 */
public class HistoryItem {
    private final String role;     // "user" 或 "assistant"
    private final String content;  // 該輪的文字

    public HistoryItem(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}
