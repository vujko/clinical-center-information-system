package com.example.demo.api;


import com.example.demo.dto.AppointmentDTO;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.ExaminationType;
import com.example.demo.service.ExaminationTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/ex_type")
@RestController
public class ExaminationTypeController {

    private final ExaminationTypeService examinationTypeService;

    public ExaminationTypeController(ExaminationTypeService examinationTypeService) {
        this.examinationTypeService = examinationTypeService;
    }

    @GetMapping("/getTypes")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN', 'PATIENT')")
    public List<ExaminationType> getExaminationType(){
        return examinationTypeService.findAllTypes();
    }

    @DeleteMapping(path="/delete/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN')")
    public ResponseEntity deleteExaminationType(@PathVariable("id") Integer id){
        if(examinationTypeService.removeType(id)){
            return ResponseEntity.ok().body("Successfully deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cant delete used type");
    }
    @PostMapping("/updateType")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN')")
    public ResponseEntity updateType(@RequestBody ExaminationType examinationType) throws NotFoundException {
        ExaminationType ex_type = examinationTypeService.updateType(examinationType);

        return ResponseEntity.ok().body(ex_type);

    }

    @PostMapping("/addType")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN')")
    public ResponseEntity<ExaminationType> addType(@RequestBody ExaminationType examinationType){
        ExaminationType savedType = examinationTypeService.saveType(examinationType);

        if(savedType != null)
            return new ResponseEntity<ExaminationType>(savedType, HttpStatus.CREATED);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
