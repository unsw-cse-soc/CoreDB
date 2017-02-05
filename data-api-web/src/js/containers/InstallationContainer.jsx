import React from 'react';
import InstallationComponent from "../components/InstallationComponent";
import { createClient } from '../actions/AuthActions';

export default class InstallationContainer extends React.Component {
    render() {
        return <InstallationComponent
            handleCreateClientSubmit={(values, dispatch) => {
                dispatch(createClient(values.client));
            } }
            handleCreateUserSubmits={(values, dispatch) => {
                dispatch(createClient(values.client));
            } }
            handleLoginSubmit={(values, dispatch) => {
                dispatch(createClient(values.client));
            } } />
    }
}
