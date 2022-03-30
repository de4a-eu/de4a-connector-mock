import React, { useState, Fragment, useContext } from "react";
import { Button, Col, Row, } from 'react-bootstrap'
import './index-it2.scss';
import XMLViewer from 'react-xml-viewer'

import Context from "./context/context";

const  ReviewNotif = ({ translate, notification, notificationRoot, notificationId, gotoSent, gotoInit }) => {
	const context = useContext(Context);
	const [notifId, setNotifId] = useState("")
	
	const parser = new DOMParser()
    const xmlNotification = parser.parseFromString( notification, "application/xml")
    const xmlNS = xmlNotification.createNSResolver(xmlNotification)
    const xmlRoot = xmlNotification.evaluate(
        notificationRoot, 
        xmlNotification,
        xmlNS,
        XPathResult.ANY_UNORDERED_NODE_TYPE,
        null).singleNodeValue

    console.log("xmlRoot", xmlRoot)
	
	const onSend = () => {
		console.log("onSend notificationId", notificationId)
		gotoSent(notificationId)
	}
	
	const specialNodes = {
        
        "NotificationId" : (node) => {
            return handleTextChild(node)
        }
    }

	const handleTextChild = (node) => {
		console.log("handleTextChild", node)
        if (node.childElementCount === 1
            && node.firstChild.localName === "text") {
                return <Row className='evidenceField'>
                    <Col>
                        <p>{translate(`canonicalEvidenceFields.${node.localName}`)}</p>
                    </Col>
                    <Col>
                        <p>{node.firstChild.innerHTML}</p>
                    </Col>
                </Row>
        } else if (node.childElementCount === 0) {
            return printLeaf(node)
        } else {
            return <Fragment>
                {printNode(node)}
                {Array.from(node.childNodes)
                    .map((child, i) => <Fragment key={i}> {parseNode(child)} </Fragment>)}
            </Fragment>
        }
    }

	const printNode = (node) => {
        return <Row className='evidenceHeading'>
            <Col>
                <h2>{node.localName}</h2>
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

	const parseNode = (node) => {
		//console.log("parseNode --> node:", node)
        if (Object.keys(specialNodes).includes(node.localName)) {
			console.log("specialNode")
			console.log("node.localName", node.localName)
			console.log("node.innerHtml", node.innerHTML)
			notificationId = node.innerHTML;
			console.log("notificationId", notificationId)
			console.log("node", node)
            return specialNodes[node.localName](node)
        } else if (node.childElementCount === 0) {
            return printLeaf(node)
        } else {
            return <Fragment>
                {printNode(node)}
                {Array.from(node.childNodes)
                    .map((child, i) => <Fragment key={i}> {parseNode(child)} </Fragment>)}
            </Fragment>
        }
    }

	return (
		
		<div class="container">
			{xmlRoot !== null &&
		        <div class="container">
					<p class="paragraph">{parseNode(xmlRoot)}</p>
				</div>
		    }
			<div class="container">
				 <p>
                   <XMLViewer xml={notification} /> 
                </p>
			</div>
			<div class="container">
				<Button onClick={onSend} variant='primary'>Send notification</Button>
				<Button onClick={gotoInit} variant='primary'>Back</Button>
			</div>
		</div>
	);
}
export default ReviewNotif;