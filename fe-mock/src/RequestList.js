import {Button, Col, Row} from "react-bootstrap";
import { useState } from "react";


const RequestList = ({ requestIds, onSelect, translate }) => {

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
                <h3>{translate('requestListTitle')}</h3>
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
                <Button type="submit">{translate('previewButton')}</Button>
            </Col>
        </Row>
    </form>

}

export default RequestList