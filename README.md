*DE4A-connector-mock*

**Install**
- (until [de4a-commons](https://github.com/de4a-wp5/de4a-commons) is on maven central) install the [de4a-commons](https://github.com/de4a-wp5/de4a-commons) project locally.
- ``mvn clean package`` to build and package into a runnable jar.

To build a war instead build and package with the `war` profile 
`mvn clean package -P war`

**Deployment**

The project is deployed at https://de4a-dev-mock.egovlab.eu

The endpoints can be tested using a simple curl command.
```
curl --request POST \
--url https://de4a-dev-mock.egovlab.eu/[endpoint] \
--header 'Content-Type: application/xml' \
--data '@/path/request/data'
```

***Endpoints***

- /do1/im/extractevidence
- /do1/usi/extractevidence
- /dr1/im/transferevidence
- /dr1/usi/transferevidence
- /de1/usi/forwardevidence
- /dt1/usi/transferevidence

***Example***

The path to the request assumes it is run from project root
```
curl --request POST \
--url https://de4a-dev-mock.egovlab.eu/do1/im/extractevidence \
--header 'Content-Type: application/xml' \
--data '@src/main/resources/examples/DO1-IM-request.xml'
```

***Configuration***

The mock is configured in the [application.properties](src/main/resources/application.properties) file.

Note that the following settings should be changed from their default values:

- `spring.profiles.active=do, de, dt, dr`
  
  where you set what mocked interfaces to set up.
- `mock.kafka.topic=de4a-mock`

    where you set the topic for the package tracker messages.
- DO specific
    - `mock.allowedOriginList=https://de4a-dev-mock.egovlab.eu`
      
        where you set the allowed origins, needed for CORS
    - `mock.baseurl=https://de4a-dev-mock.egovlab.eu` 
      
        needed to construct the redirect url.
- DR specific
    - `mock.dr.forward.do.im=https://de4a-dev-mock.egovlab.eu/requestExtractEvidenceIM`
      
      `mock.dr.forward.do.usi=https://de4a-dev-mock.egovlab.eu/do1/usi/extractevidence`
      
      where the mocked dr should forward to
    
- DT specific
    - `mock.dt.forward.de.usi=https://de4a-dev-mock.egovlab.eu/api/response`
      
      where the mocked dt should forward to
    

 [Change-log](CHANGELOG.md)   