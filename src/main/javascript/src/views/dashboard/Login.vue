<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-clipboard-text"
      title="Aliases"
      class="px-5 py-3"
    >
      <v-form
        ref="form"
        v-model="valid"
        lazy-validation
      >
        <v-text-field
          v-model="username"
          label="Username"
          required
        />

        <v-text-field
          v-model="password"
          label="Password"
          required
        />
      </v-form>

      <v-btn
        :disabled="!valid"
        color="success"
        @click="login"
      >
        Login
      </v-btn>
    </base-material-card>
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'LoginForm',

    data: () => ({
      enableMock: process.env.VUE_APP_MOCK === 'true',
      username: '',
      password: '',
      valid: true,
    }),

    methods: {
      login: function () {
        localStorage.removeItem('authToken')
        var that = this
        $.ajax({
          url: this.enableMock ? process.env.BASE_URL + 'mock/Authenticate' : process.env.BASE_URL + 'ws/Authenticate',
          dataType: 'text/plain',
          type: this.enableMock ? 'GET' : 'POST',
          data: {
            username: this.username,
            password: this.password,
          },
          success: function (response) {
            var token = response
            // we store the token in localStorage, because this is not a Single Page App
            // is this safe?
            localStorage.setItem('authToken', token)
            that.$emit('authenticated')
          },
          error: function (response) {
            // TODO pretty print
            if (response && response.responseJSON && response.responseJSON.error && response.responseJSON.error.value) {
              alert(response.responseJSON.error.value)
            } else {
              console.log(response)
              alert('Authentication error')
            }
          },
        })
      },
    },
  }
</script>
