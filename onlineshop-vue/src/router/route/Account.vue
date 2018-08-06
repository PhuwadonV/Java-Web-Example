<template>
  <section class="section">
    <div class="field">
      <label class="label">Name</label>
      <div class="control">
        <input class="input" type="text" v-model="account.name" :disabled="disabled">
      </div>
    </div>
    <div class="field">
      <label class="label">Phone Number</label>
      <div class="control has-icons-left">
        <input class="input" type="tel" v-model="account.phoneNumber" @focusout="validatePhoneNumber" :disabled="disabled">
        <span class="icon is-left">
          <i class="fas fa-phone"></i>
        </span>
      </div>
    </div>
    <div class="field">
      <label class="label">Address</label>
      <div class="control">
        <textarea name="address" class="textarea" v-model="account.address" :disabled="disabled"></textarea>
      </div>
    </div>
    <button class="button" type="button" @click="updateServer" :disabled="disabled">Update</button>
        <button class="button" type="button" @click="updateClient">Reset</button>
  </section>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import Sync from '@/js/Sync.js'

export default {
  name: 'Account',
  data () {
    return {
      disabled: true,
      account: {
        name: '',
        phoneNumber: '',
        address: ''
      }
    }
  },
  computed: {
    ...mapState({
      accountInfo: state => state.account.info
    })
  },
  methods: {
    ...mapMutations({
      setAccountInfo: 'account/info',
      alert: 'alert/show'
    }),
    getJson() {
      let account = this.account
      return {
        name: account.name,
        phoneNumber: account.phoneNumber,
        address: account.address,
      }
    },
    setJson(json) {
      let account = this.account
      account.name = json.name
      account.phoneNumber = json.phoneNumber
      account.address = json.address
    },
    async updateClient() {
      this.ChangeRouteIfPermissionLevel(level => level != 1)
      const info = await Sync.WaitValue(this.accountInfo)
      this.setJson(info)
      this.disabled = false;
    },
    updateServer() {
      const json = this.getJson()
      this.PutJson('/api/account/put', json)
        .then(this.ResponseMatch(_ => {
          this.setAccountInfo({ status: true, value: json })
          this.alert({title: 'Success', msg: 'Updated'})
        }, _ => this.alert({title: 'Error', msg: 'Update Failed'})))
    },
    validatePhoneNumber() {
      const account = this.account
      account.phoneNumber = this.ValidatePhoneNumber(account.phoneNumber)
    }
  },
  created() {
    this.updateClient()
  }
}
</script>
