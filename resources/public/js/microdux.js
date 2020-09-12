class Store {
  constructor(reducer) {
    this.reducer = reducer
    this.state = reducer(undefined, {type: undefined})
    this.subscribers = []
  }

  subscribe(func) {
    this.subscribers.push(func)
  }

  dispatch(action) {
    this.state = this.reducer(this.state, action)
    var that = this

    this.subscribers.map((func) => {
      func(this.state, that)
    })
    return this.state
  }
}
