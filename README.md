# cmbot


[![Build Status](https://travis-ci.org/yashino91/cmbot.svg?branch=master)](https://travis-ci.org/yashino91/cmbot)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/dwyl/esta/issues)


A simple telegram bot for fetching price information about crypto currencies from CoinMarketCap.

![Alt text](/screenshots/bot-example.png?raw=true "Bot Example")


## Installation

In order to run the bot, you have to set your telegram bot api token as an environment variable:


```sh
$CMBOT_TELEGRAM_TOKEN={YOUR_API_KEY}
```


```sh
$ git clone https://github.com/yashino91/cmbot.git
$ cd cmbot/
$ mvn assembly:assembly
```

### Run application using Docker

```sh
$ docker build -t cmbot .
$ docker run cmbot
```

### Run application without Docker

Make sure you have at least jdk8 installed.

```sh
$ java -jar target/cmbot-<version>.jar
```