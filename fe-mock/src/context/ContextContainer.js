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
import React from "react";
import Context from "./context";

const defaultValues = {
  DE: "",
  setDE: DE => (defaultValues.DE = DE),
  DO: "",
  setDO: DO => (defaultValues.DO = DO),
  companyName: "",
  setCompanyName: companyName => (defaultValues.companyName = companyName),
  company: "",
  setCompany: company => (defaultValues.company = company),
  event: "",
  setEvent: event => (defaultValues.event = event)
};

const ContextContainer = props => (
  <Context.Provider value={defaultValues}>
    {props.children}
  </Context.Provider>
);

export default ContextContainer;