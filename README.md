# custom-spring-initializr
Customized Spring Initializr (https://start.spring.io) for projects.

Just a note of caution - if you want to run it locally make sure you enable the `local` profile, otherwise it will try to publish statistics out to a DynamoDB table that you have no control over. If you run the application using `./gradlew bootRun` it will do that automatically for you.
