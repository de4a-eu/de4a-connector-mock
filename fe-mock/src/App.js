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

String.prototype.format = function () {
  var formatted = this;
  for (var prop in arguments[0]) {
    var regexp = new RegExp('\\{' + prop + '\\}', 'gi');
    formatted = formatted.replace(regexp, arguments[0][prop]);
  }
  return formatted;
};

const App = () => {
  
  const [evidence, setEvidence] = useState({})
  const [evidenceStatus, setEvidenceStatus] = useState(EvidenceStatus.FetchingEvidence)

  const search = useLocation().search
  const requestId = new URLSearchParams(search).get('requestId')
  
  const acceptEvidence = () => axios.get(DO_CONST['previewAcceptEndpoint'].format({requestId: requestId}))
      .then(response => setEvidenceStatus(EvidenceStatus.Accepted))
      .catch(error => {
        setEvidenceStatus(EvidenceStatus.Error)
        console.log(error)
      })

  const rejectEvidence = () => axios.get(DO_CONST['previewRejectEndpoint'].format({requestId: requestId}))
      .then(response => setEvidenceStatus(EvidenceStatus.Rejected))
      .catch(error => {
        setEvidenceStatus(EvidenceStatus.Error)
        console.log(error)
      })
  
  const fetchEvidence = () => axios.get(DO_CONST['previewEndpoint'].format({requestId: requestId}))
      .then(response => {
        setEvidence(response.data)
        setEvidenceStatus(EvidenceStatus.Unanswered)
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
    console.log("doConfig: ", DO_CONST)
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
