import * as ActionTypes from '../constants/ActionTypes';

export function createClient(name) {
    return {
        type: ActionTypes.CREATE_CLIENT,
        payload: {
            name: name
        }
    }
}

export function createClientFulfilled(data) {
    return {
        type: ActionTypes.CREATE_CLIENT_FULFILLED,
        payload: data
    };
}

export function createClientRejected(error) {
    return {
        type: ActionTypes.CREATE_CLIENT_REJECTED,
        payload: error
    };
}