class CeasslAPI {
  constructor(csrf) {
    this.csrf = csrf
  }

  async request(url, method = 'get', options = {}) {
    return fetch(url, {
      method: method,
      headers: Object.assign({
        "X-CSRF-Token": this.csrf,
        'Content-type': 'application/json',
      }),
      body: options.body ? JSON.stringify(options.body) : null
    })
  }

  async getTargets() {
    var resp = await this.request('/api/targets')
    return resp.json()
  }

  async deleteTarget(id) {
    var resp = await this.request('/api/targets/' + id, 'delete')
    return resp.json()
  }

  async addTarget(target) {
    var resp = await this.request('/api/targets', 'post', {
      body: {host: target}
    })

    return resp.json()
  }
}

const DATA_LOADED = 'DATA_LOADED'
const DATA_LOADING = 'DATA_LOADING'
const ADDING_TARGET = 'ADDING_TARGET'
const TARGET_ADDED = 'TARGET_ADDED'

function patch(state, newState) {
  return Object.assign({}, state, newState)
}

function stateReducer(state = {
  targets: [],
  loading: false,
  adding: false
}, action) {

  switch (action.type) {
    case ADDING_TARGET:
      return patch(state, {adding: true})

    case TARGET_ADDED:
      return patch(state, {adding: false})

    case DATA_LOADING:
      return patch(state, {loading: true})

    case DATA_LOADED:
      return patch(state, {
        targets: action.targets,
        loading: false
      })

    default:
      return state
  }
}


var store = createStore(stateReducer)

// we want to redraw our view whenever the store updates the state
store.subscribe(() => m.redraw())

const api = new CeasslAPI(csrfToken)

async function fetchTargets() {
  store.dispatch({type: DATA_LOADING})

  var resp = await api.getTargets()
  store.dispatch({type: DATA_LOADED, targets: resp})
}

async function init() {
  fetchTargets()
}

async function deleteTarget(id) {
  var resp = await api.deleteTarget(id)
  fetchTargets()
}

async function addTarget(target, targetInput) {
  store.dispatch({type: ADDING_TARGET})
  try {
    var resp = await api.addTarget(target)
    store.dispatch({type: TARGET_ADDED})
    targetInput.value = ''
    fetchTargets()
  } catch (e) {
    alert(e)
  }
}

async function onCreateFormSubmit() {
  var targetInput = document.getElementsByName('target-host')[0]

  var target = targetInput.value
  addTarget(target, targetInput)
}

var AddForm = {
  view: () => {
    return m('form#create-form.form', {
          onsubmit: function () {
            onCreateFormSubmit();
            return false;
          }
        },
        m('fieldsert', {disabled: true},
            m('div.form-group',
                m('div.input-group.input-inline',
                    m('input.form-input',
                        {name: 'target-host', placeholder: 'example.com'}),
                    m('button.btn.btn-primary.input-group-btn',
                        store.state.adding ? 'Adding…' : 'Add')))))
  }
}

var Header = {
  view: () => {
    return m('header.navbar',
        m('section.navbar-section',
            m(m.route.Link, {
              class: 'navbar-brand mr-2',
              href: '/'
            }, 'Ceassl'),
            store.state.loading ? 'loading…' : null),
        m(AddForm))
  }
}

var Layout = {
  view: (vnode) => {
    return m('div.container',
        m('div.columns',
            m('div.column.col-7.col-mx-auto',
                m(Header),
                vnode.children,
                m('div.container',
                    {class: 'text-center', style: 'margin-top: 50px'}))))
  }
}

var Main = {
  view: () => {
    var Table =
        m('table.table',
            m('thead',
                m('tr',
                    m('th', {class: 'col-9'}, "Host"),
                    m('th', {class: 'col-2'}, "Expiration"),
                    m('th', {class: 'text-right col-1'}, "Action"))),
            m('tbody',
                store.state.targets.map(p =>
                    m('tr',
                        m('td', {class: 'text-' + p.level}, p.host),
                        m('td', p.expires_in + ' days'),
                        m('td', {class: 'text-right'}, m('a', {
                          href: '', onclick: () => {
                            deleteTarget(p.id);
                            return false
                          }
                        }, 'del'))))))

    return [
      Table
    ]
  }
}

m.route(document.body, "/", {
  "/": {
    view: () => {
      return m(Layout, m(Main))
    }
  },

  "/settings": {
    view: () => {
      return m(Layout, 'Settings')
    }
  }
})

init()
