import * as ActionTypes from '../constants/ActionTypes';
import { take, put, call } from 'redux-saga/effects';
import * as Api from '../services/Apis';
import * as AuthAction from '../actions/AuthActions';
import { normalize } from 'normalizr';
import schema from '../schemas';

export function* handlePostClientRequest() {
    // run the daemon
    while (true) {
        try {
            // wait for a create client request
            const {payload} = yield take(ActionTypes.CREATE_CLIENT);
            // call the api
            const data = yield call(Api.Post, '/api/client', {
                name: payload.name
            });
            // call the success
            yield put(AuthAction.createClientFulfilled(normalize(data.body, schema.client)));
        } catch (e) {
            // call the error
            yield put(AuthAction.createClientRejected(e));
        }
    }
}

export function* handlePostUserRequest() {
    // run the daemon
    while (true) {
        try {
            // wait for a create user request
            const {payload} = yield take(ActionTypes.CREATE_USER);
            // call the api
            const data = yield call(Api.Post, '/api/account/register', {
                userName: payload.userName,
                password: payload.password,
                role: payload.role,
                clientName: payload.clientName,
                clientSecret: payload.clientSecret,
            });
            // call the success
            yield put(AuthAction.createUserFulfilled(normalize(data.body, schema.user)));
        } catch (e) {
            // call the error
            yield put(AuthAction.createUserRejected(e));
        }
    }
}