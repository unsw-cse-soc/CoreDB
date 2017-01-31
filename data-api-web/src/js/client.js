import 'babel-polyfill';
import React from 'react';
import ReactDOM from "react-dom";
import { render } from 'react-dom';
import { Provider } from "react-redux";
import { Router, Route, IndexRoute, browserHistory } from "react-router";
import { syncHistoryWithStore } from 'react-router-redux';
import injectTapEventPlugin from 'react-tap-event-plugin';
import store from "./store";
import Layout from './pages/Layout';
import MainContainer from './containers/MainContainer';
import 'materialize-css/css/ghpages-materialize.css';
import 'materialize-css/bin/jquery-2.1.1.min';
import 'materialize-css/bin/materialize';

const app = document.getElementById('app');
const history = syncHistoryWithStore(browserHistory, store);

// Needed for onTouchTap
injectTapEventPlugin();

ReactDOM.render(
    <Provider store={store}>
        <Router history={history}>
            <Route name="home" path="/" component={Layout}>
                <IndexRoute component={MainContainer}></IndexRoute>
                <Route name="doc" path="(/:invId)" component={MainContainer}></Route>
            </Route>
        </Router>
    </Provider>,
    app);