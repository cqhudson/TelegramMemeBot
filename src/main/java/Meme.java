import com.google.gson.Gson;

import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Meme {

    private final String MEME_API_URI = "https://meme-api.com/gimme";
    MemeJson memeJson = new MemeJson();

    public void getRandomMemeJson() {

        System.out.println("Fetching meme JSON data from " + MEME_API_URI);

        getJsonData(MEME_API_URI);
    }

    public void getRandomMemeJsonFromSubreddit(String subreddit) {

        final String endpoint = MEME_API_URI + "/" + subreddit;

        System.out.println("Fetching meme JSON from " + endpoint);

        getJsonData(endpoint);
    }

    private void getJsonData(String endpoint) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            // Create the GET request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .GET()
                    .build();
            try {
                // Send the GET request
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                // Parse the Json into a MemeJson object
                memeJson = new Gson().fromJson(response.body(), MemeJson.class);
                System.out.println("Successfully fetched random meme JSON data.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (UncheckedIOException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public String getPostLink() { return memeJson.getPostLink(); }

    public String getImageUri () { return memeJson.getUrl(); }

    public Boolean isNsfw () { return memeJson.getNsfw(); }

}
