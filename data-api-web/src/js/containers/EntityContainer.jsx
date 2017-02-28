import React from 'react';
import EntityComponent from "../components/EntityComponent";
import { createClient } from '../actions/AuthActions';

export default class EntityContainer extends React.Component {
    render() {
        return <EntityComponent
            handleCreateEntitySubmit={values => {
                
            }} />
    }
}
