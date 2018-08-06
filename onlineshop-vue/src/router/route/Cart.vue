<template>
  <div>
    <OkCancelPopup :isActive="editAmountData.isActive" :title="'Edit Amount'" :onOk="editAmountOnOk" :onCancel="editAmountOnCancel" :disabledOk="disabledPopupOk">
      <p style="margin-bottom: 10px">Max Amount : {{ editAmountData.maxAmount }}</p>
      <input class="input" type="text" v-model="editAmountData.input" @keyup="validateEditAmountOnKeyup" @focusout="validateEditAmount">
    </OkCancelPopup>
    <div class="box">
      <div v-for="(product, index) in cart" style="margin-bottom: 20px">
        <div class="box" style="display: flex; flex-wrap: wrap; background-color: hsl(0, 0%, 86%)">
          <div style="margin-right: 10px; margin-bottom: 10px; flex-grow: 1">
            <table class="table is-bordered is-narrow">
              <col max-width="100%">
              <col width="100%">
              <tr>
                <td>ID</td>
                <td style="word-break: break-all;">{{ product.id }}</td>
              </tr>
              <tr>
                <td>Name</td>
                <td>{{ product.name }}</td>
              </tr>
              <tr>
                <td style="white-space: nowrap">Price (Bath)</td>
                <td>{{ product.price }}</td>
              </tr>
              <tr>
                <td>Amount</td>
                <td :title="product.amount > product.maxAmount ? 'Not enough product' : ''" :style="{ 'background-color': product.amount > product.maxAmount ?  'hsl(348, 100%, 61%)' : 'inherit' }">{{ product.amount }}</td>
              </tr>
              <tr>
                <td style="white-space: nowrap">Total Price (Bath)</td>
                <td>{{ product.amount * product.price }}</td>
              </tr>
            </table>
          </div>
          <div>
            <div class="inlineWhenMobile" align="center"><button class="button is-danger" style="margin-bottom: 10px; width: 83px;" type="button" @click="removeProduct(index)">Remove</button></div>
            <div class="inlineWhenMobile" align="center"><button class="button is-info" style="width: 83px;" type="button" @click="editAmount(index)">Edit</button></div>
          </div>
        </div>
      </div>
      <p class="title">Total : {{ total }}</p>
      <div style="margin-bottom: 20px;">
        <p class="title">Shipping Address</p>
        <label class="checkbox">
          <input type="checkbox" v-model="isUsedDefaultAddress" @click="onClickCheckbox">
          Use default address
        </label>
        <textarea class="textarea subtitle" name="address" v-model="shippingAddress" :disabled="isUsedDefaultAddress"></textarea>
      </div>
      <div align="center">
        <button class="button is-link" type="button" :disabled="!enableSubmit" @click="submit">Submit</button>
        <button class="button is-info" type="button" @click="refreshData">Refresh</button>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations, mapActions } from 'vuex'
import Sync from '@/js/Sync.js'
import OkCancelPopup from '@/components/OkCancelPopup'

export default {
  name: 'Cart',
  data() {
    return {
      total: 0,
      enableSubmit: true,
      editAmountData: {
        isActive: false,
        input: '0',
        index: 0,
        maxAmount: 0
      },
      isUsedDefaultAddress: false,
      shippingAddress: '',
      disabledPopupOk: false
    }
  },
  components: {
    OkCancelPopup
  },
  computed: {
    ...mapState({
      accountInfo: state => state.account.info,
      cart: state => state.cart.products
    })
  },
  methods: {
    ...mapMutations({
      alert: 'alert/show',
      setCart: 'cart/set',
      setCartAmount: 'cart/setAmount',
      removeItem: 'cart/removeItem',
      setCartNoti: 'notification/cart',
      setCartsNoti: 'notification/carts'
    }),
    ...mapActions({
      updateCartData: 'cart/update'
    }),
    async usedDefaultAddress() {
      this.isUsedDefaultAddress = true
      const accountInfo = await Sync.WaitValue(this.accountInfo)
      localStorage.setItem('usedDefaultAddress', '')
      this.shippingAddress = accountInfo.address
    },
    onClickCheckbox() {
      if(!this.isUsedDefaultAddress) this.usedDefaultAddress()
      else {
        this.checkboxInput = false
        this.isUsedDefaultAddress = false
        localStorage.removeItem('usedDefaultAddress')
      }
    },
    async submit() {
      this.enableSubmit = false

      if(this.shippingAddress === '') {
        this.alert({title: 'Failed', msg: 'Address should not be empty'})
        this.enableSubmit = true
        return
      }

      const cart = this.cart
      const length = cart.length
      let productCarts = []
      for(let i = 0; i < length; i++) {
        const product = cart[i]
        productCarts.push({
          productId: product.id,
          amount: product.amount
        })
      }

      let cartData = {
        address: this.shippingAddress,
        totalPrice: this.total,
        products: productCarts
      }

      this.PostJson('/api/cart/add', cartData)
      .then(this.ResponseMatch(resp => resp.json()
      .then(json => {
        if(json) {
          this.setCart([])
          sessionStorage.removeItem('cart')
          this.alert({title: 'Success', msg: 'Sended' })
          this.setCartsNoti(true)
          this.$router.replace('/')
        }
        else this.alert({title: 'Failed', msg: 'Please refresh and try again'})
        this.enableSubmit = true
      }),
      _ => this.enableSubmit = true))
    },
    editAmountOnOk() {
      this.disabledPopupOk = true
      const editAmountData = this.editAmountData
      this.setCartAmount({ index: editAmountData.index, value: Number(editAmountData.input) })
      sessionStorage.setItem('cart', JSON.stringify(this.cart))
      this.total = 0
      this.cart.forEach(data => this.total += data.price * data.amount)
      editAmountData.isActive = false
      this.disabledPopupOk = false
    },
    editAmountOnCancel() {
      this.editAmountData.isActive = false
    },
    validateEditAmountOnKeyup() {
      this.editAmountData.input = this.ValidatePossitiveNumberOnKeyup(this.editAmountData.input)
    },
    validateEditAmount() {
      const editAmountData = this.editAmountData
      if(editAmountData.input === '') editAmountData.input = '1'
      else if(Number(editAmountData.input) > editAmountData.maxAmount) editAmountData.input = String(editAmountData.maxAmount)
    },
    editAmount(index) {
      const editAmountData = this.editAmountData
      const product = this.cart[index]
      editAmountData.index = index
      editAmountData.input = String(product.amount > product.maxAmount ? product.maxAmount : product.amount)
      editAmountData.maxAmount = product.maxAmount
      editAmountData.isActive = true
    },
    refreshData() {
      this.updateCartData({ self: this, onComplete: () => {
        if(this.cart.length !== 0) {
          this.total = 0
          this.cart.forEach(data => this.total += data.price * data.amount)
        }
        else {
          sessionStorage.removeItem('cart')
          this.$router.replace('/')
        }
      }})
    },
    removeProduct(index) {
      const productTotal = this.cart[index].amount * this.cart[index].price
      this.removeItem(index)
      if(this.cart.length !== 0) {
        sessionStorage.setItem('cart', JSON.stringify(this.cart))
        this.total = 0
        this.cart.forEach(data => this.total += data.price * data.amount)
      }
      else {
        sessionStorage.removeItem('cart')
        this.$router.replace('/')
      }
    },
    updateClient() {
      this.ChangeRouteIfPermissionLevel(level => level !== 1)
      this.setCartNoti(false)
      if(localStorage.getItem('usedDefaultAddress') === '') this.usedDefaultAddress()
      if(this.cart.length === 0) {
        const result = sessionStorage.getItem('cart')
        if(result !== null) this.setCart(JSON.parse(result))
        else this.$router.replace('/')
      }
      this.total = 0
      this.cart.forEach(data => this.total += data.price * data.amount)
    }
  },
  created() {
    window.scrollTo(0, 0);
    this.updateClient()
  }
}
</script>
<style>
.inlineWhenMobile {
  display: block;
}

@media screen and (max-width: 600px) {
  .inlineWhenMobile {
    display: inline;
  }
}
</style>
