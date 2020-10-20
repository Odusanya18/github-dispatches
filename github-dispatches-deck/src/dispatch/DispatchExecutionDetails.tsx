import React from 'react';

import {
  ExecutionDetailsSection,
  IExecutionDetailsSectionProps,
  StageFailureMessage,
  StageExecutionLogs,
} from '@spinnaker/core';

export function DispatchExecutionDetails({
  stage: { context = {} },
  stage,
  name,
  current,
}: IExecutionDetailsSectionProps) {
  return (
    <ExecutionDetailsSection name={name} current={current}>
      <dl className="dl-narrow dl-horizontal">
        <dt>Dispatch Response</dt>
        <dd>{context.dispatch}</dd>
      </dl>
      <StageFailureMessage stage={stage} message={stage.failureMessage} />
      <StageExecutionLogs stage={stage} />
    </ExecutionDetailsSection>
  );
}

// TODO: refactor this to not use namespace
// eslint-disable-next-line
export namespace DispatchExecutionDetails {
  export const title = 'dispatchConfig';
}
