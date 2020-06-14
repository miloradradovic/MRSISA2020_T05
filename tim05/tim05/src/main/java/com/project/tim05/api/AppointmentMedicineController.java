package com.project.tim05.api;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin(origins = "https://localhost:4200")

@CrossOrigin(origins = "https://eclinic05.herokuapp.com")
@RestController
@RequestMapping(value = "/appointmentMedicine")
public class AppointmentMedicineController {

}
