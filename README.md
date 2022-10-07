# SMS Forwarder
Forwards SMS from Android device to Telegram.

## The Problem and Solution

If you are abroad, you may have problems receiving your SMS (or part of them, e.g. banking or auth messages). This app solves this problem by forwarding messages to Telegram.

## How to start

To start, you have to do the steps below:

1. Set the SIM card to a device that you leave at home.
2. [Create a Telegram bot](https://core.telegram.org/bots#how-do-i-create-a-bot) and channel.
3. Build and install APK to the device.
4. Open the app and block the device (don't minimize the application).

After that, the bot will forward all SMS to the channel.

Channel is more convenient than private chat. This way, you can create different channels for different devices. It will prevent mash and let you share access to SMS with your closes.

## Pre-build settings

Set variables `TELEGRAM_API_TOKEN` and `CHAT_ID` into `local.properties`. You can get the Telegram Bot token through [@BotFather](https://t.me/BotFather). Also, you should create Telegram Channel and add a bot to it as admin.
