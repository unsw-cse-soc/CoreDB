import isNil from 'lodash/isNil';
import Immutable from 'immutable';
import User from '../models/User';
import UserMap from '../models/UserMap';
import Client from '../models/Client';
import ClientMap from '../models/ClientMap';
import * as ActionTypes from '../constants/ActionTypes';

const initState = Immutable.Map({
    // token: new Token({
    //     accessToken: isNil(sessionStorage.getItem('auth')) ? null : JSON.parse(sessionStorage.getItem('auth')).accessToken,
    //     refreshToken: isNil(sessionStorage.getItem('auth')) ? null : JSON.parse(sessionStorage.getItem('auth')).refreshToken,
    //     expiresIn: isNil(sessionStorage.getItem('auth')) ? null : JSON.parse(sessionStorage.getItem('auth')).expiresIn,
    // }),
    fetching: false,
    fetched: false,
    error: null,
    users: new UserMap(),
    clients: new ClientMap(),
    //represents the logged user
    user: new User({
        id: isNil(sessionStorage.getItem('auth')) ? null : JSON.parse(JSON.parse(sessionStorage.getItem('auth')).user).id,
        name: isNil(sessionStorage.getItem('auth')) ? '' : JSON.parse(JSON.parse(sessionStorage.getItem('auth')).user).name,
        lastName: isNil(sessionStorage.getItem('auth')) ? '' : JSON.parse(JSON.parse(sessionStorage.getItem('auth')).user).lastName,
        email: isNil(sessionStorage.getItem('auth')) ? '' : JSON.parse(JSON.parse(sessionStorage.getItem('auth')).user).email,
        createdAt: isNil(sessionStorage.getItem('auth')) ? null : JSON.parse(JSON.parse(sessionStorage.getItem('auth')).user).createdAt,
        updatedAt: isNil(sessionStorage.getItem('auth')) ? null : JSON.parse(JSON.parse(sessionStorage.getItem('auth')).user).updatedAt,
    })
})

const mergeUsers = (state, newUsers) =>
    state.get('users').merge(newUsers.map((user) => new User(user)));

const mergeClients = (state, newClients) =>
    state.get('clients').merge(newClients.map((client) => new Client(client)));

export default function reducer(state = initState, action) {
    switch (action.type) {
        case ActionTypes.CREATE_CLIENT:
        case ActionTypes.CREATE_USER:
            return state.withMutations(map => {
                map.set('fetching', true)
                    .set('fetched', false)
                    .set('error', null);
            });
        case ActionTypes.CREATE_CLIENT_FULFILLED: {
            if (action.payload.entities.clients) {
                return state.withMutations(map => {
                    map.set('fetching', false)
                        .set('fetched', true)
                        .set('clients', mergeClients(state, Immutable.fromJS(action.payload.entities.clients)));
                });
            } else {
                return state.withMutations(map => {
                    map.set('fetching', false)
                        .set('fetched', true)
                });
            }
        }
        case ActionTypes.CREATE_USER_FULFILLED: {
            if (action.payload.entities.users) {
                return state.withMutations(map => {
                    map.set('fetching', false)
                        .set('fetched', true)
                        .set('users', mergeUsers(state, Immutable.fromJS(action.payload.entities.users)));
                });
            } else {
                return state.withMutations(map => {
                    map.set('fetching', false)
                        .set('fetched', true)
                });
            }
        }
        case ActionTypes.CREATE_CLIENT_REJECTED:
        case ActionTypes.CREATE_USER_REJECTED:
            return state.withMutations(map => {
                map.set('fetching', false)
                    .set('fetched', false)
                    .set('error', action.payload);
            });
    }
    return state;
}