import Immutable from 'immutable';
import * as ActionTypes from '../constants/ActionTypes';
import Menu from '../models/Menu';
import MenuMap from '../models/MenuMap';

export default function reducer(state = Immutable.Map({
    menus: new MenuMap({
        'installation': new Menu({
            id: 'installation',
            title: 'Installation',
            url: '/installation'
        }),
        'installation2': new Menu({
            id: 'installation2',
            title: 'Installation2',
            url: '/installation'
        })
    }),
    activeMenu: 'installation',
    error: null,
}), action) {
    switch (action.type) {
        case ActionTypes.SWITCH_MENU: {
            const menuId = action.payload;
            if (state.hasIn(['menus', menuId])) {
                return state.withMutations(map => {
                    map.set('error', null)
                        .set('activeMenu', menuId)
                });
            } else {
                return state.set('error', 'Invalid menu');
            }
        }
    }
    return state;
}