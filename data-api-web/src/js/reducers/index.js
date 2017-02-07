import { combineReducers } from "redux";
import { routerReducer } from 'react-router-redux';
import menu from './MenuReducer';
import auth from './AuthReducer';
import { reducer as formReducer } from 'redux-form';

export default combineReducers({
    menu,
    auth,
    form: formReducer,
    routing: routerReducer
});