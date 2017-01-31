import { combineReducers } from "redux";
import { routerReducer } from 'react-router-redux';
import menu from './MenuReducer';
import { reducer as formReducer } from 'redux-form';

export default combineReducers({
    menu,
    form: formReducer,
    routing: routerReducer
});