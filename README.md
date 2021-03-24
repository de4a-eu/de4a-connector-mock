*DE4A-connector-mock*

**Install**
- (until [de4a-commons](https://github.com/de4a-wp5/de4a-commons) is on maven central) install the [de4a-commons](https://github.com/de4a-wp5/de4a-commons) project locally.
- ``mvn clean package`` to build and package into a runnable jar.

**Deployment**

The project is deployed at https://de4a-dev-mock.egovlab.eu

The endpoints can be tested using a simple curl command.
```
curl --request POST \
--url https://de4a-dev-mock.egovlab.eu/[endpoint] \
--header 'Content-Type: application/xml' \
--data '@/path/request/data'
```

***endpoints***

- /do1/im/extractevidence
- /do1/usi/extractevidence
- /dr1/im/transferevidence
- /dr1/usi/transferevidence
- /de1/usi/forwardevidence
- /dt1/usi/transferevidence

***example***

The path to the request assumes it is run from project root
```
curl --request POST \
--url https://de4a-dev-mock.egovlab.eu/do1/im/extractevidence \
--header 'Content-Type: application/xml' \
--data '@src/main/resources/examples/DO1-IM-request.xml'
```

**Change-log**

- 2021-03-24 
  - Updated to the latest xml-schema [definition](https://github.com/de4a-wp5/xml-schemas/tree/ef08001696bac65cbf71c84726d3e0aa48a8579a). 
  - Changed the T4.2 Canonical Evidence from v0.4 to v0.5  
  - Changed EvidenceId from 'CompanyInfo' to 'CompanyRegistration'
  - Removed the old IDK related endpoints.
  - Updated examples

    