<template>
    <div class="home"> 
        <div>
             <v-card>
                 <v-card-title>
                     Rooms
                 </v-card-title>
                <v-card-title>
                    <v-text-field
                        v-model="search"
                        label="Search"
                        single-line
                        show-details
                        
                    ></v-text-field>
                    <v-spacer></v-spacer>
                    <v-menu
                        ref="menu"
                        v-model="menu2"
                        :close-on-content-click="false"
                        :nudge-right="40"
                        :return-value.sync="duration"
                        transition="scale-transition"
                        offset-y
                        max-width="290px"
                        min-width="290px"
                    >   
                        <template v-slot:activator="{ on }">
                        <v-text-field
                            v-model="duration"
                            label="Pick duration"
                            prepend-icon="mdi-access_time"
                            readonly
                            v-on="on"
                        ></v-text-field>
                        </template>
                        <v-time-picker
                        v-if="menu2"
                        v-model="duration"
                        :allowed-minutes="allowedMinutes"
                        :allowed-hours="allowedHours"
                        format="24h"
                        full-width
                        @click:minute="$refs.menu.save(duration)"
                        :min="nowDate"
                        ></v-time-picker>
                    </v-menu>
                    <v-spacer></v-spacer>
                    <v-menu
                        v-model="fromDateMenu"
                        :close-on-content-click="true"
                        :nudge-right="40"
                        transition="scale-transition"
                        offset-y
                        max-width="290px">
                        <template v-slot:activator="{ on }">
                            <v-text-field  v-model="date"
                                v-on="on"
                                label="Pick date"
                                :value="date"
                                hint="MM/DD/YYYY format"
                                />
                        </template>
                        <v-date-picker  v-model="date"
                                @input="fromDateMenu = false">           
                        </v-date-picker>          
                    </v-menu>
                    <v-spacer></v-spacer>
                    <v-combobox
                        v-model="type"
                        :items="types"
                        label="Select type"

                    >

                    </v-combobox>
                    <v-spacer></v-spacer>
                    <v-btn @click="filter">Search</v-btn>
                </v-card-title>          
                
                <v-data-table 
                    :ref="table"
                    :headers="headers"
                    :items="filteredRooms"
                    :items-per-page="5"
                    :expanded.sync="expanded"
                    item-key="name"
                    show-expand
                    class="white"
                    >
                <template v-slot:expanded-item="{ headers, item }">
                    <td :colspan="headers.length" v-if="item.calendar != null">
                        <tr v-for="it in item.calendar.eventStartDates.length" v-bind:key=it.name>
                            <td>Start date: {{ dateToString(item.calendar.eventStartDates[it-1])}}</td>
                            <td>End date: {{ dateToString(item.calendar.eventEndDates[it-1])}}</td>
                        </tr>
                        <tr v-if="availableTimes">
                            <td >First available appointment : {{dateToString(availableTimes[item.id])}}</td>
                            <td style="text-align:center; margin-left:140px;"><v-btn @click="reserveRoom(item)" color="blue" dark>Reserve room</v-btn> </td>
                            <v-row justify="center">
                                <v-dialog v-model="dialog" persistent max-width="500">
                                <v-card>
                                    <v-card-title class="headline">Choose doctors to attend operation</v-card-title>
                                    <v-container fluid>
                                    <v-row align="center">
                                        <v-col sm="100">
                                            <v-select
                                            v-model="doctorsSelect"
                                            :items="doctorsForSelect"
                                            :menu-props="{ maxHeight: '400' }"
                                            label="Select"
                                            style="width:500px"
                                            multiple
                                            ></v-select>
                                        </v-col>
                                    </v-row>
                                    </v-container>
                                    <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="blue darken-1" text @click="dialog = false">Cancel</v-btn>
                                    <v-btn color="blue darken-1" text @click="sendNotification()">Send notification</v-btn>
                                    </v-card-actions>
                                </v-card>
                                </v-dialog>
                            </v-row>
                        </tr>
                     </td>
                </template>
                <template v-slot:top>
                
                 </template>
                <!-- <template v-slot:item.actions="{ item }">
                </template> -->
                </v-data-table>
                </v-card>
        </div>
        
    </div> 
</template>
<script>

import {mapGetters, mapActions} from 'vuex'
 
export default {
    name: 'Rooms',
    

    data(){
        return {
            headers: [
                {
                    text: 'Name', value: 'name',fileterable: true
                },
                {
                    text: 'Number', value: 'number', fileterable:true            
                },
                { 
                    text: 'Type', value: 'type', sortable: true 
                },
            ],
            search: "",
            date:"",
            type:"",
            types:['Appointment','Operation'],
            expanded: [],
            duration: "00:00",
            menu2: false,
            dialog: false,
            editedIndex: -1,
            typesEx: ["APPOINTMENT","OPERATION"],
            doctorsSelect: [],
            mainDoctor: {email: ''},
            selectedRoom : null
        }
    },
    methods: {
        ...mapActions('room',['fetchRooms','filterRooms', 'fetchClinicDoctors',
         'handleReservation']),

        dateToString(item){
            var d = new Date(item);
            return d.toString().substring(0,21);
        },
        filter(){
          this.filterRooms({'search': this.search, 'date' : this.date, 'duration' : this.duration, 'type': this.type ? this.type : ''});         
        },

        reserveRoom(room){
            this.selectedRoom = room;
            if(room.type.toUpperCase().match('OPERATION')){
                this.showSelectDoctorsDialog();
            }
            else{
                this.sendNotification();
            }
        },
        showSelectDoctorsDialog(){
            this.doctorsSelect = [];
            this.dialog = true;
        },

        sendNotification(){
            let doctorsIds = [];
            this.doctorsSelect.concat(this.mainDoctor.firstName + ' ' + this.mainDoctor.lastName).forEach(name => {
                doctorsIds.push(this.clinicDoctorsDict[name].id);
            });
            let payload = {doctorsIds: doctorsIds, requestId : this.request.id, room : this.selectedRoom, reservedTime : this.availableTimes[this.selectedRoom.id]}
            this.handleReservation(payload);
            this.dialog = false;
            this.$emit('reserved');
            this.resetTable();
        },     
        resetTable(){
            this.$data.expanded = [];
            this.type = null;
        },
        allowedMinutes: m => m % 15 === 0,
        allowedHours: h => h <= 10,


        setUpFields(request){
            this.request = request;
            this.date = request.time;
            this.mainDoctor = request.doctor;
            this.duration = this.floatToHours(request.doctor.examinationType.duration);
        },

        getDoctorsForSelect(){
            let clinicDoctorsDictCopy = {...this.clinicDoctorsDict};
            for(let key in clinicDoctorsDictCopy){
                if(clinicDoctorsDictCopy[key].email.match(this.mainDoctor.email)){
                    delete clinicDoctorsDictCopy[key];
                }
            }
            return Object.keys(clinicDoctorsDictCopy);
        },

        floatToHours(durationMilliseconds){
            var minutes = Math.floor((durationMilliseconds * 60) % 60),
                hours = Math.floor((durationMilliseconds) % 24);

            hours = (hours < 10) ? "0" + hours : hours;
            minutes = (minutes < 10) ? "0" + minutes : minutes;
            return (hours+ ':' + minutes);
        }
    },
    computed:{ 
        ...mapGetters('room', ['getAllRooms','getAvailableTimes', 'getFilteredRooms', 'getClinicDoctorsDict']),
        
        doctorsForSelect: function(){
            return this.getDoctorsForSelect();
        },

        filteredRooms: function(){
            return this.getFilteredRooms();
        },

        availableTimes: function(){
            return this.getAvailableTimes();
        },

        clinicDoctorsDict: function(){
            return this.getClinicDoctorsDict();
        },
        formTitle () {
            return this.editedIndex === -1 ? "Add new room" : "Edit room"
        },
        requiredRule(){
          return (value) => !!value || "Required.";
        },
        numberRule(){
            return v => /(^(\+)?\d+(\.\d+)?$)/.test(v) || "Input must be valid.";
        },
        nowDate(){
            return new Date().toISOString().slice(0,10);
        }
    },
    created(){
        this.fetchRooms();
        this.fetchClinicDoctors();
    },
}
</script>


<style>
.upper{
    position: relative;
}


</style>