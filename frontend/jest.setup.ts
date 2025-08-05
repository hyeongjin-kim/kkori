import '@testing-library/jest-dom';
import { TextEncoder, TextDecoder } from 'util';

Object.assign(global, {
  TextEncoder,
  TextDecoder,
});

global.MediaStream = class {
  getTracks() {
    return [];
  }
} as any;

HTMLFormElement.prototype.requestSubmit = function (submitter?: HTMLElement) {
  const submitEvent = new Event('submit', {
    bubbles: true,
    cancelable: true,
  });
  this.dispatchEvent(submitEvent);
};

Element.prototype.scrollTo = () => {};
