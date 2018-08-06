import Sync from '@/js/Sync.js'

const state = {
  permissionLevel: new Sync.WaitedValue(0),
  info: new Sync.WaitedValue()
}

const mutations = {
  permissionLevel(state, {status, value}) {
    state.permissionLevel.setStatus(status, value)
  },
  info(state, {status, value}) {
    const data = state.info.data
    if(data !== null) {
      value.id = data.id
      value.email = data.email
    }
    state.info.setStatus(status, value)
  }
}

const actions = {
  update({ commit }, self) {
    function updateInfo() {
      self.Get('/api/account', { cache: 'reload' }).then(self.ResponseMatch(
        resp => resp.json()
          .then(json => commit('info', { status: true, value: json }))),
        resp => {
            commit('info', { status: false, value: resp })
            self.LogError(resp)
          })
    }

    self.Get('/api/auth/permissionLevel', { cache: 'reload' }).then(self.ResponseMatch(
      resp => resp.json()
        .then(json => {
          commit('permissionLevel',  { status: true, value: json })
          if(json == 1) updateInfo()
          else commit('info', { status: false, value: 'Client is not loggin' })
        }),
      resp => {
          commit('permissionLevel',  { status: false, value: resp })
          self.LogError(resp)
      }))
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
