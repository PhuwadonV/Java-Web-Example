import Sync from '@/js/Sync.js'

const state = {
  products: []
}

const mutations = {
  set(state, value) {
    state.products = value
  },
  addItem(state, value) {
    state.products.push(value)
  },
  setAmount(state, {index, value}) {
    state.products[index].amount = value
  },
  removeItem(state, value) {
    state.products.splice(value, 1)
  },
  removeItemFromId(state, id) {
    const products = state.products
    const length = products.length
    for(let i = 0; i < length; i++) {
      if(products[i].id === id) {
        products.splice(i, 1)
        break
      }
    }
  },
  addAmount(state, {index, value}) {
    state.products[index].amount += value
  },
  updateFromJson(state, json) {
    const products = state.products
    const length = products.length
    for(let i = 0; i < length; i++) {
      let product = products[i]
      if(product.id === json.id) {
        product.name = json.name
        product.price = json.price
        product.maxAmount = json.amount
        if(json.amount === 0) this.commit('cart/removeItem', i)
        break
      }
    }
  }
}

const actions = {
  update({ commit, state }, {self, onComplete}) {
    const products = state.products
    const length = products.length
    let count = 0
    for(let i = 0; i < length; i++) {
      const productId = products[i].id
      self.Get('/api/product/' + productId, { cache: 'reload' })
      .then(self.ResponseMatch(resp => resp.json()
      .then(json => {
        commit('updateFromJson', json)
        count++
      }),
      _ => {
        commit('removeItemFromId', productId)
        count++
      }))
    }

    Sync.WaitUntil(() => count === length, () => {
      sessionStorage.setItem('cart', JSON.stringify(state.products))
      onComplete()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
