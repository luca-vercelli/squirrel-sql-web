<template>
  <base-material-card
    icon="mdi-clipboard-text"
    title="Results"
    class="px-5 py-3"
  >
    <v-simple-table>
      <thead>
        <tr>
          <th class="primary--text">
            Name
          </th>
          <th class="text-right primary--text">
            Actions
          </th>
        </tr>
      </thead>

      <tbody>
        <tr
          v-for="driver in drivers"
          :key="driver.identifier.string"
        >
          <td>{{ driver.name }}</td>
          <td class="text-right">
            <v-btn
              color="success"
              class="mr-4"
              title="Edit"
              :to="'/driver/' + driver.identifier.string"
            >
              <i
                aria-hidden="true"
                class="v-icon notranslate mdi mdi-pencil theme--dark"
              />
            </v-btn> &nbsp;
            <v-btn
              color="success"
              class="mr-4"
              title="Clone"
              :to="'/clone-driver/' + driver.identifier.string"
            >
              <i
                aria-hidden="true"
                class="v-icon notranslate mdi mdi-content-copy theme--dark"
              />
            </v-btn> &nbsp;
            <v-btn
              color="error"
              class="mr-4"
              title="Delete"
              @click="deletingIdentifier = driver.identifier.string; showDeleteDialog = true"
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
</template>

<script>
  export default {
    name: 'SqlResults',

    props: {
      session: {
        type: Object,
        default: Object,
      },
    },

    data () {
      return {
        enableMock: true,
      }
    },

    computed: {
    },

    created: function () {
    },

    methods: {
    },
  }
</script>
