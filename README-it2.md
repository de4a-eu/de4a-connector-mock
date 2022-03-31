*DE4A-Mock DO - iteration 2*

**New features**
- enables support for multi-item messaging
- adds create and send notification to Controller
- receive a subscription message, creates a notification and send it to Controller
- JUnit util to create random compatible iteration 2 messages
	
**Setup**
- addressing Connector: See below Configuration
- Integrated Spring-boot deployment launch mode
    - `Run as Java application eu.de4a.connector.mock.Mock.java`
    		
**Deployment path**

	[https://pre-smp-dr-de4a.redsara.es/de4a-mock-connector](https://pre-smp-dr-de4a.redsara.es/de4a-mock-connector)

**Endpoints**
- Previous DO endpoints are also available. 
- New endpoints for iteration 2 added.
	
	- /do1/preview/index - USI pattern supports multi-evidence 
	- /do1/subscription/eventSubscription - Subscription pattern
	- /notification - Notification pattern

**Configuration**

The mock is configured in the [application.properties](src/main/resources/application.properties) file.

The property **mock.do.preview.dt.url** is used to address the Connector 
- `mock.do.preview.dt.url=http://localhost:8077/de4a-connector/`
