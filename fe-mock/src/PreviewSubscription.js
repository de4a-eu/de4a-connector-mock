import Markdown from 'markdown-to-jsx'
import React from 'react'
import { Button, Col, Container, Row } from 'react-bootstrap'
import SubscriptionTable from './SubscriptionTable'

const PreviewSubscription = ({ evidence, evidenceRoot, evidenceIgnore, acceptEvidence, rejectEvidence, translate, }) => {
    
    return <Container>
        <Row><Col>
            <h1>{translate('previewTitle')}</h1>
        </Col></Row>
        <Row><Col>
            <Markdown>{translate('subscriptionExplanation')}</Markdown>
        </Col></Row>
        <Row><Col>
			<SubscriptionTable evidence={evidence} evidenceRoot={evidenceRoot} evidenceIgnore={evidenceIgnore} translate={translate}/>
        </Col></Row>
        <Row>
            <Col><Button variant='primary' onClick={rejectEvidence}>{translate('sendNotification')}</Button></Col>
        </Row>
        <Row><Col>
            <Markdown>{translate('legalText')}</Markdown> 
        </Col></Row>
        
    </Container>
}

export default PreviewSubscription