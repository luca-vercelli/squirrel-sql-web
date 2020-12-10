<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-clipboard-text"
      title="Alias"
      class="px-5 py-3"
    >
      <v-form
        ref="form"
        v-model="valid"
        lazy-validation
      >
        <v-text-field
          v-model="alias.name"
          label="Name"
          required
        />

        <v-text-field
          v-model="alias.url"
          label="Example URL"
          required
        />

        <v-text-field
          v-model="alias.webSiteUrl"
          label="Website URL"
          class="md-4"
          required
        />

        <v-text-field
          v-model="alias.aliasClassName"
          label="Driver class name"
          required
        />
      </v-form>

      <v-btn
        v-if="creating"
        :disabled="!valid"
        color="success"
        visible="false"
        @click="createAlias"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-content-save theme--dark"
        />
        Create
      </v-btn>

      <v-btn
        v-if="!creating"
        :disabled="!valid"
        color="success"
        @click="saveAlias"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-content-save theme--dark"
        />
        Save
      </v-btn>

      <v-btn
        :disabled="!valid"
        color="success"
        @click="connect"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-connection theme--dark"
        />
        Connect
      </v-btn>
    </base-material-card>
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'Alias',

    props: {
      identifier: {
        type: String,
        default: '',
      },
      origIdentifier: {
        type: String,
        default: '',
      },
    },

    data () {
      return {
        alias: {},
        enableMock: true,
        valid: true,
        editEnabled: false,
        creating: true,
      }
    },

    computed: {
      wsUrl: function () {
        return this.enableMock ? process.env.BASE_URL + 'mock/SingleAlias.json' : '../ws/Aliases(' + this.identifier + ')'
      },
    },

    created: function () {
      if (this.$route.params.identifier || this.$route.params.origIdentifier) {
        this.loadAlias(this.$route.params.identifier || this.$route.params.origIdentifier, this.$route.params.origIdentifier)
      }
    },

    methods: {
      loadAlias: function (identifier, boolClone) {
        this.editEnabled = false
        this.alias = {}
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleAlias.json' : '../ws/Aliases(' + identifier + ')'
        var that = this
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            console.log('response:', response)
            that.alias = response.value
            if (boolClone) {
              that.alias.identifier = {}
              that.creating = true
            } else {
              that.creating = false
            }
            that.editEnabled = true
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.editEnabled = true
          },
        })
      },
      validate: function () {
        this.$refs.form.validate()
      },
      createAlias: function () {
        console.log('create')
        this.validate()
        this.editEnabled = false
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleAlias.json' : '../ws/Aliases'
        var that = this
        $.ajax({
          type: this.enableMock ? 'GET' : 'POST',
          url: url,
          contentType: 'application/json',
          data: JSON.stringify(this.alias),
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            that.alias = data.value
            that.editEnabled = true
            that.creating = false
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.editEnabled = true
          },
        })
      },
      saveAlias: function () {
        console.log('save')
        this.validate()
        this.editEnabled = false
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleAlias.json' : `../ws/Aliases(${this.alias.identifier})`
        var that = this
        $.ajax({
          type: this.enableMock ? 'GET' : 'PUT',
          url: url,
          contentType: 'application/json',
          data: JSON.stringify(this.alias),
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            that.alias = data.value
            that.editEnabled = true
            that.creating = false
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.editEnabled = true
          },
        })
      },
      connect: function () {
        alert('TODO')
      },
    },
  }
</script>
