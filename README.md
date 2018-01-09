# cmbot


[![Build Status](https://travis-ci.org/yashino91/cmbot.svg?branch=master)](https://travis-ci.org/yashino91/cmbot)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/dwyl/esta/issues)


A simple telegram bot for fetching price information about currencies at CoinMarketCap.


## Installation


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

```sh
$ java -jar target/cmbot-<version>.jar
```

## Contribution

If you like to contribute or work on your own on this project, make sure that you have installed the following components on you system:
- Maven 3
- JDK 8

```sh
$ git clone https://github.com/yashino91/cmbot.git
$ cd cmbot/
$ mvn clean install
```