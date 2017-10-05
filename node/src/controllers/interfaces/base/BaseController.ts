import { ReadController } from '../common/ReadController';
import { WriteController } from '../common/WriteController';

export interface BaseController extends ReadController, WriteController { }