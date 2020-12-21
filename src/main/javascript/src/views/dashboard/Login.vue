<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-login"
      title="Login"
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

        <p>Default username/password is admin/admin</p>

        <v-btn
          :disabled="!valid"
          color="success"
          @click="login"
        >
          Login
        </v-btn>
      </v-form>
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
          url: this.enableMock ? process.env.BASE_URL + 'mock/Authenticate.json' : process.env.BASE_URL + 'ws/Authenticate',
          contentType: 'application/json',
          type: this.enableMock ? 'GET' : 'POST',
          data: JSON.stringify({
            username: this.username,
            password: this.password,
          }),
          success: function (response) {
            var token = response.value
            // we store the token in localStorage, because this is not a Single Page App
            // is this safe?
            localStorage.setItem('authToken', token)
            that.$emit('authenticated')
          },
          error: function (response) {
            if (response && response.responseJSON && response.responseJSON.error && response.responseJSON.error.value) {
              // This is a well-formed OData-like error message
              that.$emit('notify', { message: response.responseJSON.error.value, type: 'error' })
            } else {
              console.log(response)
              that.$emit('notify', { message: 'Authentication error', type: 'error' })
            }
          },
        })
      },
    },
  }
</script>
