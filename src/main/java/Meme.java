import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Meme {

    private final String MEME_API_URI = "https://meme-api.com/gimme";

    public String getMemeJson() {

        System.out.println("Fetching a meme from " + MEME_API_URI);

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MEME_API_URI))
                    .GET()
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
         catch (UncheckedIOException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

}
