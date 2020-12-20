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

    <v-tabs
      v-model="tab"
      center-active
    >
      <v-tab
        v-for="item in items"
        :key="item.tab"
      >
        {{ item.tab }}
      </v-tab>
    </v-tabs>

    <v-tabs-items v-model="tab">
      <v-tab-item
        v-for="item in items"
        :key="item.tab"
      >
        <v-card flat>
          <objects-tree
            v-if="item.type=='objects' && session.identifier"
            :session-identifier="session.identifier"
            @open-table="addTableTab"
            @notify="$emit('notify', $event)"
            @ajax-error="$emit('ajax-error', $event)"
          />
          <sql-query
            v-if="item.type=='query' && session.identifier"
            :session-identifier="session.identifier"
            @notify="$emit('notify', $event)"
            @ajax-error="$emit('ajax-error', $event)"
          />
          <table-tab
            v-if="item.type=='table' && session.identifier"
            :session-identifier="session.identifier"
            :table-node="item.node"
            @notify="$emit('notify', $event)"
            @ajax-error="$emit('ajax-error', $event)"
          />
          <!-- TODO should add some kind of "close" button -->
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
      SqlQuery: () => import('./SqlQuery'),
      ObjectsTree: () => import('./ObjectsTree'),
      TableTab: () => import('./Table'),
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
          { tab: 'Objects tree', type: 'objects' },
          { tab: 'SQL Query', type: 'query' },
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
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleSession.json' : process.env.BASE_URL + 'ws/Session(' + identifier + ')'
        var that = this
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            that.session = response.value
            that.editEnabled = true
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.editEnabled = true
          },
        })
      },
      disconnect: function () {
        // TODO should give some warning
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
          success: function (data, status) {
            that.$router.push('/')
          },
          error: function (response, status) {
            console.log(response)
            // TODO showAjaxError(response)
            this.editEnabled = true
          },
        })
      },
      addTableTab: function (node) {
        var tab = { tab: node.simpleName, type: 'table', node: node }
        this.items.push(tab)
        this.tab = this.items.length - 1
      },
    },
  }
</script>
