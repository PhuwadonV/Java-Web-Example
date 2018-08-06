import Vue from 'vue'
import Router from 'vue-router'
import Home from './route/Home'
import Find from './route/Find'
import About from './route/About'
import HowTo from './route/HowTo'
import AddProduct from './route/AddProduct'
import ProductDetail from './route/ProductDetail'
import Employees from './route/Employees'
import Account from './route/Account'
import Cart from './route/Cart'
import Carts from './route/Carts'
import CartDetail from './route/CartDetail'
import MinimumStock from './route/MinimumStock'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path: '/find',
      name: 'Find',
      component: Find
    },
    {
      path: '/account',
      name: 'Account',
      component: Account
    },
    {
      path: '/cart',
      name: 'Cart',
      component: Cart
    },
    {
      path: '/carts',
      name: 'Carts',
      component: Carts
    },
    {
      path: '/employees',
      name: 'Employees',
      component: Employees
    },
    {
      path: '/about',
      name: 'About',
      component: About
    },
    {
      path: '/minimumStock',
      name: 'MinimumStock',
      component: MinimumStock
    },
    {
      path: '/howTo',
      name: 'HowTo',
      component: HowTo
    },
    {
      path: '/addProduct',
      name: 'AddProduct',
      component: AddProduct
    },
    {
      path: '/productDetail/:productId',
      name: 'ProductDetail',
      component: ProductDetail
    },
    {
      path: '/cartDetail/:cartId',
      name: 'CartDetail',
      component: CartDetail
    },
  ]
})
