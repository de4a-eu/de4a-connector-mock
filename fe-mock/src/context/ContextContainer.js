import React from "react";
import Context from "./context";

const defaultValues = {
  DE: "",
  setDE: DE => (defaultValues.DE = DE),
  DO: "",
  setDO: DO => (defaultValues.DO = DO),
  dataEvaluator: "",
  setDataEvaluator: dataEvaluator => (defaultValues.dataEvaluator = dataEvaluator),
  eventCatalogue: "",
  setEventCatalogue: eventCatalogue => (defaultValues.eventCatalogue = eventCatalogue)
};

const ContextContainer = props => (
  <Context.Provider value={defaultValues}>
    {props.children}
  </Context.Provider>
);

export default ContextContainer;