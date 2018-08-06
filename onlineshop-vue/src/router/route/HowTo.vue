<template>
  <section class="section">
    <div class="hero-body">
    <div class="container">
      <h1 class="title">How to buy products</h1>
      <ol>
        <li class="subtitle">Sign-in</li>
        <li class="subtitle">Search for product</li>
        <li class="subtitle">Select the product</li>
        <li class="subtitle">Add product to cart</li>
        <li class="subtitle">Fill shipping address form</li>
        <li class="subtitle">Confirm the cart</li>
        <li class="subtitle">Upload transfer bill image to cart</li>
        <li class="subtitle">Check is cart confirm</li>
        <ul>
          <li class="subtitle" style="margin-left: 30px;">If it’s not confirmed, please contact us</li>
          <li class="subtitle" style="margin-left: 30px;" >If it’s confirmed, wait for a delivery</li>
        </ul>
      </ol>
    </div>
  </div>
  </section>
</template>

<script>
export default {
  name: 'HowTo',
  created() {
    this.ChangeRouteIfPermissionLevel(level => level > 1)
  }
}
</script>
