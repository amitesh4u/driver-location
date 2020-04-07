# Go-Jek Codathon Project

This is a Java / Maven / Spring Boot / MongoDB based application that can be used to consume Driver Locations and then return Near by Driver locations as per Customer location.

# Why this Tech Stack

* I would have gone for NodeJS but since OOP language was required and I am from JAVA background so picked it.
 
* Maven and Gradle are currently the Top build tools. **Maven** has an upper hand over **Ant** as it is more structured and has better *dependency management* where we just declare the dependency and Maven will download them, along with the dependencies of the dependency, add them to class path etc. I have hands-on on Maven so picked it over Gradle.

* Spring-Boot is a light-weighted suite of pre-configured applications which provides the MVC structure, dependency injection with less code and xml configurations as compared to Core Spring framework. It is easy to create RESTful services (among other types of applications). 

* MongoDB is one of the top NoSQL db which stores data in JSON type format and is better suited for read and write as compared to Relational databases. Since the requirement of this application states that there will be frequent data reads and writes, picking a NoSQL db will certainly improve the performance.

* *All are open-source and have support of huge community base*

# Infrastructure requirements
* **OS** - It can run on any OS with JRE 8+ and MongoDB installed
* **RAM** - 512mb or more
* **Hard-disk** - The project size is under 20mb so any thing above is fine

# Setup instructions

## Development Environment
* Install JDK8+

    Download and install Sun JDK8+ on the system. Refer this [link](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html#CJAGAACB).

* Install Maven 3.x

      1) Download and unpack the archive where you would like to store the binaries, eg:
    
        Unix-based operating systems (Linux, Solaris and Mac OS X)
          tar zxvf apache-maven-3.x.y.tar.gz
        Windows
          unzip apache-maven-3.x.y.zip
    
      2) A directory called "apache-maven-3.x.y" will be created.
    
      3) Add the bin directory to your PATH, eg:
    
        Unix-based operating systems (Linux, Solaris and Mac OS X)
          export PATH=/usr/local/apache-maven-3.x.y/bin:$PATH
        Windows
          set PATH="c:\program files\apache-maven-3.x.y\bin";%PATH%
    
      4) Make sure JAVA_HOME is set to the location of your JDK
    
      5) Run "mvn --version" to verify that it is correctly installed.
      
     Refer this [link](http://maven.apache.org/install.html) for more details.

* Install MongoDB
    
    * Download and install MongoDB on the system. Refer this [link](https://docs.mongodb.com/manual/installation/).
    
    * Goto mongo console
        
            mongo
        
    * Switch to admin db
    
            use admin
    
    * Create Admin user
    
            db.createUser(
              {
                user: "myDBAdmin",
                pwd: "MyDB@dmin",
                roles: [ { role: "userAdminAnyDatabase", db: "admin" },{ "role" : "readWriteAnyDatabase", "db" : "admin" } ]
              }
            )
            
     * Enable Authorization in *mongod.cfg*/*mongodb.cfg* file or Start mongod with --auth

            security:
                   authorization: enabled
	    OR
	    
            auth = true

    * Restart Mongodb (Create a service for Mongodb on windows for easy control. Restart mongodb service on linux)
    
    * Login to verify
    
            mongo -u "myDBAdmin" -p "MyDB@dmin" admin
            
    * Create Users for gojek and gojektest db
    
            use gojek
            
            db.createUser(
              {
                user: "rwuser",
                pwd: "myr3@dwrit3pwd",
                roles: [ { role: "readWrite", db: "gojek" } ]
              }
            )
            
            use gojektest
            
            db.createUser(
              {
                user: "testinguser",
                pwd: "testinguser123",
                roles: [ { role: "readWrite", db: "gojektest" } ]
              }
            )
            
    * Exit (exit) and Login again with gojek account to verify
            
            mongo -u rwuser -p myr3@dwrit3pwd gojek
            	
* Build Project
    * Download the **gojekcodathon** code and place it in any folder
    * Run maven command to clean, test and package the project with development Profile.
    
            mvn -Pdev clean package
            
    * If you have just installed Maven or running this build for the first time, it may take a while on the first run. 
    This is because Maven is downloading the required artifacts (plugin jars and other files) 
    into your local repository.
    
    * The command line will print out various actions, and end with the following:
    
             ...
            [INFO] ------------------------------------------------------------------------
            [INFO] BUILD SUCCESSFUL
            [INFO] ------------------------------------------------------------------------
            [INFO] Total time: 36.59 seconds
            [INFO] Finished at: 2017-02-11T17:44:11+05:30
            [INFO] Final Memory: 32M/259M
            [INFO] ------------------------------------------------------------------------

    * Once successfully built, you can run the service by one of these two methods:
      
          java -jar -Dlog4j.debug -Dlog4j.configuration=file:log4j.xml target\gojekcodathon-1.0-SNAPSHOT.jar
          
          OR
          
          mvn spring-boot:run -Dlog4j.debug -Dlog4j.configuration=file:log4j.xml
      
    * Check the console for any exceptions. Once the application runs you should see something like this
      
          2017-02-11 19:49:05.151  INFO 11512 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http)
          2017-02-11 19:49:05.162  INFO 11512 --- [           main] com.gojek.DriverLocationApp              : Started DriverLocationApp in 12.125 seconds (JVM running for 25.242)

    * Test it by hitting the following endpoint.
    
            http://localhost:8080/
            
    * You should get a message similar to this.
    
            Hello. Please use # PUT /drivers/{id}/location OR # GET /drivers apis
        
#### Debugging the app remotely from your IDE

   Run the service with these command line options:

    mvn spring-boot:run -Dlog4j.debug -Dlog4j.configuration=file:log4j.xml -Drun.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"

   and then connect to it remotely using your IDE on Port 5005 (change it if req.).
    
   For example, from IntelliJ You have to add remote debug configuration: 
        
        Edit configuration -> Remote.

       
## Production/Testing Environment

* Install JRE1.8+ and MongoDB on the system. Refer links mentioned above.
* Create Users for MongoDB with different account/db name if req. 
* Add the account details in application.properties file under production profile
        
        gojekcodathon/src/main/resources/prod/application.properties
        
* Make updates in other properties files as well as log4j.xml if req.  

* Build the JAR with Production/Testing profile (prod/qa) on *Development* server. Refer **pom.xml** profile section.
        
        mvn -Pprod clean package
        
* Copy the JAR and log4j.xml files together in any *Production* server folder.

* Run it by this command

      java -jar -Dlog4j.configuration=file:log4j.xml gojekcodathon-1.0-SNAPSHOT.jar
              
* The log files will be found under **log** folder.        


## APIs

The project is Driver Location REST service. There are two part of the service.

### Store Driver's Location

Drivers will send their current location every 60 seconds. They’ll call following
API to update their location

    PUT /drivers/{id}/location
    {
    "latitude": 12.97161923,
    "longitude": 77.59463452,
    "accuracy": 0.7
    }

**Response:**
- 200 OK on successful update
Body: {}
- 404 Not Found if the driver ID is invalid (valid driver ids - 1 to 50000)
Body: {}
- 422 Unprocessable Entity - with appropriate message. For example:
{"errors": ["Latitude should be between -90 and 90"]}


### Find nearest Drivers
   
   Customer applications will use following API to find drivers around a given location

    GET /drivers
    Parameters:
    "latitude" - mandatory
    "longitude" - mandatory
    "radius" - optional defaults to 500 meters
    "limit" - optional defaults to 10

**Response:**
- 200 OK
        
        [
        {id: 42, latitude: 12.97161923, longitude: 77.59463452, distance: 123},
        {id: 84, latitude: 12.97161923, longitude: 77.59463452, distance: 123}
        ]
- 400 Bad Request - If the parameters are wrong

        {"errors": ["Latitude should be between -90 and 90"]}

# Configuration

* You can add/update validation related constraints in **env.config** (prefix *validation.*)

* Few configurations
  
  **dataFreshnessLimitInMin** - Drivers location will be considered if location data is present within the time limit.
        i.e. Location Data > (NOW - Data Freshness Limit). Set 0 or less to fetch All. 
        
       dataFreshnessLimitInMin=60
  **driverLocationResponseSortedByDistance** - Fetch Nearest driver sorted by Distance from User
  
       driverLocationResponseSortedByDistance=true
  
  **driverLocationResponseSortedAsc** - Sort the distance Ascending/Descending
  
       driverLocationResponseSortedAsc=true
  **DriverId Min/Max** - Set the valid range for Driver Id
          
       validation.driverIdMin=1
       validation.driverIdMax=50000
  
  **Latitude / Longitude min/max** - Set the range of Location co-ordinates to consume. Helpful to restrict City/State/Country wide data consumptions.         
      
      validation.latitudeMin=-15
      validation.latitudeMax=15
      validation.longitudeMin=-80
      validation.longitudeMax=80
      
  **Accuracy** - Set the range of valid accuracy range. *A difference of 1 degree ~ 111 km*
  
      validation.accuracyMin=0
      validation.accuracyMax=1

# Future Enhancements

* Using OAuth for security. Spring-boot has internal Oauth library for this - *spring-security-oauth2*.
* Using embedded MongoDB for testing
* Add MongoDB replica.
* Using REST Assured for Testing which is more verbose.
* Spring-boot Actuator that gives the application the following endpoints helpful in monitoring and operating the service:
    
    **/metrics** Shows “metrics” information for the current application.
    
    **/health** Shows application health information.
    
    **/info** Displays arbitrary application info.
    
    **/configprops** Displays a collated list of all @ConfigurationProperties.
    
    **/mappings** Displays a collated list of all @RequestMapping paths.
    
    **/beans** Displays a complete list of all the Spring Beans in your application.
    
    **/env** Exposes properties from Spring’s ConfigurableEnvironment.
    
    **/trace** Displays trace information (by default the last few HTTP requests).
* Using Exception classes for better exception handling.
* Using sl4j which provides better logging options.
