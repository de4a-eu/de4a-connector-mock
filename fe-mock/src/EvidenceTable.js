import { Fragment } from 'react'
import { Col, Row, } from 'react-bootstrap'

const EvidenceTable = ({ evidence, evidenceRoot, evidenceIgnore, translate }) => {
    
    const parser = new DOMParser()
    const xmlEvidence = parser.parseFromString( evidence, "application/xml")
    const xmlRoot = xmlEvidence.evaluate(
        evidenceRoot, 
        xmlEvidence, 
        xmlEvidence.createNSResolver(xmlEvidence), 
        XPathResult.ANY_UNORDERED_NODE_TYPE, 
        null).singleNodeValue
    
    const printNode = (node) => {
        return <Row className='evidenceHeading'>
            <Col>
                <h2>{translate(`canonicalEvidenceFields.${node.localName}`)}</h2>
            </Col>
        </Row>
    }

    const printLeaf = (node) => {
        return <Row className='evidenceField'>
            <Col>
                <p>{translate(`canonicalEvidenceFields.${node.localName}`)}</p>
            </Col>
            <Col>
                <p>{node.innerHTML}</p>
            </Col>
        </Row>
    }
    
    const mapChildrenToMap = (children, map) => {
        let ret = {}
        children
            .filter((child) => Object.keys(map).includes(child.localName) )
            .map((child) => ret[map[child.localName]] = child.innerHTML)
        return ret
    }
    
    const specialNodes = {
        "CanonicalEvidence" : (node) => {
            return Array.from(node.childNodes)
                .filter((child) => !(child.localName in evidenceIgnore))
                .map((child, i) => <Fragment key={i}> {parseNode(child)} </Fragment>)
        },
        "RegisteredAddress" : (node) => {
            return <Row className='evidenceField'>
                <Col>
                    <p>{translate(`canonicalEvidenceFields.${node.localName}`)}</p>
                </Col>
                <Col>
                    <p>{
                         printAddress(mapChildrenToMap(Array.from(node.childNodes), addressMap))
                    }</p>
                </Col>
            </Row>
        },
        "PostalAddress" : (node) => {
            return <Row className='evidenceField'>
                <Col>
                    <p>{translate(`canonicalEvidenceFields.${node.localName}`)}</p>
                </Col>
                <Col>
                    <p>{
                         printAddress(mapChildrenToMap(Array.from(node.childNodes), addressMap))
                    }</p>
                </Col>
            </Row>
        },
    }
    const addressMap = {
        "Thoroughfare": "street",
        "LocationDesignator": "no",
        "PostCode": "pno",
        "PostName": "city",
        "AdminUnitL1": "al1",
        "AdminUnitL2": "al2"
    }

    const getIfExist = (map, key, def="") => {
        if (Object.keys(map).includes(key)) {
            return map[key]
        } 
        return def
    }
    
    const printAddress = (address) => {
        return `${getIfExist(address, 'street')} ${getIfExist(address,'no')}, ${getIfExist(address,'pno')} ${getIfExist(address,'city')}, ${getIfExist(address,'al1')} ${getIfExist(address,'al2')}`
    }
    
    const parseNode = (node) => {
        if (Object.keys(specialNodes).includes(node.localName)) {
            return specialNodes[node.localName](node)
        } else if (node.childElementCount === 0) {
            return printLeaf(node)
        } else {
            return <Fragment>
                {printNode(node)}
                {Array.from(node.childNodes)
                    .filter((child) => !(child.localName in evidenceIgnore))
                    .map((child, i) => <Fragment key={i}> {parseNode(child)} </Fragment>)}
            </Fragment>
        }
    }
    
    return <Fragment>
        {parseNode(xmlRoot)}
        </Fragment>
}

export default EvidenceTable