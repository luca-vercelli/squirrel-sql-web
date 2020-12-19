<template>
  <base-material-snackbar
    v-model="snackbar"
    :type="color"
  >
    <span v-html="msg" />
  </base-material-snackbar>
</template>

<script>
  export default {
    name: 'Notify',

    props: {
      ajaxErrorResponse: {
        type: Object,
        default: Object,
      },
    },

    data: () => ({
      snackbar: false,
      msg: '',
      color: null,
    }),

    watch: {
      ajaxErrorResponse () {
        // if changed, open popup
        var response = this.ajaxErrorResponse
        if (response) {
          this.snackbar = true
          this.color = 'error'
          if (response && response.responseJSON && response.responseJSON.error && response.responseJSON.error.value) {
            // well-formed OData-like error
            this.msg = response.responseJSON.error.value
          } else if (response && response.responseText) {
            // other text error
            this.msg = response.responseText
          } else {
            console.log(response)
            this.msg = 'Error contacting server'
          }
        } else {
          this.snackbar = false
          this.msg = ''
        }
      },
    },

    methods: {
    },
  }
</script>
