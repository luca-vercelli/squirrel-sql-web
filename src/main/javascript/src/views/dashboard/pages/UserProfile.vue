<template>
  <v-container
    id="user-profile"
    fluid
    tag="section"
  >
    <v-row justify="center">
      <v-col
        cols="12"
        md="8"
      >
        <base-material-card>
          <template v-slot:heading>
            <div class="display-2 font-weight-light">
              {{ $t('UserProfile.title') }}
            </div>
          </template>

          <v-form>
            <v-container class="py-0">
              <v-row>
                <v-col
                  cols="12"
                  md="6"
                >
                  <v-text-field
                    v-model="user.username"
                    class="purple-input"
                    :label="$t('UserProfile.username')"
                  />
                </v-col>

                <v-col
                  cols="12"
                  md="6"
                >
                  <v-text-field
                    v-model="user.email"
                    :label="$t('UserProfile.email')"
                    class="purple-input"
                  />
                </v-col>

                <v-col
                  cols="12"
                  md="6"
                >
                  <v-text-field
                    v-model="user.name"
                    :label="$t('UserProfile.firstname')"
                    class="purple-input"
                  />
                </v-col>

                <v-col
                  cols="12"
                  md="6"
                >
                  <v-text-field
                    v-model="user.surname"
                    :label="$t('UserProfile.lastname')"
                    class="purple-input"
                  />
                </v-col>

                <v-col cols="12">
                  <v-text-field
                    v-model="user.roles"
                    :label="$t('UserProfile.roles')"
                    class="purple-input"
                  />
                </v-col>
                <v-col
                  cols="12"
                  class="text-right"
                >
                  <v-btn
                    color="success"
                    class="mr-0"
                    disabled
                  >
                    {{ $t('UserProfile.action.update') }}
                  </v-btn>
                </v-col>
              </v-row>
            </v-container>
          </v-form>
        </base-material-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'UserProfile',

    data () {
      return {
        user: {},
        enableMock: process.env.VUE_APP_MOCK === 'true',
        editEnabled: false,
      }
    },

    created: function () {
      this.loadUser()
    },

    methods: {
      loadUser: function () {
        this.editEnabled = false
        this.session = {}
        var url = this.enableMock ? process.env.BASE_URL + 'mock/CurrentUser.json' : process.env.BASE_URL + 'ws/CurrentUser'
        var that = this
        $.ajax({
          url: url,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            that.user = response.value
            that.editEnabled = true
          },
          error: function (data, status) {
            console.log('Data:', data, 'Status:', status)
            that.editEnabled = true
          },
        })
      },
    },
  }
</script>
