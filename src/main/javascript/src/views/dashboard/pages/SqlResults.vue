<template>
    <v-simple-table>
      <thead>
        <tr>
          <th
            v-for="(title, index) in dataSet.columnHeaders"
            :key="index"
            class="primary--text"
          >
            {{ title }}
          </th>
        </tr>
      </thead>

      <tbody>
        <tr
          v-for="(row, rowIndex) in dataSet.rows"
          :key="rowIndex"
        >
          <td
            v-for="(cellValue, colIndex) in row"
            :key="colIndex"
          >
            {{ cellValue }}
          </td>
        </tr>
      </tbody>
    </v-simple-table>
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
