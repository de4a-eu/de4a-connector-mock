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
import React from 'react';
import ReactDOM from 'react-dom';
import {BrowserRouter as Router, Switch, Route} from "react-router-dom";
import 'bootstrap'
import './index.scss';
import App from './App';
import AppSubscription from './AppSubscription';
import Notification from './Notification';
import reportWebVitals from './reportWebVitals';

import ContextContainer from "./context/ContextContainer";

ReactDOM.render(
  <React.StrictMode>
	<ContextContainer>	
      <Router basename={'/de4a-mock-connector'}>
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
        </Switch>
      </Router>
	</ContextContainer>
  </React.StrictMode>,
  document.getElementById('do-root')

);
//<Router basename={'/de4a-mock-connector'}> for playground
// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();