<template>
  <base-material-snackbar
    v-model="snackbar"
    :type="type"
  >
    <span v-html="msg" />
  </base-material-snackbar>
</template>

<script>
  export default {
    name: 'Notify',

    /**
    Usage: you can set notify = { message: ... type: ...}
    or, you can set ajaxErrorResponse= the response of some $.ajax call in error
    */
    props: {
      notify: {
        type: Object,
        default: Object,
      },
      ajaxErrorResponse: {
        type: Object,
        default: Object,
      },
    },

    data: () => ({
      snackbar: false,
      msg: '',
      type: null,
    }),

    watch: {
      ajaxErrorResponse () {
        // if changed, open popup
        var response = this.ajaxErrorResponse
        if (response) {
          this.snackbar = true
          this.type = 'error'
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
        console.log('HERE', this.msg, this.type)
      },
      notify () {
        console.log('DEBUG2', this.notify)
        if (this.notify && this.notify.message) {
          this.snackbar = true
          this.msg = this.notify.message
          this.type = this.notify.type
        } else {
          this.snackbar = false
        }
        console.log('THERE', this.msg, this.type)
      },
    },

    methods: {
    },
  }
</script>
