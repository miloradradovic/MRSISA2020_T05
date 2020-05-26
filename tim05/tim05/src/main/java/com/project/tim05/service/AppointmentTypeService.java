package com.project.tim05.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.tim05.dto.AppointmentTypeDTO;
import com.project.tim05.model.AppointmentType;
import com.project.tim05.model.Hall;
import com.project.tim05.repository.AppointmentTypeRespository;

@Service
public class AppointmentTypeService {

	@Autowired
	private AppointmentTypeRespository atr;

	
	public int addAppointmentType(AppointmentType at) {
		int flag = 0;
		try {
			atr.save(at);
			flag = 1;
		}
		catch(Exception e){
			return flag;
		}
		return flag;
		
	}
	
	public ArrayList<AppointmentTypeDTO> getAppointmentTypes(){
		ArrayList<AppointmentTypeDTO> apps = new ArrayList<AppointmentTypeDTO>();
		for(AppointmentType at : atr.findAll()) {
			AppointmentTypeDTO adto = new AppointmentTypeDTO();
			adto.setName(at.getName());
			adto.setId(at.getId());
			apps.add(adto);
		}
		return apps;
	}
	
	public AppointmentType getAppointmentTypebyId(int id) {
		return atr.findById(id).orElse(null);
	}
	
	public AppointmentType getAppointmentTypebyName(String name) {
		return atr.findByName(name);
	}
	
	public AppointmentType changeAppointmentType(int id , String name) {
		AppointmentType at = atr.findById(id).orElse(null);
		if(atr.findByName(name) == null) {
			at.setName(name);
			atr.save(at);
			return null;
		}
		return at;
	}
	
	public int deleteAppointmentType(int id) {
		int success = 0;
		try {
			//Connection connection = DriverManager.getConnection("jdbc:postgresql://ec2-54-247-89-181.eu-west-1.compute.amazonaws.com:5432/d1d2a9u0egu6ja", "xslquaksjvvetl", "791a6dd69c36471adccf1118066dae6841cf2b7145d82831471fdd6640e5d99a");
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
	        
	        String query = "DELETE FROM appointment_types WHERE appointment_type_id = ?";
	        PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, id);
			
			success = ps.executeUpdate();
			
			connection.close();
			ps.close();
			
		} catch (SQLException e) {
			success = 0;
		}
		
		return success;
	}
	
	public ArrayList<AppointmentTypeDTO> getClinicAppointmentTypes(int id){
		Connection conn;
		try {
			//conn = DriverManager.getConnection("jdbc:postgresql://ec2-54-247-89-181.eu-west-1.compute.amazonaws.com:5432/d1d2a9u0egu6ja", "xslquaksjvvetl", "791a6dd69c36471adccf1118066dae6841cf2b7145d82831471fdd6640e5d99a");
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
	        
			PreparedStatement st;
			
			st = conn.prepareStatement("SELECT * FROM appointment_types WHERE clinic = ?");
			
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			ArrayList<AppointmentTypeDTO> lh = new ArrayList<AppointmentTypeDTO>();
			while(rs.next()) {
				AppointmentTypeDTO at = new AppointmentTypeDTO();
				at.setId(rs.getInt("appointment_type_id"));
				at.setName(rs.getString("name"));
				lh.add(at);
			}
			st.close();
			conn.close();
			rs.close();
	
			return lh;
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}	

		return null;	
	}

	public ArrayList<AppointmentTypeDTO> search(String searchInput) {
		ArrayList<AppointmentTypeDTO> search = new ArrayList<AppointmentTypeDTO>();
		AppointmentTypeDTO dto = new AppointmentTypeDTO();
		AppointmentType search_result = atr.findByName(searchInput);
		if(search_result != null) {
			dto.setName(search_result.getName());
			search.add(dto);
		}
		return search;
	}
	
	
	
}
