import React from "react";
import ReactDOM from "react-dom";
import { push } from 'react-router-redux';
import { connect } from "react-redux";
import { Link } from 'react-router';
import { changeMenu } from '../actions/MenuActions';
import { getMenus } from '../selectors/MenuSelectors';
import { getActiveMenu } from '../selectors/MenuSelectors';

class MenuContainer extends React.Component {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
    }

    componentDidUpdate(prevProps) {
        if (prevProps.acttiveMenu.id !== this.props.acttiveMenu.id) {
            this.props.onNavigateTo(this.props.acttiveMenu.id);
        }
    }

    render() {
        const {menus, acttiveMenu, onMenuChange } = this.props;
        return <header>
            <nav class="top-nav blue darken-1">
                <div class="container">
                    <div class="nav-wrapper"><a class="page-title">{acttiveMenu.title}</a></div>
                </div>
            </nav>
            <ul class="side-nav fixed">
                {
                    menus.valueSeq().map(menu => <li key={menu.id}>
                        <Link className="waves-effect" to={menu.url} onClick={() => { onMenuChange(menu.id) } }>{menu.title}</Link>
                    </li>)
                }
            </ul>
        </header>
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        menus: getMenus(state),
        acttiveMenu: getActiveMenu(state)
    };
}

const mapDispatchToProps = dispatch => {
    return {
        onNavigateTo(dest) {
            dispatch(push(dest));
        },
        onMenuChange(menuId) {
            dispatch(changeMenu(menuId));
        }
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(MenuContainer);