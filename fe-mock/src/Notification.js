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
	//const [notificationId, setNotificationId] = useState("")
/*	
	const context = useContext(Context);
	const [redireccion, setRedireccion] = useState(false);

	const [DE, setDE] = useState("");
	const [DO, setDO] = useState("");
	const [subject, setSubject] = useState("");
	const [company, setCompany] = useState("");
	const [notification, setNotification] = useState("")
	
	const handleDEChange = e => setDE(e.target.value);
	const handleDOChange = e => setDO(e.target.value);
	const handleSubjectChange = e => setSubject(e.target.value);
	const handleCompanyChange = e => setCompany(e.target.value);
	
	const format = (str, args) => {
	    console.log("args", args)
	    var formatted = str;
	    for (var prop in args) {
	        var regexp = new RegExp('\\{' + prop + '\\}', 'gi');
	        formatted = formatted.replace(regexp, args[prop]);
	    }
	    return formatted;
	}
	
    const handleFormSubmit = e => {
      e.preventDefault();
      context.setDE(DE);
	  context.setDO(DO);
	  context.setSubject(subject);
	  context.setCompany(company);
      setRedireccion(true);
	  
	  console.log("Data Evaluator = ", DE)
	  console.log("Data Owner = ", DO)
	  console.log("subject = ", subject)
	  console.log("company = ", company)
	  onCreate();
	  
    };

	const onCreate = () => {
		console.log("onCreate DE = ", DE)
		console.log("onCreate DO = ", DO)
		console.log("onCreate subject = ", subject)
		console.log("onCreate company = ", company)
		axios.get(
                format(window.DO_CONST['createNotif'],
                    {dataEvaluator: DE, dataOwner: DO, subject: subject, company: company}))
                .then(response => {
                    console.log(response)
					setNotification(response.data)
                })
				.catch(error => {
                    console.error("Hay Error: ", error)
                })
    }
*/
	const format = (str, args) => {
	    console.log("args", args)
	    var formatted = str;
	    for (var prop in args) {
	        var regexp = new RegExp('\\{' + prop + '\\}', 'gi');
	        formatted = formatted.replace(regexp, args[prop]);
	    }
	    return formatted;
	}
	
	const goToReview = (DE, DO, companyName, company) => {
		console.log("inside goToReview")
        setBrowsingStep(BrowsingStep.reviewNotif)
		
		console.log("goToReview DE = ", DE)
		console.log("goToReview DO = ", DO)
		console.log("goToReview companyName = ", companyName)
		console.log("goToReview company = ", company)
		axios.get(
                format(window.DO_CONST['createNotif'],
                    {dataEvaluator: DE, dataOwner: DO, companyName: companyName, company: company}))
                .then(response => {
                    console.log(response)
					setNotification(response.data)
                })
				.catch(error => {
                    console.error("Hay Error: ", error)
                })
	}
	
	const gotoSent = (notificationId) => {
		console.log("gotoSent notificationId", notificationId)
		sendNotification(notificationId)
	}
	
	const gotoInit = (DE, DO, subject, company) => {
		console.log("gotoInit")
	}
	
	const sendNotification = (notificationId) => {
		console.log("inside sendNotification")
        setBrowsingStep(BrowsingStep.sentNotif)
		
		console.log("sending Notification = ", notification)
		console.log("Notification Id= ", notificationId)

		axios.get(
                format(window.DO_CONST['sendNotif'],
                    {notificationId: notificationId}))
                .then(response => {
                    console.log(response)
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