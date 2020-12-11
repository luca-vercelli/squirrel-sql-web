<template>
  <base-material-card
    icon="mdi-clipboard-text"
    title="Results"
    class="px-5 py-3"
  >
    <v-simple-table>
      <thead>
        <tr>
          <th
            v-for="column in dataSet.columnHeaders"
            class="primary--text"
            >
            {{ column.title }}
          </th>
        </tr>
      </thead>

      <tbody>
        <tr
          v-for="row in dataSet.allRowsForReadOnly"
          :key="row.index"
        >
          <td
            v-for="column in row"
            :key="column.index"
          >
            {{ column }}
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
      dataSet: {
        type: Object,
        default: Object,
      },
    },

    data () {
      return {
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
