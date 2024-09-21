import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;

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

        // TODO: put database stuff here
            storeInDatabase(message);
        //

        // Log each message sent to us to the console for now (debugging purposes)
        System.out.println(username + ": " + message.getText());

        if (message.isCommand()) {
            if (message.getText().equals("/meme")) {

                // Initialize meme and GET the JSON
                Meme meme = new Meme();
                meme.getJson();

                // Log the meme url to the console for now (debugging purposes)
                System.out.println(meme.getImageUri());

                if (meme.getImageUri().contains(".gif")){
                    sendMemeAnimation(id, meme);
                }
                else if (meme.getImageUri().contains(".png") || meme.getImageUri().contains(".jpg")){
                    sendMemePhoto(id, meme);
                }
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

    private void storeInDatabase(Message message) {

        final String databasePath = ("jdbc:sqlite:" + dotenv.get("TELEGRAM_BOT_DATABASE_PATH"));
        Connection connection = null;

        // STEP 1 //
        try {
            // Create a new database file if it does not exist, otherwise connect to a pre-existing database.
            connection = DriverManager.getConnection(databasePath);

        } catch (SQLException e) {
            System.out.println("STEP 1: " + e.getMessage());
            return;
        }

        // STEP 2 //
        try {
            // Create the "users" table if it does not exist.
            Statement statement = connection.createStatement();

            // The backslash gets rid of the newline character in multi-line text blocks.
            // https://stackoverflow.com/questions/59310303/java-13-triple-quote-text-block-without-newlines
            final String sqlStatement = """
                    CREATE TABLE IF NOT EXISTS users ( \
                    telegram_user_id BIGINT PRIMARY KEY NOT NULL, \
                    telegram_user_username TEXT UNIQUE NOT NULL, \
                    telegram_user_first_name TEXT, \
                    telegram_user_last_name TEXT, \
                    telegram_user_message_count INTEGER NOT NULL DEFAULT 1 \
                    );""".trim();

            statement.execute(sqlStatement);
            statement.close();

        } catch (SQLException e) {
            System.out.println("STEP 2: " + e.getMessage());
            return;
        }

        // STEP 3 //
        try {
            // Add a record to the database.
            Statement statement = connection.createStatement();

            // Get the data to store in record
            final String telegram_user_id = message.getFrom().getId().toString();
            final String telegram_user_username = message.getFrom().getUserName();
            final String telegram_user_first_name = message.getFrom().getFirstName();
            final String telegram_user_last_name = message.getFrom().getLastName();

            /*
            // Query to check for existing record, assign it an alias 'exists'
            final String sqlQueryRecordExists = String.format("""
                    
                    SELECT COUNT(*) > 0 AS exists FROM users WHERE telegram_user_id = %s; \
                    """, telegram_user_id).trim();
            ResultSet resultSet = statement.executeQuery(sqlQueryRecordExists);
            */

            final String sqlQueryRecordExists = "SELECT COUNT(*) > 0 FROM users WHERE telegram_user_id = ?";
            ResultSet resultSet = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQueryRecordExists)) {
                preparedStatement.setString(1, telegram_user_id);
                resultSet = preparedStatement.executeQuery();
            } catch (SQLException e) {
                System.out.println("STEP 3, inner try block: " + e.getMessage());
                return;
            }

            // Check if a record already exists
            if (resultSet.next()) {
                int exists = resultSet.getInt("exists");

                if (exists == 1) {
                    // Record already exists, update the existing record
                    final String sqlStatement = String.format("""
                            UPDATE users \
                            SET telegram_user_username = '%s', \
                            telegram_user_first_name = '%s', \
                            telegram_user_last_name = '%s', \
                            telegram_user_message_count = telegram_user_message_count + 1 \
                            WHERE telegram_user_id = %s; \
                            """, telegram_user_username, telegram_user_first_name, telegram_user_last_name, telegram_user_id).trim();

                    statement.execute(sqlStatement);

                } else {
                    // Record does not exist, create a new one
                    final String sqlStatement = String.format("""
                            INSERT INTO users (telegram_user_id, telegram_user_username, telegram_user_first_name, telegram_user_last_name) \
                            VALUES (%s, '%s', '%s', '%s'); \
                            """, telegram_user_id, telegram_user_username, telegram_user_first_name, telegram_user_last_name).trim();

                    statement.execute(sqlStatement);
                }

            }

            statement.close();

        } catch (SQLException e) {
            System.out.println("STEP 3: " + e.getMessage());
            return;
        }

    }

}
