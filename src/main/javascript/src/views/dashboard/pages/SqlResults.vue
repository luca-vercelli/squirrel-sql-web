<template>
  <div>
    <v-simple-table dense>
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
    <v-row justify="center">
      <v-pagination
        v-if="numOfPages > 1"
        v-model="page"
        :length="numOfPages"
        total-visible="6"
        @next="nextPage()"
        @previous="previousPage()"
        @input="gotoPage($event)"
      />
    </v-row>
  </div>
</template>

<script>
  export default {
    name: 'SqlResults',

    props: {
      dataSet: {
        type: Object,
        default: Object,
      },
      rowsPerPage: {
        type: Number,
        default: 20,
      },
      numOfPages: {
        type: Number,
        default: 1,
      },
    },

    data () {
      return {
        currentPage: 0,
      }
    },

    computed: {
    },

    created () {
    },

    methods: {
      nextPage () {
        // PRE: ++this.currentPage < numOfPages
        this.gotoPage(++this.currentPage)
      },
      prevPage () {
        // PRE: --this.currentPage >= 0
        this.gotoPage(--this.currentPage)
      },
      gotoPage (pageNumber) {
        // PRE: 0 <= pageNumber < numOfPages
        this.$emit('goto-page', pageNumber)
      },
    },
  }
</script>
