<template>
  <div class="box">
    <div class="level">
      <div class="level-left">
        <div class="level-item">
          <label class="label">Product ID</label>
        </div>
        <div class="level-item">
          <input class="input" type="text" v-model="productId">
        </div>
        <div class="level-item">
          <button class="button is-link" type="button" @click="find">Find</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Find',
  data() {
    return {
      productId: ''
    }
  },
  methods: {
    find() {
      this.$router.replace('/productDetail/' + this.productId)
    }
  }
}
</script>
