<template>
  <base-material-snackbar
    v-model="snackbar"
    :type="color"
  >
    Welcome to <span class="font-weight-bold">&nbsp;MATERIAL DASHBOARD&nbsp;</span> — a beautiful admin panel for every web developer.
  </base-material-snackbar>
</template>

<script>
  export default {
    name: 'Notify',

    data: () => ({
      snackbar: true,
      color: 'info',
    }),

    computed: {
    },

    methods: {
    },
  }
</script>
