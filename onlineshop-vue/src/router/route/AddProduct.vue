<template>
  <form class="section" id="addProductForm">
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
        <textarea class="textarea" name="description" form="addProductForm"></textarea>
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
      <label class="label">Date</label>
      <div class="control">
        <input class="input" type="date" v-model="date">
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
          <button class="button is-danger" type="button" v-if="previewImgSrc !== null" @click="removeImg">Remove Image</button>
        </label>
      </div>
    </div>
    <div class="field">
      <button class="button is-link" type="button" :disabled="!enabledSubmit" @click="addProduct">Submit</button>
    </div>
  </form>
</template>

<script>
import { mapMutations } from 'vuex'

export default {
  name: 'AddProduct',
  data () {
    return {
      name: '',
      tag: '',
      tags: [],
      price: '0',
      amount: '0',
      sold: '0',
      minimumStock: '0',
      previewImgSrc: null,
      imgType: '',
      enabledSubmit: true,
      date: 0
    }
  },
  methods: {
    ...mapMutations({
      alert: 'alert/show'
    }),
    resetForm() {
      this.name = ''
      this.price = '0'
      this.amount = '0'
      this.sold = '0'
      this.minimumStock = '0'
      this.tag = ''
      this.tags = []
      this.removeImg()
    },
    onSelectedImg(e) {
      const file = e.target.files[0]
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
    addProduct(e) {
      this.enabledSubmit = false

      if(this.name === '') {
        this.alert({title: 'Error', msg: 'Please specify name'})
        this.enabledSubmit = true
        return
      }

      let formData = new FormData(addProductForm)
      formData.append('date', this.date === 0 ? 0 : new Date(this.date).getTime())
      formData.append('imageType', this.imgType)
      formData.append('tags', this.tags.length === 0 ? '' : this.tags.reduce((left, right) => left + " " + right))

      this.PostFormData('/api/product/add', formData)
      .then(this.ResponseMatch(resp => resp.text()
      .then(text => {
        if(text == '') this.alert({title: 'Failed', msg: 'Fail to add new product'})
        else {
          this.alert({title: 'Success', msg: 'Submited : ' + text})
          this.resetForm()
        }
        this.enabledSubmit = true
      }),
      _ => {
        this.alert({title: 'Failed', msg: 'Please try again' })
        this.enabledSubmit = true
      }))
    },
    removeImg() {
      this.previewImgSrc = null
      this.imgType = ''
      imageInput.value = null
    },
    validateNonNagativeNumber(property) {
      this[property] = this.ValidateNonNagativeNumber(this[property]);
    },
    updateClient() {
      this.ChangeRouteIfPermissionLevel(level => level < 2)
    }
  },
  created() {
    window.scrollTo(0, 0);
    this.updateClient()
  }
}
</script>
