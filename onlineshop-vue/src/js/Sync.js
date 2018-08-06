// WaitedObject
function WaitedObject() {
  this.status = null
}

WaitedObject.prototype.success = function() {
  this.status = undefined
  delete this.status
}

WaitedObject.prototype.error = function(err) {
  this.status = err
}

WaitedObject.prototype.setStatus = function(status, err) {
  if(status) this.success()
  else this.error(err)
}

WaitedObject.prototype.reset = function(data = null) {
  this.status = null
}

// WaitedValue
function WaitedValue(data = null) {
  this.data = data
  this.err = null
}

WaitedValue.prototype.success = function(data) {
  this.data = data
  this.err = undefined
  delete this.err
}

WaitedValue.prototype.error = function(err) {
  this.err = err
}

WaitedValue.prototype.setStatus = function(status, value) {
  if(status) this.success(value)
  else this.error(value)
}

WaitedValue.prototype.reset = function(data = null) {
  this.data = data
  this.err = null
}

function Wait(ms) {
  return new Promise((r, j) => setTimeout(r, ms))
}

async function WaitUntil(check, then, ms = 33) {
  while(!check()) await Wait(ms)
  then()
}

async function WaitObject(value, ms = 33) {
  while(true) {
    if(!('status' in value)) return
    else if(value.status !== null) throw value.status
    await Wait(ms)
  }
}

async function WaitValue(value, ms = 33) {
  while(true) {
    if(!('err' in value)) return value.data
    else if(value.err !== null) throw value.err
    await Wait(ms)
  }
}

export default {
  Wait,
  WaitUntil,
  WaitValue,
  WaitObject,
  WaitedValue,
  WaitedObject
}
