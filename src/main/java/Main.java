import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Calendar;

public class Main {
    public static void main (String[] args) throws TelegramApiException {

        Dotenv dotenv = Dotenv.load();

        Long myTelegramAccountId = Long.parseLong(dotenv.get("TELEGRAM_MY_ID"));

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        botsApi.registerBot(bot);

        // Notify me of my bot start time
        bot.sendText(
                myTelegramAccountId,
                "The current session was started on " + getBotStartTime()
        );



    }

    public static String getBotStartTime() {
        return Calendar.getInstance().getTime().toString();
    }
}
