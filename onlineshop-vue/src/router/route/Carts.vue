<template>
  <div>
    <div class="box">
      <div class="level">
        <div class="level-left">
          <div class="level-item">
            <button class='button is-info' type="button" @click="refresh">Refresh</button>
          </div>
        </div>
        <div class="level-right">
          <div class="level-item">
            <label class="label" style="margin-right: 10px;">Page ({{ pageMode[mode].totalPage }})</label>
            <input class="input" style="margin-right: 10px; width: 70px; text-align: right;" type="text" v-model="pageMode[mode].pageInput" @keyup="validatePossitiveNumberOnKeyup" @focusout="validatePossitiveNumber">
            <button class="button is-link" style="margin-right: 10px;" type="button" :disabled="pageMode[mode].currentPage === 1" @click="previousPage">Previous</button>
            <button class="button is-link" style="margin-right: 10px;" type="button" :disabled="pageMode[mode].currentPage === pageMode[mode].totalPage" @click="nextPage">Next</button>
          </div>
        </div>
      </div>
    </div>
    <div class="tabs is-centered">
      <ul>
        <li :class="{ 'is-active': mode == 0 }" @click="selectMode(0)"><a>Payment</a></li>
        <li :class="{ 'is-active': mode == 1 }" @click="selectMode(1)"><a>Unconfirmed</a></li>
        <li :class="{ 'is-active': mode == 2 }" @click="selectMode(2)"><a>Confirmed</a></li>
      </ul>
    </div>
    <div class="box">
      <CartElem v-for="(cart, index) in carts"
        :key="index"
        :mode="mode"
        :id="cart.id"
        :accountId="cart.accountId"
        :email="cart.email"
        :totalPrice="cart.totalPrice"
        :confirm="cart.confirm"
        :date="cart.date"
        :billImgUrl="cart.billImgUrl === '' ? 'https://vignette.wikia.nocookie.net/simpsons/images/6/60/No_Image_Available.png/revision/latest?cb=20170219125728' : cart.billImgUrl"/>
      <div align="center">
        <button class="button is-link" style="margin-bottom: 20px;" type="button" onclick="window.scrollTo(0, 0)">Go to top</button>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import Sync from '@/js/Sync.js'
import CartElem from '@/components/Cart'

export default {
  name: 'Carts',
  components: {
    CartElem
  },
  data () {
    return {
      carts: [],
      itemPerPage: 10
    }
  },
  computed: {
    ...mapState({
      permissionLevel: state => state.account.permissionLevel,
      mode: state => state.carts.mode,
      pageMode: state => state.carts.pageMode
    })
  },
  methods: {
    ...mapMutations({
      setCartsNoti: 'notification/carts',
      setMode: 'carts/mode',
      setPageInput: 'cart/pageInput',
      setTotalPage: 'carts/totalPage',
      setCurrentPage: 'carts/totalPage'
    }),
    selectMode(mode) {
      if(this.mode === mode) return
      this.setMode(mode)
      this.updateClient()
    },
    refresh() {
      const mode = this.mode
      this.setCurrentPage({ index: mode, value: Number(this.pageMode[mode].pageInput) })
      this.updateClient()
    },
    previousPage() {
      const mode = this.mode
      const pageMode = this.pageMode[mode]
      this.setCurrentPage({ index: mode, value: pageMode.currentPage - 1})
      this.setPageInput({ index: mode, value: String(pageMode.currentPage)})
      this.updateClient()
    },
    nextPage() {
      const mode = this.mode
      const pageMode = this.pageMode[mode]
      this.setCurrentPage({ index: mode, value: pageMode.currentPage + 1})
      this.setPageInput({ index: mode, value: String(pageMode.currentPage)})
      this.updateClient()
    },
    validatePossitiveNumber() {
      const mode = this.mode
      const pageMode = this.pageMode[mode]
      if(pageMode.pageInput === '') this.setPageInput({ index: mode, value: '1'})
      else if(property === 'pageInput') {
        const num = Number(pageMode.pageInput)
        if(num > pageMode.totalPage) this.setPageInput({ index: mode, value: String(pageMode.totalPage)})
      }
    },
    validatePossitiveNumberOnKeyup() {
      const mode = this.mode
      const pageMode = this.pageMode[mode]
      this.setPageInput({ index: mode, value: this.ValidatePossitiveNumberOnKeyup(pageMode.pageInput)})
    },
    countPage(queryMode) {
      const mode = this.mode
      const pageMode = this.pageMode[mode]
      this.Get('/api/cart/count' + queryMode, { cache: 'reload' })
      .then(this.ResponseMatch(resp => resp.json()
      .then(json => {
        const totalPage = Math.floor((json - 1) / this.itemPerPage) + 1
        if(totalPage < 1) this.setTotalPage({ index: mode, value: 1 })
        else this.setTotalPage({ index: mode, value: totalPage })
      })))
    },
    async updateClient() {
      this.ChangeRouteIfPermissionLevel(level => level < 1)

      this.setCartsNoti(false)
      const pageMode = this.pageMode[this.mode]
      let queryMode = ''
      switch (this.mode) {
        case 0:
          queryMode = 'Payment'
          break;
        case 1:
          queryMode = 'Unconfirmed'
          break;
        case 2:
          queryMode = 'Confirmed'
          break;
      }
      const permissionLevel = await Sync.WaitValue(this.permissionLevel)
      if(permissionLevel == 1) queryMode += 'FromUser'
      this.countPage(queryMode)
      this.Get('/api/cart/search' + queryMode + '/' + ((pageMode.currentPage - 1) * this.itemPerPage) + '/' + this.itemPerPage)
      .then(this.ResponseMatch(resp => resp.json()
      .then(json => this.carts = json)))
    }
  },
  created() {
    window.scrollTo(0, 0);
    this.updateClient()
  }
}
</script>
