import React, { useContext } from "react";
import { Button } from 'react-bootstrap'
import './index-it2.scss';
import { useHistory } from 'react-router-dom';

import Context from "./context/context";

const  NotifSummary = ({ translate, }) => {
	const context = useContext(Context);
	
	
	let history = useHistory();
	const redirectSend = () => {
    	history.push('/notifsent')
	}
	
	const redirectFinish = () => {
    	history.push('/notifsent')
	}
	
	//const { state } = this.props.location//.company

	return (
		<div class="container">
			<h2>Notification</h2>
			<div class="container">
				 <p>Company: {context.companyName}</p>
			</div>
			<div class="container">
				 <p>Company Id: {context.companyId}</p>
			</div>
			<div class="container">
				 <p>Data Evaluator: {context.dataEvaluator}</p>
			</div>
			<div class="container">
				 <p>Event Catalogue: {context.eventCatalogue}</p>
			</div>
			<div class="container">
			&lt;EventNotificationType&gt;<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;NotificationId&gt;&lt;/NotificationId&gt;<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;SpecificationId&gt;&lt;/SpecificationId&gt;<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;TimeStamp&gt;&lt;/TimeStamp&gt;<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;DataEvaluator&gt;{context.dataEvaluator}&lt;/DataEvaluator&gt;<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;DataOwner&gt;&lt;/DataOwner&gt;<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;EventNotificationItem&gt;<br/>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;NotificationItemId&gt;&lt;/NotificationItemId&gt;<br/>
			</div>
				<div class="container">
			
			<Button onClick={redirectSend} variant='primary'>Send notification</Button>
			<Button onClick={redirectFinish} variant='primary'>Finish</Button>
			</div>
		</div>
	);
}

export default NotifSummary;