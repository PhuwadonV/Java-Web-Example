const state = {
  mode: 0,
  pageMode:[
    {
      pageInput: '1',
      totalPage: 1,
      currentPage: 1
    },
    {
      pageInput: '1',
      totalPage: 1,
      currentPage: 1
    },
    {
      pageInput: '1',
      totalPage: 1,
      currentPage: 1
    }
  ]
}

const mutations = {
  mode(state, value) {
    state.mode = value
  },
  pageInput(state, {index, value}) {
    state.pageMode[index].pageInput = value
  },
  totalPage(state, {index, value}) {
    state.pageMode[index].totalPage = value
  },
  currentPage(state, {index, value}) {
    state.pageMode[index].currentPage = value
  }
}

export default {
  namespaced: true,
  state,
  mutations
}
