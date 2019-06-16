# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)

To compile:
```
mvn clean install
```

To run
```
mvn spring-boot:run
```

Active endpoints 
* http://localhost:8080/isActive to check if the application is running
* http://localhost:8080/getStats to start crawling an URL
* http://localhost:8080/getLinks to get the link transition matrix
* http://localhost:8080/stop to stop crawling an URL

In order to make it easier to test this app, I've created a Postman project.