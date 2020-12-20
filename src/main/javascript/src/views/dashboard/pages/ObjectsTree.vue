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
          activatable
          :active.sync="selectedNodes"
          :items="[rootNode]"
          item-key="simpleName"
          :open="openNodes"
          :load-children="loadChildren"
          @update:active="clickTreeNode"
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
        enableMock: process.env.VUE_APP_MOCK === 'true',
        editEnabled: false,
        rootNode: {}, // id, name, children
        nodes: {},
        selectedNodes: [], // the
        openNodes: [],
        allNodes: {},
      }
    },

    computed: {
      selected () {
        if (!this.selectedNodes.length) return undefined
        const id = this.selectedNodes[0]
        return this.nodes[id]
      },
    },

    watch: {
      selected: 'clickLeafNode',
    },

    created: function () {
      this.loadRootNode()
    },

    methods: {
      loadRootNode: function () {
        this.editEnabled = false
        this.objectsTree = null
        // TODO hideMessages();
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/RootNode.json' : process.env.BASE_URL + `ws/Session(${this.sessionIdentifier})/RootNode`,
          type: 'GET',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data) {
            var rootNode = data.value
            that.addTreeViewNodeProperties(rootNode)
            if (rootNode.children) {
              that.addTreeViewNodePropertiesAll(rootNode.children)
            }
            that.rootNode = rootNode
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
        this.allNodes[node.simpleName] = node
      },
      addTreeViewNodePropertiesAll: function (nodes) {
        var that = this
        nodes.forEach(x => that.addTreeViewNodeProperties(x))
        return nodes
      },
      removeUnwantedChildren: function (nodes) {
        nodes.forEach(node => { if (node.objectType == null || node.objectType === 'TABLE' || node.objectType === 'VIEW') node.children = undefined })
        return nodes
      },
      async loadChildren (node) {
        var that = this
        const url = this.enableMock ? process.env.BASE_URL + 'mock/ExpandNode.json' : process.env.BASE_URL + `ws/Session(${this.sessionIdentifier})/ExpandNode`
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
          .then(nodes => that.removeUnwantedChildren(nodes))
          .then(nodes => (node.children.push(...nodes)))
          .catch(err => console.warn(err))
      },
      clickTreeNode: function (simpleName) {
        var node = this.allNodes[simpleName]
        if (node.objectType === 'TABLE' || node.objectType === 'VIEW') {
          this.$emit('open-table', node)
        } else if (node.objectType !== 'SESSION' && node.objectType !== 'CATALOG' && node.objectType !== 'SCHEMA') {
          console.log('Unsupported object type:' + node.objectType)
        }
        // TODO other types of nodes
      },
      alert: function (msg) {
        alert(msg)
      },
    },
  }
</script>
