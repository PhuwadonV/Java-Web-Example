<template>
  <div class="productCard" style="margin-right: 20px; margin-bottom: 20px;">
    <a :href="'/#/productDetail/' + this.id">
      <div class="card" style="height: 100%; position:relative;">
        <p class="has-text-centered title is-4" style="margin: 10px">{{ name }}</p>
        <div style="height: 140px;"></div>
        <div class="card-image productCard" style="visibility: hidden;">
          <figure class="image is-4by3">
            <img style="object-fit: contain;" :src="imgUrl">
          </figure>
        </div>
        <div class="card-image productCard" style="position: absolute; bottom: 140px;">
          <figure class="image is-4by3">
            <img style="object-fit: contain;" :src="imgUrl">
          </figure>
        </div>
        <div class="content productCard" style="position: absolute; bottom: 0;">
          <table class="table is-bordered is-narrow">
            <col max-width="100%">
            <col width="100%">
            <tr>
              <td style="white-space: nowrap">Price (Bath)</td>
              <td>{{ price }}</td>
            </tr>
            <tr>
              <td>Amount</td>
              <td>{{ amount }}</td>
            </tr>
            <tr>
              <td>Sold</td>
              <td>{{ sold }}</td>
            </tr>
            <tr>
              <td>Date</td>
              <td>{{ day + "/" + month + "/" + year + " - " + hour + ":" + (minute < 10 ? "0" + minute : minute) }}</td>
            </tr>
          </table>
        </div>
      </div>
    </a>
  </div>
</template>

<script>
export default {
  name: 'Product',
  props: ['id', 'name', 'price', 'amount', 'sold', 'imgUrl', 'date', 'updateFx'],
  data() {
    return {
      minute: 0,
      hour: 0,
      day: 0,
      month: 0,
      year: 0
    }
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

<style>
.productCard {
  width: 400px;
}

@media screen and (max-width: 400px) {
  .productCard {
    width: 100%;
  }
}
</style>
