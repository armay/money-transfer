Sample RESTful API for money transfers with JDBI and Javalin
============================================================

RESTful​ ​API​ ​including​ ​data​ ​model​ ​and​ ​the​ ​backing​ ​implementation​ ​for​ ​money transfers​ ​between​ ​accounts 
built with JDBI and Javalin (and tested with Unirest). 

Build
-----

This software is built with [Gradle](https://gradle.org)
and tested to work with Java 8.

Usage
-----

By default, the API is accessible on port 8080. 
The following requests are supported:

- GET /accounts?phone=%2B71234567890 - account with phone +71234567890

- GET /accounts?pan=1234567890123456 - account with PAN 1234567890123456

- GET /cards?phone=%2B71234567890 - card with phone +71234567890

- GET /cards/1234567890123456 - card with PAN 1234567890123456

- GET /events?transfer_id=96e69a45afafe95e9daf1ce120280e9975281de4 - 
events with transfer ID 96e69a45afafe95e9daf1ce120280e9975281de4

- GET /events/dde476be9322785985ac4c660ad53d84dc30ad06 - 
event with ID dde476be9322785985ac4c660ad53d84dc30ad06

- POST /events - create new transfer request. 
Request body example:

```json 
{ 
    "internal": true, 
    "senderPan": "1234567890123456", 
    "receiverPan": "6123456789012345", 
    "message": "Hello", 
    "value": 10.00, 
    "createdAt": "2018-09-17T03:45:15.057826Z[UTC]" 
}
``` 

Sample response:

```json 
{ 
    "id": "9e6074387502baaf999f4f13de8647e582c24b10", 
    "pan": "1234567890123456", 
    "value": -10.00, 
    "description": "Hello", 
    "createdAt": "2018-09-17T03:45:15.157441Z", 
    "transferId": "05e25d0a11d6b088bfc7d2b36b340978acc7934e" 
}
``` 

Here `id` is the SHA1 hash of the event (Alice was charged $10.00) 
and `transferId` is the SHA1 hash of the transfer (Alice sent Bob $10.00).
On success, Bob gets notification of the corresponding event (Bob was credited $10.00):

```json 
{ 
    "id": "9e87cbaa8293f21a17028f3c973ed001c61b9469", 
    "pan": "6123456789012345", 
    "value": 10.00, 
    "description": "Hello", 
    "createdAt": "2018-09-17T03:45:15.197692Z", 
    "transferId": "05e25d0a11d6b088bfc7d2b36b340978acc7934e" 
}
``` 

License
-------

This project is released under [MIT License](LICENSE.md). 

Links
-----

http://jdbi.org/ - 
The Jdbi library provides convenient, idiomatic access to relational databases in Java.

https://javalin.io - 
Javalin is a simple web framework for Java and Kotlin.

http://unirest.io - 
Unirest is a set of lightweight HTTP libraries available in multiple languages.
