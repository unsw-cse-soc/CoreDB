import React from 'react';
import DatabaseComponent from "../components/DatabaseComponent";
import { createClient } from '../actions/AuthActions';

export default class DatabaseContainer extends React.Component {
    render() {
        return <DatabaseComponent
            handleCreateDatabaseSubmit={(values, dispatch) => {

            } }
            handleDeleteDatabaseSubmit={(values, dispatch) => {
                dispatch(createClient(values.client));
            } } />
    }
}
