import React, { useState, useContext } from "react";
import './index-it2.scss';
import { Redirect } from "react-router-dom";
import axios from "axios";
import Containter from 'react-bootstrap/Container'
import {Col, Row} from "react-bootstrap";

import Context from "./context/context";
import CreateNotif from "./CreateNotif"
import ReviewNotif from "./ReviewNotif"
import SentNotif from "./SentNotif"

import translate from 'translate-js'
import trans_en from './translate/en'

translate.add(trans_en, 'en')
translate.whenUndefined = (key, locale) => {
    return `${key}:undefined:${locale}`
}


const BrowsingStep = {
    createNotif: 'createNotif',
    reviewNotif: 'reviewNotif',
	sentNotif:   'sentNotif',
    Error: 'Error',
}

const  Notification = () => {
	
	const [browsingStep, setBrowsingStep] = useState(BrowsingStep.createNotif)
	const [notification, setNotification] = useState("")
	
	const format = (str, args) => {
	    var formatted = str;
	    for (var prop in args) {
	        var regexp = new RegExp('\\{' + prop + '\\}', 'gi');
	        formatted = formatted.replace(regexp, args[prop]);
	    }
	    return formatted;
	}
	
	const goToReview = (DE, DO, companyName, company) => {
        setBrowsingStep(BrowsingStep.reviewNotif)
		axios.get(
                format(window.DO_CONST['createNotif'],
                    {dataEvaluator: DE, dataOwner: DO, companyName: companyName, company: company}))
                .then(response => {
					setNotification(response.data)
                })
				.catch(error => {
                    console.error("Hay Error: ", error)
                })
	}
	
	const gotoSent = (notificationId) => {
		sendNotification(notificationId)
	}
	
	const gotoInit = (DE, DO, subject, company) => {
		setBrowsingStep(BrowsingStep.createNotif)
	}
	
	const sendNotification = (notificationId) => {
        setBrowsingStep(BrowsingStep.sentNotif)
		
		console.log("sending Notification = ", notification)
		console.log("Notification Id= ", notificationId)

		axios.get(
                format(window.DO_CONST['sendNotif'],
                    {notificationId: notificationId}))
                .then(response => {
					setNotification(response.data)
                })
				.catch(error => {
                    console.error("Hay Error: ", error)
                })
	}
	
	const renderSwitch = (browsingStep) => {
        switch (browsingStep) {
            case BrowsingStep.createNotif:
                return <CreateNotif translate={translate} goToReview={goToReview} />
            case BrowsingStep.reviewNotif:
                return <ReviewNotif translate={translate} notification={notification} 
					notificationRoot="//*[local-name() = 'EventNotification']" 
					notificationId="//*[local-name() = 'NotificationId']" 
					gotoSent={gotoSent} gotoInit={gotoInit} />
            case BrowsingStep.sentNotif:
                return <SentNotif/>
            case BrowsingStep.Error:
            default:
                return <p>Error occurred</p>
        }
    }
	
		
return <Containter>
        <Row>
            <Col><h1>Notification</h1></Col>
        </Row>
        <Row>
            <Col>
                {renderSwitch(browsingStep)}
            </Col>
        </Row>
    </Containter>
	
}



export default Notification;