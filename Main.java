import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.Proxy;
import java.net.InetSocketAddress;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class Main {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "修改为你的API";
    private static final String MODEL = "gpt-3.5-turbo-16k";

    public static void main(String[] args) {
        try {
            String prompt = "What is the meaning of life?";
            ChatGPTResponse response = sendChatGPTRequest(prompt);
            String content = response.getChoices().get(0).getMessage().getContent();
            System.out.println("Assistant Content: " + content);
        } catch (IOException e) {
            System.err.println("Error during API request: " + e.getMessage());
        }
    }

    private static ChatGPTResponse sendChatGPTRequest(String prompt) throws IOException {
        URL url = new URL(API_ENDPOINT);

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);

        String postData = "{\"model\":\"" + MODEL + "\",\"messages\":[{\"role\":\"user\",\"content\":\"" + prompt + "\"}], \"temperature\": 0.7}";

        connection.setDoOutput(true);
        connection.getOutputStream().write(postData.getBytes("UTF-8"));

        StringBuilder response = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response.toString(), ChatGPTResponse.class);
    }


    public static class ChatGPTResponse {
        private List<Choice> choices;

        public List<Choice> getChoices() {
            return choices;
        }

        public void setChoices(List<Choice> choices) {
            this.choices = choices;
        }

        public static class Choice {
            private Message message;

            public Message getMessage() {
                return message;
            }

            public void setMessage(Message message) {
                this.message = message;
            }
        }

        public static class Message {
            private String role;
            private String content;

            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}
