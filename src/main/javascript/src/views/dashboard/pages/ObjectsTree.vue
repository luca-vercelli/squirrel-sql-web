<template>
  <v-container
    id="objects"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-database"
      :title="$t('ObjectTreeTab.title')"
      class="px-5 py-3"
    >
      <template>
        <v-text-field
          v-model="search"
          :label="$t('ObjectTreeTab.search')"
          clearable
          clear-icon="mdi-close-circle-outline"
        />
        <v-btn
          :disabled="!editEnabled"
          color=""
          @click="refresh"
        >
          <i
            aria-hidden="true"
            class="v-icon notranslate mdi mdi-refresh theme--dark"
          />
          {{ $t('Action.refresh') }}
        </v-btn>
        <v-treeview
          activatable
          :active.sync="selectedNodes"
          :items="[rootNode]"
          item-key="simpleName"
          :open="openNodes"
          :load-children="loadChildren"
          :search="search"
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
        search: null,
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
            that.editEnabled = true
          },
          error: function (response) {
            that.$emit('ajax-error', response)
            that.editEnabled = true
          },
        })
      },
      refresh: function () {
        if (this.rootNode) {
          this.gc(this.rootNode)
        }
        this.loadRootNode()
      },
      /**
       * Free up some memory. Is this useful?
       */
      gc: function (node) {
        if (node.children) {
          node.children.forEach(x => { this.gc(x) })
          node.children = []
        }
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
        const leafTypes = [null, 'TABLE', 'VIEW', 'PROCEDURE', 'FUNCTION']
        nodes.forEach(node => { if (leafTypes.includes(node.objectType)) node.children = undefined })
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
        this.$emit('open-tab', node)
      },
    },
  }
</script>
