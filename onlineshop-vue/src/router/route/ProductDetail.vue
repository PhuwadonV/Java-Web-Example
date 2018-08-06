<template>
  <ProductDetailEdit v-if="permissionLevel.data > 1" :id="$route.params.productId"/>
  <ProductDetailInfo v-else :id="$route.params.productId"/>
</template>

<script>
import { mapState } from 'vuex'
import ProductDetailInfo from '@/components/ProductDetailInfo'
import ProductDetailEdit from '@/components/ProductDetailEdit'

export default {
  name: 'ProductDetail',
  components: {
    ProductDetailInfo,
    ProductDetailEdit
  },
  computed: {
    ...mapState({
      permissionLevel: state => state.account.permissionLevel
    })
  }
}
</script>
