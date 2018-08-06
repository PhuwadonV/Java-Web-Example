const state = {
  search: '',
  tags: [],
  page: 1
}

const mutations = {
  search(state, value) {
    state.search = value
  },
  tags(state, value) {
    state.tags = value
  },
  addTag(state, value) {
    state.tags.push(value)
  },
  removeTag(state, value) {
    state.tags.splice(value, 1)
  },
  page(state, value) {
    state.page = value
  }
}

export default {
  namespaced: true,
  state,
  mutations
}
