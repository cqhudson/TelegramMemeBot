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

        // First we check for callback data, which means a user pressed an Inline button
        if (update.hasCallbackQuery() && update.getCallbackQuery().getData() != null) {
            Long id = update.getCallbackQuery().getFrom().getId();

            Meme meme = new Meme();

            if (update.getCallbackQuery().getData().equals("r/memes")) {
                // GET meme JSON from r/memes
                meme.getRandomMemeJsonFromSubreddit("memes");
                sendMeme(id, meme);
            }
            else if (update.getCallbackQuery().getData().equals("r/dankmemes")) {
                // GET meme JSON from r/memes
                meme.getRandomMemeJsonFromSubreddit("dankmemes");
                sendMeme(id, meme);
            }
            else if (update.getCallbackQuery().getData().equals("r/wholesomememes")) {
                // GET meme JSON from r/memes
                meme.getRandomMemeJsonFromSubreddit("wholesomememes");
                sendMeme(id, meme);
            }
            else if (update.getCallbackQuery().getData().equals("r/nsfwmemes")) {
                // GET meme JSON from r/memes
                meme.getRandomMemeJsonFromSubreddit("nsfwmemes");
                sendMeme(id, meme);
            }
            else if (update.getCallbackQuery().getData().equals("r/4chan")) {
                // GET meme JSON from r/4chan
                meme.getRandomMemeJsonFromSubreddit("4chan");
                sendMeme(id, meme);
            }
            else if (update.getCallbackQuery().getData().equals("r/greentext")) {
                // GET meme JSON from r/greentext
                meme.getRandomMemeJsonFromSubreddit("greentext");
                sendMeme(id, meme);
            }

        }

        // If user did not press Inline button, check for a command
        else if (update.getMessage().isCommand()) {

            Message message = update.getMessage();
            User user = message.getFrom();
            Long id = user.getId();

            Meme meme = new Meme();

            if (message.getText().equals("/start")) {
                sendText(id,"""
                        Welcome to Connor's Meme Bot. Here are some of the commands you can use:
                        /start - show this message
                        /meme - fetch a random meme from Reddit
                        /source - fetch a random meme from a specific subreddit
                        """);
            }

            else if (message.getText().equals("/meme")) {

                // GET meme JSON
                meme.getRandomMemeJson();

                // Log the meme url to the console for now (debugging purposes)
                System.out.println(meme.getImageUri());

                sendMeme(id, meme);
            }

            else if (message.getText().equals("/source")) {

                // Create Inline Buttons to display to the user

                // ROW 1
                InlineKeyboardButton buttonMemes = InlineKeyboardButton.builder()
                        .text("r/Memes")
                        .callbackData("r/memes")
                        .build();
                InlineKeyboardButton buttonDankMemes = InlineKeyboardButton.builder()
                        .text("r/DankMemes")
                        .callbackData("r/dankmemes")
                        .build();
                InlineKeyboardButton buttonWholesomeMemes = InlineKeyboardButton.builder()
                        .text("r/WholesomeMemes")
                        .callbackData("r/wholesomememes")
                        .build();

                // ROW 2
                InlineKeyboardButton buttonNsfwMemes = InlineKeyboardButton.builder()
                        .text("r/NsfwMemes")
                        .callbackData("r/nsfwmemes")
                        .build();
                InlineKeyboardButton button4chanMemes = InlineKeyboardButton.builder()
                        .text("r/4chan")
                        .callbackData("r/4chan")
                        .build();

                // ROW 3
                InlineKeyboardButton buttonGreentextMemes = InlineKeyboardButton.builder()
                        .text("r/greentext")
                        .callbackData("r/greentext")
                        .build();

                // Create Lists of buttons to represent rows
                List<InlineKeyboardButton> row1Buttons = List.of(buttonMemes, buttonDankMemes, buttonWholesomeMemes);
                List<InlineKeyboardButton> row2Buttons = List.of(buttonNsfwMemes, button4chanMemes);
                List<InlineKeyboardButton> row3Buttons = List.of(buttonGreentextMemes);

                // Create the Inline Keyboard
                InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                        .keyboardRow(row1Buttons)
                        .keyboardRow(row2Buttons)
                        .keyboardRow(row3Buttons)
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

    // Handles whether we are sending a static image, or an animated gif.
    public void sendMeme(Long id, Meme meme) {

        if (meme.getImageUri().contains(".gif")){
            sendMemeAnimation(id, meme);
        }
        else if (meme.getImageUri().contains(".png") || meme.getImageUri().contains(".jpg")){
            sendMemePhoto(id, meme);
        }
    }

    // This method sends a meme using the Telegram SendPhoto class to build a message with a Photo
    public void sendMemePhoto(Long id, Meme meme) {

        // Store the image as an InputFile to pass into the SendPhoto builder
        InputFile image = new InputFile();
        image.setMedia(meme.getImageUri());

        // Build caption based on NSFW status
        String isNSFW = "Warning, this meme is considered Not Safe For Work (NSFW)\nSource: " + meme.getPostLink();
        String isSFW = "Source: " + meme.getPostLink();

        // If the meme is NSFW, set the caption accordingly.
        String caption = meme.isNsfw() ? isNSFW : isSFW;

        // Create a SendPhoto object to build the message with a photo to send
        SendPhoto sp = SendPhoto.builder()
                .chatId(id.toString())
                .photo(image)
                .caption(caption)
                .hasSpoiler(meme.isNsfw())
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

        // Build caption based on NSFW status
        String isNSFW = "Warning, this meme is considered Not Safe For Work (NSFW)\nSource: " + meme.getPostLink();
        String isSFW = "Source: " + meme.getPostLink();

        // If the meme is NSFW, set the caption accordingly.
        String caption = meme.isNsfw() ? isNSFW : isSFW;

        // Build the SendAnimation object to build the message with an animation (animation) to send
        SendAnimation sa = SendAnimation.builder()
                .chatId(id.toString())
                .animation(animation)
                .caption(caption)
                .hasSpoiler(meme.isNsfw())
                .build();

        // Attempt to send the message
        try {
            execute(sa);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
