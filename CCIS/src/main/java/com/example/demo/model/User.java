package com.example.demo.model;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User implements UserDetails {
   @Id
   @GeneratedValue(strategy=GenerationType.TABLE, generator="ust_seq_user")
   @SequenceGenerator(name = "ust_seq_user", sequenceName = "ust_seq_user", initialValue = 1, allocationSize=1)
   private Integer id;

   @Column(name = "counter", unique = false, nullable = false)
   private Integer counter = 0;

   @Version
   @Column(name = "version", unique = false, nullable = false)
   private int version;

   @Column(name = "email", unique = true, nullable = false)
   private String email;

   @Column(name = "password", nullable = false)
   private String password;

   @Column(name = "first_name", nullable = false)
   private String firstName;

   @Column(name = "last_name", nullable = false)
   private String lastName;

   @Column(name = "address", nullable = false)
   private String address;

   @Column(name = "city", nullable = false)
   private String city;

   @Column(name = "country", nullable = false)
   private String country;

   @Column(name = "phone_number", nullable = false)
   private String phoneNumber;

   @Column(name = "social_security_number", unique = true, nullable = false)
   private String socialSecurityNumber;

   @Column(name = "last_password_reset_date")
   private Timestamp lastPasswordResetDate;

   @Column(name = "is_password_changed")
   @Type(type = "true_false")
   private Boolean passwordChanged;

   @ManyToMany(fetch = FetchType.EAGER)
   @JoinTable(name = "user_authority",
           joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
           inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
   private List<Authority> authorities;

   public User() {
   }

   public User(Integer id, String email, String password, String firstName, String lastName, String address, String city, String country, String phoneNumber, String socialSecurityNumber, Timestamp lastPasswordResetDate, List<Authority> authorities) {
      this.id = id;
      this.email = email;
      this.password = password;
      this.firstName = firstName;
      this.lastName = lastName;
      this.address = address;
      this.city = city;
      this.country = country;
      this.phoneNumber = phoneNumber;
      this.socialSecurityNumber = socialSecurityNumber;
      this.lastPasswordResetDate = lastPasswordResetDate;
      this.authorities = authorities;
   }

   public User(String email, String password, String firstName, String lastName, String address, String city, String country, String phoneNumber, String socialSecurityNumber, Timestamp lastPasswordResetDate, List<Authority> authorities, Boolean passwordChanged) {
      this.email = email;
      this.password = password;
      this.firstName = firstName;
      this.lastName = lastName;
      this.address = address;
      this.city = city;
      this.country = country;
      this.phoneNumber = phoneNumber;
      this.socialSecurityNumber = socialSecurityNumber;
      this.lastPasswordResetDate = lastPasswordResetDate;
      this.authorities = authorities;
      this.passwordChanged = passwordChanged;
   }

   public Integer getCounter() {
      return counter;
   }

   public void setCounter(Integer counter) {
      this.counter = counter;
   }

   public Boolean isPasswordChanged() {
      return passwordChanged;
   }

   public void setPasswordChanged(Boolean passwordChanged) {
      this.passwordChanged = passwordChanged;
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   @Override
   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public String getCity() {
      return city;
   }

   public void setCity(String city) {
      this.city = city;
   }

   public String getCountry() {
      return country;
   }

   public void setCountry(String country) {
      this.country = country;
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String phone) {
      this.phoneNumber = phone;
   }

   public String getSocialSecurityNumber() {
      return socialSecurityNumber;
   }

   public void setSocialSecurityNumber(String socialSecurityNumber) {
      this.socialSecurityNumber = socialSecurityNumber;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof User)) return false;
      User user = (User) o;
      return socialSecurityNumber.equals(user.socialSecurityNumber);
   }

   @Override
   public int hashCode() {
      return Objects.hash(socialSecurityNumber);
   }

   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
      return this.authorities;
   }

   public void setAuthorities(List<Authority> authorities) {
      this.authorities = authorities;
   }

   public Timestamp getLastPasswordResetDate() {
      return lastPasswordResetDate;
   }

   public void setLastPasswordResetDate(Timestamp lastPasswordResetDate) {
      this.lastPasswordResetDate = lastPasswordResetDate;
   }

   @Override
   public String getUsername() {
      return email;
   }

   @Override
   public boolean isAccountNonExpired() {
      return true;
   }

   @Override
   public boolean isAccountNonLocked() {
      return true;
   }

   @Override
   public boolean isCredentialsNonExpired() {
      return true;
   }

   @Override
   public boolean isEnabled() {
      return true;
   }
}