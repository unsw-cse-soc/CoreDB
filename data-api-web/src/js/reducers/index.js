import { combineReducers } from "redux";
import { routerReducer } from 'react-router-redux';
import menu from './MenuReducer';
import auth from './AuthReducer';
import database from './DatabaseReducer';
import { reducer as formReducer } from 'redux-form';

export default combineReducers({
    menu,
    auth,
    database,
    form: formReducer,
    routing: routerReducer
});