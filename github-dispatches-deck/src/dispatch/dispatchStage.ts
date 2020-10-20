import { IStageTypeConfig } from '@spinnaker/core';
import { DispatchExecutionDetails } from './DispatchExecutionDetails';

export const dispatchStage: IStageTypeConfig = {
  label: 'Github Dispatch',
  description: 'Makes a Demo Github Dispatch',
  key: 'githubDispatch',
  executionDetailsSections: [DispatchExecutionDetails],
  strategy: true
};
