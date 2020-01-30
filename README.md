# ceassl

**Ceassl** – Cease + SSL is a SSL/TLS checker that notifies you when one of the monitored HTTPS target's certificate is about to expire. So that you finally can take action in time because you  didn't put a reminder in your calendar to renew certificate before the website becomes unavailable.

[![Travis-CI](https://api.travis-ci.org/synthomat/ceassl.svg?branch=master)](https://travis-ci.org/synthomat/ceassl) [![Clojure CI](https://github.com/synthomat/ceassl/workflows/Clojure%20CI/badge.svg)](https://github.com/synthomat/ceassl/actions?query=workflow%3A%22Clojure+CI%22) [![MIT License](https://img.shields.io/github/license/synthomat/ceassl)](https://github.com/synthomat/ceassl/blob/master/LICENSE)

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server-headless

## License

Copyright © 2020, Synthomat
