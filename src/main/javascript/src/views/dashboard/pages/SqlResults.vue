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
    <v-row
      v-if="pagination"
      justify="center"
    >
      <v-btn
        :disabled="page <= 0"
        @click="prevPage()"
      >
        <i
          class="v-icon notranslate mdi mdi-chevron-left theme--light"
          aria-hidden="true"
        />
      </v-btn>
      <span>
        {{ $t('Pagination.label', [page+1, numOfPages === null ? $t('Pagination.unknown') : numOfPages ]) }}
      </span>
      <v-btn
        :disabled="page > numOfPages"
        @click="nextPage()"
      >
        <i
          class="v-icon notranslate mdi mdi-chevron-right theme--light"
          aria-hidden="true"
        />
      </v-btn>
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
        // null means unknown
        type: Number,
        default: null,
      },
      pagination: {
        type: Boolean,
        default: false,
      },
      page: {
        type: Number,
        default: 0,
      },
    },

    data () {
      return {
      }
    },

    computed: {
    },

    created () {
      if ((this.numOfPages === null) || (this.numOfPages !== null && this.numOfPages > 1)) {
        this.pagination = true
      }
      if (this.numOfPages === null && this.dataSet.allDataForReadOnly.length < this.rowsPerPage) {
        if (this.dataSet.allDataForReadOnly.length > 0) {
          // this is the last page
          this.numOfPages = this.page + 1
        } else {
          --this.page
          this.numOfPages = this.page - 1
        }
      }
    },

    methods: {
      nextPage () {
        // PRE: ++this.currentPage < numOfPages
        this.gotoPage(++this.page)
      },
      prevPage () {
        // PRE: --this.currentPage >= 0
        this.gotoPage(--this.page)
      },
      gotoPage (pageNumber) {
        // PRE: 0 <= pageNumber < numOfPages
        this.$emit('goto-page', pageNumber)
      },
    },
  }
</script>
