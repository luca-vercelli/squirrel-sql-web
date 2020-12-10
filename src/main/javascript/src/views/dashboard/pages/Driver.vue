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
          label="Name"
          required
        />

        <v-text-field
          v-model="driver.url"
          label="Example URL"
          required
        />

        <v-text-field
          class="md-4"
          v-model="driver.webSiteUrl"
          label="Website URL"
          required
        />

        <v-text-field
          v-model="driver.driverClassName"
          label="Driver class name"
          required
        />
      </v-form>

      <v-btn
        :disabled="!valid"
        color="success"
        @click="validate"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-content-save theme--dark"
        />
        {{ buttonCaption }}
      </v-btn>
      <v-btn
        color="success"
        class="mr-4"
        :disabled="driver.webSiteUrl==''"
        target="_blank"
        :href="driver.webSiteUrl"
      >
        <i
          aria-hidden="true"
          class="v-icon notranslate mdi mdi-call-made theme--dark"
        />
        Visit website
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
        valid: true,
      }
    },

    computed: {
      wsUrl: function () {
        return this.enableMock ? process.env.BASE_URL + 'mock/SingleDriver.json' : '../ws/Drivers(' + this.identifier + ')'
      },
      buttonCaption: function () {
        return this.driver && this.driver.identifier && this.driver.identifier.string ? 'Save' : 'Create'
      },
    },

    created: function () {
      console.log('HERE')
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
      validate: function () {
        this.$refs.form.validate()
      },
    },
  }
</script>
