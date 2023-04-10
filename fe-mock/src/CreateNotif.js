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
import React, { useState, useContext } from "react";
import './index-it2.scss';
import {Button } from "react-bootstrap";

import Context from "./context/context";

const  CreateNotif = ({ translate, goToReview }) => {
	
	const context = useContext(Context);
	const [redireccion, setRedireccion] = useState(false);

	const [DE, setDE] = useState("");
	const [DO, setDO] = useState("");
	const [companyName, setCompanyName] = useState("");
	const [company, setCompany] = useState("");
	const [event, setEvent] = useState("")
	
	const handleDEChange = e => setDE(e.target.value);
	const handleDOChange = e => setDO(e.target.value);
	const handleCNameChange = e => setCompanyName(e.target.value);
	const handleCIdChange = e => setCompany(e.target.value);
	const handleEventChange = e => setEvent(e.target.value);
	
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
	  context.setCompanyName(companyName);
	  context.setCompany(company);
	  context.setEvent(event);
      setRedireccion(true);
	  onCreate();
	  
    };

	const onCreate = () => {
		
		goToReview(DE, DO, companyName, company, event)
		
    }

	return <form  onSubmit={handleFormSubmit}>
		<div class="container">
			<div id="formulario">
				<h2>Build Notification</h2>
					<label>
						<span>Data Evaluator:</span>
						<select class="select" name="de" id="de" onChange={handleDEChange}>
							<option value="" selected disabled hidden>Choose</option>
							<option value="iso6523-actorid-upis::9999:nl000000024-mock-it2">iso6523-actorid-upis::9999:nl000000024-mock-it2</option>
							<option value="iso6523-actorid-upis::9999:se000000013-mock-it2">iso6523-actorid-upis::9999:se000000013-mock-it2</option>
							<option value="iso6523-actorid-upis::9999:ro000000006-mock-it2">iso6523-actorid-upis::9999:ro000000006-mock-it2</option>
							<option value="iso6523-actorid-upis::9999:at000000271-mock-it2">iso6523-actorid-upis::9999:at000000271-mock-it2</option>
						</select>
					</label>
					<label>
						<span>Data Owner:</span>
						<select name="do" id="do" onChange={handleDOChange}>
							<option value="" selected disabled hidden>Choose</option>
							<option value="iso6523-actorid-upis::9999:nl000000024-mock-it2">iso6523-actorid-upis::9999:nl000000024-mock-it2</option>
							<option value="iso6523-actorid-upis::9999:se000000013-mock-it2">iso6523-actorid-upis::9999:se000000013-mock-it2</option>
							<option value="iso6523-actorid-upis::9999:ro000000006-mock-it2">iso6523-actorid-upis::9999:ro000000006-mock-it2</option>
							<option value="iso6523-actorid-upis::9999:at000000271-mock-it2">iso6523-actorid-upis::9999:at000000271-mock-it2</option>
						</select>
					</label>
					<label>
						<span>Company Name:</span>
						<select name="company" id="company" onChange={handleCNameChange}>
							<option value="" selected disabled hidden>Choose</option>
							<option value="Company 1">Company 1</option>
							<option value="Company 2">Company 2</option>
							<option value="Company 3">Company 3</option>
							<option value="Company 4">Company 4</option>
						</select>
					</label>
					<label>
						<span>Company id:</span>
						<select name="companyId" id="companyId" onChange={handleCIdChange}>
							<option value="" selected disabled hidden>Choose</option>
							<option value="CompanyId1">CompanyId1</option>
							<option value="CompanyId2">CompanyId2</option>
							<option value="CompanyId3">CompanyId3</option>
							<option value="CompanyId4">CompanyId4</option>
						</select>
					</label>
					<label>
						<span>Event Catalogue:</span>
						<select name="event" id="event" onChange={handleEventChange}>
							<option value="" selected disabled hidden>Choose</option>
							<option value="LPI-ID-1219045100">LPI-ID-1219045100</option>
							<option value="LPI-ID-9871461955">LPI-ID-9871461955</option>
							<option value="LPI-ID-1548892894">LPI-ID-1548892894</option>
							<option value="LPI-ID-5654545894">LPI-ID-5654545894</option>
						</select>
					</label>
					
					<Button type="submit">{translate('buildNotification')}</Button>
				</div>
			</div>
		</form>
	
}

export default CreateNotif;