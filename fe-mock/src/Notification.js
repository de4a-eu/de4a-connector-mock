import React from "react";

function Notification() {
	return (
		<div class="container">
			<div id="formulario">
				<h2>Build Notification</h2>
				<form>
					<label>
						<span>Company name:</span>
						<select name="companyName" id="companyName">
							<option value="company1">Company1</option>
							<option value="company2">Company2</option>
							<option value="company3">Company3</option>
							<option value="company4">Company4</option>
						</select>
					</label>
					<label>
						<span>Company id:</span>
						<select name="companyId" id="companyId">
							<option value="cid1">CompanyId1</option>
							<option value="cid2">CompanyId2</option>
							<option value="cid3">CompanyId3</option>
							<option value="cid4">CompanyId4</option>
						</select>
					</label>
					<label>
						<span>Data evaulator:</span>
						<select name="dataEvaluator" id="dataEvaluator">
							<option value="de1">dataEvaluator1</option>
							<option value="de2">dataEvaluator2</option>
							<option value="de3">dataEvaluator3</option>
							<option value="de4">dataEvaluator4</option>
						</select>
					</label>
					<label>
						<span>Event catalog:</span>
						<select name="eventCatalog" id="eventCatalog">
							<option value="ev1">Event1</option>
							<option value="ev2">Event2</option>
							<option value="ev3">Event3</option>
							<option value="ev4">Event4</option>
						</select>
					</label>
					<input type="submit" value="Continue" />
				</form>
			</div>
		</div>
	);
}

export default Notification;