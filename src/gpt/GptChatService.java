package gpt;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionAssistantMessageParam;

import java.util.ArrayList;
import java.util.List;

/**
 * GptChatService：單例服務，用於呼叫 OpenAI Chat Completion API，
 * 並且保留整個對話歷史（history），讓每次呼叫 ask(...) 時，都能把前面所有訊息帶上去。
 *
 * 此版本可以在一開始加入一條「系統提示（system prompt）」，
 * 之後 user 與 assistant 的訊息都會接續在這條系統提示之後。
 *
 * 需要先在環境變數裡設定 OPENAI_API_KEY，或是直接把 apiKey 硬寫/傳入 getInstance(...)。
 */
public class GptChatService {
    private static GptChatService instance;
    private final OpenAIClient client;
    // 用來保留對話歷史：第一筆可以是 system prompt，後面才是 user/assistant
    private final List<HistoryItem> history;

    // 儲存第一次加入的系統提示內容
    private final String systemPrompt;

    /**
     * 私有建構子，只能透過 getInstance(...) 取得單例。
     *
     * @param apiKey       若想硬寫 API Key，可把它傳進來；若要用環境變數，則不用傳。
     * @param systemPrompt 第一條系統提示，如果為 null 或空白，則不插入任何 system 訊息。
     */
    private GptChatService(String apiKey, String systemPrompt) {
        // 用 OPENAI_API_KEY 環境變數建立 client：
        this.client = OpenAIOkHttpClient.fromEnv();
        // 若要直接用程式碼傳入 apiKey（不想靠環境變數），可以用下方這行取代：
        // this.client = OpenAIOkHttpClient.builder().apiKey(apiKey).build();

        this.systemPrompt = systemPrompt != null ? systemPrompt.trim() : null;
        this.history = new ArrayList<>();

        // 如果有指定 systemPrompt，就把它先加入 history
        if (this.systemPrompt != null && !this.systemPrompt.isEmpty()) {
            this.history.add(new HistoryItem("system", this.systemPrompt));
        }
    }

    /**
     * 取得單例。第一次呼叫時，可以傳 systemPrompt；之後再呼叫會忽略後續的 systemPrompt 參數。
     *
     * 範例：
     *   GptChatService svc = GptChatService.getInstance(null, "你是一個友善的助理。");
     *   svc.ask("你好！");
     *
     * @param apiKey       若想硬寫 API Key，可把它傳進來；否則傳 null，讓 fromEnv() 自動取環境變數。
     * @param systemPrompt 第一條系統提示；只會在 instance 尚未建立時生效，之後不再更改。
     */
    public static GptChatService getInstance(String apiKey, String systemPrompt) {
        if (instance == null) {
            instance = new GptChatService(apiKey, systemPrompt);
        }
        return instance;
    }

    /**
     * 如果你不需要特別的 system prompt，就用這個簡化版：
     * GptChatService.getInstance(apiKey);
     */
    public static GptChatService getInstance(String apiKey) {
        // 第二個參數 systemPrompt 給 null 表示不插入任何系統提示
        return getInstance(apiKey, null);
    }

    /**
     * 傳一次 userInput 給 OpenAI Chat Completion，並保留上下文(history)。
     *
     * @param userInput 使用者這一輪想問的文字
     * @return GPT 的回覆字串
     */
    public String ask(String userInput) {
        // 1. 把 user 這輪的訊息先加入 history
        history.add(new HistoryItem("user", userInput));

        // 2. 準備 ChatCompletionCreateParams.Builder
        ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4O);
        // 如果帳號有 GPT-4 權限，可改成 .model(ChatModel.GPT_4)
        // 或 GPT-4O 系列：.model(ChatModel.GPT_4_O) / .model(ChatModel.GPT_4O_MINI)

        // 3. 把 history 裡所有訊息一次丟到 builder
        //    history 裡可能包含一條 system prompt (role="system")、多條 user/assistant
        for (HistoryItem item : history) {
            String role = item.getRole();
            String content = item.getContent();

            switch (role) {
                case "system":
                    builder.addSystemMessage(content);
                    break;
                case "user":
                    builder.addUserMessage(content);
                    break;
                case "assistant":
                    // 「assistant」的訊息需要用 ChatCompletionAssistantMessageParam
                    builder.addMessage(
                            ChatCompletionAssistantMessageParam.builder()
                                    .content(content)
                                    .build()
                    );
                    break;
                default:
                    // 如果將來要支援 function 或其他 role，可在這裡擴充
                    builder.addUserMessage(content);
                    break;
            }
        }

        // 4. 組參數並呼叫 API
        ChatCompletionCreateParams params = builder.build();
        ChatCompletion response = client.chat().completions().create(params);

        // 5. 取回第一組 choice 的回覆文字 (Optional<String> -> String)
        String reply = response
                .choices()
                .get(0)
                .message()
                .content()
                .orElse("");

        // 6. 把 assistant 的回覆也加入 history
        history.add(new HistoryItem("assistant", reply));

        return reply;
    }
}
