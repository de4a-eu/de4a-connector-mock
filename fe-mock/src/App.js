import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom"
import Containter from 'react-bootstrap/Container'
import axios from "axios";
import ClipLoader from 'react-spinners/ClipLoader'
import translate from 'translate-js'
import trans_en from './translate/en'

import Preview from "./Preview"

translate.add(trans_en, 'en')
translate.whenUndefined = (key, locale) => {
  return `${key}:undefined:${locale}`
}

const EvidenceStatus = {
  Accepted: 'Accepted',
  Rejected: 'Rejected',
  Unanswered: 'Unanswered',
  FetchingEvidence: 'FetchingEvidence',
  NoSuchEvidence: 'NoSuchEvidence',
  Error: 'Error',
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

const App = () => {
  
  const [evidence, setEvidence] = useState("")
  const [evidenceStatus, setEvidenceStatus] = useState(EvidenceStatus.FetchingEvidence)

  const search = useLocation().search
  const urlParams = new URLSearchParams(search)
  const requestId = urlParams.get('requestId')
  const backUrl = urlParams.get('backUrl')
  
  const acceptEvidence = () => axios.get(format(window.DO_CONST['previewAcceptEndpoint'], {requestId: requestId}))
      .then(response => {
          setEvidenceStatus(EvidenceStatus.Accepted)
          const url = new URL(backUrl)
          url.searchParams.append('accept', 'true')
          window.location.replace(url.toString());
      })
      .catch(error => {
        setEvidenceStatus(EvidenceStatus.Error)
      })

  const rejectEvidence = () => axios.get(format(window.DO_CONST['previewRejectEndpoint'], {requestId: requestId}))
      .then(response => {
          setEvidenceStatus(EvidenceStatus.Rejected)
          const url = new URL(backUrl)
          url.searchParams.append('accept', 'false')
          window.location.replace(url.toString());
      })
      .catch(error => {
        setEvidenceStatus(EvidenceStatus.Error)
        console.log(error)
      })
  
  const fetchEvidence = () => axios.get(format(window.DO_CONST['previewEndpoint'], {requestId: requestId}))
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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])
  
  useEffect(() => {
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
        return <Preview evidence={evidence} evidenceRoot="//*[local-name() = 'CanonicalEvidence']" evidenceIgnore={[]} acceptEvidence={acceptEvidence} rejectEvidence={rejectEvidence} translate={translate}/>
      case EvidenceStatus.Accepted:
        return <p>Evidence accepted</p>
      case EvidenceStatus.Rejected:
        return <p>Evidence rejected</p>
      case EvidenceStatus.Error:
      default:
        return <p>Error occured</p>
    }
  }
  
  return <Containter>
    {renderSwitch(evidenceStatus)}
  </Containter>
}

export default App;
