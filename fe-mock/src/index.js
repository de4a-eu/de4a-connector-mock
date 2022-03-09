import React from 'react';
import ReactDOM from 'react-dom';
import {BrowserRouter, Switch, Route} from "react-router-dom";
import 'bootstrap'
import './index.scss';
import App from './App';
import AppSubscription from './AppSubscription';
import Notification from './Notification';
import NotifSummary from './NotifSummary';
import NotifSent from './NotifSent';
import reportWebVitals from './reportWebVitals';

import ContextContainer from "./context/ContextContainer";

ReactDOM.render(
  <React.StrictMode>
	<ContextContainer>	
      <BrowserRouter>
        <Switch>
            <Route path='/do1/preview/index'>
                <App />
            </Route>
            <Route path='/do1/subscription/eventSubscription'>
                <AppSubscription />
            </Route>
			<Route path='/notification'>
                <Notification />
            </Route>
			<Route path='/notifsummary'>
                <NotifSummary />
            </Route>
			<Route path='/notifsent'>
                <NotifSent />
            </Route>
        </Switch>
      </BrowserRouter>
	</ContextContainer>
  </React.StrictMode>,
  document.getElementById('do-root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();