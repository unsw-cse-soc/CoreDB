import React from "react";
import ReactDOM from "react-dom";
import RenderInput from './ui/RenderInput';
import RenderSubmitButton from './ui/RenderSubmitButton';
import RenderDropDown from './ui/RenderDropDown';
import { reduxForm, Field } from 'redux-form';

class InstallationComponent extends React.Component {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
    }

    componentDidUpdate(prevProps) {
    }

    render() {
        const { handleCreateClientSubmit, handleCreateUserSubmit, handleLoginSubmit, clients } = this.props;
        return <div class="row">
            <div class="col s12 m12 l12">
                <div class="card-panel teal lighten-2">Step1: Create your client</div>
                <p class="caption">
                    Data API employs OAuth 2.0 method for authorisation. In order to be able to communicate with the API you need to create a client which allows you to authenticate to the system.
                In order to create a client, you need to send a POST request to https:// for example:
                </p>
                <CreateClientForm onSubmit={handleCreateClientSubmit} />
            </div>
            <div class="col s12 m12 l12">
                <div class="card-panel teal lighten-2">Step2: Create users</div>
                <p class="caption">
                    Once you created a client, you will be able to define one or multiple users. It can be a single admin user or multiple users.
                    Later one you can set permission based on the role of a user. Access level can be applied on action and resource level.
                </p>
                <CreateUserForm clients={clients} onSubmit={handleCreateUserSubmit} />
            </div>
            <div class="col s12 m12 l12">
                <div class="card-panel teal lighten-2">Step3: Acquire access token</div>
                <p class="caption">
                    Finaly, you will need to obtain an access token which will be included in authorization header of all requests.
                    The token is valid for 15 minutes and you will need to request a new token using the refresh token which be provided with the access token.
                </p>
                <GetTokeForm onSubmit={handleLoginSubmit} />
            </div>
        </div>
    }
}

export default InstallationComponent;

class CreateClientForm extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const {handleSubmit, submitting} = this.props;
        return <div class="row">
            <div class="col s12 m12 l12">
                <div class="card blue-grey darken-1">
                    <div class="card-content white-text">
                        <pre>curl -X POST -d --data "{"this is for test"}" http://example.com/path/to/resource --header "Content-Type:application/json"</pre>
                    </div>
                </div>
            </div>
            <form class="col s12 m12 l12" onSubmit={handleSubmit}>
                <Field name="client"
                    id="client-input"
                    label="Name"
                    component={RenderInput}
                    type="text" />
                <RenderSubmitButton label="Submit" id="client-submit" name="client-submit" />
            </form>
        </div>
    }
}

CreateClientForm = reduxForm({
    form: 'newClientForm',
    validate: values => {
        const errors = {};
        return errors;
    }
})(CreateClientForm);

class CreateUserForm extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const {clients} = this.props;
        return <div class="row">
            <div class="col s12 m12 l12">
                <div class="card blue-grey darken-1">
                    <div class="card-content white-text">
                        <pre>curl -X POST -d --data "{"this is for test"}" http://example.com/path/to/resource --header "Content-Type:application/json"</pre>
                    </div>
                </div>
            </div>
            <form class="col s12 m12 l12" onSubmit={this.props.onSubmit}>
                <Field name="userClientId"
                    id="user-client-input"
                    label="Client"
                    component={RenderDropDown}
                    options={clients.map((client) => { return { value: client.id, text: client.name } }).toArray()} />
                <Field name="userName"
                    id="username-input"
                    label="Username"
                    component={RenderInput}
                    type="text" />
                <Field name="password"
                    id="password-input"
                    label="Password"
                    component={RenderInput}
                    type="password" />
                <Field name="role"
                    id="role-input"
                    label="Role"
                    component={RenderInput}
                    type="text" />
                <RenderSubmitButton label="Submit" id="client-submit" name="client-submit" />
            </form>
        </div>
    }
}

CreateUserForm = reduxForm({
    form: 'newUserForm',
    validate: values => {
        const errors = {};
        return errors;
    }
})(CreateUserForm);

class GetTokeForm extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <div class="row">
            <div class="col s12 m12 l12">
                <div class="card blue-grey darken-1">
                    <div class="card-content white-text">
                        <pre>curl -X POST -d --data "{"this is for test"}" http://example.com/path/to/resource --header "Content-Type:application/json"</pre>
                    </div>
                </div>
            </div>
            <form class="col s12 m12 l12" onSubmit={this.props.onSubmit}>
                <Field name="loginUserName"
                    id="login-userName-input"
                    label="Username"
                    component={RenderInput}
                    type="text" />
                <Field name="loginPassword"
                    id="login-password-input"
                    label="Password"
                    component={RenderInput}
                    type="password" />
                <RenderSubmitButton label="Submit" id="client-submit" name="client-submit" />
            </form>
        </div>
    }
}

GetTokeForm = reduxForm({
    form: 'getTokeForm',
    validate: values => {
        const errors = {};
        return errors;
    }
})(GetTokeForm);

