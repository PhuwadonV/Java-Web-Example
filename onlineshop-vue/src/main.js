import Vue from 'vue'
import App from '@/App'
import router from '@/router'
import store from '@/store'
import Sync from '@/js/Sync.js'

Vue.config.productionTip = process.env.NODE_ENV !== 'production'

Vue.mixin({
  methods: {
    LogError(resp) {
      console.log('Error', resp.status, resp.statusText, resp.url)
    },
    ShowError() {
      alert('Error')
    },
    ResponseMatch(ok, error = this.LogError) {
      return function(resp) {
        if(resp.status >= 200 && resp.status < 300) return ok(resp)
        else return error(resp)
      }
    },
    FetchData(method, path, contentType, data, opt) {
      return fetch(path,  {
        method: method,
        credentials: 'same-origin',
        headers: {
          'Content-Type': contentType
        },
        body: data,
        ...opt
      })
    },
    FetchFormData(method, path, data, opt) {
      return fetch(path,  {
        method: method,
        credentials: 'same-origin',
        body: data,
        ...opt
      })
    },
    Get(path, opt) {
      return fetch(path, { credentials: 'same-origin', ...opt })
    },
    PostData(path, contentType, data, opt) {
      return this.FetchData('POST', path, contentType, data, opt)
    },
    PostText(path, text, opt) {
      return this.FetchData('POST', path, 'text/plain', text, opt)
    },
    PostJson(path, json, opt) {
      return this.PostData(path, 'application/json', JSON.stringify(json), opt)
    },
    PostFormData(path, formData, opt) {
      return this.FetchFormData('POST', path, formData, opt)
    },
    PutData(path, contentType, data, opt) {
      return this.FetchData('PUT', path, contentType, data, opt)
    },
    PutText(path, text, opt) {
      return this.FetchData('PUT', path, 'text/plain', text, opt)
    },
    PutJson(path, json, opt) {
      return this.PutData(path, 'application/json', JSON.stringify(json), opt)
    },
    PutFormData(path, formData, opt) {
      return this.FetchFormData('PUT', path, formData, opt)
    },
    Delete(path, opt) {
      return fetch(path,  {
        method: 'DELETE',
        credentials: 'same-origin',
        ...opt
      })
    },
    DeleteData(path, contentType, data, opt) {
      return this.FetchData('DELETE', path, contentType, data, opt)
    },
    DeleteText(path, text, opt) {
      return this.FetchData('DELETE', path, 'text/plain', text, opt)
    },
    DeleteJson(path, json, opt) {
      return this.DeleteData(path, 'application/json', JSON.stringify(json), opt)
    },
    async ChangeRouteIfPermissionLevel(check) {
      try {
        const permissionLevel = await Sync.WaitValue(this.$store.state.account.permissionLevel)
        if(check(permissionLevel)) this.$router.replace('/')
      }
      catch(e) {
        if(check(0)) this.$router.replace('/')
      }
    },
    ValidatePhoneNumber(str) {
      str = str.replace(/[^\d|-]/g, '')
      str = str.replace(/-(-)+/g, '-')
      if(str.charAt(0) === '-') str = str.substring(1)
      const lastChar = str.length -1
      if(str.charAt(lastChar) === '-') str = str.substring(0, lastChar)
      return str
    },
    ValidateNonNagativeNumber(str, defaultValue = '0') {
      if(str.length === 0) return defaultValue
      else {
        str = str.replace(/\D/g, '')
        if(str.length === 0) return defaultValue
        let firstNonZero = 0
        let success = false
        for(; firstNonZero < str.length; firstNonZero++) {
          if(str.charAt(firstNonZero) !== '0') {
            success = true
            break
          }
        }
        if(success) {
            if(firstNonZero === 0) return str
            else return str.substring(firstNonZero)
        }
        else return '0'
      }
    },
    ValidatePossitiveNumber(str, defaultValue = '1') {
      const result = this.ValidateNonNagativeNumber(str, defaultValue)
      return result == '0' ? defaultValue : result
    },
    ValidatePossitiveNumberOnKeyup(str) {
      const result = this.ValidateNonNagativeNumber(str, '')
      return result == '0' ? '' : result
    },
    ValidateAndAddTag(str, tags, push) {
      str = str.replace(/[^\w|\s]/g, '').trim().replace(/\s+/g, '_').toLowerCase()
      if(str === '' || str === '_') return
      else if(tags.find(elem => elem === str) === undefined) push(str)
    },
    IsEmailFormat(str) {
      var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
      return re.test(str);
    }
  }
})

const app = new Vue({
  el: '#app',
  components: { App },
  template: '<App/>',
  router,
  store,
})
