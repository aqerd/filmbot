# Filmbot
#### This bot helps you find films you want to watch based on some filters like genre, year and etc

## 🚀 Usage
You can use bot [here](https://t.me/findfilmsbyfiltersbot) in Telegram

## 🔗 How to start the project on your own
To start this you can clone our repository:
```bash
git clone git@github.com:aqerd/filmbot.git
```
Next, create a file `assets/token.env` and add your API keys like this:
```env
TELEGRAM_BOT_TOKEN=0123456789:rs96Th1SaPiK3YiSdEfin1t3lyFaK310ak8
TMDB_ACCESS_TOKEN=2ak4fake34api4433key4lol7035rs39
```
You can get your TMDb API key [here](https://www.themoviedb.org/settings/api) and create your Telegram bot [here](https://t.me/BotFather).
Now you ready to run this project on your machine.
Please note that you cannot access the TMDb API from Russia and Kazakhstan. To resolve this issue, manually change your network connection's DNS to `9.9.9.9`.

## 🔨 Tools
- Java with Maven
- Telegram API with [telegrambots](https://github.com/rubenlagus/TelegramBots)
- The Movie Database API
- [Feign](https://github.com/OpenFeign/feign) with [Gson](https://github.com/google/gson)

## 🧑‍💻 Credits
Project made by **Ruslan Suleymanov** and **Anna Khairullina** for Object-Oriented Programming course at Ural Federal University 
