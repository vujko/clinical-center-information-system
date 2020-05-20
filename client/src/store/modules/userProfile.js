import Vue from 'vue'

const state = {
    userProf: null
};

const getters = {
    getUserProf: (state) => state.userProf,
};

const actions = {
    async fetchUserProf({commit}){
        const response = await Vue.$axios.get('http://localhost:8081/auth/userDetails');
        commit('setUserProf', response.data);
    },
    async updateProfile({commit}, editItem){
        const response = await Vue.$axios.post('http://localhost:8081/auth/updateProfile',editItem);
        commit('setUserProf', response.data);
    },
    async changePassword({commit}, passForm){
        try{
            await Vue.$axios.post('http://localhost:8081/auth/changePassword', passForm);
            commit('alertPasswordChange', "Successfully.");
        }catch(error){
            console.log(error);
            commit('alertPasswordChange', "Netacan stari password");
        }
    }
};

const mutations = {
    setUserProf: (state, user) => state.userProf = user, 
    alertPasswordChange(message){
        alert(message);
    }
};

const namespaced = true;

export default{
    namespaced,
    state,
    getters,
    actions,
    mutations
}