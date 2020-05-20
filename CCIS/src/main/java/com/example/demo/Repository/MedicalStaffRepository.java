package com.example.demo.Repository;

import com.example.demo.model.MedicalStaff;
import com.example.demo.model.MedicalStaffRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicalStaffRepository extends JpaRepository<MedicalStaffRequest, Integer> {

//    @Query("delete from MedicalStaffRequest req where req.id=:id")
//    void deleteRequest(@Param("id") Integer id);

}