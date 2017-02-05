import React from "react";
import ReactDOM from "react-dom";
import RenderInput from './ui/RenderInput';
import RenderDropDown from './ui/RenderDropDown';
import RenderSubmitButton from './ui/RenderSubmitButton';
import { reduxForm, Field } from 'redux-form';

class DatabaseComponent extends React.Component {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
    }

    componentDidUpdate(prevProps) {
    }

    render() {
        const { handleCreateDatabaseSubmit, handleDeleteDatabaseSubmit } = this.props;
        return <div class="row">
            <div class="col s12 m12 l12">
                <div class="card-panel teal lighten-2">Create a database</div>
                <p class="caption">
                    Data API supports two types of databases: Relational database and JSON store. Although we recommand using JSON store due mainly to higher performance, this decision is related to use case.
                In order to create a database, you need to set its type and name.
                </p>
                <CreateDatabaseForm onSubmit={handleCreateDatabaseSubmit} />
            </div>
            <div class="col s12 m12 l12">
                <div class="card-panel teal lighten-2">Delete a database</div>
                <p class="caption">
                    You are able to delete a database using a POST request. Please be advised that it is not possible to recover a deleted database.
                </p>
                <DeleteDatabaseForm onSubmit={handleDeleteDatabaseSubmit} />
            </div>
        </div>
    }
}

export default DatabaseComponent;

class CreateDatabaseForm extends React.Component {
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
                <div class="row">
                    <Field name="databaseName"
                        id="database-name-input"
                        label="Name"
                        component={RenderInput}
                        withContainer={false}
                        type="text" />
                    <Field name="databaseType"
                        id="database-type-input"
                        label="Type"
                        component={RenderDropDown}
                        withContainer={false}
                        options={[
                            { value: 'relational', text: 'Relational' },
                            { value: 'json', text: 'JSON Store' }
                        ]} />
                </div>
                <RenderSubmitButton label="Submit" id="client-submit" name="client-submit" />
            </form>
        </div>
    }
}

CreateDatabaseForm = reduxForm({
    form: 'newDatabaseForm',
    validate: values => {
        const errors = {};
        return errors;
    }
})(CreateDatabaseForm);

class DeleteDatabaseForm extends React.Component {
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
                <Field name="databaseName"
                    id="database-name-input"
                    label="Name"
                    component={RenderInput}
                    type="text" />
                <RenderSubmitButton label="Submit" id="client-submit" name="client-submit" />
            </form>
        </div>
    }
}

DeleteDatabaseForm = reduxForm({
    form: 'deleteDatabaseForm',
    validate: values => {
        const errors = {};
        return errors;
    }
})(DeleteDatabaseForm);