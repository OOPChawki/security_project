# REST JPA JEE

This project is a Maven-based Java EE application designed to run on the Payara server. It features a suite of RESTful endpoints for managing college system entities such as courses, students, professors, and club memberships.

## Postman Collection

Included in this project is a collection of Postman requests to test and interact with the available endpoints. Import the collection into Postman to get started with testing the API.

## Configuration

Before running the application on Payara server, update the MySQL connection credentials in the `payara_resources.xml` file to match your local MySQL setup.

## Group Contribution

This project has been developed by Group 45, with the following members and contributions:

- **041081021, Moulayat Chawki**: Implementation of `CourseRegistrationResource`, annotation corrections, debugging, and 20 JUnit test cases.
- **04107092, Rohan, Kim**: Development of `ClubMembershipResource`, `CourseResource`, `MembershipCardResource`, `ProfessorResource`, and various `TODO` items.
- **041014922, Hoskol Mohamed**: Implementation of 10 user cases.
- **041055255, Nikhil Krishnaa**: Implementation of 10 user cases.

## Running the Application

To run the application:

1. Ensure that Payara Server is installed and running.
2. Update the database connection credentials in `payara_resources.xml`.
3. Deploy the application to your Payara server instance.
4. Use Postman to test the API endpoints with the provided collection.

## Feedback and Support

For feedback or support regarding the application, please reach out to the contributors mentioned above or raise an issue in the project's repository.

# REST_JPA_JEE