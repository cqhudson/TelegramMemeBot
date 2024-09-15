import com.google.gson.Gson;

import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Meme {

    private final String MEME_API_URI = "https://meme-api.com/gimme";
    MemeJson memeJson = new MemeJson();

    public void getJson() {

        System.out.println("Fetching meme JSON data from " + MEME_API_URI);

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MEME_API_URI))
                    .GET()
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                memeJson = new Gson().fromJson(response.body(), MemeJson.class);
                System.out.println("Successfully fetched meme JSON date.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
         catch (UncheckedIOException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

}
