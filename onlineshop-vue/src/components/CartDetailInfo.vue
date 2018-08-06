<template>
  <div>
    <OkCanclePopup :isActive="buyAmount.isActive" :title="'Add to cart'" :onOk="buyAmountOnOk" :onCancle="buyAmountOnCancle">
      <input class="input" type="text" v-model="buyAmount.input" @keyup="validateBuyAmountInputOnKeyup" @focusout="validateBuyAmountInput">
    </OkCanclePopup>
    <div class="box">
      <div v-if="hasData">
        <p class="has-text-centered title is-1">{{ name }}</p>
        <img style="display: block; max-height: 400px; margin: auto;" :src="imgUrl">
        <p class="subtitle">ID : {{ id }}</p>
        <p class="subtitle">Price (Bath) : {{ price }}</p>
        <p class="subtitle">Amount : {{ amount }}</p>
        <p class="subtitle">Sold : {{ sold }}</p>
        <p class="subtitle">Date : {{ day + "/" + month + "/" + year + " - " + hour + ":" + (minute < 10 ? "0" + minute : minute) }}</p>
        <div class="box">
          <p class="subtitle">Tags</p>
          <div class="tags">
            <span class="tag is-medium is-info" v-for="tag in tags">{{ tag }}</span>
          </div>
        </div>
        <div class="box">
          <p class="subtitle">Description</p>
          <p style="word-wrap: break-word;">{{ description }}</p>
        </div>
        <button class="button is-link" type="button" v-if="permissionLevel.data === 1" @click="askBuyAmount">Add to cart</button>
      </div>
      <p class="has-text-centered title is-1" v-else>Product not found</p>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import OkCanclePopup from '@/components/OkCanclePopup'

export default {
  name: 'CartDetailInfo',
  components: {
    OkCanclePopup
  },
  props: ['id'],
  data() {
    return {
      buyAmount: {
        isActive: false,
        input: '1'
      },
      hasData: true,
      name: '',
      price: 0,
      amount: 0,
      imgUrl: '',
      sold: 0,
      tags: [],
      description: '',
      date: 0,
      minute: 0,
      hour: 0,
      day: 0,
      month: 0,
      year: 0
    }
  },
  computed: {
    ...mapState({
      permissionLevel: state => state.account.permissionLevel,
      cart: state => state.cart.products
    })
  },
  methods: {
    ...mapMutations({
      alert: 'alert/show',
      setCart: 'cart/set',
      addToCart: 'cart/addItem',
      addAmountToCart: 'cart/addAmount'
    }),
    setCartSessionStorage() {
      sessionStorage.setItem('cart', JSON.stringify(this.cart))
    },
    askBuyAmount() {
      this.buyAmount.isActive = true
      this.buyAmount.input = '1'
    },
    updateCart() {
      if(this.cart.length === 0) {
        const result = sessionStorage.getItem('cart')
        if(result !== null) this.setCart(JSON.parse(result))
      }
    },
    buyAmountOnOk() {
      this.updateCart()
      const boughtAmount = Number(this.buyAmount.input)
      if(boughtAmount > this.amount) {
        this.alert({title: 'Failed', msg: 'Not enough product'})
        return
      }

      let success = false
      const cart = this.cart
      const length = this.cart.length
      for(let i = 0; i < length; i++) {
        if(cart[i].id === this.id) {
          if(cart[i].amount + boughtAmount > this.amount) {
            this.alert({title: 'Failed', msg: 'Not enough product'})
            return
          }
          this.addAmountToCart({index: i, value: boughtAmount})
          this.setCartSessionStorage()
          success = true
          this.buyAmount.isActive = false
          break
        }
      }
      if(!success) {
        this.addToCart({ id: this.id, name: this.name, amount: boughtAmount, maxAmount: this.amount, price: this.price })
        this.setCartSessionStorage()
        this.buyAmount.isActive = false
      }
    },
    buyAmountOnCancle() {
      this.buyAmount.isActive = false
    },
    validateBuyAmountInputOnKeyup() {
      this.buyAmount.input = this.ValidatePossitiveNumberOnKeyup(this.buyAmount.input)
    },
    validateBuyAmountInput() {
      if(this.buyAmount.input === '') this.buyAmount.input = '1'
    },
    updateClient() {
      this.Get('/api/product/' + this.$route.params.productId)
      .then(this.ResponseMatch(resp => resp.json()
        .then(json => {
          this.name = json.name
          this.price = json.price
          this.amount = json.amount
          this.imgUrl = json.imgUrl === '' ? 'https://vignette.wikia.nocookie.net/simpsons/images/6/60/No_Image_Available.png/revision/latest?cb=20170219125728' : json.imgUrl
          this.sold = json.sold
          this.date = json.date

          let trimTags = json.tags.trim()
          this.tags = trimTags === '' ? [] : trimTags.split(/\s/)
          this.description = json.description

          const date = new Date(json.date)
          this.minute = date.getMinutes()
          this.hour = date.getHours()
          this.day = date.getDate()
          this.month = date.getMonth() + 1
          this.year = date.getFullYear()
        }),
        _ => this.hasData = false))
    }
  },
  created() {
    window.scrollTo(0, 0);
    this.updateClient()
  }
}
</script>
