<template>
  <div class="modal" :class="{ 'is-active': isActive }">
    <div class="modal-background" @click="close"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">{{ title }}</p>
      </header>
      <section class="modal-card-body">
          <slot></slot>
      </section>
      <footer class="modal-card-foot">
        <button class="button is-success" @click="close">OK</button>
      </footer>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'

export default {
  name: 'Alert',
  computed: {
    ...mapState({
      isActive: state => state.alert.isActive,
      title: state => state.alert.title
    })
  },
  methods: {
    ...mapMutations({
      close: 'alert/close'
    })
  }
}
</script>
