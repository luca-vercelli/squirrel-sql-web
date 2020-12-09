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
      QUI CI VA UN FORM
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
    },

    data () {
      return {
        driver: null,
        enableMock: true,
      }
    },

    computed: {
      wsUrl: function () {
        return this.enableMock ? process.env.BASE_URL + 'mock/SingleDriver.json' : '../ws/Drivers(' + this.identifier + ')'
      },
    },

    created: function () {
      this.loadDriver()
    },

    methods: {
      loadDriver: function () {
        var url = this.wsUrl
        var that = this
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            that.driver = response.value
          },
        })
      },
    },
  }
</script>
