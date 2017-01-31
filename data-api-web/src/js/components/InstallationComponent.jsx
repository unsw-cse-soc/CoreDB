import React from "react";
import ReactDOM from "react-dom";
import RenderInput from './ui/RenderInput';
import RenderSubmitButton from './ui/RenderSubmitButton';
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
        const { handleSubmit, submitting } = this.props;
        return <div>
            <div class="card-panel teal lighten-2">Step1: Create your client</div>
            <p class="caption">
                Data API employs OAuth 2.0 method for authorisation. In order to be able to communicate with the API you need to create a client which allows you to authenticate to the system.
                In order to create a client, you need to send a POST request to https:// for example:

            </p>
            <div class="row">
                <form class="col s12 m12 l12" onSubmit={handleSubmit}>
                    <Field name="client"
                        id="client-input"
                        label="Name"
                        component={RenderInput}
                        type="text" />
                    <RenderSubmitButton label="Submit" id="client-submit" name="client-submit" />
                </form>
            </div>
        </div>
    }
}

export default reduxForm({
    form: 'newClient',
    validate: values => {
        const errors = {};
        return errors;
    }
})(InstallationComponent);