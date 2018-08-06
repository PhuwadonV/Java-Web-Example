<template>
  <a :href="'/#/cartDetail/' + this.id">
    <div style="margin-bottom: 20px">
      <div class="box" style="display: flex; flex-wrap: wrap; justify-content: center; background-color: hsl(0, 0%, 86%);">
        <div style="margin-right: 10px; margin-bottom: 10px; flex-grow: 1">
          <table class="table is-bordered is-narrow">
            <col max-width="100%">
            <col width="100%">
            <tr>
              <td>ID</td>
              <td style="word-break: break-all;">{{ id }}</td>
            </tr>
            <tr v-if="permissionLevel.data > 1">
              <td>Account</td>
              <td style="word-break: break-all;">{{ accountId }}</td>
            </tr>
            <tr v-if="permissionLevel.data > 1">
              <td>Email</td>
              <td>{{ email }}</td>
            </tr>
            <tr>
              <td style="white-space: nowrap">Total Price (Bath)</td>
              <td>{{ totalPrice }}</td>
            </tr>
            <tr>
              <td>Date</td>
              <td>{{ day + "/" + month + "/" + year + " - " + hour + ":" + (minute < 10 ? "0" + minute : minute) }}</td>
            </tr>
          </table>
        </div>
        <div>
          <p align="center">Bill image</p>
          <img style="max-width: 300px; max-height: 145px;" :src="billImgUrl">
        </div>
      </div>
    </div>
  </a>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'Cart',
  props: ['mode', 'id', 'accountId', 'email', 'totalPrice', 'date', 'billImgUrl'],
  data() {
    return {
      minute: 0,
      hour: 0,
      day: 0,
      month: 0,
      year: 0
    }
  },
  computed: {
    ...mapState({
      permissionLevel: state => state.account.permissionLevel
    })
  },
  methods: {
    updateClient() {
      const date = new Date(this.date)
      this.minute = date.getMinutes()
      this.hour = date.getHours()
      this.day = date.getDate()
      this.month = date.getMonth() + 1
      this.year = date.getFullYear()
    }
  },
  created() {
    this.updateClient()
  }
}
</script>
