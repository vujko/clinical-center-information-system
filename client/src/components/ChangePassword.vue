<template>
    <div>
        <CustomToolbar/>
        <v-row justify="center">
                <v-card style="width: 40%; margin-top:20px">
                    <v-card-title>
                        <v-toolbar height="45px" color="orange lighten-1" class="white--text">
                            <span class="headline">Change password</span>
                        </v-toolbar>
                    </v-card-title>
                    <v-card-text>
                        <v-container>
                            <v-form ref="form">
                            <v-text-field
                                :rules="[requiredRule]"
                                v-model="oldPass"
                                label="Old password"
                                type="password">
                            </v-text-field>
                            <v-text-field
                                :rules="[requiredRule]"
                                v-model="newPass"
                                label="New password"
                                type="password">
                            </v-text-field>
                            <v-text-field
                                :rules="[passwordMatch,requiredRule]"
                                v-model="confirmPass"
                                label="Confirm password"
                                type="password">
                            </v-text-field>
                            </v-form>
                        </v-container>
                    </v-card-text>

                    <v-card-actions>
                    <v-spacer></v-spacer>
                        <v-btn color="red darken-1"  text v-on:click="closeDialog">Back to Homepage</v-btn>
                        <v-btn color="green darken-1" text @click="savePassword">Save</v-btn>
                    </v-card-actions>

                </v-card>
        
        </v-row>
    </div>
</template>

<script>
import CustomToolbar from "../components/CustomToolbar";
import { mapActions, mapGetters } from 'vuex';
export default {
    name: "ChangePassword",

    components: {
        CustomToolbar
    },

    data(){
        return {
            dialogPass: false,
            oldPass: "",
            newPass: "",
            confirmPass: ""
        }
    },
    created(){
        this.fetchUserProf();
    },
    computed: {
        //mora da se fetchujeee
        ...mapGetters('userProfile', ['getUserProf']),

        passwordMatch(){
            return () => (this.newPass === this.confirmPass) || "Password must match.";
        },
        requiredRule(){
            return (value) => !!value || "Required.";
        }
    },

    methods: {
        ...mapActions('userProfile',['changePassword','fetchUserProf']),
        ...mapActions('snackbar',['showError']),

        closeDialog(){
            this.dialogPass = false;
            this.$refs.form.reset();
            const role = localStorage.getItem('user_role')
            if(localStorage.getItem('is_password_changed') == 'true' || role === 'ROLE_PATIENT'){
                this.$router.push({
                    name: "InstantHomeRedirect"
                });
            }
            else{
                this.showError("You can't access Home page before changing password");
            }
        },
        savePassword(){
            if(this.$refs.form.validate()){
                var passForm = {
                    id : this.getUserProf.id,
                    old : this.oldPass,
                    new_pass : this.newPass
                }
                this.changePassword(passForm)
                    .then(() => {
                        this.closeDialog(); 
                    }).catch(() => {});

                
            }
        }
    }
}
</script>

