package com.project.tim05.dao;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.project.tim05.model.Doctor;
import com.project.tim05.model.Patient;

@Repository("DB")
public class DB implements DoctorAccess, PatientAccess {

	ArrayList<Doctor> doctors = new ArrayList<Doctor>();
	ArrayList<Patient> patients = new ArrayList<Patient>();
	
	@Override
	public void addDoctor(Doctor doctor) {
		this.doctors.add(doctor);
		
	}

	@Override
	public List<Doctor> getDoctors() {
		return this.doctors;
		
	}

	@Override
	public void editPatient(Patient patient) {
		
		for(Patient p : this.patients) {
			if(p.getEmail().equals(patient.getEmail())) {
				this.patients.remove(p);
				this.patients.add(patient);
			}
		}
		
	}

	@Override
	public List<Patient> getPatients() {
		return this.patients;
	}

	@Override
	public void addPatient(Patient patient) {
		this.patients.add(patient);
		
	}

}
