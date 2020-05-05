package com.project.tim05.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.tim05.model.Authority;
import com.project.tim05.model.Clinic;
import com.project.tim05.model.MedicalRecord;
import com.project.tim05.model.Patient;
import com.project.tim05.repository.PatientRepository;


@Service
public class PatientService {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthorityService authService;

	
	@Autowired
	private PatientRepository pa;
	
	public int editPatient(Patient patient) {
		int flag = 0;
		try {
			patient.setPassword(passwordEncoder.encode(patient.getPassword()));
	        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
	        String query = "UPDATE patient set password = ?, name = ?, surname = ?, address = ?, city = ?, country = ?, phone_number = ?, insurance_number = ? WHERE email = ?;";
	        PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, patient.getPassword());
			ps.setString(2, patient.getName());
			ps.setString(3, patient.getSurname());
			ps.setString(4, patient.getAddress());
			ps.setString(5, patient.getCity());
			ps.setString(6, patient.getCountry());
			ps.setString(7, patient.getPhone_number());
			ps.setString(8, patient.getInsurance_number());
			ps.setString(9, patient.getEmail());
			flag = ps.executeUpdate();
			
			ps.close();
			connection.close();
			return flag;
		} catch (SQLException e) {
			return flag;
		}	
	}
	
	public List<Patient> getPatients(){
		return pa.findAll();
	}
	
	public int getPatientId(String email)
	{
		int id = -1;
		try {
			Patient p = pa.findByEmail(email);
			id = p.getId();
			
			
		} catch (Exception e) {
			
			return id;
		}
		return id;
	}
	
	
	public int addPatient(Patient patient) {
		try {
			Date date = new Date();
			List<Authority> auth = authService.findByname("PATIENT");
			patient.setAuthorities(auth);
			patient.setPassword(passwordEncoder.encode(patient.getPassword()));
			patient.setLastPasswordResetDate(new Timestamp(date.getTime()));
			patient.setMedicalRecord(new MedicalRecord());
			pa.save(patient);
			
		} catch (Exception e) {
			
			return 0;
		}
		
		return 1;
	}
	
	public List<Patient> getPatients(Clinic cl)
	{
		List<Patient> patients = new ArrayList<Patient>();
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
			
			PreparedStatement st = conn.prepareStatement("SELECT p FROM patients WHERE p.clinic.name = ? and p.clinic.address = ?");
			st.setString(1, cl.getName());
			st.setString(2, cl.getAddress());
			ResultSet rs = st.executeQuery();
			
			while(rs.next())
			{	
				patients.add(new Patient(rs.getString("email"), rs.getString("name"), rs.getString("surname"), rs.getString("address"), rs.getString("city"), rs.getString("country"), rs.getString("phone_number"), rs.getString("insurance_number")));	
			}
			
			rs.close();
			st.close();		
			conn.close();
			
		} catch (SQLException e) {
			
			return patients;
		}
		
		
		return patients;
		
	}

}
