# TOPCODER - IMPLEMENT BILLING ACCOUNT SERVICE

## Prerequesits

- Java 8 with Maven 3
- Docker

# Docker Informix Image

To run Informix locally go to the 'local' folder and execute the following command:

```
docker-compose up
```

If you should ever encounter the error:

```
[java] Caused by: com.topcoder.db.connectionfactory.DBConnectionException: error occurs while creating the connection.
You already have 20 connections at this
time. Any further connections
```

Try to restart the informix database server as below:
```
# log into the tc-informix docker container first
docker exec -it tc-informix bash

# restart the database server
onmode -ky
oninit
```

## Configuration

The configuration is done via environment variables, these can be set in 'local/run.sh'.

|Name	             | Description                     |
|--------------------|:-------------------------------:|
|IP                  | The IP of your docker container |
|TIME_OLTP_PW        | The Informix password           |
|TIME_OLTP_USER      | The Informix user               |
|TIME_OLTP_URL       | The Informix URL                |
|TC_JWT_KEY          | The JWT key                     |

### Set up Local Maven repository for Appirio Maven

- Since maven.appirio.net is not accessible, We need to set up a local maven repository with the zip file `temp-maven-repo-master.zip`

- Unzip the contents to any of the directory in your System. **In Linux, the directory should not be a mounted directory like pen drive, hard disk, etc..**

- Open `pom.xml` in `service` directory and replace the `<url>` of Local repository with `absolute path of the unzipped file`. E.g. If you have unzipped the files to `/home/user/temp-maven-repo-master`, then POM entry will look like

```
<repository>
	<id>1_temp_appirio_maven_repo</id>
	<name>Appirio Maven Local Repository</name>
	<url>file:///home/user/temp-maven-repo-master</url>
</repository>
```

## Start Microservice

In the 'service' folder of this project run:

```
mvn clean package
```

Then start the service using the following command in the 'local' folder:

```
./run.sh
```

You may use git bash under windows to run shell script.

## Data setup 
Connect to informix server with informix/1nf0rm1x credentials and execute the SQL file doc/test_data.sql.
If you are executing these SQL statements one by one, then make sure that the correct database is selected.


## Verification

If the maven build process was successful the unit tests ran without error.

-- Postman collection  : doc/billing-accounts-and-clients.postman_collection.json
-- Postman environment : doc/billing-accounts_clients-env.postman_environment.json
       
The postman environment defines the following variables :
  - apiUrl : The base API URL
  - no-admin-token : A token for user with no admin access.
  - admin-token : A token for an admin user ( 'heffan')


Load the postman collection and environemnt in the 'doc' folder and execute the provided requests to verify the REST API.

Please make sure to update the billing accounts ids and user ids and client ids in the REST path accordingly.


There are two 'Add User to billing account' tests in the provided collection, this is to validate the following requirement :
"6. When Adding Billing Account Users, If the tc handle doesn’t exist in the time_oltp:user_account table, then we’ll need to add it before we can create a project_manager record"

-- The user 'Yoshi', already has a user account in time_oltp:user_account ( From initial informix data setup).
-- The user 'wyzmo' does not have a user account.

'Get Billing Account Users' can be used to check if the users are actually added to the billing account.


--You can check if the user account is created by querying the user_account table in time_oltp :

```
select * from time_oltp:user_account
```

The existing code mixed the userId (from common_oltp:user) with user account id (from time_oltp:user_account).
The userId to specify for the Add/Remove user from billing account is the user id from common_oltp, these ids can be retrieved by executing the following statement :

```
select  user_id, handle from common_oltp:user
```
