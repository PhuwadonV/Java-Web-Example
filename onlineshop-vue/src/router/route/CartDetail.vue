<template>
  <div>
    <OkCancelPopup :isActive="cancelPopup.isActive" :title="'Cancel cart'" :onOk="cancelPopupOnOk" :onCancel="cancelPopupOnCancel" :disabledOk="disabledCancelPopupOk">
      <p>Do you want to cancel this cart</p>
    </OkCancelPopup>
    <OkCancelPopup :isActive="popup.isActive" :title="'Bill image'" :onOk="popup.onOk" :onCancel="popupOnCancel" :disabledOk="disabledPopupOk">
      <div align="center">
        <img :src="previewImgSrc" style="max-width: 300px; max-height: 300px">
      </div>
      <form id="uploadBillForm">
        <div class="file">
          <label class="file-label">
            <input class="file-input" name="image" type="file" accept="image/*" @change="onSelectedImg">
            <span class="file-cta">
              <span class="file-icon">
                <i class="fas fa-upload"></i>
              </span>
              <span class="file-label">Choose a file…</span>
            </span>
          </label>
        </div>
      </form>
    </OkCancelPopup>
    <div class="box">
      <div class="level">
        <div class="level-left">
          <div class="level-item">
            <button class='button is-info' type="button" @click="updateClient">Refresh</button>
          </div>
        </div>
        <div class="level-right" v-if="permissionLevel.data == 1 && Math.abs(confirm) < 0.5">
          <div class="level-item">
            <button class='button is-info' style="margin-right: 10px" type="button" @click="uploadBill">Upload Bill</button>
            <button class='button is-danger' type="button" @click="cancel">Cancel</button>
          </div>
        </div>
        <div class="level-right" v-if="permissionLevel.data > 1">
          <div class="level-item">
            <button class='button is-info' style="margin-right: 10px" type="button" :disabled="disableConfirm" @click="onConfirm" v-if="Math.abs(confirm) < 0.5">Confirm</button>
            <button class='button is-info' style="margin-right: 10px" type="button" :disabled="disableUnconfirm" @click="onUnconfirm" v-else>Unconfirm</button>
            <button class='button is-danger' type="button" @click="cancel">Cancel</button>
          </div>
        </div>
      </div>
    </div>
    <p class="title" align="center">Bill image</p>
    <img style="display: block; margin: auto;" :src="billImgUrl">
    <div class="box">
      <table class="table is-bordered is-narrow">
        <col max-width="100%">
        <col width="100%">
        <tr>
          <td>ID</td>
          <td style="word-break: break-all;">{{ id }}</td>
        </tr>
        <tr v-if="permissionLevel.data > 1">
          <td style="white-space: nowrap">Account ID</td>
          <td style="word-break: break-all;">{{ accountId }}</td>
        </tr>
        <tr v-if="permissionLevel.data > 1">
          <td>Email</td>
          <td>{{ email }}</td>
        </tr>
        <tr v-if="permissionLevel.data > 1">
          <td style="white-space: nowrap">Phone Number</td>
          <td>{{ phoneNumber }}</td>
        </tr>
        <tr>
          <td style="white-space: nowrap">Total Price (Bath)</td>
          <td>{{ totalPrice }}</td>
        </tr>
        <tr>
          <td>Comfirmed</td>
          <td>{{ Math.abs(confirm) > 0.5 ? 'Yes' : 'No' }}</td>
        </tr>
        <tr>
          <td>Address</td>
          <td>{{ address }}</td>
        </tr>
        <tr>
          <td>Date</td>
          <td>{{ day + "/" + month + "/" + year + " - " + hour + ":" + (minute < 10 ? "0" + minute : minute) }}</td>
        </tr>
      </table>
      <p class="title" align="center">Products</p>
      <a v-for="product in products" :href="'/#/productDetail/' + product.productId">
      <div style="margin-bottom: 20px">
        <div class="box" style="background-color: hsl(0, 0%, 86%);">
          <table class="table is-bordered is-narrow">
            <col max-width="100%">
            <col width="100%">
            <tr>
              <td>ID</td>
              <td style="word-break: break-all;">{{ product.productId }}</td>
            </tr>
            <tr>
              <td>Name</td>
              <td>{{ product.productName }}</td>
            </tr>
            <tr>
              <td>Amount</td>
              <td>{{ product.amount }}</td>
            </tr>
            <tr>
              <td>Price (Bath)</td>
              <td>{{ product.price }}</td>
            </tr>
          </table>
        </div>
      </div>
      </a>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import Sync from '@/js/Sync.js'
import OkCancelPopup from '@/components/OkCancelPopup'

export default {
  name: 'CartDetail',
  components: {
    OkCancelPopup
  },
  data() {
    return {
      id: '',
      accountId: '',
      totalPrice: 0,
      address: '',
      email: '',
      phoneNumber: '',
      products: [],
      confirm: 0,
      billImgUrl: 'https://vignette.wikia.nocookie.net/simpsons/images/6/60/No_Image_Available.png/revision/latest?cb=20170219125728',
      date: 0,
      minute: 0,
      hour: 0,
      day: 0,
      month: 0,
      year: 0,
      popup: {
        isActive: false,
        onOk() {}
      },
      cancelPopup: {
        isActive: false
      },
      previewImgSrc: null,
      imgType: '',
      disabledPopupOk: false,
      disabledCancelPopupOk: false,
      disableConfirm: false,
      disableUnconfirm: false
    }
  },
  computed: {
    ...mapState({
      permissionLevel: state => state.account.permissionLevel
    })
  },
  methods: {
    ...mapMutations({
      alert: 'alert/show'
    }),
    onConfirm() {
      this.disableConfirm = true
      this.PostText('/api/cart/confirm', this.id)
      .then(this.ResponseMatch(
        _ => {
          this.updateClient()
          this.disableConfirm = false
        },
        _ => {
          this.alert({title: 'Failed', msg: 'Please refresh and try again'})
          this.disableConfirm = false
        }))
    },
    onUnconfirm() {
      this.disableUnconfirm = true
      this.PostText('/api/cart/unconfirmed', this.id)
      .then(this.ResponseMatch(
        _ => {
          this.updateClient()
          this.disableUnconfirm = false
        },
        _ => {
          this.alert({title: 'Failed', msg: 'Please refresh and try again'})
          this.disableUnconfirm = false
        }))
    },
    cancel() {
      this.cancelPopup.isActive = true
    },
    async cancelPopupOnOk() {
      this.disabledCancelPopupOk = true
      const permissionLevel = await Sync.WaitValue(this.permissionLevel)
      this.PostText('/api/cart/cancel' + (permissionLevel == 1 ? 'FromUser' : ''), '' + this.id)
      .then(this.ResponseMatch(
        _ => this.$router.replace('/carts'),
        _ => {
          this.alert({title: 'Failed', msg: 'Please refresh and try again'})
          this.disabledCancelPopupOk = false
        }))
    },
    cancelPopupOnCancel() {
      this.cancelPopup.isActive = false
    },
    sendBillImage() {
      this.disabledPopupOk = true
      let formData = new FormData(uploadBillForm)
      formData.append('imageType', this.imgType)

      this.PostFormData('/api/cart/updateBillImg/' + this.id, formData)
      .then(this.ResponseMatch(
        _ => {
          this.alert({title: 'Success', msg: 'Uploaded'})
          this.updateClient();
          this.popupOnCancel()
          this.disabledPopupOk = false
        },
        _ => {
          this.alert({title: 'Failed', msg: 'Please refresh and try again'})
          this.disabledPopupOk = false
        }
      ))
    },
    onSelectedImg(e) {
      const file = e.target.files[0]
      this.previewImgSrc = window.URL.createObjectURL(file)
      this.imgType = file.type
    },
    popupOnCancel() {
      this.popup.isActive = false
      this.previewImgSrc = null
      this.imgType = ''
    },
    uploadBill() {
      this.popup.isActive = true
      this.popup.onOk = this.sendBillImage
    },
    async updateClient() {
      this.ChangeRouteIfPermissionLevel(level => level < 1)
      const permissionLevel = await Sync.WaitValue(this.permissionLevel)
      this.Get('/api/cart/get' + (permissionLevel == 1 ? 'FromUser' : '') + '/' + this.$route.params.cartId.replace(/\s+/g, ''))
      .then(this.ResponseMatch(resp => resp.json()
      .then(json => {
        if(permissionLevel > 1) {
          this.Get('/api/account/get/' + json.accountId)
            .then(this.ResponseMatch(resp => resp.json()
            .then(json => this.phoneNumber = json.phoneNumber)))
        }
        this.id = json.id
        this.accountId = json.accountId
        this.email = json.email
        this.totalPrice = json.totalPrice
        this.confirm = json.confirm
        this.address = json.address
        this.products = json.products
        this.billImgUrl = json.billImgUrl === '' ? 'https://vignette.wikia.nocookie.net/simpsons/images/6/60/No_Image_Available.png/revision/latest?cb=20170219125728' : json.billImgUrl

        const date = new Date(json.date)
        this.minute = date.getMinutes()
        this.hour = date.getHours()
        this.day = date.getDate()
        this.month = date.getMonth() + 1
        this.year = date.getFullYear()
      }),
      _ => this.$router.replace('/')))
    }
  },
  created() {
    window.scrollTo(0, 0);
    this.updateClient()
  }
}
</script>
