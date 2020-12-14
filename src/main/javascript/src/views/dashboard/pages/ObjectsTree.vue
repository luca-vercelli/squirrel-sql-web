<template>
  <v-container
    id="objects"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-clipboard-text"
      title="Objects"
      class="px-5 py-3"
    >
      <template>
        <v-treeview :items="[rootNode]"></v-treeview>
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
        rootNode: {}, // id, name, children
        nodes: {},
      }
    },

    computed: {},

    created: function () {
      this.loadRootNode()
    },

    methods: {
      loadRootNode: function () {
        this.editEnabled = false
        this.objectsTree = null
        this.nodes = {}
        // TODO hideMessages();
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/RootNode.json' : `../ws/Session(${this.sessionIdentifier})/RootNode`,
          type: 'GET',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data) {
            var rootNode = data.value
            this.addTreeViewNodeProperties(rootNode)
            if (rootNode.children) {
              rootNode.children.forEach(x => this.addTreeViewNodeProperties(x))
            }
            that.rootNode = rootNode
            that.rootNode.expanded = true
          },
          error: function (response, status) {
            // TODO showAjaxError(response)
            console.log(response)
          },
        })
      },
      addTreeViewNodeProperties: function (node) {
        node.id = node.simpleName
        node.name = node.simpleName
      },
      expandTreeNode: function (node) {
        // TODO hideMessages();
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/ExpandNode.json' : `../ws/Session(${this.sessionIdentifier})/ExpandNode`,
          type: this.enableMock ? 'GET' : 'POST',
          contentType: 'application/json',
          data: JSON.stringify(node),
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data) {
            node.children = data.data
            node.expanded = true
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
