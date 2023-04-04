import Markdown from 'markdown-to-jsx'
import React from 'react'
import { Button, Col, Container, Row } from 'react-bootstrap'
import SubscriptionTable from './SubscriptionTable'

const PreviewSubscription = ({ evidence, evidenceRoot, evidenceIgnore, buildNotifFromSubscrip, translate, }) => {
    
    return <Container>
        <Row><Col>
            <h1>{translate('detailButton')}</h1>
        </Col></Row>
        <Row><Col>
            <Markdown>{translate('subscriptionExplanation')}</Markdown>
        </Col></Row>
        <Row><Col>
			<SubscriptionTable evidence={evidence} evidenceRoot={evidenceRoot} evidenceIgnore={evidenceIgnore} translate={translate}/>
        </Col></Row>
        
        <Row><Col>
			<p><Button variant='primary' onClick={buildNotifFromSubscrip}>{translate('buildNotification')}</Button></p>
        </Col></Row>
        

		<p><Markdown>{translate('legalText')}</Markdown></p> 
    </Container>
}

export default PreviewSubscription