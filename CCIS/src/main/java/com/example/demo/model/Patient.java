package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient extends User {

   @OneToOne(mappedBy = "patient", cascade = {CascadeType.ALL}, fetch = LAZY)
   private MedicalRecord medicalRecord;

   @ManyToOne(fetch = LAZY)
   @JoinColumn(name="clinic_id")
   private Clinic clinic;

   @LazyCollection(LazyCollectionOption.FALSE)
   @OneToMany(mappedBy = "patient", cascade = {CascadeType.ALL},fetch = LAZY)
   private Collection<Appointment> appointments;

   @Type(type = "true_false")
   private boolean verified;


   public Patient() {
      this.appointments = new HashSet<Appointment>();
   }
   public Patient(Integer id, String email, String password, String name, String lastName, String address, String city, String country, String phone, String socialSecurityNumber, MedicalRecord medicalRecord, List<Authority> authorities) {
      super(id, email, password, name, lastName, address, city, country, phone, socialSecurityNumber, null, authorities);
      this.medicalRecord = medicalRecord;
      this.appointments = new HashSet<Appointment>();
   }

   public Patient(String email, String password, String name, String lastName, String address, String city, String country, String phone, String socialSecurityNumber, List<Authority> authorities) {
      super(email, password, name, lastName, address, city, country, phone, socialSecurityNumber, null, authorities, false);
      this.medicalRecord = new MedicalRecord(this);
      this.appointments = new HashSet<Appointment>();
   }

   public Patient(String email, String password, String name, String lastName, String address, String city, String country, String phone, String socialSecurityNumber, List<Authority> authorities, boolean verified) {
      super(email, password, name, lastName, address, city, country, phone, socialSecurityNumber, null, authorities, false);
      this.medicalRecord = new MedicalRecord(this);
      this.appointments = new HashSet<Appointment>();
      this.verified = verified;
   }

   public Collection<Appointment> getAppointments() {
      return appointments;
   }

   public void setAppointments(Collection<Appointment> appointments) {
      this.appointments = appointments;
   }

   public boolean isVerified() {
      return verified;
   }

   public void setVerified(boolean verified) {
      this.verified = verified;
   }

   public MedicalRecord getMedicalRecord() {
      return medicalRecord;
   }

   public void setMedicalRecord(MedicalRecord medicalRecord) {
      this.medicalRecord = medicalRecord;
   }

   public Clinic getClinic() {
      return clinic;
   }

   public void setClinic(Clinic clinic) {
      this.clinic = clinic;
   }

   public void addAppointment(Appointment a){
      this.appointments.add(a);
   }

   public void removeAppointment(Appointment a){
      if(a == null) return;
      if(appointments.contains(a)){
         appointments.remove(a);
      }
   }
}