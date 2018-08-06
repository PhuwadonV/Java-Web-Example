const state = {
  isActive: false,
  title: '',
  msg: ''
}

const mutations = {
  show(state, {title, msg}) {
    state.title = title
    state.msg = msg
    state.isActive = true
  },
  close(state) {
    state.isActive = false
    state.title = ''
    state.msg = ''
  }
}

export default {
  namespaced: true,
  state,
  mutations
}
