<template>
  <div>
    <div class="box">
      <div class="level">
        <div class="level-left">
          <div class="level-item">
            <label class="label" style="margin-right: 10px;">Email</label>
            <input class="input" v-model="input.email" type="email">
          </div>
          <div class="level-item">
            <label class="label" style="margin-right: 10px;">Name</label>
            <input class="input" v-model="input.name" type="text">
          </div>
          <div class="level-item">
            <button class="button is-link" type="button" @click="addEmployees">Add/Edit</button>
          </div>
        </div>
      </div>
    </div>
    <div class="section" style="overflow-x:auto;">
      <table class="table is-fullwidth">
        <thead>
          <tr>
            <th style="width: 50%;">Name</th>
            <th>Email</th>
            <th style="width: 10px"></th>
          </tr>
        </thead>
        <tbody>
          <Employee v-for="(employee, index) in employees" :key="index" :name='employee.name' :email='employee.email' :updateFx="updateClient"/>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { mapMutations } from 'vuex'
import Employee from '@/components/Employee'

export default {
  name: 'Employees',
  data () {
    return {
      input: {
        email: '',
        name: ''
      },
      employees: []
    }
  },
  components: {
    Employee
  },
  methods: {
    ...mapMutations({
      alert: 'alert/show'
    }),
    updateInfo() {
      this.Get('/api/employee/search')
        .then(this.ResponseMatch(resp => resp.json().then(json => this.employees = json)))
    },
    updateClient() {
      this.ChangeRouteIfPermissionLevel(level => level < 2)
      this.updateInfo();
    },
    addEmployees(e) {
      const input = this.input
      if(!this.IsEmailFormat(input.email)) {
        this.alert({title: 'Error', msg: 'Wrong email format'})
        return
      }
      this.PutJson('/api/employee/put', { email: input.email, name: input.name })
        .then(this.ResponseMatch(resp => {
          this.updateInfo()
          input.email = ''
          input.name = ''
        }), this.ShowError)
    }
  },
  created() {
    window.scrollTo(0, 0);
    this.updateClient()
  }
}
</script>
