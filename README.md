# Telegram Meme Bot

This is a simple Telegram bot that finds memes based on an API, and then sends the memes to the user that requested them as a Telegram message.

To use the bot, open the following link [t.me/connors_meme_bot](https://t.me/connors_meme_bot)

Here is a list of current supported commands by the bot:

- `/start` - show welcome command and usage
- `/meme` - fetch a random meme from Reddit (chosen from either *r/memes*, *r/dankmemes*, or *r/me_irl*)
- `/source` - fetch a random meme from a specific subreddit (Currently supported subreddits are *r/memes*, *r/dankmemes*, *r/wholesomememes*, and *r/nsfwmemes*. This is subject to change in the future)

## How does it work?

The bot uses the [Telegram Bot API](https://core.telegram.org/bots/api) to register the bot with Telegram's servers. Then, the bot constantly polls the servers with a request to get any updates since the last poll.
In this project, I'm specifically using the [TelegramBots](https://github.com/rubenlagus/TelegramBots) Java library to interact with the Bot API. 

Once the bot receives an update, we check the contents of the update (whether or not it was a command sent by text, or a callbackQuery sent via button click). We then
take action based on the content of the update. 

In our case, the action being taken is sending a GET request to the [meme-api.com](https://github.com/D3vd/Meme_Api) API, which scrapes various subreddits on Reddit for memes, and returns the JSON data of the meme.

We parse the JSON data and return it to the users in a nicely formatted message. 

![image](https://github.com/user-attachments/assets/54b792dd-dc83-4b6c-9c15-eae293849a18)

We can even make decisions based on the JSON data, such as whether or not to blur an image if it is marked at Not Safe for Work (NSFW).

![image](https://github.com/user-attachments/assets/f6cfc0ca-8da6-47e8-ac0f-a06965be83e7)
