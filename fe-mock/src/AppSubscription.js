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
import React, {useEffect, useState, useRef, useContext} from "react";
import {useLocation} from "react-router-dom"
import Containter from 'react-bootstrap/Container'
import axios from "axios";
import ClipLoader from 'react-spinners/ClipLoader'
import * as SockJS from 'sockjs-client'
import { Client } from "@stomp/stompjs";

import translate from 'translate-js'
import trans_en from './translate/en'

import PreviewSubscription from "./PreviewSubscription"
import RequestSubscriptionList from "./RequestSubscriptionList";
import ReviewNotif from "./ReviewNotif"
import {Col, Row} from "react-bootstrap";
import Context from "./context/context";

translate.add(trans_en, 'en')
translate.whenUndefined = (key, locale) => {
    return `${key}:undefined:${locale}`
}

const EvidenceStatus = {
    Accepted: 'Accepted',
    Rejected: 'Rejected',
    Processing: 'Processing',
    Unanswered: 'Unanswered',
    FetchingEvidence: 'FetchingEvidence',
    NoSuchEvidence: 'NoSuchEvidence',
    NoEvidenceChosen: 'NoEvidenceChosen',
    Error: 'Error',
	Build: 'Build,'
}

const format = (str, args) => {
    console.log("args", args)
    var formatted = str;
    for (var prop in args) {
        var regexp = new RegExp('\\{' + prop + '\\}', 'gi');
        formatted = formatted.replace(regexp, args[prop]);
    }
    return formatted;
}

const AppSubscription = () => {

	const context = useContext(Context);
	const [notifId, setNotifId] = useState("")
    const [evidence, setEvidence] = useState("")
    const [evidenceStatus, setEvidenceStatus] = useState(EvidenceStatus.FetchingEvidence)
    const [evidencesList, setEvidencesList] = useState([])
    const [requestId, setRequestId] = useState("")
	const [notification, setNotification] = useState("")

    const evidencesListRef = useRef(evidencesList)
    const setEvidencesListRef = useRef(setEvidencesList)

    const search = useLocation().search
    const urlParams = new URLSearchParams(search)
    const requestIdParam = urlParams.get('requestId')
    const pathName = useLocation().pathname

	const gotoSent = (notificationId) => {
		sendNotification(notificationId)
	}
	
	const gotoInit = (DE, DO, subject, company) => {
		console.log("gotoInit")
	}
	
	const sendNotification = (notificationId) => {
        setEvidenceStatus(EvidenceStatus.Accepted)
		
		console.log("sending Notification = ", notification)
		console.log("Notification Id= ", notificationId)

		axios.get(
                format(window.DO_CONST['sendNotif'],
                    {notificationId: notificationId}))
                .then(response => {
					setNotification(response.data)
                })
				.catch(error => {
                    console.error("Hay Error: ", error)
                })
	}
	
	const buildNotifFromSubscrip = () => {
		setEvidenceStatus(EvidenceStatus.Build)
		axios.get(
                format(window.DO_CONST['buildNotifFromSubscrip'],
                    {requestId: requestId}))
                .then(response => {
					setNotification(response.data)
					setRequestId(requestId)
                })
				.catch(error => {
                    console.error("Hay Error: ", error)
                })
	}
	
    /*const acceptEvidence = () => {
        setEvidenceStatus(EvidenceStatus.Processing)
        axios.get(format(window.DO_CONST['previewAcceptEndpoint'], {requestId: requestId}))
            .then(response => {
                let locationUrl = response.data
                setEvidenceStatus(EvidenceStatus.Accepted)
                if (locationUrl === "") {
                    console.error("no back url provided")
                    setEvidenceStatus(EvidenceStatus.Error)
                    return
                }
                window.location.replace(locationUrl);
            })
            .catch(error => {
                console.error("error: ", error)
                setEvidenceStatus(EvidenceStatus.Error)
            })
    }

    const rejectEvidence = () => {
        setEvidenceStatus(EvidenceStatus.Processing)
        axios.get(format(window.DO_CONST['previewRejectEndpoint'], {requestId: requestId}))
            .then(response => {
                let locationUrl = response.data
                setEvidenceStatus(EvidenceStatus.Rejected)
                if (locationUrl === "" ) {
                    console.error("no back url provided")
                    setEvidenceStatus(EvidenceStatus.Error)
                    return
                }
                window.location.replace(locationUrl);
            })
            .catch(error => {
                setEvidenceStatus(EvidenceStatus.Error)
                console.log(error)
            })
    }*/

    const fetchEvidence = (requestId) => {
        if (requestId && requestId !== "") {
            axios.get(
                format(window.DO_CONST['previewSubscriptionRequest'],
                    {requestId: requestId}))
                .then(response => {
                    setEvidence(response.data)
                    setRequestId(requestId)
                    setEvidenceStatus(EvidenceStatus.Unanswered)
                })
                .catch(error => {
                    setEvidenceStatus(EvidenceStatus.NoSuchEvidence)
                    setRequestId(requestId)
                    console.error(error)
                })
        } else {
            setEvidenceStatus(EvidenceStatus.NoEvidenceChosen)
        }
    }

    const fetchPreviewEvidences = () =>
        axios.get(window.DO_CONST['previewSubscriptionIdsEndpoint'])
            .then(response => {
                setEvidencesList(response.data)
            })

    const updatePreviewEvidences = (message) => {
        console.log("message: ", message)
        console.log("evidencesList: ", evidencesListRef.current)
        switch (message.action) {
            case("RM"): {
                console.log("rm")
                setEvidencesListRef.current(evidencesListRef.current
                    .filter(value => value !== message.payload))
                break
            }
            case("ADD"): {
                console.log("add")
                if (!evidencesListRef.current.includes(message.payload)) {
                    console.log("inner")
                    setEvidencesListRef.current([...evidencesListRef.current, message.payload])
                }
                break
            }
            default:
                break
        }
        console.log("evidencesList: ", evidencesList.current)
    }

    const newMessage = (message) => {
            console.log("message: ", message.body)
            updatePreviewEvidences(JSON.parse(message.body))
        }

    const client = new Client();
    client.webSocketFactory = () => {
        return new SockJS(`${window.DO_CONST['previewWebsocket']}`)
    }
    client.debug = (str) => {
        console.log(str)
    }
    client.onConnect = function (frame) {
        client.subscribe(`${pathName.replace(/index$/, window.DO_CONST['previewMessages'])}`,
                newMessage)
    };

    client.onStompError = function (frame) {
        console.log('Broker reported error: ' + frame.headers['message']);
        console.log('Additional details: ' + frame.body);
    };

    const subscribeRequestId = () => {
        client.activate()
    }

    useEffect(() => {
        fetchEvidence(requestIdParam)
        fetchPreviewEvidences()
        subscribeRequestId()
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    useEffect(() => {
        evidencesListRef.current = evidencesList
        setEvidencesListRef.current = setEvidencesList
    }, [evidenceStatus, evidencesList])

    const renderSwitch = (evidenceStatus) => {
        switch (evidenceStatus) {
            case EvidenceStatus.FetchingEvidence:
                return <ClipLoader/>
            case EvidenceStatus.NoEvidenceChosen:
                return <RequestSubscriptionList requestIds={evidencesList}
                                    onSelect={fetchEvidence}
                                    translate={translate}/>
            case EvidenceStatus.NoSuchEvidence:
                return <p>no subscription found for request with id: {requestId}</p>
            case EvidenceStatus.Unanswered:
                return <PreviewSubscription evidence={evidence} evidenceRoot="//*[local-name() = 'ResponseEventSubscriptionItem']"
                                evidenceIgnore={[]} buildNotifFromSubscrip={buildNotifFromSubscrip} 
                                translate={translate}/>
            case EvidenceStatus.Accepted:
                return <p>Notification sent</p>
            case EvidenceStatus.Rejected:
                return <p>Evidence rejected</p>
            case EvidenceStatus.Processing:
                return <Row>
                    <Col>
                        <ClipLoader/>
                    </Col>
                    <Col>
                        <p>Processing answer</p>
                    </Col>
                </Row>
			case EvidenceStatus.Build:
				console.log("case requestId ="+requestId)
				
				return <ReviewNotif translate={translate} notification={notification} 
					notificationRoot="//*[local-name() = 'EventNotification']" 
					requestId={requestId} 
					gotoSent={gotoSent} gotoInit={gotoInit} />
            case EvidenceStatus.Error:
            default:
                return <p>Error occurred</p>
        }
    }

    return <Containter>
        <Row>
            <Col><h1>{translate('doTitle')}</h1></Col>
        </Row>
        <Row>
            <Col>
                {renderSwitch(evidenceStatus)}
            </Col>
        </Row>
    </Containter>
}

export default AppSubscription;
