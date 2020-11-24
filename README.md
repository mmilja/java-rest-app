## Java application implementing REST server and uses JWT for authorization

Java 12 application that implements REST server using [openapi generator](https://github.com/OpenAPITools/openapi-generator). Uses [jjwt](https://github.com/jwtk/jjwt) for session authorization with JWT.

Built and packaged with maven.

#
<a name="description"></a>
### Description

Contains two modules:
   * bookmark-api
   * bookamrk-server
   
Also, contains a parent pom that defines dependency versions and plugins used in both api and server modules.

Api module contains a specification file from which the REST API model and client is built.
- [bookmark-api-specification.yaml](bookmark-api/src/main/resources/specifications/bookmark-api-specification.yaml)

Server module implements the http server in java using [jetty](https://www.eclipse.org/jetty/) and [javax-annotations](https://mvnrepository.com/artifact/javax.annotation/javax.annotation-api)
Contains the [log4j2](http://logging.apache.org/log4j/2.x/) for logging purposes whose config is defined in the [log4j2.xml](bookmark-server/src/main/resources/log4j/log4j2.xml)

Entities defined in the application:
   * User
     - name
     - password
   * Bookmark
     - name
     - URI
     - public/private access mode 

Functionality of the app includes:
   * registering users
   * log-in/out a registered user
   * add/update/delete/get a bookmark for a logged-in user
   * get all public bookmarks for a logged-in user 

#
<a name="instalation"></a>
### Installation

Clone the git repository into a system that has Java 12 and Maven 3 installed.

Build the project with:

```
mvn clean package
```

Project includes a shade plugin which creates an uber-jar from which the app can be started.

To run the application with console logging:

```
java -Dlog4j.configurationFile=<path_to_log4j2.xml> -jar <path_to_build_jar>
```
 
 #
<a name="instalation"></a>
### Example commands

When logging in, you will receive an auth token which needs to be provided for other requests requiring authorization.

* Register user:
        ```
        curl -X POST "http://localhost:8080/management/user/register" -H  "accept: application/json" -H  "Content-Type: application/json" -d '{"name":"user","password":"password"}'
        ```
#
* Log-in user:
        ```
        curl -X POST "http://localhost:8080/management/user/login" -H  "accept: application/json" -H  "Content-Type: application/json" -d '{"name":"user","password":"password"}'
        ```
#
* Log-out user:
        ```
        curl -X POST "http://localhost:8080/management/user/logout" -H  "accept: application/json" -H  'Authorization: <auth_token>' -H  "Content-Type: application/json" -d 'user'
        ```
#
* Get all bookmarks for user:
        ```
        curl -X GET "http://localhost:8080/management/bookmark" -H  "accept: application/json" -H  'Authorization: <auth_token>' -H  "Content-Type: application/json"
        ```
#
* Add a bookmark for user:
        ```
        curl -X POST "http://localhost:8080/management/bookmark" -H  "accept: application/json" -H  'Authorization: <auth_token>' -H  "Content-Type: application/json" -d '{"bookmarkLink":{"uri":"string","name":"string"},"access":"PRIVATE"}'
        ```
#
* Delete a bookmark for user:
        ```
        curl -X DELETE "http://localhost:8080/management/bookmark/myBookmark" -H  "accept: application/json" -H  'Authorization: <auth_token>'
        ```
#
* Update a bookmark for user:
        ```
        curl -X POST "http://localhost:8080/management/bookmark/myBookmark" -H  "accept: application/json" -H  'Authorization: <auth_token>' -H  "Content-Type: application/json" -d '{"bookmarkLink":{"uri":"string","name":"string"},"access":"PRIVATE"}'
        ```
#
* Get all public bookmarks for user:
        ```
        curl -X GET "http://localhost:8080/management/bookmark/public" -H  "accept: application/json" -H  'Authorization: <auth_token>'
        ```
#