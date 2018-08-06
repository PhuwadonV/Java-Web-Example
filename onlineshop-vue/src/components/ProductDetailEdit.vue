<template>
  <div>
    <OkCancelPopup :isActive="changeAmountData.isActive" :title="changeAmountData.title" :onOk="changeAmountData.onOk" :onCancel="changeAmountOnCancel" :disabledOk="disabledChangeAmountPopupOk">
      <input class="input" type="text" v-model="changeAmountData.input" @keyup="validateChangeAmountInput">
    </OkCancelPopup>
    <OkCancelPopup :isActive="popup.isActive" :title="popup.title" :onOk="popup.onOk" :onCancel="popupOnCancel" :disabledOk="disabledPopupOk">
      <p>{{ popup.body }}</p>
    </OkCancelPopup>
    <form class="section" id="updateProductForm" v-if="hasData">
      <div class="field">
        <div class="level">
          <div class="level-left">
            <div class="level-item">
              <button class="button is-link" type="button" style="margin-right: 10px;" @click="askAddAmount">Add Amount</button>
              <button class="button is-link" type="button" @click="askSubtractAmount">Subtract Amount</button>
            </div>
          </div>
        </div>
      </div>
      <div class="field">
        <label class="label">ID</label>
        <div class="control">
          <p>{{ this.id }}</p>
        </div>
      </div>
      <div class="field">
        <label class="label">Name</label>
        <div class="control">
          <input class="input" name="name" type="text" v-model="name">
        </div>
      </div>
      <div class="field">
        <label class="label">Price (Bath)</label>
        <div class="control">
          <input class="input" name="price" type="text" v-model="price" @keyup="validateNonNagativeNumber('price')">
        </div>
      </div>
      <div class="field">
        <label class="label">Amount</label>
        <div class="control">
          <input class="input" name="amount" type="text" v-model="amount" @keyup="validateNonNagativeNumber('amount')">
        </div>
      </div>
      <div class="box">
        <div class="field">
          <div class="level">
            <div class="level-left">
              <div class="level-item">
                <label class="label" style="margin-right: 10px;">Tag</label>
                <input class="input" type="text" v-model="tag">
              </div>
              <div class="level-item">
                <button class="button is-link" type="button" style="margin-right: 10px;" @click="addTag">Add</button>
                <button class="button is-link" type="button" @click="clearTags">Clear</button>
              </div>
            </div>
          </div>
        </div>
        <div class="field is-grouped is-grouped-multiline">
          <p class="control" v-for="(value, index) in tags">
            <button class="button" type="button" @click="removeTag(index)">{{ value }}</button>
          </p>
        </div>
      </div>
      <div class="field">
        <label class="label">Description</label>
        <div class="control">
          <textarea class="textarea" name="description" v-model="description" form="updateProductForm"></textarea>
        </div>
      </div>
      <div class="field">
        <label class="label">Sold</label>
        <div class="control">
          <input class="input" name="sold" type="text" v-model="sold" @keyup="validateNonNagativeNumber('sold')">
        </div>
      </div>
      <div class="field">
        <label class="label">Minimum Stock</label>
        <div class="control">
          <input class="input" name="minimumStock" type="text" v-model="minimumStock" @keyup="validateNonNagativeNumber('minimumStock')">
        </div>
      </div>
      <div class="field">
        <img :src="previewImgSrc" style="max-width: 300px; max-height: 300px">
      </div>
      <div class="field">
        <div class="file">
          <label class="file-label">
            <input class="file-input" id="imageInput" name="image" type="file" accept="image/*" @change="onSelectedImg">
            <span class="file-cta">
              <span class="file-icon">
                <i class="fas fa-upload"></i>
              </span>
              <span class="file-label">Choose a file…</span>
            </span>
            <button class="button is-danger" type="button" v-if="previewImgSrc !== ''" @click="removeImg">Remove Image</button>
          </label>
        </div>
      </div>
      <div class="field">
        <label class="label">Date</label>
        <div class="control">
          <p>{{ day + "/" + month + "/" + year + " - " + hour + ":" + (minute < 10 ? "0" + minute : minute) }}</p>
        </div>
      </div>
      <div class="field">
        <label class="label">New Date</label>
        <div class="control">
          <input class="input" type="date" v-model="newDate">
        </div>
      </div>
      <div class="field">
        <button class="button is-link" type="button" :disabled="!enabledUpdate" @click="updateServer">Update</button>
        <button class='button is-info' type="button" @click="askRefreshData">Refresh</button>
        <button class='button is-danger' type="button" @click="askRemove">Remove</button>
      </div>
    </form>
    <div class="box" v-else>
      <p class="has-text-centered title is-1">Product not found</p>
    </div>
  </div>
</template>

<script>
import { mapMutations } from 'vuex'
import OkCancelPopup from '@/components/OkCancelPopup'

export default {
  name: 'ProductDetailEdit',
  components: {
    OkCancelPopup
  },
  props: ['id'],
  data() {
    return {
      popup: {
        isActive: false,
        title: 'Warning !',
        body: '',
        onOk() {}
      },
      changeAmountData: {
        title: '',
        isActive: false,
        onOk() {},
        input: '0'
      },
      hasData: true,
      name: '',
      price: '0',
      amount: '0',
      imgUrl: '',
      minimumStock: '0',
      sold: '0',
      tag: '',
      tags: [],
      description: '',
      date: 0,
      previewImgSrc: '',
      imgType: '',
      newDate: 0,
      minute: 0,
      hour: 0,
      day: 0,
      month: 0,
      year: 0,
      updatedTime: -1,
      enabledUpdate: true,
      disabledChangeAmountPopupOk: false,
      disabledPopupOk: false
    }
  },
  methods: {
    ...mapMutations({
      alert: 'alert/show'
    }),
    addAmount() {
      this.disabledChangeAmountPopupOk = true
      const addedAmount = Number(this.changeAmountData.input)
      this.PostJson('/api/product/addAmount/' + this.id, addedAmount)
        .then(this.ResponseMatch(_ => {
          this.changeAmountData.isActive = false
          this.alert({title: 'Success', msg: 'Amount Updated'})
          this.updateClient()
          this.disabledChangeAmountPopupOk = false
        }, _ => this.disabledChangeAmountPopupOk = false))
    },
    subtractAmount() {
      this.disabledChangeAmountPopupOk = true
      const subtractedAmount = -Number(this.changeAmountData.input)
      this.PostJson('/api/product/addAmount/' + this.id, subtractedAmount)
        .then(this.ResponseMatch(resp => resp.json()
        .then(json => {
          this.changeAmountData.isActive = false
          if(json == 0) this.alert({title: 'Success', msg: 'Amount Updated'})
          else if(json == 1) this.alert({title: 'Failed', msg: 'Amount is least than zero'})
          this.updateClient()
          this.disabledChangeAmountPopupOk = false
        }), _ => this.disabledChangeAmountPopupOk = false))
    },
    askAddAmount() {
      const changeAmountData = this.changeAmountData
      changeAmountData.title = 'Add Amount'
      changeAmountData.input = '0'
      changeAmountData.onOk = this.addAmount
      changeAmountData.isActive = true
    },
    askSubtractAmount() {
      const changeAmountData = this.changeAmountData
      changeAmountData.title = 'Subtract Amount'
      changeAmountData.input = '0'
      changeAmountData.onOk = this.subtractAmount
      changeAmountData.isActive = true
    },
    popupOnCancel() {
      this.popup.isActive = false
    },
    changeAmountOnCancel() {
      this.changeAmountData.isActive = false
    },
    refreshData() {
      this.popup.isActive = false
      this.updateClient()
    },
    askRefreshData() {
      const popup = this.popup
      popup.body = 'Do you want to refresh the data.'
      popup.onOk = this.refreshData
      popup.isActive = true
    },
    remove() {
      this.disabledPopupOk = true
      this.DeleteText('/api/product/delete', this.id)
      .then(this.ResponseMatch(_ => this.$router.replace('/'),
      _ => {
        this.alert({title: 'Failed', msg: 'Please try again'})
        this.disabledPopupOk = false
      }));
    },
    askRemove() {
      const popup = this.popup
      popup.body = 'Do you want to remove this product.'
      popup.onOk = this.remove
      popup.isActive = true
    },
    onSelectedImg(e) {
      const file = e.target.files[0]
      this.fileName = file.name
      this.previewImgSrc = window.URL.createObjectURL(file)
      this.imgType = file.type
    },
    addTag() {
      this.ValidateAndAddTag(this.tag, this.tags, x => this.tags.push(x))
      this.tag = ''
    },
    removeTag(index) {
      this.tags.splice(index, 1)
    },
    clearTags() {
      this.tags = null
    },
    removeImg() {
      this.previewImgSrc = ''
      this.imgType = ''
      imageInput.value = null
    },
    validateNonNagativeNumber(property) {
      this[property] = this.ValidateNonNagativeNumber(this[property]);
    },
    validateChangeAmountInput() {
      this.changeAmountData.input = this.ValidateNonNagativeNumber(this.changeAmountData.input)
    },
    updateDate() {
      const date = new Date(this.date)
      this.minute = date.getMinutes()
      this.hour = date.getHours()
      this.day = date.getDate()
      this.month = date.getMonth() + 1
      this.year = date.getFullYear()
    },
    updateClient() {
      this.ChangeRouteIfPermissionLevel(level => level < 2)

      this.Get('/api/product/' + this.$route.params.productId.replace(/\s+/g, ''), { cache: 'reload' })
      .then(this.ResponseMatch(resp => resp.json()
        .then(json => {
          this.name = json.name
          this.price = String(json.price)
          this.amount = String(json.amount)
          this.imgUrl = json.imgUrl
          this.previewImgSrc = json.imgUrl
          this.minimumStock = String(json.minimumStock)
          this.sold = String(json.sold)
          this.date = json.date
          this.updatedTime = json.updatedTime

          let trimTags = json.tags.trim()
          this.tags = trimTags === '' ? [] : trimTags.split(/\s/)
          this.description = json.description

          this.updateDate()
        }),
        _ => this.hasData = false))
    },
    updateServer() {
      this.enabledUpdate = false

      if(this.name === '') {
        this.alert({title: 'Error', msg: 'Please specify name'})
        this.enabledUpdate = true
        return
      }

      const isNewDate = this.newDate !== 0
      const newDate = isNewDate ? new Date(this.newDate).getTime() : this.date

      let formData = new FormData(updateProductForm)
      formData.append('id', this.id)
      formData.append('tags', this.tags.length === 0 ? '' : this.tags.reduce((left, right) => left + " " + right))
      formData.append('isNewImg', this.previewImgSrc === this.imgUrl ? false : true)
      formData.append('imageType', this.imgType)
      formData.append('image', imageInput.value)
      formData.append('isNewDate', isNewDate)
      formData.append('date', newDate)
      formData.append('lastUpdatedTime', this.updatedTime)

      this.PostFormData('/api/product/update', formData)
      .then(this.ResponseMatch(resp => resp.json()
      .then(json => {
        if(json >= 0) {
          this.updatedTime = json
          this.newDate = 0
          this.date = newDate + (new Date().getTime() % 86400000)
          this.updateDate()
          this.alert({title: 'Success', msg: 'Updated'})
          this.enabledUpdate = true
        }
        else {
          this.enabledUpdate = true
          this.alert({title: 'Failed', msg: 'Please refresh data'})
        }
      })))
    }
  },
  created() {
    window.scrollTo(0, 0);
    this.updateClient()
  }
}
</script>
