import React from "react";

const defaultValues = {
  DE: "",
  setDE: () => {},
  DO: "",
  setDO: () => {},
  dataEvaluator: "",
  setDataEvaluator: () => {},
  eventCatalogue: "",
  setEventCatalogue: () => {}
};
const context = React.createContext(defaultValues);
export default context;