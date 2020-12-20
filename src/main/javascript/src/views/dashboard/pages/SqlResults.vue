<template>
  <v-simple-table>
    <!-- TODO right-align numbers -->
    <thead>
      <tr>
        <th
          v-for="column in dataSet.dataSetDefinition.columnDefinitions"
          :key="column.columnName"
          class="primary--text"
          :title="column.sqlTypeName"
        >
          {{ column.columnName }}
        </th>
      </tr>
    </thead>

    <tbody>
      <tr
        v-for="(row, rowIndex) in dataSet.allDataForReadOnly"
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
