import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    // Use this to grab our API tokens and other special variables from ".env" file
    Dotenv dotenv = Dotenv.load();

    // Required to communicate with the bot framework
    @Override
    public String getBotToken() { return dotenv.get("TELEGRAM_BOT_HTTP_API_TOKEN"); }

    // Required to communicate with the bot framework as well
    @Override
    public String getBotUsername() { return dotenv.get("TELEGRAM_BOT_USERNAME"); }

    // The method that handles all the updates
    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        User user = message.getFrom();
        String username = user.getUserName();
        Long id = user.getId();

        System.out.println(username + ": " + message.getText());


        if (message.isCommand()) {
            if (message.getText().equals("/meme")) {

                // Initialize meme and GET the JSON
                Meme meme = new Meme();
                meme.getJson();

                System.out.println(meme.getImageUri());
                sendMemeImageOnly(id, meme);
            }
        }



    }

    public void sendText(Long id, String text) {
        SendMessage sm = SendMessage.builder()
                .chatId(id.toString())
                .text(text)
                .build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMemeImageOnly (Long id, Meme meme) {
        InputFile memeFile = new InputFile();
        memeFile.setMedia(meme.getImageUri());
        SendPhoto sp = SendPhoto.builder()
                .chatId(id.toString())
                .photo(memeFile)
                .protectContent(meme.isNsfw()) // if meme is marked NSFW, blur it
                .caption("Source: " + meme.getPostLink())
                .build();
        try {
            execute(sp);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
