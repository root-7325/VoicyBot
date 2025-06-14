# VoicyBot
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://java.com)
[![Telegram API](https://img.shields.io/badge/Telegram%20Bot%20API-9.0-lightblue.svg)](https://core.telegram.org/bots/api)

VoicyBot is a Telegram bot that processes voice messages, provides summaries and expands ideas. It supports both voice messages and manual text prompts.

## Features ✨

- 🎙️ Voice message analysis (speech-to-text)
- 📝 Automatic summarization of voice content
- 💡 Idea development and expansion
- 📌 Text prompt support via `/ask` command

## Requirements
- JDK 17+
- Maven 3.8+
- Telegram Bot token from [@BotFather](https://t.me/BotFather)
- Vosk model (can be obtained from [here](https://github.com/alphacep/vosk-space/blob/master/models.md))
- API key on [OpenRouter](https://openrouter.ai)

## Installation and running
1. Clone this repository
2. Rename and fill in `config.yaml.template` in `src/main/resources`
3. Build and run

## License 📄
This project is licensed under the [MIT License](LICENSE) - so do whatever you want with it, just don't blame me if something goes sideways.