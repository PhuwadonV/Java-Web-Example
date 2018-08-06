import Vue from 'vue'
import Vuex from 'vuex'
import alert from './modules/alert'
import account from './modules/account'
import home from './modules/home'
import cart from './modules/cart'
import carts from './modules/carts'
import notification from './modules/notification'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    alert,
    account,
    home,
    cart,
    carts,
    notification
  },
  strict: process.env.NODE_ENV !== 'production'
})
