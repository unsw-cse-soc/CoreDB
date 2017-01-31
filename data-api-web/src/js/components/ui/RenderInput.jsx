import React from 'react';

const RenderInput = ({ input, label, id, meta: { touched, error, warning } }) => {
    return (
        <div class="row">
            <div class="input-field col s12 m12 l6">
                <input {...input} id={id} name={id} type="text" class="validate" />
                <label for={id}>{label}</label>
            </div>
        </div>
    );
}
export default RenderInput;