package com.example.demo.dto;

import com.example.demo.model.*;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClinicDTO {
    private String id;
    private String name;
    private String address;
    private String description;
    private String priceList;
    private String rating;
    private List<String> doctors;
    private List<String> nurses;
    private List<AppointmentDTO> appointments;
    private List<String> rooms;
//    private String codebook; //

    public void setDTOFields(Clinic clinic){
        this.setId(clinic.getId());
        this.setRating(clinic.getRating());
        this.setDoctors(clinic.getDoctors());
        this.setNurses(clinic.getNurses());
        this.setAppointments(clinic.getAppointments());
        this.setRooms(clinic.getRooms());
    }

    public String getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriceList() {
        return priceList;
    }

    public void setPriceList(String priceList) {
        this.priceList = priceList;
    }

    public String getRating() {
        return rating;
    }

    //ovo mozda moze biti problem
    public void setRating(Rating rating){ this.rating = rating.getAverageGrade()+"";}

    public List<String> getDoctors() {
        return doctors;
    }

    public void setDoctors(Collection<Doctor> doctors) {
        if(doctors == null) return;
        List<String> strDoctors = new ArrayList<>();

        for (Doctor doctor : doctors) {
            strDoctors.add(doctor.getFirstName() + " " + doctor.getLastName());
        }

        this.doctors = strDoctors;
    }

    public List<String> getNurses() {
        return nurses;
    }

    public void setNurses(Collection<Nurse> nurses) {
        if(nurses == null) return;
        List<String> strNurses = new ArrayList<>();

        for (Nurse nurse : nurses) {
            strNurses.add(nurse.getFirstName() + " " + nurse.getLastName());
        }

        this.nurses = strNurses;
    }

    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(Collection<Appointment> appointments) {
        if(appointments == null) return;
        List<AppointmentDTO> appointmentDTOS = new ArrayList<>();

        ModelMapper modelMapper = new ModelMapper();
        for (Appointment appointment : appointments) {
            AppointmentDTO appointmentDTO = modelMapper.map(appointment, AppointmentDTO.class);
            appointmentDTO.setFields(appointment);
            appointmentDTOS.add(appointmentDTO);
        }

        this.appointments = appointmentDTOS;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public void setRooms(Collection<Room> rooms) {
        if(rooms == null) return;
        List<String> strOperationRooms = new ArrayList<>();

        for (Room room : rooms) {
            strOperationRooms.add(room.getName() + " " + room.getClinic().getName());
        }

        this.rooms = strOperationRooms;
    }

//    public String getCodebook() {
//        return codebook;
//    }
//
//    public void setCodebook(String codebook) {
//        this.codebook = codebook;
//    }
}
