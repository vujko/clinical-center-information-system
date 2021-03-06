package com.example.demo.dto;

import com.example.demo.model.Clinic;
import com.example.demo.model.Doctor;
import com.example.demo.model.PriceList;
import com.example.demo.model.PriceListItem;
import com.example.demo.model.Rating;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClinicsDTO {

    private static ModelMapper modelMapper = new ModelMapper();

    private String id;
    private String name;
    private String address;
    private String description;
    private Map<String, Float> priceList = new HashMap<>();
    private Float rating;
    private List<DoctorDTO> doctors = new ArrayList<>();

    public ClinicsDTO() {

    }

    public void setDTOFields(Clinic clinic) {
        setPriceList(clinic.getPriceList());
        setDoctors(clinic.getDoctors());
        setRating(clinic.getRating());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Map<String, Float> getPriceList() {
        return priceList;
    }

    public void setPriceList(PriceList priceList) {
        if (priceList == null)
            return;
        for (PriceListItem item : priceList.getItems())
            this.priceList.put(item.getExaminationType().getName(), item.getPrice());
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating.getAverageGrade();
    }

    public List<DoctorDTO> getDoctors() {
        return doctors;
    }

    public void setDoctors(Collection<Doctor> doctors) {
        this.doctors.clear();
        for (Doctor doctor : doctors) {
            DoctorDTO doctorDTO = modelMapper.map(doctor, DoctorDTO.class);
            doctorDTO.setFields(doctor);
            this.doctors.add(doctorDTO);
        }
    }
//
//    public void setDoctors(List<DoctorDTO> doctors) {
//        this.doctors = doctors;
//    }
}
