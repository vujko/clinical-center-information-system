<template>
    <v-dialog v-model="dialog" persistent max-width="450px">
        <template v-slot:activator="{ on }">
            <v-btn  v-on="on" color="blue">Oceni doktora</v-btn>
        </template>
        <v-card>
            <v-toolbar height="45px" color="blue" class="white--text">
                <span class="headline">Ocenite doktora</span>
            </v-toolbar>
            <v-card-text>
                <v-form ref="form">
                    <v-container>
                        <v-row cols="12" sm = "6">
                            <v-select
                            v-model="id"
                            label="Doctor"
                            :items="this.doctors"
                            :rules="[requiredRule]"
                            item-text="fullName"
                            item-value="id">
                            </v-select>
                        </v-row>
                        <v-row cols="12" sm="6">
                            <v-rating
                            v-model="newGrade"
                            v-bind:length="10"
                            color="yellow darken-3"
                            background-color="grey darken-1"
                            empty-icon="$ratingFull"
                            half-increments
                            dense
                            large
                            hover>
                            </v-rating>
                        </v-row>
                    </v-container>
                </v-form>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="closeDialog">Odustani</v-btn>
                <v-btn color="blue darken-1" text @click="save">Oceni</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import Vue from "vue";
export default {
    name: "GradeDoctor",
    props: ["doctors"],

    data() {
        return {
            id: 0,
            newGrade: 10,
            dialog: false
        }
    },

    methods: {
        closeDialog(){
            this.dialog = false;
            this.$refs.form.reset();
        },
        save(){
            if (this.$refs.form.validate()) {
                Vue.$axios.post("http://localhost:8081/doctors/gradeDoctor", {
                    itemId: new Number(this.id),
                    newGrade: this.newGrade,
                    patientEmail: localStorage.getItem("user_email")
                })
                this.closeDialog();
            }
        }
    },

    computed: {
        requiredRule(){
          return (value) => !!value || "Required.";
        },
    }
}
</script>

<style scoped>

</style>