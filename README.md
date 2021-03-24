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

 [Change-log](CHANGELOG.md)   