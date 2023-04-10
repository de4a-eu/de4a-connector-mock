/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        if (Object.keys(specialNodes).includes(node.localName)) {
			console.log("node.localName", node.localName)
			console.log("node.innerHtml", node.innerHTML)
			notificationId = node.innerHTML;
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