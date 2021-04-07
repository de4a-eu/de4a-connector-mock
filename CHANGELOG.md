**Change-log**

- 2021-04-07
  
    - Added support to build to war instead of jar

- 2021-03-31
    - Split the interfaces into profiles, allowing them to be run separately.
    - Made the endpoint paths configurable
    - DR can be configured to fill the evidence by making an request to a DO.
    - Updated EvidenceId CompanyRegistration to the full urn `urn:de4a-eu:CanonicalEvidenceType::CompanyRegistration`

- 2021-03-24
    - Updated to the latest xml-schema [definition](https://github.com/de4a-wp5/xml-schemas/tree/ef08001696bac65cbf71c84726d3e0aa48a8579a).
    - Changed the T4.2 Canonical Evidence from v0.4 to v0.5
    - Changed EvidenceId from 'CompanyInfo' to 'CompanyRegistration'
    - Removed the old IDK related endpoints.
    - Updated examples

