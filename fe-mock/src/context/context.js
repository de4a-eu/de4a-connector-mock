import React from "react";

const defaultValues = {
  DE: "",
  setDE: () => {},
  DO: "",
  setDO: () => {},
  companyName: "",
  setCompanyName: () => {},
  company: "",
  setCompany: () => {},
  event: "",
  setEvent: () => {}
};
const context = React.createContext(defaultValues);
export default context;