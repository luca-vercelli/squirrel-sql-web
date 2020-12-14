<template>
  <v-container
    id="objects"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-clipboard-text"
      title="Session"
      class="px-5 py-3"
    >
      <template>
        <v-treeview :items="items"></v-treeview>
      </template>
    </base-material-card>
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'ObjectsTree',

    props: {
      sessionIdentifier: {
        type: String,
        default: '',
      },
    },

    data () {
      return {
        enableMock: true,
        editEnabled: false,
        objectsTree: null,
        nodes: {},
      }
    },

    computed: {},

    created: function () {
      loadRootNodes()
    },

    methods: {
      loadRootNodes: function () {
        this.editEnabled = false
        this.objectsTree = null
        this.nodes = {}
        // TODO hideMessages();
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/RootNode.json' : `../ws/Session(${this.sessionIdentifier})/RootNode`,
          type: 'GET',
          data: {
            sessionId: this.sessionIdentifier,
            query: this.query,
          },
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data) {
            that.objectsTree = data.value;
            that.objectsTree.expanded = true;
          },
          error: function (response, status) {
            // TODO showAjaxError(response)
            console.log(response)
          },
        })
      },
    },
  }
</script>
