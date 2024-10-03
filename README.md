# Telegram Meme Bot - PoC

This is a simple Telegram bot that finds memes based on an API, and then sends the memes to the user that requested them as a Telegram message.

To try out the bot, open the following link [t.me/connors_meme_bot](https://t.me/connors_meme_bot) and send him some messages (you need a Telegram account to do so).

Here is a list of current supported commands by the bot:

- `/start` - show welcome command and usage

![image](https://github.com/user-attachments/assets/f156f313-76bb-43d0-be7b-f568d9f471b9)

- `/meme` - fetch a random meme from Reddit (chosen from either *r/memes*, *r/dankmemes*, or *r/me_irl*)

![image](https://github.com/user-attachments/assets/a5f31485-b569-4e3c-87d3-1f59bca05b69)

- `/source` - fetch a random meme from a specific subreddit (Currently supported subreddits are *r/memes*, *r/dankmemes*, *r/wholesomememes*, *r/nsfwmemes*, *r/4chan*, and *r/greentext*. This is subject to change in the future)

![image](https://github.com/user-attachments/assets/10ad12e0-c64c-468a-88c4-cdd3e75d5464)

## How does it work?

The bot uses the [Telegram Bot API](https://core.telegram.org/bots/api) to register the bot with Telegram's servers. Then, the bot constantly polls the servers with a request to get any updates since the last poll.
In this project, I'm specifically using the [TelegramBots](https://github.com/rubenlagus/TelegramBots) Java library to interact with the Bot API. 

Once the bot receives an update, we check the contents of the update (whether or not it was a command sent by text, or a callbackQuery sent via button click). We then
take action based on the content of the update. 

In our case, the action being taken is sending a GET request to the [meme-api.com](https://github.com/D3vd/Meme_Api) API, which scrapes various subreddits on Reddit for memes, and returns the JSON data of the meme.

We parse the JSON data and return it to the users in a nicely formatted message. 

![image](https://github.com/user-attachments/assets/54b792dd-dc83-4b6c-9c15-eae293849a18)

We can even make decisions based on the JSON data, such as whether or not to blur an image if it is marked at Not Safe for Work (NSFW).

![image](https://github.com/user-attachments/assets/d001c249-15d9-4f68-ac10-7798206884ab)

# Can I make my own version of this bot?

Absolutely! However, there are a few things you will want to do first.

- Create a Telegram account and get familiar with the UI and various Telegram features.
- Download any JDK version 22 or newer (version 22 is what this project was built with).
- IMPORTANT -> If you are unfamiliar with the Telegram API and how it works, go check out [this super helpful tutorial by Telegram which walks you through building out a Telegram Bot in Java](https://core.telegram.org/bots/tutorial).


If you want to host your own Telegram bot based on this code, there are a few steps you need to take.

1. Create a Telegram account
2. Create a bot using the @BotFather bot in Telegram (this is how you get the bot username and bot API token to interact with your bot)

![image](https://github.com/user-attachments/assets/3cc087f4-b382-473b-a1d9-5d10a6cb0fd0)
- Note: do not expose your API token to anybody. It allows anybody to control this bot from anywhere. This token in the picture will not work if you try to use it since I have revoked it. I'll be using it for this tutorial though.

4. Clone this repo
5. Create a `.env` file here: `src/main/resources/.env`
6. Put the following values in the `.env` file, inputting your own bot username, API token, and ID.
![image](https://github.com/user-attachments/assets/6a3793be-f5b8-4ffa-a99b-0a809908ddcb)
- If you don't know what to put in TELEGRAM_MY_ID, this is your Telegram user ID, if you follow the Bot tutorial by Telegram they will go into more detail on this. You don't need a valid number here, this is just to alert you every time your bot starts up. This should not affect the functionality of the bot.
- Here is an example of the notification: ![image](https://github.com/user-attachments/assets/23f2e4fd-f26c-4f62-8bd0-4b45b166574a)

7. Run the project. No need to set up any crazy servers, forward any ports, etc. The bot will just work. You can now message the bot and it should start sending you memes.


