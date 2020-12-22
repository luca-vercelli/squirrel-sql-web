<template>
  <v-container
    id="regular-tables"
    fluid
    tag="section"
  >
    <base-material-card
      icon="mdi-cog"
      title="Settings"
      class="px-5 py-3"
    >
      TODO
    </base-material-card>
  </v-container>
</template>

<script>
  export default {
    name: 'SquirrelSettings',

    data () {
      return {
        enableMock: process.env.VUE_APP_MOCK === 'true',
      }
    },

    methods: {
    },
  }
</script>
