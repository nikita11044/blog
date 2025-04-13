## About The Project

A simple Spring Boot-based blog.

### Built With

* Spring
* Thymeleaf
* PostgreSQL
* Hibernate
* Minio

### Prerequisites

* Java 21
* Gradle
* Docker

### Installation

1. Clone the repo

```bash
git clone https://github.com/nikita11044/blog.git
cd blog
```

2. Start the Docker containers

```bash
docker-compose up --build
```

**Note:** You may configure database and minio credentials as you see fit. Just make sure they are the same as the ones in `application.properties`

3. Install Application Dependencies

```bash
./gradlew build
```

### Running the Application 

To run the application locally, use the following Gradle command:
```bash
./gradlew bootRun
```
This will start the Spring application, and you should be able to access it at http://localhost:8080 by default.

### Building the Application 
To build the app into an executable JAR file, run the following command:
```bash
./gradlew build
```
The built JAR file will be located in the build/libs/ directory. You can run the JAR file with:
```bash
java -jar build/libs/blog-0.0.1-SNAPSHOT.jar
```

### Testing the Application
In the root directory of the project, run the following command to execute all tests:
```bash
./gradlew test
```
This will run all unit and integration tests and provide a summary of the results in the terminal.

After running the tests, you can check the detailed test reports, which are available in:

```bash
build/reports/tests/test/index.html
```
