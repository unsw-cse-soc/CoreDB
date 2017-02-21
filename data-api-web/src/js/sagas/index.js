import { fork } from 'redux-saga/effects';
import * as AuthSagas from './AuthSagas';

export default function* rootSaga() {
    yield [
        fork(AuthSagas.handlePostClientRequest),
        fork(AuthSagas.handlePostUserRequest),
        fork(AuthSagas.handleLoginRequest)
    ];
}