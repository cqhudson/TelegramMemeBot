import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

}
