<template>
    <div>
        <v-container>
            <v-form ref="form">
                <v-card>
                    <v-card-title>
                        <v-text-field
                        v-model="filterName"
                        label="Filter doctors by name"
                        show-details>
                        </v-text-field>

                        <v-spacer></v-spacer>

                        <v-text-field
                        v-model="filterSurname"
                        label="Filter doctors by surname"
                        show-details>
                        </v-text-field>

                        <v-spacer></v-spacer>

                        <v-select
                        v-model.number="filterRating"
                        :items="numbers"
                        label="Filter doctors by rating"
                        @click="filterRating = -1">
                        </v-select>

                        <v-spacer></v-spacer>

                        <v-menu
                        v-if="!alreadyDateTypeFiltered"
                        v-model="fromDateMenu"
                        :close-on-content-click="true"
                        :nudge-right="40"
                        transition="scale-transition"
                        offset-y
                        max-width="290px">
                            <template v-slot:activator="{ on }">
                                <v-text-field  v-model="filterDate"
                                v-on="on"
                                label="Pick date"
                                :value="filterDate"
                                hint="YYYY-MM-DD format"
                                readonly
                                :rules="[requiredRule]"
                                @click="filterDate = ''"/>
                            </template>
                            <v-date-picker  
                            v-model="filterDate"
                            :min="nowDate"
                            @input="fromDateMenu = false">           
                            </v-date-picker>          
                        </v-menu>

                        <v-spacer
                        v-if="!alreadyDateTypeFiltered">
                        </v-spacer>

                        <v-select
                        v-if="!alreadyDateTypeFiltered"
                        v-model="filterType"
                        :rules="[requiredRule]"
                        :items="this.getTypes()"
                        item-text="name"
                        label="Chose examination type"
                        @click="filterType = ''">
                        </v-select>

                        <v-spacer
                        v-if="!alreadyDateTypeFiltered">
                        </v-spacer>

                        <v-btn @click="validate">Apply filters</v-btn>
                    </v-card-title>
                </v-card>
            </v-form>

            <v-data-table
            :headers="headers"
            :items="getDoctorsTable"
            class="blue-grey darken-4 white--text"
            show-expand
            dark>

                <template v-slot:expanded-item="{ headers, item }">
                    <td :colspan="headers.length">
                        <th>Free appointment</th>
                        <tr v-for="freeAppointment in item.freeAppointments" v-bind:key="freeAppointment.id">
                            <td>{{freeAppointment.time}}</td>
                            <td>
                                <v-btn color="blue"
                                @click="onClick({
                                    doctorId: item.id,
                                    clinicId: item.clinicId,
                                    appointmentTime: freeAppointment.time,
                                    patientEmail: '',
                                    price: item.price
                                })">
                                    Zakaži pregled
                                </v-btn>
                            </td>
                        </tr>
                    </td>
                </template>
            </v-data-table>

        </v-container>
    </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import Vue from 'vue';

export default {
    name: "Doctors",

    data() {
        return {
            filterName: "",
            filterSurname: "",
            filterRating: -1,
            filterDate: "",
            filterType: "",
            alreadyDateTypeFiltered: false,
            applyFilters: false,
            doctors: [],
            numbers: Array.from(new Array(101), (item, index) => (100 - index)/10),
            headers: [
                {text: "Name", value: "firstName"},
                {text: "Surname", value: "lastName"},
                {text: "Examination type", value: "examinationType.name"},
                {text: "Rating", value: "rating"},
                {text: "Price", value: "price"},
                {text: "Clinic", value: "clinic"}
            ]
        }
    },

    methods: {
        ...mapActions("examination_type", ["fetchExaminationTypes"]),
        ...mapGetters("examination_type", ['getTypes']),

        onClick(appointmentRequest) {
            if (this.$refs.form.validate()) {
                appointmentRequest.patientEmail = localStorage.getItem("user_email");
                Vue.$axios.post("http://localhost:8081/appointmentRequests/addAppointmentRequest", appointmentRequest)
                .then(response => {
                    if (response.status === 200) {
                        this.$store.dispatch('snackbar/showSuccess',
                        "Vaš zahtev za lekarski pregled je poslat serveru. Odgovor da li je zahtev prihvaćen ili odbijen ćete dobiti na mejl.", 
                        {root: true});
                        
                    } else {
                        this.$store.dispatch('snackbar/showError', "Unknown error: " + response.status + ".\nMessage: " + response.data, {root: true});
                    }
                }).catch(error => {
                    this.$store.dispatch('snackbar/showError', error.response.data, {root: true});
                });
            }
        },

        validate() {
            if (this.$refs.form.validate()) {
                this.setApplyFilters(true);
            }
        },

        setApplyFilters(newApply) {
            this.applyFilters = newApply;
        },

        // Izdvaja i sate
        dateToString(item){
            var d = new Date(item);
            return d.toString().substring(0,21);
        },

        setAppointments(doctor) { 
            doctor["freeAppointments"] = [];
            var durationMiliseconds = doctor.examinationType.duration * 3600000;
            var selectedDate = new Date(this.filterDate);
            selectedDate.setHours(7,0,0,0);

            var endDay = new Date(this.filterDate);
            endDay.setHours(14,0,0,0);

            var takenTimes = [];
            for (let i = 0; i < doctor.calendar.eventStartDates.length; i++) {
                var startDateElement = new Date(doctor.calendar.eventStartDates[i]);
                var endDateElement = new Date(doctor.calendar.eventEndDates[i]);
                if (selectedDate.toDateString() == startDateElement.toDateString()) {
                    takenTimes.push({
                        eventStartDate: startDateElement,
                        eventEndDate: endDateElement
                    })
                }
            }

            var fakeId = 0;
            if (takenTimes.length === 0) {
                while (selectedDate.getTime() + durationMiliseconds <= endDay.getTime()) {
                    fakeId++;
                    doctor.freeAppointments.push({
                        id: doctor.id.toString() + fakeId.toString(),
                        time: this.dateToString(selectedDate)
                    });
                    selectedDate.setTime(selectedDate.getTime() + durationMiliseconds)
                }
            } else {
                fakeId = 0;
                for (var i = 0; i < takenTimes.length; i++) {
                    const taken = takenTimes[i];
                    var startDate = taken.eventStartDate;
                    var endDate = taken.eventEndDate;

                    while (selectedDate.getTime() + durationMiliseconds <= startDate.getTime() &&
                        selectedDate.getTime() + durationMiliseconds <= endDay.getTime()) {
                        doctor.freeAppointments.push({
                            id: doctor.id.toString() + fakeId.toString(),
                            time: this.dateToString(selectedDate)
                        });
                        selectedDate.setTime(selectedDate.getTime() + durationMiliseconds);
                    }

                    selectedDate.setTime(endDate.getTime());
                }

                while (selectedDate.getTime() + durationMiliseconds <= endDay.getTime()) {
                    fakeId++;
                    doctor.freeAppointments.push({
                        id: doctor.id.toString() + fakeId.toString(),
                        time: this.dateToString(selectedDate)
                    });
                    selectedDate.setTime(selectedDate.getTime() + durationMiliseconds)
                }
            }
        }
    },

    created() {
        this.doctors = JSON.parse(sessionStorage.getItem("doctors"));

        var filterDetails = JSON.parse(sessionStorage.getItem("filterDetails"));
        this.filterDate = filterDetails.filterDate;
        this.filterType = filterDetails.filterType;
        this.alreadyDateTypeFiltered = filterDetails.alreadyFiltered;

        if (this.alreadyDateTypeFiltered) {
            this.doctors.forEach(doctor => {
                this.setAppointments(doctor);
            });
        }

        this.fetchExaminationTypes();
    },

    computed: {
        nowDate(){
            return new Date().toISOString().slice(0,10);
        },

        requiredRule(){
            return (value) => !!value || "Required.";
        },

        getDoctorsTable: function() {
            // Is button clicked for applying filters?
            if (this.applyFilters) {
                // Reset state of button and perform filter
                this.setApplyFilters(false);
                return this.doctors.filter(doctor => {
                    var foundByName = false;
                    var foundBySurname = false;
                    var foundByRating = false;

                    foundByName = doctor.firstName.toLowerCase().match(this.filterName.toLowerCase());
                    
                    // If not found by name, then skip current doctor
                    if (!foundByName) {
                        return false;
                    }

                    foundBySurname = doctor.lastName.toLowerCase().match(this.filterSurname.toLowerCase());
                    
                    // If not found by surname, then skip current doctor
                    if (!foundBySurname) {
                        return false;
                    }

                    if (this.filterRating === -1) {
                        foundByRating = true;
                    } else {
                        foundByRating = Number(doctor.rating) === this.filterRating;
                    }

                    // If not found by rating, then skip current doctor
                    if (!foundByRating) {
                        return false;
                    }

                    if (this.alreadyDateTypeFiltered) {
                        return true;
                    } else {
                        // Does chosen examination name matches doctors examination type?
                        if (doctor.examinationType.name.match(this.filterType)) {
                            
                            let startSelectedDay = (new Date(this.filterDate));
                            startSelectedDay.setHours(7,0,0,0);
                            let startSelectedDayMiliseconds = startSelectedDay.getTime();
                            
                            let endSelectedDay = new Date(this.filterDate);
                            endSelectedDay.setHours(14,0,0,0);

                            let vacationDates = doctor.calendar.vacationDates;
                            var hasVacation = vacationDates.length == 2;
                            if (hasVacation) {
                                let startVacation = vacationDates[0];
                                let endVacation = vacationDates[1];

                                // If doctor is on vacation, skip him 
                                if (startVacation.getTime() <= startSelectedDayMiliseconds < endVacation.getTime()) {
                                    return false;
                                }
                            }

                            var hours = doctor.examinationType.duration;
                            let durationMilliseconds = hours * 1000 * 60 * 60;

                            let eventStartDates = doctor.calendar.eventStartDates.slice();
                            let eventEndDates = doctor.calendar.eventEndDates;

                            eventStartDates.unshift(startSelectedDay);

                            for (var i = 1; i <= eventStartDates.length; i++) {
                                let startAppDate = new Date(eventStartDates[i]);
                                let endAppDate = new Date(eventEndDates[i-1]);
                                
                                // Can the appointment be set BEFORE the first already set appointment 
                                if(i == 1 && new Date(eventStartDates[i-1]).getTime() + durationMilliseconds <= startAppDate.getTime()){
                                    this.setAppointments(doctor);
                                    return true;
                                }
                                
                                // Can the appointment be set INBETWEEN already set appointments
                                if(i < eventStartDates.length && endAppDate.getTime() + durationMilliseconds  <= new Date(eventStartDates[i + 1]).getTime()){
                                    this.setAppointments(doctor);
                                    return true;
                                }

                                // Can the appontment be set AFTER the last already set appointment
                                if(i == eventStartDates.length - 1 && new Date(endAppDate).getTime() + durationMilliseconds <= endSelectedDay.getTime()){
                                    this.setAppointments(doctor);
                                    return true;
                                }
                            }
                            // Doctor does not have time for chosen examination date
                            return false;
                            
                        } else {
                            // Chosen examination type does not match current doctors examination type
                            return false;
                        }
                    }
                });
            } else {
                // Button for applying filters is not clicked, return all clinics
                return this.doctors;
            }
        },
    }
}
</script>

<style scoped>

</style>