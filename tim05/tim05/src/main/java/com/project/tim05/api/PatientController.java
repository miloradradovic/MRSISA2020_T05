package com.project.tim05.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.tim05.model.Doctor;
import com.project.tim05.model.Patient;
import com.project.tim05.service.DoctorService;
import com.project.tim05.service.PatientService;

public class PatientController {
	
	private final PatientService ps;
	
	@Autowired
	public PatientController(PatientService ps) {
		this.ps = ps;
	}
	
	@GetMapping("/getPatients")
	public List<Patient> getPatients(){
		return ps.getPatients();
	}
	
	@PostMapping("/editPatient")
	public int editPatient(@RequestBody Patient patient) {
		ps.editPatient(patient);
		return 1;
	}
	
	@PostMapping("/addPatient")
	public int addPatient(@RequestBody Patient patient) {
		ps.addPatient(patient);
		return 1;
	}
	
}
