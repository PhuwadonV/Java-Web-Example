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
            <label class="label" style="margin-right: 10px;">Page ({{ totalPage }})</label>
            <input class="input" style="margin-right: 10px; width: 70px; text-align: right;" type="text" v-model="pageInput" @keyup="validatePossitiveNumberOnKeyup('pageInput')" @focusout="validatePossitiveNumber('pageInput')">
            <button class="button is-link" style="margin-right: 10px;" type="button" :disabled="currentPage === 1" @click="previousPage">Previous</button>
            <button class="button is-link" style="margin-right: 10px;" type="button" :disabled="currentPage === totalPage" @click="nextPage">Next</button>
          </div>
        </div>
      </div>
    </div>
    <div class="box">
      <div v-for="product in products" style="margin-bottom: 20px">
        <div class="box" style="display: flex; flex-wrap: wrap; background-color: hsl(0, 0%, 86%)">
          <div style="margin-right: 10px; margin-bottom: 10px; flex-grow: 1">
            <a :href="'/#/productDetail/' + product.id">
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
                  <td>Amount</td>
                  <td>{{ product.amount }}</td>
                </tr>
                <tr>
                  <td style="white-space: nowrap">Minimum Stock</td>
                  <td>{{ product.minimumStock }}</td>
                </tr>
              </table>
            </a>
          </div>
          <div>
            <div align="center"><button class="button is-danger" style="margin-bottom: 10px; width: 83px;" type="button" @click="remove(product.id)">Remove</button></div>
          </div>
        </div>
      </div>
      <div align="center">
        <button class="button is-link" style="margin-bottom: 20px;" type="button" onclick="window.scrollTo(0, 0)">Go to top</button>
      </div>
    </div>
  </div>
</template>

<script>
import { mapMutations } from 'vuex'

export default {
  name: 'MinimumStock',
  data () {
    return {
      products: [],
      itemPerPage: 10,
      pageInput: '1',
      totalPage: 1,
      currentPage: 1
    }
  },
  methods: {
    ...mapMutations({
      setMinimumStockNoti: 'notification/minimumStock',
      setHideMinimumStock: 'notification/hideMinimumStock'
    }),
    refresh() {
      this.currentPage = Number(this.pageInput)
      this.updateClient()
    },
    previousPage() {
      this.currentPage--
      this.pageInput = String(this.currentPage)
      this.updateClient()
    },
    nextPage() {
      this.currentPage++
      this.pageInput = String(this.currentPage)
      this.updateClient()
    },
    validatePossitiveNumber(property) {
      if(this[property] === '') this[property] = '1'
      else if(property === 'pageInput') {
        const num = Number(this.pageInput)
        if(num > this.totalPage) this.pageInput = String(this.totalPage)
      }
    },
    validatePossitiveNumberOnKeyup(property) {
      this[property] = this.ValidatePossitiveNumberOnKeyup(this[property]);
    },
    remove(id) {
      this.DeleteText('/api/minimumStock/delete', id)
        .then(this.ResponseMatch(_ => {
          this.updateClient()
        }))
    },
    countPage() {
      this.Get('/api/minimumStock/count', { cache: 'reload' })
      .then(this.ResponseMatch(resp => resp.json()
      .then(json => {
        if(json === 0) {
          this.setHideMinimumStock(true)
          this.$router.replace('/')
        }
        else {
          this.totalPage = Math.floor((json - 1) / this.itemPerPage) + 1
          if(this.totalPage < 1) this.totalPage = 1
        }
      })))
    },
    updateClient() {
      this.ChangeRouteIfPermissionLevel(level => level < 2)
      this.setMinimumStockNoti(false)
      this.countPage()
      this.Get('/api/minimumStock/search/' + ((this.currentPage - 1) * this.itemPerPage) + '/' + this.itemPerPage)
      .then(this.ResponseMatch(resp => resp.json()
      .then(json => this.products = json)))
    }
  },
  created() {
    window.scrollTo(0, 0);
    this.updateClient()
  }
}
</script>
