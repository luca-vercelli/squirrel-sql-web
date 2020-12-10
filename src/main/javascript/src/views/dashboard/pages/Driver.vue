<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-clipboard-text"
      title="Drivers"
      class="px-5 py-3"
    >
      <v-form
        ref="form"
        v-model="valid"
        lazy-validation
      >
        <v-text-field
          v-model="driver.name"
          :counter="10"
          :rules="nameRules"
          label="Name"
          required
        />

        <v-text-field
          v-model="driver.url"
          :rules="nameRules"
          label="Example URL"
          required
        />

        <v-text-field
          v-model="driver.webSiteUrl"
          :rules="nameRules"
          label="Website URL"
          required
        />

        <v-text-field
          v-model="driver.driverClassName"
          :rules="nameRules"
          label="Driver class name"
          required
        />
      </v-form>

      <v-btn
        :disabled="!valid"
        color="success"
        class="mr-4"
        @click="validate"
      >
        {{ buttonCaption }}
      </v-btn>
    </base-material-card>
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'Driver',

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
        driver: {},
        enableMock: true,
      }
    },

    computed: {
      wsUrl: function () {
        return this.enableMock ? process.env.BASE_URL + 'mock/SingleDriver.json' : '../ws/Drivers(' + this.identifier + ')'
      },
      buttonCaption: function () {
        return this.driver.identifier.string ? 'Save' : 'Create'
      },
    },

    created: function () {
      this.loadDriver(this.$route.params.identifier || this.$route.params.origIdentifier, this.$route.params.origIdentifier)
    },

    methods: {
      loadDriver: function (identifier, boolClone) {
        var url = this.enableMock ? process.env.BASE_URL + 'mock/SingleDriver.json' : '../ws/Drivers(' + identifier + ')'
        var that = this
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            that.driver = response.value
            if (boolClone) {
              that.driver.identifier = {}
            }
          },
        })
      },
    },
  }
</script>
