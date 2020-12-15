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
        <v-treeview
          :items="[rootNode]"
          item-key="simpleName"
          :open="openNodes"
          :load-children="loadChildren"
        />
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
        openNodes: [],
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
            that.addTreeViewNodeProperties(rootNode)
            if (rootNode.children) {
              rootNode.children.forEach(x => that.addTreeViewNodeProperties(x))
            }
            that.rootNode = rootNode
            console.log(rootNode)
            that.openNodes.push(that.rootNode.simpleName)
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
      addTreeViewNodePropertiesAll: function (nodes) {
        nodes.forEach(x => this.addTreeViewNodeProperties(x))
        return nodes
      },
      async loadChildren (node) {
        console.log('aaa', node, this)
        const url = this.enableMock ? process.env.BASE_URL + 'mock/ExpandNode.json' : `../ws/Session(${this.sessionIdentifier})/ExpandNode`
        var that = this
        return fetch(url, {
          method: this.enableMock ? 'GET' : 'POST',
          cache: 'no-cache',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          body: this.enableMock ? null : JSON.stringify(node),
        })
          .then(response => response.json())
          .then(json => json.data)
          .then(nodes => that.addTreeViewNodePropertiesAll(nodes))
          .then(nodes => (node.children.push(...nodes)))
          .catch(err => console.warn(err))
      },
    },
  }
</script>
