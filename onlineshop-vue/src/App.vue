<template>
  <div id="app">
    <Navbar/>
    <router-view/>
    <Alert>{{ alertMsg }}</Alert>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import Navbar from '@/components/Navbar'
import Alert from '@/components/Alert'

export default {
  name: 'App',
  components: {
    Navbar,
    Alert
  },
  computed: {
    ...mapState({
      alertMsg: state => state.alert.msg
    })
  },
  methods: {
    ...mapActions({
      updateAccount: 'account/update'
    })
  },
  created() {
    this.updateAccount(this)
  }
}
</script>

<style lang="scss">
  @import "@/style/override.scss";
  @import "bulma";
</style>
