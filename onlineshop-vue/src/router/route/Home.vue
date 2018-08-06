<template>
  <div>
    <div class="box is-pinned">
      <div class="level">
        <div class="level-left">
          <div class="level-item">
            <label class="label">Name</label>
          </div>
          <div class="level-item">
            <input class="input" type="text" v-model="searchInput">
          </div>
          <div class="level-item">
            <button class="button is-link" type="button" style="margin-right: 10px;" @click="search">Search</button>
            <button class="button is-link" type="button" style="margin-right: 10px;" @click="clearSearch">Clear</button>
            <label class="checkbox">
              <input type="checkbox" v-model="onlyInStock">
              Only in-stock
            </label>
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
    <div class="container" style="margin-lift: 10px; margin-right:10px;">
      <div class="level">
        <div class="level-left">
          <div class="level-item">
            <label class="label" style="margin-left: 10px; margin-right: 10px;">Tag</label>
            <input class="input" type="text" v-model="tag">
          </div>
          <div class="level-item">
            <button class="button is-link" type="button" style="margin-right: 10px;" @click="addTag">Add</button>
            <button class="button is-link" type="button" style="margin-right: 10px;" @click="clearTags">Clear</button>
            <div class="buttons has-addons">
              <span class="button" :class="{ 'is-info': isTagIncludeAll }"  @click="changeTagIncludeAll(true)">All</span>
              <span class="button" :class="{ 'is-info': !isTagIncludeAll }" @click="changeTagIncludeAll(false)">Exist</span>
            </div>
          </div>
        </div>
      </div>
      <div class="buttons">
         <span class="button" type="button" v-for="(value, index) in currentTags" @click="removeTag(index)">{{ value }}</span>
      </div>
    </div>
    <div class="section">
      <h1 class="title">{{ title }}</h1>
      <div style="display: flex; flex-wrap: wrap;">
        <Product v-for="(product, index) in products"
          :key="index"
          :id="product.id"
          :name="product.name"
          :price="product.price"
          :amount="product.amount"
          :sold="product.sold"
          :date="product.date"
          :imgUrl="product.imgUrl === '' ? 'https://vignette.wikia.nocookie.net/simpsons/images/6/60/No_Image_Available.png/revision/latest?cb=20170219125728' : product.imgUrl"
          :updateFx="updateProducts"/>
      </div>
    </div>
    <div align="center">
      <button class="button is-link" style="margin-bottom: 20px;" type="button" onclick="window.scrollTo(0, 0)">Go to top</button>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import Product from '@/components/Product'

export default {
  name: 'Home',
  data () {
    return {
      title: 'Newest',
      searchInput: '',
      tag: '',
      isTagIncludeAll: true,
      itemPerPage: 12,
      pageInput: '1',
      totalPage: 1,
      products: [],
      onlyInStock: false
    }
  },
  components: {
    Product
  },
  computed: {
    ...mapState({
      currentSearch: state => state.home.search,
      currentPage: state => state.home.page,
      currentTags: state => state.home.tags
    })
  },
  methods: {
    ...mapMutations({
      setCurrentSearch: 'home/search',
      setCurrentPage: 'home/page',
      setCurrentTags: 'home/tags',
      addTagGlobal: 'home/addTag',
      removeTagGlobal: 'home/removeTag'
    }),
    countPage(queryString) {
      this.Get('/api/product/count' + (queryString === undefined ? '' : '/' + queryString), { cache: 'reload' })
        .then(this.ResponseMatch(resp => resp.json()
          .then(json => {
            this.totalPage = Math.floor((json - 1) / this.itemPerPage) + 1
            if(this.totalPage < 1) this.totalPage = 1
          })))
    },
    updateProducts() {
      const page = this.currentPage
      this.pageInput = String(page)
      if(this.currentSearch === '' && this.currentTags.length === 0) {
        this.title = 'Newest'
        this.countPage()
        this.Get('/api/product/newest' + (this.onlyInStock ? 'InStock' : '') + '/' + ((page - 1) * this.itemPerPage) + '/' + this.itemPerPage)
          .then(this.ResponseMatch(resp => resp.json()
            .then(json => {
              this.products = json
              this.setCurrentPage(page)
          })))
      }
      else {
          this.title = 'Search'
          const queryString = (this.currentSearch === '' ? '' : 'name: ' + this.currentSearch) +
          (this.currentTags.length === 0 ? '' : ' tags: ' +
          (this.isTagIncludeAll ? '' : '(') +
          this.currentTags.reduce((left, right) => left + (this.isTagIncludeAll ? ' ' : ' OR ') + right) +
          (this.isTagIncludeAll ? '' : ')')) +
          (this.onlyInStock ? ' NOT amount: 0' : '')

          this.countPage(queryString)
          this.PostJson('/api/product/search/',  {
            queryString: queryString,
            offset: ((page - 1) * this.itemPerPage),
            limit: this.itemPerPage })
            .then(this.ResponseMatch(resp => resp.json()
              .then(json => {
                this.products = json
                this.setCurrentPage(page)
            })))
      }
    },
    clearSearch() {
      this.searchInput = ''
      this.onlyInStock = false
      this.search()
    },
    search() {
      const search = this.searchInput.replace(/[^\w|\s|"]/g, ' ').replace(/\s+/g, ' ').toLowerCase().trim()
      if(search === this.currentSearch) {
        this.setCurrentPage(Number(this.pageInput))
        this.updateProducts()
      }
      else {
        this.pageInput = '1'
        this.setCurrentPage(1)
        this.setCurrentSearch(search)
        this.updateProducts()
      }
      this.searchInput = this.currentSearch
    },
    addTag() {
      this.ValidateAndAddTag(this.tag, this.currentTags, this.addTagGlobal)
      this.tag = ''
    },
    removeTag(index) {
      this.removeTagGlobal(index)
    },
    clearTags() {
      this.setCurrentTags([])
    },
    changeTagIncludeAll(value) {
      this.isTagIncludeAll = value
    },
    previousPage() {
      this.setCurrentPage(this.currentPage - 1)
      this.updateProducts()
    },
    nextPage() {
      this.setCurrentPage(this.currentPage + 1)
      this.updateProducts()
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
    updateClient() {
      this.searchInput = this.currentSearch
      this.pageInput = '1'
      this.updateProducts()
    }
  },
  created() {
    window.scrollTo(0, 0)
    this.updateClient()
  }
}
</script>
