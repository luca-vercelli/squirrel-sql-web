<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-database"
      :title="$t('AliasesToolWindow.windowtitle')"
      class="px-5 py-3"
    >
      <div class="text-right">
        <v-btn
          color="success"
          class="mr-4"
          :title="$t('AliasInternalFrame.addalias')"
          to="/new-alias"
        >
          <i
            aria-hidden="true"
            class="v-icon notranslate mdi mdi-plus-box theme--dark"
          />
        </v-btn>
      </div>

      <v-simple-table>
        <thead>
          <tr>
            <th class="primary--text">
              {{ $t('AliasInternalFrame.name') }}
            </th>
            <th class="text-right primary--text">
              {{ $t('Action.actions') }}
            </th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="alias in aliases"
            :key="alias.identifier"
          >
            <td>{{ alias.name }}</td>
            <td class="text-right">
              <v-btn
                color="success"
                class="mr-4"
                :title="$t('AliasInternalFrame.connect')"
                @click="connectOrDialog(alias)"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-connection theme--dark"
                />
              </v-btn> &nbsp;
              <v-btn
                color="success"
                class="mr-4"
                :title="$t('Action.edit')"
                :to="'/alias/' + alias.identifier"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-pencil theme--dark"
                />
              </v-btn> &nbsp;
              <v-btn
                color="success"
                class="mr-4"
                :title="$t('Action.clone')"
                :to="'/clone-alias/' + alias.identifier"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-content-copy theme--dark"
                />
              </v-btn> &nbsp;
              <v-btn
                color="error"
                class="mr-4"
                :title="$t('Action.delete')"
                @click="openDeleteDialog(alias)"
              >
                <i
                  aria-hidden="true"
                  class="v-icon notranslate mdi mdi-delete theme--dark"
                />
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-simple-table>
    </base-material-card>

    <v-dialog
      v-model="showDeleteDialog"
      persistent
      max-width="290"
    >
      <v-card>
        <v-card-title class="headline">
          {{ $t('Action.confirm') }}
        </v-card-title>
        <v-card-text>{{ deletingMessage }}</v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="error"
            @click="deleteAlias(); showDeleteDialog = false"
          >
            {{ $t('Action.ok') }}
          </v-btn>
          <v-btn
            @click="showDeleteDialog = false"
          >
            {{ $t('Action.cancel') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog
      v-model="showConnectDialog"
      persistent
      max-width="290"
    >
      <v-card>
        <v-card-title class="headline">
          {{ $t('ConnectionInternalFrame.connect') }}
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-text-field
              v-model="connectUserName"
              :label="$t('ConnectionInternalFrame.user')"
            />
            <v-text-field
              v-model="connectPassword"
              type="password"
              :label="$t('ConnectionInternalFrame.password')"
            />
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="error"
            @click="connect(); showConnectDialog = false"
          >
            {{ $t('Action.ok') }}
          </v-btn>
          <v-btn
            @click="showConnectDialog = false"
          >
            {{ $t('Action.cancel') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
  var $ = require('jquery')
  export default {
    name: 'Aliases',

    data () {
      return {
        aliases: [],
        enableMock: process.env.VUE_APP_MOCK === 'true',
        showDeleteDialog: false,
        showConnectDialog: false,
        deletingAlias: {},
        deletingMessage: null,
        connectingIdentifier: null,
        connectUserName: null,
        connectPassword: null,
      }
    },

    computed: {
      wsUrl: function () {
        return this.enableMock ? process.env.BASE_URL + 'mock/Aliases.json' : process.env.BASE_URL + 'ws/Aliases'
      },
    },

    created: function () {
      this.loadAliases()
    },

    methods: {
      loadAliases: function () {
        var that = this
        $.ajax({
          url: this.wsUrl,
          dataType: 'json',
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (response) {
            that.aliases = response.data
            that.aliases.sort(that.cmpNames)
          },
        })
      },
      cmpNames: function (x, y) {
        var name1 = x.name ? x.name.toLowerCase() : ''
        var name2 = y.name ? y.name.toLowerCase() : ''
        return name1 < name2 ? -1 : name1 > name2 ? +1 : 0
      },
      connectOrDialog: function (alias) {
        this.connectingIdentifier = alias.identifier
        this.connectUserName = alias.userName
        this.connectPassword = alias.password
        if (alias.autoLogon) {
          this.connect()
        } else {
          this.showConnectDialog = true
        }
      },
      connect: function () {
        var that = this
        $.ajax({
          type: this.enableMock ? 'GET' : 'POST',
          url: this.enableMock ? process.env.BASE_URL + 'mock/SingleSession.json' : process.env.BASE_URL + 'ws/Connect',
          data: {
            aliasIdentifier: this.connectingIdentifier,
            userName: this.connectUserName,
            password: this.connectPassword,
          },
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            that.$router.push('/session/' + data.value.identifier)
          },
          error: function (response) {
            that.$emit('ajax-error', response)
          },
        })
      },
      openDeleteDialog (alias) {
        this.deletingAlias = alias
        this.deletingMessage = this.$t('DeleteAliasCommand.confirm', [alias.name])
        this.showDeleteDialog = true
      },
      deleteAlias: function () {
        var that = this
        $.ajax({
          type: this.enableMock ? 'GET' : 'DELETE',
          url: this.enableMock ? process.env.BASE_URL + 'mock/JustGetOk' : process.env.BASE_URL + `ws/Aliases(${this.deletingAlias.identifier})`,
          headers: {
            Authorization: 'Bearer ' + localStorage.getItem('authToken'),
          },
          success: function (data, status) {
            that.loadAliases()
            that.deletingAlias = null
          },
          error: function (response) {
            that.deletingAlias = null
            that.$emit('ajax-error', response)
          },
        })
      },
    },
  }
</script>
