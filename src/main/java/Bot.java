import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

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

        // The Message object we pull from update contains data about the message sent to us
        Message message = update.getMessage();
        User user = message.getFrom();
        String username = user.getUserName();
        Long id = user.getId();

        // Log each message sent to us to the console for now (debugging purposes)
        System.out.println(username + ": " + message.getText());

        if (message.isCommand()) {

            Meme meme = new Meme();

            if (message.getText().equals("/meme")) {

                // GET meme JSON
                meme.getRandomMemeJson();

                // Log the meme url to the console for now (debugging purposes)
                System.out.println(meme.getImageUri());

                if (meme.getImageUri().contains(".gif")){
                    sendMemeAnimation(id, meme);
                }
                else if (meme.getImageUri().contains(".png") || meme.getImageUri().contains(".jpg")){
                    sendMemePhoto(id, meme);
                }
            }

            else if (message.getText().equals("/source")) {

                // Create Inline Buttons to display to the user
                InlineKeyboardButton buttonMemes = InlineKeyboardButton.builder()
                        .text("r/Memes")
                        .callbackData("r/memes")
                        .build();
                InlineKeyboardButton buttonDankMemes = InlineKeyboardButton.builder()
                        .text("r/DankMemes")
                        .callbackData("r/dankmemes")
                        .build();
                InlineKeyboardButton buttonNsfwMemes = InlineKeyboardButton.builder()
                        .text("r/NSFWMemes")
                        .callbackData("r/nsfwmemes")
                        .build();

                // Create a List of buttons
                List<InlineKeyboardButton> buttons = List.of(buttonMemes, buttonDankMemes, buttonNsfwMemes);


                // Create the Inline Keyboard
                InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                        .keyboardRow(buttons)
                        .build();

                // Send the keyboard to the user
                SendMessage sm = SendMessage.builder()
                        .chatId(id.toString())
                        .text("Select a subreddit to fetch a meme from")
                        .parseMode("HTML")
                        .replyMarkup(keyboard)
                        .build();
                try { execute(sm); } catch (TelegramApiException e) { throw new RuntimeException(e); }

            }
        }
    }

    // This method sends text as a Telegram Message, no images or files.
    public void sendText(Long id, String text) {

        // Create a SendMessage object to build the message to send
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

    // This method sends a meme using the Telegram SendPhoto class to build a message with a Photo
    public void sendMemePhoto(Long id, Meme meme) {

        // Store the image as an InputFile to pass into the SendPhoto builder
        InputFile image = new InputFile();
        image.setMedia(meme.getImageUri());

        // Create a SendPhoto object to build the message with a photo to send
        SendPhoto sp = SendPhoto.builder()
                .chatId(id.toString())
                .photo(image)
                .protectContent(meme.isNsfw()) // if meme is marked NSFW, blur it
                .caption("Source: " + meme.getPostLink())
                .build();

        // Attempt to send the message
        try {
            execute(sp);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    // This method sends a meme in using the Telegram SendAnimation class to build a message with an Animation
    public void sendMemeAnimation(Long id, Meme meme) {

        // Store the animation as an InputFile to pass into the SendAnimation builder
        InputFile animation = new InputFile();
        animation.setMedia(meme.getImageUri());

        // Build the SendAnimation object to build the message with an animation (animation) to send
        SendAnimation sa = SendAnimation.builder()
                .chatId(id.toString())
                .animation(animation)
                .protectContent(meme.isNsfw()) // If the meme is marked NSFW, blur it
                .caption("Source: " + meme.getPostLink())
                .build();

        // Attempt to send the message
        try {
            execute(sa);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
