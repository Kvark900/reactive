# Getting Started

### Setting up the database

Set up the postgres database according to `application.properties` file or run `docker-compose up` in `src/main/resources` to deploy 
`postgres` image.  

If you are using `Docker` for `postgres` database be sure to modify `application.properties` according to `docker-compose.yml` file.
Use user, password, and port from docker config:

````
version: "3.9"

services:
db:
image: postgres
environment:
POSTGRES_USER: root
POSTGRES_PASSWORD: root
ports:
- "5499:5432"
volumes:
- reactive_data:/var/lib/postgresql/data
volumes:
reactive_data:
````

Next step is to define the schema and tables.  
Please use the script provided in `schema.sql` to create the schema and needed tables.

### Running the app
The application uses Spring Boot so it is easy to run. You can start it any of a few ways:

- Run the main method from `ReactiveApiApplication`
- Use the Maven Spring Boot plugin: `mvn spring-boot:run`


### Using the app
Two routes are exposed in `ProductController`:  
- `/products` - to get merged and formatted products from heroku routes and store metadata for each heroku request
- `/products/stats` - for getting response time statistics for a current day


Enjoy.
### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.6/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.6/maven-plugin/reference/html/#build-image)
* [Spring Data R2DBC](https://docs.spring.io/spring-boot/docs/2.5.6/reference/html/spring-boot-features.html#boot-features-r2dbc)

### Guides

The following guides illustrate how to use some features concretely:

* [Acessing data with R2DBC](https://spring.io/guides/gs/accessing-data-r2dbc/)

### Additional Links

These additional references should also help you:

* [R2DBC Homepage](https://r2dbc.io)

## Missing R2DBC Driver

Make sure to include a [R2DBC Driver](https://r2dbc.io/drivers/) to connect to your database.
