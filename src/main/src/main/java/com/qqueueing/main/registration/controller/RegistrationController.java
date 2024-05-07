package com.qqueueing.main.registration.controller;

import com.qqueueing.main.common.SuccessResponse;
import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.service.RegistrationService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/queue")
@RestController
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<?> createRegistration(@RequestBody Registration registration) {
        Registration createdRegistration = registrationService.createRegistration(registration);
        String message = "등록되었습니다.";
        SuccessResponse response = new SuccessResponse(HttpStatus.CREATED.value(), message, createdRegistration);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllRegistrations() {
        List<Registration> registrations = registrationService.getAllRegistrations();
        String message = "조회에 성공했습니다.";
        SuccessResponse response = new SuccessResponse(HttpStatus.OK.value(), message, registrations);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRegistrationById(@PathVariable String id) throws ChangeSetPersister.NotFoundException {
        Registration registration = registrationService.getRegistrationById(id);
        String message = "상세 조회에 성공했습니다.";
        SuccessResponse response = new SuccessResponse(HttpStatus.OK.value(), message, registration);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateRegistrationById(@PathVariable String id, @RequestBody RegistrationUpdateRequest request) {
//        Registration updatedRegistration = registrationService.updateRegistrationById(id, request);
//        String message = "수정에 성공했습니다.";
//        SuccessResponse response = new SuccessResponse(HttpStatus.OK.value(), message, updatedRegistration);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}
