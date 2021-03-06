import axios from "axios";
import Vue from 'vue';

const getDefaultState = () => {
    return {
        rooms : [],
        filteredRooms : [],
        availableTimes : null,
        clinicDoctors: [],
        //Key: doctors name, Value: doctor
        clinicDoctorsDict: {}
    }
};

const state = getDefaultState();

const getters = {
    getAllRooms: (state) => state.rooms,

    getFilteredRooms: (state) => () => {
        return state.filteredRooms;
    },

    getAvailableTimes: (state) => () =>{
        return state.availableTimes;
    },

    getClinicDoctorsDict: (state) => () =>{
        return state.clinicDoctorsDict;
    }
};

const actions = {
    
    
    async filterRooms({commit,state}, payload){
        
        Vue.$axios.get("http://localhost:8081/rooms/getFiltered?duration=" + payload.duration + "&date=" + payload.date)
        .then(response => {
            commit('setAvailableTimes', response.data);    
        })
        let filtered = [];
        if((payload.search == "" && payload.type == "") || payload.type === undefined || payload.search === undefined){
            state.rooms.forEach(room => {
                filtered.push(room);
            })
        }
        else if(payload.type == ""){
            state.rooms.forEach(room => {
                if(room.name.toUpperCase() == payload.search.toUpperCase()){
                    filtered.push(room);
                }
            })
        }
        else if(payload.search == ""){
            state.rooms.forEach(room => {
                if(room.type.toUpperCase() == payload.type.toUpperCase()){
                    filtered.push(room);
                }
            })
        }
        else{
            state.rooms.forEach(room =>{
                if(room.type.toUpperCase() == payload.type.toUpperCase() && room.name.toUpperCase() == payload.search.toUpperCase()){
                    filtered.push(room);
                }
            })
        }
        commit('setFilteredRooms', filtered);
              
    },

    async handleReservation({dispatch, commit}, payload){
        
        await Vue.$axios.post("http://localhost:8081/clinicAdmins/handleReservation", payload)
        .then(response => {
            dispatch("snackbar/showSuccess", response.data, {root:true});
            dispatch('appointmentRequests/deleteRequest',  payload.requestId, {root : true});
        })
        .catch(err => {
            dispatch("snackbar/showError", err.response.data, {root: true});
        })
        const response = await Vue.$axios.get("http://localhost:8081/rooms/getRoom/" +  payload.room.id);
        commit('updatedRoom', response.data);        
        commit('resetTable');



    },

    async fetchRooms({commit}){
      
        const response = await  Vue.$axios.get("http://localhost:8081/rooms/getRooms");

        commit('setRooms', response.data);
        commit('setFilteredRooms', response.data);

    },

    async fetchClinicDoctors({commit}){
        
        let config = {
            headers: {
                Authorization: "Bearer " + localStorage.getItem("JWT"),
            }
          }
        const response = await axios.get('http://localhost:8081/clinicAdmins/getClinicDoctors/' + localStorage.getItem('user_email'), config);
        commit('setClinicDoctors', response.data);
        commit('setClinicDoctorsDict');

    },

    async deleteRoom({commit, dispatch}, id){
        try{
            await Vue.$axios.delete('http://localhost:8081/rooms/deleteRoom/' + id);
            commit('deletedRoom', id);
            dispatch('snackbar/showSuccess', "Successfully deleted.", {root: true});
        }catch(error){
            dispatch('snackbar/showWarning', "Can't delete. Room is scheduled.", {root: true});
        }
    },
    async addRoom({commit}, room){
        try{
            const response = await Vue.$axios.post('http://localhost:8081/rooms/addRoom', room);
            commit('addedRoom', response.data); 
        }catch(error){
            alert(error.response);
        }
    },
    async updateRoom({commit,dispatch}, room){
        try{
            const response = await Vue.$axios.post('http://localhost:8081/rooms/updateRoom', room);
            commit('updatedRoom', response.data);
            dispatch("snackbar/showSuccess", "Successfully updated", {root: true});
        }catch(error){
            dispatch("snackbar/showWarning", "Can't update. Room is scheduled.", {root:true});
        }
    },

    resetRoom({commit}) {
        commit("resetState");
    }

};

const mutations = {
    setRooms: (state, rooms) => {
        state.rooms = rooms
    },
    setFilteredRooms: (state, frooms) => {
        state.filteredRooms = frooms;
    },
    setAvailableTimes: (state, times) => {
        state.availableTimes = times;
    },
    setClinicDoctors: (state, doctors) =>{
        state.clinicDoctors = doctors;
    },
    setClinicDoctorsDict: (state) => {
        state.clinicDoctorsDict = {};
        state.clinicDoctors.forEach(doctor => {
            state.clinicDoctorsDict[doctor.firstName + ' ' + doctor.lastName] = doctor;
        });
    },
    deletedRoom(state,id) {
        const index = state.rooms.findIndex(type => type.id === id);
        state.rooms.splice(index,1);
    },
    addedRoom: (state,room) => state.rooms.push(room),

    updatedRoom (state,room){
        const index = state.rooms.findIndex(t => t.id === room.id);
        Object.assign(state.rooms[index], room);
    },

    resetTable: (state) => {
        state.filteredRooms = state.rooms;
        state.availableTimes = null;
    },
    resetState(state) {
        Object.assign(state, getDefaultState());
    }
};


const namespaced = true;

export default {
    namespaced,
    state,
    getters,
    actions,
    mutations
};