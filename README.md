## NOTE: this repository is deprecated in favour of generating your own API client code using our OpenAPI specification, please refer to the documentation at https://developer.oanda.com/exchange-rates-api/#sample-code

java-exchange-rates
===================
OANDA Exchange Rates API client class for Java

This class provides an easy interface to the [OANDA Exchange Rates API](http://www.oanda.com/rates) using Java. The [API documentation page](http://developer.oanda.com/exchange-rates-api/) has a full reference of all the endpoints.  This service requires you to [sign up](http://www.oanda.com/rates/#pricing) for a trial or paying subscription to obtain an API key.

- [Installation](#installation)
- [Usage](#usage)
- [Author](#author)
- [Copyright and License](#copyright_license)
- [Release History](#release_history)

## <a name="installation"></a>Installation
Maven is used for building. Install from http://maven.apache.org/download.cgi. On Ubuntu, you can run sudo apt-get install maven.

Apache httpclient, httpcore, and commons-logging external jar files are referenced.
Google's gson (https://code.google.com/p/google-gson/) is also used to parse json.

## <a name="usage"></a>Usage

Include ExchangeRatesClient.java as part of the project, as well as dependent jar files.
Usage example is shown in included Example.java.

## <a name="author"></a>Author

    James Chung <jchung@oanda.com>

## <a name="copyright_license"></a>Copyright and License

This software is copyright (c) 2014 by OANDA Corporation and distributed under MIT License.

## <a name="release_history"></a>Release History
