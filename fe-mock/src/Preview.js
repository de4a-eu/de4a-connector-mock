import React from 'react'
import { Button, Col, Container, Row } from 'react-bootstrap'
import EvidenceTable from './EvidenceTable'

const Preview = ({ evidence, evidenceRoot, evidenceIgnore, acceptEvidence, rejectEvidence, translate, }) => {
    
    return <Container>
        <Row><Col>
            <h1>{translate('previewTitle')}</h1>
        </Col></Row>
        <Row><Col>
            <p>{translate('explanation')}</p>
        </Col></Row>
        <Row><Col>
            <EvidenceTable evidence={evidence} evidenceRoot={evidenceRoot} evidenceIgnore={evidenceIgnore} translate={translate}/>
        </Col></Row>
        <Row><Col>
            <p> {translate('legalText')} </p>
        </Col></Row>
        <Row>
            <Col><Button variant='secondary' onClick={rejectEvidence}>{translate('rejectButton')}</Button></Col>
            <Col><Button variant='primary' onClick={acceptEvidence}>{translate('acceptButton')}</Button></Col>
        </Row>
        
    </Container>
}

export default Preview