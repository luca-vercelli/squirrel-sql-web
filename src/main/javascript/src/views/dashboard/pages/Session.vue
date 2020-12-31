<template>
  <v-card>
    <v-btn
      color="error"
      @click="disconnect()"
    >
      <i
        aria-hidden="true"
        class="v-icon notranslate mdi mdi-connection theme--dark"
      />
      Disconnect
    </v-btn>
    <v-btn
      color="success"
      @click="addSqlTab()"
    >
      <i
        aria-hidden="true"
        class="v-icon notranslate mdi mdi-database-plus theme--dark"
      />
      {{ $t('SQLPanel.newtab') }}
    </v-btn>

    <v-tabs
      v-model="tab"
      center-active
    >
      <v-tab
        v-for="(item, index) in items"
        :key="index"
      >
        {{ item.tab }}
      </v-tab>
    </v-tabs>

    <v-tabs-items v-model="tab">
      <v-tab-item
        v-for="(item, index) in items"
        :key="index"
      >
        <v-card
          v-if="session.identifier"
          flat
        >
          <objects-tree
            v-if="item.type=='objects'"
            :session-identifier="session.identifier"
            @open-tab="addTab"
            @notify="$emit('notify', $event)"
            @ajax-error="$emit('ajax-error', $event)"
          />
          <sql-query
            v-if="item.type=='query'"
            :session="session"
            @notify="$emit('notify', $event)"
            @ajax-error="$emit('ajax-error', $event)"
            @close-tab="closeTab(index)"
          />
          <table-tab
            v-if="item.type=='table'"
            :session-identifier="session.identifier"
            :node="item.node"
            @notify="$emit('notify', $event)"
            @ajax-error="$emit('ajax-error', $event)"
            @close-tab="closeTab(index)"
          />
          <procedure-tab
            v-if="item.type=='procedure'"
            :session-identifier="session.identifier"
            :node="item.node"
            @notify="$emit('notify', $event)"
            @ajax-error="$emit('ajax-error', $event)"
            @close-tab="closeTab(index)"
          />
          <schema-info-tab
            v-if="item.type=='schemaInfo'"
            :session-identifier="session.identifier"
            :node="item.node"
            @notify="$emit('notify', $event)"
            @ajax-error="$emit('ajax-error', $event)"
            @close-tab="closeTab(index)"
          />
        </v-card>
      </v-tab-item>
    </v-tabs-items>
  </v-card>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'Session',

    components: {
      ProcedureTab: () => import('./Procedure'),
      SqlQuery: () => import('./SqlQuery'),
      ObjectsTree: () => import('./ObjectsTree'),
      TableTab: () => import('./Table'),
      SchemaInfoTab: () => import('./SchemaInfo'),
    },

    props: {
      identifier: {
        type: String,
        default: '',
      },
    },

    data () {
      return {
        session: {},
        enableMock: process.env.VUE_APP_MOCK === 'true',
        editEnabled: false,
        tab: null,
        items: [
          { tab: this.$t('ObjectTreeTab.title'), type: 'objects' },
          { tab: this.$t('SQLTab.title'), type: 'query', number: 1 },
        ],
      }
    },

    computed: {},

    created: function () {
      this.loadSession(this.$route.params.identifier)
    },

    methods: {
      loadSession: function (identifier) {
        this.editEnabled = false
        this.session = {}
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleSession.json' : process.env.BASE_URL + `ws/Session(${identifier})`
        var that = this
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            that.session = response.value
            if (!that.session) {
              console.log('Wrong session id. Probably the user typed an old url: ' + location.href)
              that.$router.push('/')
            }
            that.editEnabled = true
          },
          error: function (response) {
            console.error('Error retrieving session id. Probably the user typed an old url: ' + location.href)
            that.$emit('ajax-error', response)
            that.$router.push('/')
          },
        })
      },
      disconnect: function () {
        // TODO should give some warning. Or not?
        this.editEnabled = false
        var that = this
        $.ajax({
          type: this.enableMock ? 'GET' : 'POST',
          url: this.enableMock ? process.env.BASE_URL + 'mock/JustGetOk' : process.env.BASE_URL + 'ws/Disconnect',
          data: {
            sessionId: this.session.identifier,
          },
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function () {
            that.$router.push('/')
          },
          error: function (response) {
            that.$emit('ajax-error', response)
            that.$router.push('/')
          },
        })
      },
      addTab: function (node) {
        if (node == null) {
          // maybe the user has de-selected some node
          return
        }
        var tab = null
        if (node.objectType === 'TABLE' || node.objectType === 'VIEW') {
          tab = { tab: node.simpleName, type: 'table', node: node }
        } else if (node.objectType === 'PROCEDURE' || node.objectType === 'FUNCTION') {
          tab = { tab: node.simpleName, type: 'procedure', node: node }
        } else if (node.objectType === 'SESSION') {
          tab = { tab: node.simpleName, type: 'schemaInfo', node: node }
        } else {
          if (node.objectType !== 'CATALOG' && node.objectType !== 'SCHEMA') {
            console.log('Object type not supported:' + node.objectType)
          }
          return
        }
        this.items.push(tab)
        this.tab = this.items.length - 1
      },
      addSqlTab: function () {
        var number = 1
        this.items.forEach(x => { if (x.type === 'query' && x.number >= number) number = x.number + 1 })
        var tab = { tab: this.$t('AdditionalSQLTab.title', [number]), type: 'query', number: number }
        this.items.push(tab)
        this.tab = this.items.length - 1
      },
      closeTab: function (index) {
        this.items.splice(index, 1)
        this.tab = index - 1
      },
    },
  }
</script>
