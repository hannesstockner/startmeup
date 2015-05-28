Start Me Up
============

## Running with Docker

###Pre-Requisites

- Docker [https://docs.docker.com/installation/]

###Usage

Build Docker image:

- ```docker build -t hannesstockner/startmeup .```

Run Docker image:

- ```docker run -i -t hannesstockner/startmeup```

## Running without Docker

###Pre-Requisites

- Oracle JDK 8
- Gradle 2.4

###Usage

Run tests and build application:

- ```gradle clean test fatJar```

Run application:

- ```java -jar build/libs/startmeup-all-1.0.jar```

Alternative:

- import the project in your favourite IDE and run com.hannesstockner.startmeup.Main :)

