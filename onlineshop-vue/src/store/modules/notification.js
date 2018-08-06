const state = {
  minimumStock: false,
  hideMinimumStock: false,
  carts: false,
  cart: false
}

const mutations = {
  minimumStock(state, value) {
    state.minimumStock = value
  },
  hideMinimumStock(state, value) {
    state.hideMinimumStock = value
  },
  cart(state, value) {
    state.cart = value
  },
  carts(state, value) {
    state.carts = value
  },
}

export default {
  namespaced: true,
  state,
  mutations
}
