import React from 'react';
import SchemaComponent from "../components/SchemaComponent";
import { createClient } from '../actions/AuthActions';

export default class EntityContainer extends React.Component {
    render() {
        return <SchemaComponent />
    }
}
