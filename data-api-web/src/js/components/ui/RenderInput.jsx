import React from 'react';

const RenderInput = ({ input, label, id, type, smallSize, mediumSize, largeSize, withContainer, meta: { touched, error, warning } }) => {
    return (
        withContainer === true ?
            <div class="row">
                <div class={`input-field col ${smallSize} ${mediumSize} ${largeSize}`}>
                    <input {...input} id={id} name={id} type={type} class="validate" />
                    <label for={id}>{label}</label>
                </div>
            </div> :
            <div class={`input-field col ${smallSize} ${mediumSize} ${largeSize}`}>
                <input {...input} id={id} name={id} type={type} class="validate" />
                <label for={id}>{label}</label>
            </div>
    );
}
RenderInput.propTypes = {
    input: React.PropTypes.object.isRequired,
    label: React.PropTypes.string.isRequired,
    id: React.PropTypes.string.isRequired,
    type: React.PropTypes.string.isRequired,
    smallSize: React.PropTypes.string.isRequired,
    mediumSize: React.PropTypes.string.isRequired,
    largeSize: React.PropTypes.string.isRequired,
    withContainer: React.PropTypes.bool.isRequired
}
RenderInput.defaultProps = {
    type: "text",
    smallSize: "s12",
    mediumSize: "m12",
    largeSize: "l6",
    withContainer: true
}
export default RenderInput;