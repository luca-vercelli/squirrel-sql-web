<template>
  <div
    v-if="dataSet && dataSet.dataSetDefinition"
    :class="{ 'd-none': !visible }"
  >
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
    >
      <v-col
        style="text-align:right"
      >
        <v-btn
          :disabled="noMoreItems"
          class="pagination-btn"
          elevation="3"
          @click="loadMore()"
        >
          <i
            class="v-icon notranslate mdi mdi-chevron-down theme--light"
            aria-hidden="true"
          />
          {{ noMoreItems ? $t('Pagination.noMore') : $t('Pagination.loadMore') }}
        </v-btn>
      </v-col>
    </v-row>
  </div>
</template>

<script>
  export default {
    name: 'SqlResults',

    props: {
      dataSet: {
        type: Object,
        default: null,
      },
      pagination: {
        type: Boolean,
        default: false,
      },
      visible: {
        type: Boolean,
        default: false,
      },
      noMoreItems: {
        type: Boolean,
        default: false,
      },
    },

    data () {
      return {
        internalDataSet: null,
      }
    },

    computed: {
    },

    watch: {
    },

    created () {
    },

    methods: {
      loadMore () {
        this.$emit('load-more', this.dataSet)
      },
    },
  }
</script>

<style>
.pagination-label {
  padding: 5px;
}
.pagination-btn {
  /* this does not work */
}
</style>
