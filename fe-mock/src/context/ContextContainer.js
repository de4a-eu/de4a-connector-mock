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