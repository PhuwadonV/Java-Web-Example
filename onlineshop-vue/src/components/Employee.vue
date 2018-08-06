<template>
    <tr>
      <td>{{ name }}</td>
      <td>{{ email }}</td>
      <td><button class='button is-danger' type="button" @click="remove">Remove</button></td>
    </tr>
</template>

<script>
export default {
  name: 'Employee',
  props: ['name', 'email', 'updateFx'],
  methods: {
    remove() {
      this.DeleteText('/api/employee/delete', this.email).then(this.updateFx);
    }
  }
}
</script>
