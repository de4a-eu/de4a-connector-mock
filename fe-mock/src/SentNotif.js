import React from "react";
import { Button } from 'react-bootstrap'
import './index-it2.scss';
import { useHistory } from 'react-router-dom';

const  SentNotif = ({ translate, }) => {
	
	let history = useHistory();
	const redirect = () => {
    	history.push('/notification')
	}

	return (
		<div class="container">
			<div class="container">
				<h2>Notification sent</h2>
			</div>
			<div class="container">
				<p>The notification was sent to the subscriber</p>
			</div>
			<div class="container">
				<Button onClick={redirect} variant='primary'>View notification</Button>
			</div>
		</div>
	);
}

export default SentNotif;