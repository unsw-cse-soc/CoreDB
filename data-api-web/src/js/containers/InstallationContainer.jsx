import React from 'react';
import InstallationComponent from '../components/InstallationComponent';
import { connect } from 'react-redux';
import { createClient, createUser } from '../actions/AuthActions';
import { getClients, getResponses } from '../selectors/AuthSelectors';

class InstallationContainer extends React.Component {
    render() {
        const {clients, responses, onCreateClient, onCreateUser} = this.props;
        return <InstallationComponent
            responses={responses}
            handleCreateClientSubmit={values => {
                onCreateClient(values.client);
            }}
            handleCreateUserSubmit={values => {
                onCreateUser(values.userName, values.password, values.role, values.userClientName, values.userClientSecret);
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
        onCreateUser(userName, password, role, clientName, clientSecret) {
            dispatch(createUser(userName, password, role, clientName, clientSecret));
        },
    };
};

const mapStateToProps = (state) => {
    return {
        responses: getResponses(state)
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(InstallationContainer);
