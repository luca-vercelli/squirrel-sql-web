<template>
  <v-content>
    <router-view
      @notify="$emit('notify', $event)"
      @ajax-error="$emit('ajax-error', $event)"
    />

    <dashboard-core-footer />
  </v-content>
</template>

<script>
  export default {
    name: 'DashboardCoreView',

    components: {
      DashboardCoreFooter: () => import('./Footer'),
    },
  }
</script>
