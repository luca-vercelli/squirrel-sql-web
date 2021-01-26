<template>
  <v-data-table
    dense
    :headers="jarTableHeaders"
    :items="jarTableItems"
    :items-per-page="5"
    class="elevation-1"
  >
    <template v-slot:top>
      <v-toolbar
        flat
      >
        <v-toolbar-title>
          {{ $t('DriverInternalFrame.extraclasspath') }}
        </v-toolbar-title>
        <v-divider
          class="mx-4"
          inset
          vertical
        />
        <v-spacer />
        <v-dialog
          v-model="jarDialog"
          max-width="500px"
        >
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              color="primary"
              dark
              class="mb-2"
              v-bind="attrs"
              :title="$t('DriverInternalFrame.add')"
              v-on="on"
            >
              <i
                aria-hidden="true"
                class="v-icon notranslate mdi mdi-plus-box theme--dark"
              />
            </v-btn>
          </template>
          <v-card>
            <v-card-title>
              <span class="headline">{{ jarAction }}</span>
            </v-card-title>

            <v-card-text>
              <v-container>
                <v-row>
                  <v-col>
                    <v-text-field
                      v-model="editedItem.name"
                      :label="$t('JAR File Name')"
                    />
                  </v-col>
                </v-row>
                <v-row>
                  <v-col>
                    SO FAR, YOU MUST ENTER ABSOLUTE FILE PATH OF JAR FILE ON SERVER
                  </v-col>
                </v-row>
              </v-container>
            </v-card-text>

            <v-card-actions>
              <v-spacer />
              <v-btn
                color="primary"
                :disabled="!editedItem.name"
                @click="addJar"
              >
                {{ jarAction }}
              </v-btn>
              <v-btn
                @click="closeJarDialog"
              >
                {{ $t('Action.cancel') }}
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-toolbar>
    </template>
    <template v-slot:item.actions="{ item }">
      <v-icon
        small
        class="mr-2"
        :title="$t('Action.edit')"
        @click="openJarDialog(item)"
      >
        mdi-pencil
      </v-icon>
      <v-icon
        small
        :title="$t('Action.delete')"
        @click="deleteItem(item)"
      >
        mdi-delete
      </v-icon>
    </template>
  </v-data-table>
</template>

<script>
  export default {
    name: 'DriverJarTable',

    props: {
      jarFileNames: {
        type: Array,
        default: Array,
      },
      editEnabled: {
        type: Boolean,
        default: false,
      },
    },

    data () {
      return {
        jarTableHeaders: [
          {
            text: this.$t('JAR File name'),
            align: 'start',
            sortable: true,
            value: 'name',
          },
          {
            text: 'Actions',
            value: 'actions',
            sortable: false,
          },
        ],
        editedIndex: -1,
        editedItem: {},
        jarDialog: false,
      }
    },

    computed: {
      jarTableItems () {
        const items = []
        if (this.jarFileNames) {
          this.jarFileNames.forEach(x => { items.push({ name: x }) })
        }
        return items
      },
      jarAction () {
        return this.editedIndex === -1 ? this.$t('Action.create') : this.$t('Action.edit')
      },
    },

    created () {
    },

    methods: {
      openJarDialog (item) {
        this.editedIndex = this.jarFileNames.indexOf(item.name)
        this.editedItem = Object.assign({}, item)
        this.jarDialog = true
      },
      closeJarDialog () {
        this.jarDialog = false
        this.editedItem = {}
        this.editedIndex = -1
      },
      addJar () {
        console.log('GIOING TO SAVE:', this.editedIndex, this.editedItem, this.jarFileNames)
        const newlist = this.jarFileNames ? [...this.jarFileNames] : []
        if (this.editedIndex > -1) {
          newlist[this.editedIndex] = this.editedItem.name
        } else {
          newlist.push(this.editedItem.name)
        }
        this.$emit('update', newlist)
        this.closeJarDialog()
      },
      deleteItem (item) {
        console.log('Deleting:', item.name)
        const editedIndex = this.jarFileNames.indexOf(item.name)
        this.jarFileNames.splice(editedIndex, 1)
      },
    },
  }
</script>
