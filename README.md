# ExcelTemplate

This project is used to get values from users as key-value pairs and store it in Excel Sheet where the keys are stored as column names and the values are stored as column values.

### API Endpoints

POST ```/organizations/{orgName}/add-data```: Upload data to an organization's Excel template.

GET ```/organizations/{orgName}/download-excel```: Download an Excel template with defined fields for an organization.

GET ```/organizations/{orgName}/retrieve-data```: Retrieve data in Excel format for an organization.


### Clone the repository to your local machine:

```git clone <repository-url>```

### Open the project in your preferred IDE.


### Build the project using Maven:

```mvn clean install```

### Run the Spring Boot application:

```mvn spring-boot:run```

### Configuration

The application can be configured through application.properties. You may configure the server port, database connection, and other settings as needed.

### DATABASE:MongoDB







