class CeasslAPI {
  constructor(csrf) {
    this.csrf = csrf
  }

  async getTargets() {
    var resp = await fetch('/api/targets')
    return resp.json()
  }

  async deleteTarget(id) {
    var resp = await fetch('/api/targets/' + id, {
      method: 'delete',
      headers: {"X-CSRF-Token": this.csrf}
    })

    return resp.json()
  }

  async addTarget(target) {
    var resp = await fetch('/api/targets', {
      method: 'post',
      headers: {"X-CSRF-Token": this.csrf},
      body: JSON.stringify({host: target})
    })

    console.log(resp.status)

    return resp.json()
  }
}

const DATA_LOADED = 'DATA_LOADED'
const DATA_LOADING = 'DATA_LOADING'
const ADDING_TARGET = 'ADDING_TARGET'
const TARGET_ADDED = 'TARGET_ADDED'

function stateReducer(state = {
  targets: [],
  loading: false,
  adding: false
}, action) {
  switch (action.type) {
    case ADDING_TARGET:
      return Object.assign({}, state, {adding: true})

    case TARGET_ADDED:
      return Object.assign({}, state, {adding: false})

    case DATA_LOADING:
      return Object.assign({}, state, {loading: true})

    case DATA_LOADED:
      return Object.assign({}, state, {
        targets: action.targets,
        loading: false
      })

    default:
      return state
  }
}


var store = new Store(stateReducer)
store.subscribe(() => m.redraw())

const api = new CeasslAPI(csrfToken)

async function fetchTargets() {
  store.dispatch({type: DATA_LOADING})

  var resp = await api.getTargets()

  setTimeout(function () {
    store.dispatch({type: DATA_LOADED, targets: resp})
  }, 1000)
}

async function init() {
  fetchTargets()
}

async function deleteTarget(id) {
  var resp = await api.deleteTarget(id)
  fetchTargets()
}

async function addTarget(target) {
  store.dispatch({type: ADDING_TARGET})
  try {
    var resp = await api.addTarget(target)
    store.dispatch({type: TARGET_ADDED})
    targetInput.value = ''
    fetchTargets()
  } catch (e) {
    store.dispatch({type: TARGET_ADDED})
  }
}

async function onCreateFormSubmit() {
  var targetInput = document.getElementsByName('target-host')[0]

  var target = targetInput.value
  addTarget(target)
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
                    {class: 'text-center', style: 'margin-top: 50px'},
                    m(m.route.Link, {href: '/settings', class: 'text-center'},
                        'settings')))))
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
