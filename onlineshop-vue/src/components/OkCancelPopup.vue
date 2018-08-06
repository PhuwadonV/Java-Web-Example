<template>
  <div class="modal" :class="{ 'is-active': isActive }">
    <div class="modal-background" @click="onCancel"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">{{ title }}</p>
      </header>
      <section class="modal-card-body">
          <slot></slot>
      </section>
      <footer class="modal-card-foot">
        <button class="button is-success" :disabled="disabledOk" @click="onOk">OK</button>
        <button class="button" @click="onCancel">Cancel</button>
      </footer>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OkCancelPopup',
  props: ['isActive', 'title', 'onOk', 'onCancel', 'disabledOk']
}
</script>
