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
import {Button, Col, Row} from "react-bootstrap";
import { useState } from "react";


const RequestSubscriptionList = ({ requestIds, onSelect, translate }) => {

    const [selectedRequest, setSelectedRequest] = useState("")

    const onSubmit = (e) => {
        e.preventDefault()
        console.log(`submitting with requestId ${selectedRequest} `,e)
        onSelect(selectedRequest)
    }

    const select = (e) => {
        console.log(`selected: ${e.target.value}`)
        setSelectedRequest(e.target.value)
    }

    return <form onSubmit={onSubmit}>
        <Row>
            <Col>
                <h3>{translate('requestSubscriptionListTitle')}</h3>
            </Col>
        </Row>
        <Row>
            <Col>
                <select className="form-control" size="10" onChange={select}>
                    {requestIds.map((requestId, i) => <option name="reqid" value={requestId} key={i}>{requestId}</option> )}
                 </select>
            </Col>
        </Row>
        <Row>
            <Col md={{span: 4, offset: 8}}>
                <Button type="submit">{translate('detailButton')}</Button>
            </Col>
        </Row>
    </form>

}

export default RequestSubscriptionList