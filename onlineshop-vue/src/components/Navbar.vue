<template>
  <nav class="navbar is-fixed-top is-primary">
    <div class="navbar-brand">
      <a role="button" class="navbar-burger" :style="{ 'background-color': cartNoti || cartsNoti || minimumStockNoti ? 'hsl(348, 100%, 61%)' : 'inherit' }" :class="{ 'is-active': isBurgerActive }" style="color: white;" @click="toggleBurger">
        <span></span>
        <span></span>
        <span></span>
      </a>
    </div>
    <div class="navbar-menu" :class="{ 'is-active': isBurgerActive }">
      <div class="navbar-start">
        <router-link class="navbar-item" @click.native="closeBurger" to="/">Home</router-link>
        <router-link class="navbar-item" @click.native="closeBurger" to="/find">Find</router-link>
        <router-link class="navbar-item" @click.native="closeBurger" v-if="permissionLevel.data > 1" to="/addProduct">Add Product</router-link>
        <router-link class="navbar-item" :style="{ 'background-color': cartsNoti ? 'hsl(348, 100%, 61%)' : navColor[0] }" @mouseover.native="mouseOver(0)" @mouseleave.native="mouseLeave(0)" @click.native="closeBurger" v-if="permissionLevel.data > 0" to="/carts">Carts</router-link>
        <router-link class="navbar-item" @click.native="closeBurger" v-if="permissionLevel.data > 2" to="/employees">Employees</router-link>
        <router-link class="navbar-item" @click.native="closeBurger" v-if="permissionLevel.data < 2" to="/howTo">How to</router-link>
        <router-link class="navbar-item" @click.native="closeBurger" v-if="permissionLevel.data < 2" to="/about">About</router-link>
        </span>
      </div>
      <div class="navbar-end">
        <router-link class="navbar-item" :style="{ 'background-color': cartNoti ? 'hsl(348, 100%, 61%)' : navColor[1] }" @mouseover.native="mouseOver(1)" @mouseleave.native="mouseLeave(1)" @click.native="closeBurger" v-if="permissionLevel.data == 1 && cart.length > 0" to="/cart">Cart</router-link>
        <router-link class="navbar-item" @click.native="closeBurger" v-if="permissionLevel.data == 1" to="/account">Account</router-link>
        <router-link class="navbar-item" :style="{ 'background-color': minimumStockNoti ? 'hsl(348, 100%, 61%)' : navColor[2] }" @mouseover.native="mouseOver(2)" @mouseleave.native="mouseLeave(2)" @click.native="closeBurger" v-if="permissionLevel.data > 2 && !hideMinimumStock" to="/minimumStock">Minimum Stock</router-link>
        <a class="navbar-item" href="/api/auth/logout" v-if="permissionLevel.data > 0">
          <span class="icon">
            <i class="fa fa-sign-out-alt"></i>
          </span>
          <span>Logout</span>
        </a>
        <a class="navbar-item" @click="login" v-else>
          <span class="icon">
              <i class="fa fa-sign-in-alt"></i>
          </span>
          <span>Login</span>
        </a>
      </div>
    </div>
  </nav>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import Sync from '@/js/Sync.js'

export default {
  name: 'Navbar',
  data() {
    return {
      isBurgerActive: false,
      navColor: ['inherit', 'inherit', 'inherit']
    }
  },
  computed: {
    ...mapState({
      permissionLevel: state => state.account.permissionLevel,
      cart: state => state.cart.products,
      minimumStockNoti: state => state.notification.minimumStock,
      hideMinimumStock: state => state.notification.hideMinimumStock,
      cartNoti: state => state.notification.cart,
      cartsNoti: state => state.notification.carts
    })
  },
  methods: {
    ...mapMutations({
      setMinimumStockNoti: 'notification/minimumStock',
      setHideMinimumStock: 'notification/hideMinimumStock',
      setCart: 'cart/set',
      setCartsNoti: 'notification/carts'
    }),
    mouseOver(index) {
      this.$set(this.navColor, index, 'hsl(0, 0%, 4%, 13%)')
    },
    mouseLeave(index) {
      this.$set(this.navColor, index, 'inherit')
    },
    login() {
      window.location.href = '/api/auth/login#' + this.$router.currentRoute.path
    },
    closeBurger() {
      this.isBurgerActive = false
    },
    toggleBurger() {
      this.isBurgerActive = !this.isBurgerActive
    },
    checkMinimumStockNotEmpty() {
      this.Get('/api/minimumStock/count', { cache: 'reload' })
      .then(this.ResponseMatch(resp => resp.json()
      .then(json => {
        if(json === 0) this.setHideMinimumStock(true)
        else this.setHideMinimumStock(false)
        setTimeout(this.checkMinimumStockNotEmpty, 5000)
      }),
      _ => setTimeout(this.checkMinimumStockNotEmpty, 5000)))
    },
    hasNewMinimumStock() {
      this.Get('/api/minimumStock/hasNew', { cache: 'reload' })
      .then(this.ResponseMatch(resp => resp.json()
      .then(json => {
        if(json) {
          this.setMinimumStockNoti(true)
          this.setHideMinimumStock(false)
        }
        setTimeout(this.hasNewMinimumStock, 5000)
      }),
      _ => setTimeout(this.checkMinimumStockNotEmpty, 5000)))
    },
    hasNewCarts() {
      this.Get('/api/cart/hasNew', { cache: 'reload' })
      .then(this.ResponseMatch(resp => resp.json()
      .then(json => {
        if(json) this.setCartsNoti(true)
        setTimeout(this.hasNewCarts, 5000)
      }),
      _ => setTimeout(this.hasNewCarts, 5000)))
    },
    async updateClient() {
      const permissionLevel = await Sync.WaitValue(this.permissionLevel)
      if(permissionLevel > 1) {
        this.hasNewCarts()
        this.hasNewMinimumStock()
        this.checkMinimumStockNotEmpty()
      }
      else {
        const result = sessionStorage.getItem('cart')
        if(result !== null) this.setCart(JSON.parse(result))
      }
    }
  },
  created() {
    this.updateClient()
  }
}
</script>
<style>
  .dimOnHover:hover {
    background-color: green;
  }
<style>
