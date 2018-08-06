<template>
  <section class="section">
    <div class="hero-body">
    <div class="container">
      <h1 class="title">Online Shop</h1>
      <p>
        <h2 class="subtitle">Tel : 02-000-0000</h2>
      </p>
      <p>
        <h2 class="subtitle">Email : online-shop@mail.com</h2>
      </p>
      <p>
        <h2 class="subtitle">Address : 1 Street District Province Postcode</h2>
      </p>
    </div>
  </div>
  </section>
</template>

<script>
export default {
  name: 'About',
  created() {
    this.ChangeRouteIfPermissionLevel(level => level > 1)
  }
}
</script>
