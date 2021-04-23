import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom"
import Containter from 'react-bootstrap/Container'
import axios from "axios";
import ClipLoader from 'react-spinners/ClipLoader'

import Preview from "./Preview"

const EvidenceStatus = {
  Accepted: 'Accepted',
  Rejected: 'Rejected',
  Unanswered: 'Unanswered',
  FetchingEvidence: 'FetchingEvidence',
  NoSuchEvidence: 'NoSuchEvidence',
  Error: 'Error',
}


const App = () => {
  
  const [evidence, setEvidence] = useState({})
  const [evidenceStatus, setEvidenceStatus] = useState(EvidenceStatus.Unanswered)

  const search = useLocation().search
  const requestId = new URLSearchParams(search).get('requestId')
  
  const acceptEvidence = () => axios.get('evidence/{requestId}/accept')
      .then(response => setEvidenceStatus(EvidenceStatus.Accepted))
      .catch(error => {
        setEvidenceStatus(EvidenceStatus.Error)
        console.log(error)
      })

  const rejectEvidence = () => axios.get('evidence/{requestId}/reject')
      .then(response => setEvidenceStatus(EvidenceStatus.Rejected))
      .catch(error => {
        setEvidenceStatus(EvidenceStatus.Error)
        console.log(error)
      })
  
  const fetchEvidence = () => axios.get('evidence/{requestId}')
      .then(response => {
        setEvidence(response.data)
        console.log(response)
      })
      .catch(error => {
        setEvidenceStatus(EvidenceStatus.NoSuchEvidence)
        console.error(error)
      })
  
  useEffect(() => {
    fetchEvidence();
  }, [])
  
  useEffect(() => {
    console.log("reloading")
  }, [evidenceStatus])

  const renderSwitch = (evidenceStatus) => {
    switch (evidenceStatus) {
      case EvidenceStatus.FetchingEvidence:
        return <ClipLoader/>
      case EvidenceStatus.NoSuchEvidence:
        if (!requestId || requestId === "")
          return <p>no request id given</p>
        else
          return <p>no evidence found for request with id: {requestId}</p>
      case EvidenceStatus.Unanswered:
        return <Preview evidence={evidence} acceptEvidence={acceptEvidence} rejectEvidence={rejectEvidence} />
      case EvidenceStatus.Accepted:
        return <p>Evidence accepted</p>
      case EvidenceStatus.Rejected:
        return <p>Evidence rejected</p>
      case EvidenceStatus.Error:
        return <p>Error occured</p>
    }
  }
  
  return <Containter>
    {renderSwitch(evidenceStatus)}
  </Containter>
}

export default App;
