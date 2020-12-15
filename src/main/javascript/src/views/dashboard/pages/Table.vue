<template>
  <v-container
    id="table"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-clipboard-text"
      :title="'Table ' + tableName"
      class="px-5 py-3"
    >
      <template>
        THIS IS A TABLE
      </template>
    </base-material-card>
  </v-container>
</template>

<script>
  // var $ = require('jquery')
  export default {
    name: 'TableTab',

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
        tableName: '',
      }
    },

    computed: {},

    created: function () {
    },

    methods: {
    },
  }
</script>
