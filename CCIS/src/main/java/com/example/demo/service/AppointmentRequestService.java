package com.example.demo.service;

import com.example.demo.Repository.AppointmentRequestRepository;
import com.example.demo.model.AppointmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentRequestService {

    @Autowired
    private AppointmentRequestRepository appointmentRequestRepository;

    public List<AppointmentRequest> getRequests(){
        return appointmentRequestRepository.findAll();
    }

    public boolean deleteRequest(Integer id){
        Optional<AppointmentRequest> req = appointmentRequestRepository.findById(id);
        if(req.isPresent()){
            appointmentRequestRepository.deleteById(id);
            return true;
        }
        return false;


    }
}