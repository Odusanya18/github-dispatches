import { IDeckPlugin } from '@spinnaker/core';
import { dispatchStage } from './dispatch/dispatchStage';

export const plugin: IDeckPlugin = {
  stages: [dispatchStage],
};
