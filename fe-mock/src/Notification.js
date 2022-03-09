import React, { useState, useContext } from "react";
//import { Button } from 'react-bootstrap'
import './index-it2.scss';
//import { useHistory } from 'react-router-dom';
import { Redirect } from "react-router-dom";
import axios from "axios";

import Context from "./context/context";

const  Notification = ({ translate }) => {
	
	const context = useContext(Context);
	const [redireccion, setRedireccion] = useState(false);

	const [DE, setDE] = useState("");
	const [DO, setDO] = useState("");
	//const [companyId, setCompanyId] = useState("");
	const [dataEvaluator, setDataEvaluator] = useState("");
	const [eventCatalogue, setEventCatalogue] = useState("");
	
	const [content, setContent] = useState("");
	
	/*
	const handleCompanyNameChange = e => setCompanyName(e.target.value);
	const handleCompanyIdChange = e => setCompanyId(e.target.value);
	const handleDataEvaluatorChange = e => setDataEvaluator(e.target.value);
	const handleEventCatalogueChange = e => setEventCatalogue(e.target.value);
	*/
	const handleDEChange = e => setDE(e.target.value);
	const handleDOChange = e => setDO(e.target.value);
	const handleSubjectChange = e => setDataEvaluator(e.target.value);
	const handleCompanyChange = e => setEventCatalogue(e.target.value);
	
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
	  //context.setCompanyId(companyId);
	  context.setDataEvaluator(dataEvaluator);
	  context.setEventCatalogue(eventCatalogue);
      setRedireccion(true);
	  
	  console.log("Data Evaluator = ", DE)
	  console.log("Data Owner = ", DO)
	  onCreate(DE, DO);
	  
    };

	const onCreate = () => {
		console.log("onCreate DE = ", DE)
		console.log("onCreate DO = ", DO)
		axios.get(
                format(window.DO_CONST['createNotif'],
                    {dataEvaluator: DE, dataOwner: DO}))
                .then(response => {
                    console.log(response)
                })
				.catch(error => {
                    console.error("Hay Error: ", error)
                })
    }

	return <form  onSubmit={handleFormSubmit}>
		<div class="container">
			<div id="formulario">
				<h2>Build Notification</h2>
					<label>
						<span>Data Evaluator:</span>
						<select name="de" id="de" onChange={handleDEChange}>
							<option value="" selected disabled hidden>Choose</option>
							<option value="LegalName-1428738580">LegalName-1428738580</option>
							<option value="LegalName-1428737845">LegalName-1428737845</option>
							<option value="company3">Company3</option>
							<option value="company4">Company4</option>
						</select>
					</label>
					<label>
						<span>Data Owner:</span>
						<select name="do" id="do" onChange={handleDOChange}>
							<option value="" selected disabled hidden>Choose</option>
							<option value="LPI-ID-1018251099">LPI-ID-1018251099</option>
							<option value="cid2">CompanyId2</option>
							<option value="cid3">CompanyId3</option>
							<option value="cid4">CompanyId4</option>
						</select>
					</label>
					<label>
						<span>Data subject:</span>
						<select name="subject" id="subject" onChange={handleSubjectChange}>
							<option value="" selected disabled hidden>Choose</option>
							<option value="de1">dataEvaluator1</option>
							<option value="de2">dataEvaluator2</option>
							<option value="de3">dataEvaluator3</option>
							<option value="de4">dataEvaluator4</option>
						</select>
					</label>
					<label>
						<span>Company:</span>
						<select name="company" id="company" onChange={handleCompanyChange}>
							<option value="" selected disabled hidden>Choose</option>
							<option value="ev1">Event1</option>
							<option value="ev2">Event2</option>
							<option value="ev3">Event3</option>
							<option value="ev4">Event4</option>
						</select>
					</label>
					<input type="submit" value="Create mocked notification" />
				</div>
			</div>
		</form>
	
}

export default Notification;