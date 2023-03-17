
# Calls Web Service
A service to manage call resources, each representing a phone call between two numbers.
Calls Web Service is a Java Spring-boot application, with a view layer served by Thymeleaf.
The web service exposes some operations (create, read, delete) via a REST controller.
Additionally, the service can be run in a docker container alongside with a MariaDB container.

## Running the application via gradle

Building and running unit tests:
````
gradlew clean build
````

To run the application, in the project root folder run the command below.
A local mariaDB instance should be first configured.
````
gradlew run
````

## Running the application via docker
Building the docker image using the Jib gradle plugin.

````
gradlew jibDockerBuild --image=call-center-server
````

Spinning up the docker containers. In the project root folder (default location of the docker-compose.yml file) run:

````
docker-compose up
````

To remove the data volume:

````
docker volume rm challenge-call-center_db_data
````

## Usage and Configuration

By default, the application will run on port 9000.
The different operations are mapped to:
* /call-center/calls/create
* /call-center/calls/createMany
* /call-center/calls/delete/{id}
* /call-center/calls/list
* /call-center/calls/statistics

The HTML pages are mapped as below.
Generate and clear are dev tools, allowing the user to easily generate and clear all data.

* /
* /createForm
* /create
* /createMany
* /delete/{id}
* /list
* /statistics
* /clear
* /generate

For the create operations, an object (or list of objects for the createMany) is expected with the following format:
````
{
    "callerNumber": "+351 919293284",
    "calleeNumber": "+351 919293186",
    "type": "Inbound",
    "startTime": 1615116875,
    "endTime": 1615117554
}
````

The prices of the calls can be configured via the following properties:

````
challenge.call-center.call.first-five-minutes-cost=0.10
challenge.call-center.call.cost-per-minute-after-five-minutes=0.05
````

## Testing and Generating data

The web service can be tested via any API client like Postman or Insomnia.
A file 'Insomnia_Tests.json' is already provided with this project with a sample test case for each operation.

Testing data can be easily generated via a JSON generator. 
The example below works for https://www.json-generator.com/
````
[
  '{{repeat(1000)}}',
    {
      callerNumber: '+351 {{phone("9192932xx")}}',
      calleeNumber: '+351 {{phone("9192931xx")}}',
      type: '{{random("Inbound", "Outbound")}}',
      startTime:  '{{Math.round(date(new Date(2020, 0, 1), new Date()).getTime()/1000)}}',
      endTime(tags) {
        return this.startTime + tags.integer(0,999);
      }
    }
]
````
