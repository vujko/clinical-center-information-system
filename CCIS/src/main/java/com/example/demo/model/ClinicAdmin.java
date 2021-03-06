package com.example.demo.model;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "clinic_admins")
public class ClinicAdmin extends User {

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "clinic_id",nullable = false)
   @LazyToOne(LazyToOneOption.NO_PROXY)
   private Clinic clinic;

   public ClinicAdmin() {
   }

   public ClinicAdmin(Integer id, String email, String password, String name, String lastName, String address, String city, String country, String phone, String socialSecurityNumber, List<Authority> authorities) {
      super(id, email, password, name, lastName, address, city, country, phone, socialSecurityNumber, null, authorities);
   }

   public ClinicAdmin(Integer id, String email, String password, String name, String lastName, String address, String city, String country, String phone, String socialSecurityNumber, Clinic clinic, List<Authority> authorities) {
      super(id, email, password, name, lastName, address, city, country, phone, socialSecurityNumber, null, authorities);
      this.clinic = clinic;
   }

   public ClinicAdmin(String email, String password, String name, String lastName, String address, String city, String country, String phone, String socialSecurityNumber, Clinic clinic, List<Authority> authorities, Boolean passwordChanged) {
      super(email, password, name, lastName, address, city, country, phone, socialSecurityNumber, null, authorities, passwordChanged);
      this.clinic = clinic;
   }

   public Clinic getClinic() {
      return clinic;
   }

   public void setClinic(Clinic clinic) {
      this.clinic = clinic;
   }
}