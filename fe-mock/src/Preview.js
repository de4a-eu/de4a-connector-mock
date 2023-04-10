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
import Markdown from 'markdown-to-jsx'
import React from 'react'
import { Button, Col, Container, Row } from 'react-bootstrap'
import EvidenceTable from './EvidenceTable'

const Preview = ({ evidence, evidenceRoot, evidenceIgnore, acceptEvidence, rejectEvidence, translate, }) => {
    
    return <Container>
        <Row><Col>
            <h1>{translate('previewTitle')}</h1>
        </Col></Row>
        <Row><Col>
            <Markdown>{translate('explanation')}</Markdown>
        </Col></Row>
        <Row><Col>
            <EvidenceTable evidence={evidence} evidenceRoot={evidenceRoot} evidenceIgnore={evidenceIgnore} translate={translate}/>
        </Col></Row>
        <Row>
            <Col><Button variant='secondary' onClick={rejectEvidence}>{translate('rejectButton')}</Button></Col>
            <Col><Button variant='primary' onClick={acceptEvidence}>{translate('acceptButton')}</Button></Col>
        </Row>
        <Row><Col>
            <Markdown>{translate('legalText')}</Markdown> 
        </Col></Row>
        
    </Container>
}

export default Preview