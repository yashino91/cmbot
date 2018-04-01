# cmbot

[![Coverage Status](https://coveralls.io/repos/github/yashino91/cmbot/badge.svg?branch=master)](https://coveralls.io/github/yashino91/cmbot?branch=master)
[![Build Status](https://travis-ci.org/yashino91/cmbot.svg?branch=master)](https://travis-ci.org/yashino91/cmbot)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/dwyl/esta/issues)


A simple telegram bot for fetching price information about crypto currencies from CoinMarketCap. 

![Alt text](/screenshots/example.png?raw=true "Bot Example - Formatted as a String or rendered as an Image")


As seen above the result can be displayed in 2 different ways:
1. Rendered as an Image. Just use a single slash / for the command. I.e. /eth
2. Formatted as a String with a Link to CoinMarketCap. Use double slash // for the command. I.e. //eth 

You can try it out  [here](https://telegram.me/PriceLeechBot).


## Configuration
The configuration is done in the config.yaml file, located in the resource directory.

| Key 			            | Value 								                |
| ----------------------    |-------------------------------------------------------|
| botName 	                | Name of your Telegram Bot				                |
| allowedCurrencySlugs      | Array of currency slugs that are allowed to request. If empty every currency is allowed |
| stringCommand             | Command to request currency details as a formatted string |
| imageCommand              | Command to request currency details as a rendered image |


## Installation

In order to run the bot, you have to set your telegram bot api token as an environment variable:


```sh
$CMBOT_TELEGRAM_TOKEN={YOUR_API_KEY}
```

[Download](https://github.com/yashino91/cmbot/releases) the latest jar release and run the following command:

```sh
java -jar cmbot-<version>.jar
```


## Build from Source


```sh
git clone https://github.com/yashino91/cmbot.git
cd cmbot/
mvn assembly:assembly
```



### Run application using Docker

```sh
docker build -t cmbot .
docker run -e "CMBOT_TELEGRAM_TOKEN=YOUR_API_KEY" cmbot
```

### Run application without Docker

```sh
java -jar target/cmbot-<version>.jar
```