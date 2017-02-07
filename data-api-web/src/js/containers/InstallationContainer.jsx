import React from 'react';
import InstallationComponent from "../components/InstallationComponent";
import { connect } from "react-redux";
import { createClient, createUser } from '../actions/AuthActions';
import { getClients } from '../selectors/AuthSelectors';

class InstallationContainer extends React.Component {
    render() {
        const {clients, onCreateClient, onCreateUser} = this.props;
        return <InstallationComponent
            clients={clients}
            handleCreateClientSubmit={values => {
                onCreateClient(values.client);
            }}
            handleCreateUserSubmits={values => {
                onCreateUser(values.userName, values.password, values.role, values.clientId);
            }}
            handleLoginSubmit={(values, dispatch) => {
                dispatch(createClient(values.client));
            }} />
    }
}

const mapDispatchToProps = dispatch => {
    return {
        onCreateClient(name) {
            dispatch(createClient(name));
        },
        onCreateUser(userName, password, role, clientId) {
            dispatch(createUser(userName, password, role, clientId));
        },
    };
};

const mapStateToProps = (state) => {
    return {
        clients: getClients(state)
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(InstallationContainer);
