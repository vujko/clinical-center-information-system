<template>
  <v-app>
     <v-content>
        <router-view></router-view>  
      </v-content>
    <v-app-bar
      height="33px"
      app
      class="blue-grey darken-4 white--text"
    >
      <v-spacer></v-spacer>
      <v-toolbar-title class="white--text">Clinic center</v-toolbar-title>
      <v-spacer></v-spacer>
    </v-app-bar>
    <Snackbar></Snackbar>
  </v-app>
</template>

<script>
import Snackbar from './components/global/Snackbar'
export default {
  name: 'App',
  components: {Snackbar},
  data: () => ({
    //
  }),
};
</script>
