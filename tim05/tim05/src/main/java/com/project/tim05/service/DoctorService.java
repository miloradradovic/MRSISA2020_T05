package com.project.tim05.service;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.project.tim05.dto.AppointmentRequestDTO;
import com.project.tim05.dto.DoctorDTO;
import com.project.tim05.model.Appointment;
import com.project.tim05.model.AppointmentType;
import com.project.tim05.model.Authority;
import com.project.tim05.model.Clinic;
import com.project.tim05.model.Doctor;
import com.project.tim05.model.LeaveRequest;
import com.project.tim05.model.Patient;
import com.project.tim05.model.WorkCalendar;
import com.project.tim05.repository.DoctorRepository;
import com.project.tim05.repository.PatientRepository;
import com.project.tim05.repository.WorkCalendarRespository;

@Service
@Transactional(readOnly = false)
public class DoctorService {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthorityService authService;
	
	@Autowired
	private WorkCalendarRespository wcr;
	
	@Autowired
	private DoctorRepository dr;
	
	@Autowired
	private PatientRepository pr;
	
	@Autowired
	private DoctorService ds;
	
	@Autowired
	private ClinicService cs;
	
	@Autowired 
	private AppointmentTypeService ats;
	
	@Autowired
	private PatientService ps;
	
	@Autowired
	private WorkCalendarService wcs;
	
	@Autowired
	private AppointmentService as;
	
	@Autowired
	private ClinicAdministratorService cas;
	
	@Autowired
	private EmailService es;
	
	public int addDoctor(Doctor doctor) {
		int flag = 0;
		try {
			doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
			List<Authority> auth = authService.findByname("ROLE_DOCTOR");
			doctor.setAuthorities(auth);
			dr.save(doctor);	
			flag = 1;
		}
		catch(Exception e) {
			return flag;
		}
		return flag;
		
	}
	
	public List<Doctor> getDoctors(){
		return dr.findAll();
	}
	
	public int editProfile(Doctor doctor) {
		int flag = 0;
		try {
			//Connection connection = DriverManager.getConnection("jdbc:postgresql://ec2-54-247-89-181.eu-west-1.compute.amazonaws.com:5432/d1d2a9u0egu6ja", "xslquaksjvvetl", "791a6dd69c36471adccf1118066dae6841cf2b7145d82831471fdd6640e5d99a");
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
	        
	        if(doctor.getPassword().length()!=0) {
				doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
	        	String query = "UPDATE users set password = ?, name = ?, surname = ? WHERE email = ?";
		        PreparedStatement ps = connection.prepareStatement(query);
				ps.setString(1, doctor.getPassword());
				ps.setString(2, doctor.getName());
				ps.setString(3, doctor.getSurname());
				ps.setString(4, doctor.getEmail());
				flag = ps.executeUpdate();
				
				ps.close();
				connection.close();
				return flag;
	        }else {
	        	String query = "UPDATE users set name = ?, surname = ? WHERE email = ?";
		        PreparedStatement ps = connection.prepareStatement(query);
				ps.setString(1, doctor.getName());
				ps.setString(2, doctor.getSurname());
				ps.setString(3, doctor.getEmail());
				flag = ps.executeUpdate();
				
				ps.close();
				connection.close();
				return flag;
	        }
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return flag;
		}	
	}
	
	public Doctor getDoctorbyID(int id) {
		return dr.findById(id);
	}
	
	//metoda vraca doktore koji pripadaju klinici sa id clinic_Id koji vrse preglede tipa appointment_id
	public ArrayList<DoctorDTO> getDoctors(int clinic_id, int appointment_id){
		try {
			//Connection connection = DriverManager.getConnection("jdbc:postgresql://ec2-54-247-89-181.eu-west-1.compute.amazonaws.com:5432/d1d2a9u0egu6ja", "xslquaksjvvetl", "791a6dd69c36471adccf1118066dae6841cf2b7145d82831471fdd6640e5d99a");
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
	        
			String query = "SELECT * FROM doctors WHERE appointment_type = ? and clinic= ? and active = TRUE;";
	        PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, appointment_id);
			ps.setInt(2, clinic_id);
			ResultSet rs = ps.executeQuery();
			ArrayList<DoctorDTO> doctors = new ArrayList<DoctorDTO>();
			while(rs.next()) {
				Doctor dr1 = dr.findById(rs.getInt("user_id"));
				DoctorDTO drdto = new DoctorDTO();
				drdto.setName(dr1.getName());
				drdto.setId(dr1.getId());
				drdto.setSurname(dr1.getSurname());
				doctors.add(drdto);
			}
			connection.close();
			ps.close();
			rs.close();
			return doctors;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}	
		
	}
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	//vraca doktore koji pripadaju klinici sa id -> clinic_Id
	public ArrayList<DoctorDTO> getClinicsDoctors(int clinic_id){
		try {
			//Connection connection = DriverManager.getConnection("jdbc:postgresql://ec2-54-247-89-181.eu-west-1.compute.amazonaws.com:5432/d1d2a9u0egu6ja", "xslquaksjvvetl", "791a6dd69c36471adccf1118066dae6841cf2b7145d82831471fdd6640e5d99a");
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
	        
			String query = "SELECT * FROM doctors WHERE clinic= ? and active = TRUE;";
	        PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, clinic_id);
			ResultSet rs = ps.executeQuery();
			ArrayList<DoctorDTO> doctors = new ArrayList<DoctorDTO>();
			while(rs.next()) {
				Doctor dr1 = dr.findById(rs.getInt("user_id"));
				DoctorDTO drdto = new DoctorDTO();
				drdto.setName(dr1.getName());
				drdto.setId(dr1.getId());
				drdto.setSurname(dr1.getSurname());
				drdto.setEmail(dr1.getEmail());
				drdto.setAppointmentTypeName(dr1.getAppointmentType().getName());
				drdto.setAverage_rate(dr1.getRate());
				doctors.add(drdto);
			}
			connection.close();
			ps.close();
			rs.close();
			return doctors;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}	
		
	}
	//metoda proverava da li pacijent sa datim id-jem ima zakazanih pregleda i ako nema brise ga
	//vraca broj izmenjenih redova, 0 ako nije uspesno
	public int deleteDoctor(int id) {
		int success = 0;
		try {
			//Connection connection = DriverManager.getConnection("jdbc:postgresql://ec2-54-247-89-181.eu-west-1.compute.amazonaws.com:5432/d1d2a9u0egu6ja", "xslquaksjvvetl", "791a6dd69c36471adccf1118066dae6841cf2b7145d82831471fdd6640e5d99a");
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
	        
	        String query = "UPDATE doctors set active = FALSE WHERE user_id = ?";
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
	
	public Doctor getDoctor(String email) {
		return dr.findByEmail(email);
	}
	
	public Clinic getClinic(String email) {
		Doctor d = dr.findByEmail(email);
		Clinic c = initializeAndUnproxy.initAndUnproxy(d.getClinic());
		return c;
	}
	
	public void save(Doctor d) {
		dr.save(d);
	}
	
	public List<DoctorDTO> searchDoctors(String param, String value, int clinic_id){
		List<DoctorDTO> doctors = new ArrayList<DoctorDTO>();

		PreparedStatement st = null;
		Connection conn = null;

		// provera po kom parametru treba da se radi pretrazivanje
		try {
			// Connection conn =
			// DriverManager.getConnection("jdbc:postgresql://ec2-54-247-89-181.eu-west-1.compute.amazonaws.com:5432/d1d2a9u0egu6ja",
			// "xslquaksjvvetl",
			// "791a6dd69c36471adccf1118066dae6841cf2b7145d82831471fdd6640e5d99a");
			conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");

			if (param.equals("name")) {
				st = conn.prepareStatement("SELECT * FROM public.doctors d LEFT JOIN public.users c \r\n" + 
						"ON d.user_id = c.user_id where clinic = ? and name like ? and d.active = true;");
			
			} else if (param.equals("surname")){
				st = conn.prepareStatement("SELECT * FROM public.doctors d LEFT JOIN public.users c \r\n" + 
						"ON d.user_id = c.user_id where clinic = ? and surname like ?  and d.active = true;");
			
			}
			else {
				st = conn.prepareStatement("SELECT * FROM public.doctors d LEFT JOIN public.users c \r\n" + 
						"ON d.user_id = c.user_id where clinic = ? and email like ?  and d.active = true;");
			}
			
			st.setInt(1, clinic_id);
			st.setString(2, "%" + value + "%");
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				DoctorDTO ddto = new DoctorDTO();
				Doctor d = dr.findById(rs.getInt("user_id"));
				ddto.setId(d.getId());
				ddto.setName(d.getName());
				ddto.setSurname(d.getSurname());
				ddto.setEmail(d.getEmail());
				ddto.setAppointmentTypeName(d.getAppointmentType().getName());
				doctors.add(ddto);
			}

			rs.close();
			st.close();
			conn.close();

		} catch (Exception e) {
			return doctors;
		}

		return doctors;
	}
	
	public ArrayList<Doctor> getClinicDoctorsbyAppointmentType(int app_type_id, int clinic_id){
		
		ArrayList<Doctor> doctors = new ArrayList<Doctor>();
		
		try {
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
			String query = "SELECT * FROM public.doctors where appointment_type = ? and clinic = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, app_type_id);
			ps.setInt(2, clinic_id);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				Doctor dr1 = dr.findById(rs.getInt("user_id"));
				
				dr1.setAppointmentType(initializeAndUnproxy.initAndUnproxy(dr1.getAppointmentType()));
				doctors.add(dr1);
			}
			connection.close();
			ps.close();
			rs.close();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return doctors;
	}
	
	public ArrayList<Doctor> getDoctorsbyAppointmentType(int app_type_id){
		
		ArrayList<Doctor> doctors = new ArrayList<Doctor>();
		
		try {
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
			String query = "SELECT * FROM public.doctors where appointment_type = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, app_type_id);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				Doctor dr1 = dr.findById(rs.getInt("user_id"));
				dr1.setAppointmentType(initializeAndUnproxy.initAndUnproxy(dr1.getAppointmentType()));
				doctors.add(dr1);
			}
			connection.close();
			ps.close();
			rs.close();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return doctors;
	}
	
	public List<String> getAvailableTime(Date day, Doctor dr){
		//treba dodati proveru da li je doktor na godisnjem odmoru
		try {
		
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
        //iz baze uzimam sve termine pregleda za datum Day i doctora koji je prosledjen
        String query = "SELECT * from public.work_calendars where date = ? and doctor = ?";
        PreparedStatement ps = connection.prepareStatement(query);
		ps.setDate(1, day);
		ps.setInt(2, dr.getId());
		ResultSet rs = ps.executeQuery();
		//pravljenje mape u kojoj ce biti vreme_pregleda:trajanje u minutima od 00:00
		HashMap<Integer,Integer> appointment_times = new HashMap<Integer,Integer>();
		
		while(rs.next()) {
			if(rs.getBoolean("leave")==true) {
				return new ArrayList<String>();
			}
			String start_time = rs.getString("start_time");
			String end_time = rs.getString("end_time");
			int length = timeToMinutes(end_time) -  timeToMinutes(start_time);
			//stavljanje u mapu start_time : duzina
			appointment_times.put(timeToMinutes(start_time), length);
			
		}
		
		//pocetak radnog vremena doktora u minutima
		int doctor_ws = timeToMinutes(dr.getWorkStart());
		//kraj doktorovog radnog vremena u minutima
		int doctor_we = timeToMinutes(dr.getWorkEnd());
		
		//proveravmo da li doktor ima radno vreme koje se nastavlja u sledecem danu
		//recimo od 22:00 do 06:00
		//u tom slucaju posto zelimo za odredjen dan da zakazemo pregled gledamo samo do 24:00
		//te njegovo vreme ogranicavam na 24:00
		if(doctor_ws > doctor_we) {
			doctor_we = 24*60;
		}
		
		ArrayList<String> free_times = new ArrayList<String>();
		
		while(doctor_ws + 30<= doctor_we) {
			//flag koji oznacava da li je vreme doctor_ws slobodno u narednih 30 minuta
			int flag = 0;
			
			//provera da li doctor_ws zalazi u neki od vec zakazanih pregleda
			for (Map.Entry<Integer, Integer> entry : appointment_times.entrySet()) {
			    //ukoliko je termin izmedju pocetka i kraja zakaznog 
				if(doctor_ws >= entry.getKey() && doctor_ws + 30<= entry.getKey() + entry.getValue()) {
			    	//onda je termin zauzet i treba okinuti flag
					flag = 1;
					//krecemo opet potragu od minimalnog sledeceg
					doctor_ws = entry.getKey()+entry.getValue();
					break;
			    }
				//ukoliko nije nastavi da pretrazujes, a flag ce ostati 0
			}
			
			//znaci da je termin nije negde uleteo u drugi termin i da je slobodan
			if (flag == 0) {
				free_times.add(MinutesToTime(doctor_ws));
				//svaki pregled traje pola sata ----> ZA MENJANJE DUZINE PREGLEDA PROMENITI OVAJ PARAMETAR
				doctor_ws += 30;
			}
			
			
			

		}
		
		
		connection.close();
		ps.close();
		
		return free_times;
		}
		catch(Exception e) {
			
		}
		return null;
	}
	
	//funkcija koja kao parametar prima vreme u obliku hh:mm a zatim vraca broj minuta od 00:00
	public int timeToMinutes(String time) {
		int hours  = Integer.parseInt(time.split(":")[0]);
		int minutes = Integer.parseInt(time.split(":")[1]);
		return hours*60+minutes;
	}
	
	//funinja koja kao parametar prima integer minuta od 00:00 i vraca prirodan oblik hh:mm
	public String MinutesToTime(int minutes) {
		int minute = minutes%60;
		int hour = minutes/60;
		if (minute < 10) {
			return hour + ":" + minute + "0";
		}
		return hour + ":" + minute;
	}

	public ArrayList<Doctor> searchDoctorsByParameters(ArrayList<Doctor> doctors, String parameter, String value) {
		ArrayList<Doctor> result = new ArrayList<Doctor>();
		
		if(value.equals("")) {
			result = doctors;
		}else {
			PreparedStatement st = null;
			Connection conn = null;

			// provera po kom parametru treba da se radi pretrazivanje
			try {
				
				conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");

				if (parameter.equals("name")) {
					st = conn.prepareStatement("SELECT * FROM public.doctors d LEFT JOIN public.users c \r\n" + 
							"ON d.user_id = c.user_id where name like ? and d.active=true;");
					st.setString(1, "%" + value + "%");
					ResultSet rs = st.executeQuery();
					
					while (rs.next()) {
						Doctor d = dr.findById(rs.getInt("user_id"));
						result.add(d);
					}
					rs.close();
				
				} else if (parameter.equals("surname")){
					st = conn.prepareStatement("SELECT * FROM public.doctors d LEFT JOIN public.users c \r\n" + 
							"ON d.user_id = c.user_id where surname like ? and d.active=true;");
					st.setString(1, "%" + value + "%");
					ResultSet rs = st.executeQuery();
					
					while (rs.next()) {
						Doctor d = dr.findById(rs.getInt("user_id"));
						result.add(d);
					}
					rs.close();
				}
				else if(parameter.equals("ratefrom")){
					double ratefrom = Double.parseDouble(value);
					st = conn.prepareStatement("SELECT * FROM public.doctors where active = true;");
					ResultSet rs = st.executeQuery();
					while(rs.next()) {
						int id = rs.getInt("user_id");
						Doctor d = initializeAndUnproxy.initAndUnproxy(dr.findById(id));
						double avg = d.getRate();
						if(avg >= ratefrom) {
							result.add(d);
						}
					}
					rs.close();
					
				}else if(parameter.equals("rateto")) {
					double rateto = Double.parseDouble(value);
					st = conn.prepareStatement("SELECT * FROM public.doctors where active = true;");
					ResultSet rs = st.executeQuery();
					while(rs.next()) {
						int id = rs.getInt("user_id");
						Doctor d = initializeAndUnproxy.initAndUnproxy(dr.findById(id));
						double avg = d.getRate();
						if(avg <= rateto) {
							result.add(d);
						}
					}
					rs.close();
				}

				st.close();
				conn.close();

			} catch (Exception e) {
				return result;
			}
		}
		
		return result;
	}
	
	public boolean checkIfDoctorExists(Doctor d, ArrayList<Doctor> doctors) {
		boolean found = false;
		for(Doctor doctor : doctors) {
			if(doctor.getId() == d.getId()) {
				found = true;
				return found;
			}
		}
		return found;
	}
	
public int addLeave(LeaveRequest l) {
		int flag = 0;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			java.util.Date start = null;
			java.util.Date end = null;
			try {
				start = formatter.parse(l.getStartDate());
				end = formatter.parse(l.getEndDate());
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
	
			Calendar start_cal = Calendar.getInstance();
			start_cal.setTime(start);
			Calendar end_cal = Calendar.getInstance();
			end_cal.setTime(end);
			end_cal.add(Calendar.DATE, 1);
	
			java.sql.Date sql = null;
			for (java.util.Date date = start_cal.getTime(); start_cal.before(end_cal); start_cal.add(Calendar.DATE, 1), date = start_cal.getTime()) {
				sql = new java.sql.Date(date.getTime());
				WorkCalendar wc = new WorkCalendar("00:00", "23:59", sql, true);
				wc.setRequest(false);
				Doctor d = dr.findByEmail(l.getEmail());
				wc.setDoctor(d);
				d.getWorkCalendar().add(wc);
				wcr.save(wc);    
			}
			flag = 1;
		
		}catch (Exception e) {
			System.out.println(e.getMessage());
			flag = 0;
		}
		
		return flag;
		
	}

	public String canStartAppointment(Integer doctor_id, Integer patient_id) {
		String s = "f";
		try {
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
			String query = "SELECT * FROM appointments where doctor = ? and patient = ? and finished = false";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, doctor_id);
			ps.setInt(2, patient_id);
		
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				java.util.Date time = new java.util.Date();
				if(rs.getTimestamp("date_time").getDay() == time.getDay() && rs.getTimestamp("date_time").getMonth() == time.getMonth() && rs.getTimestamp("date_time").getYear() == time.getYear())
				{
					
					if((rs.getTimestamp("date_time").getTime() < time.getTime()) && (time.getTime() < (rs.getTimestamp("date_time").getTime() + TimeUnit.MINUTES.toMillis(rs.getInt("duration")))))
					{
						s = Integer.toString(rs.getInt("appointment_id"));
					
					}
					
				}
				
			}
			connection.close();
			ps.close();
			rs.close();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return s;
}
	
	public String canStartAppointmentCalendar(Integer doctor_id, Integer patient_id, Integer app_id) {
		String s = "0";
		try {
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
			String query = "SELECT * FROM appointments where doctor = ? and patient = ? and appointment_id = ? and finished = false";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, doctor_id);
			ps.setInt(2, patient_id);
			ps.setInt(3, app_id);
		
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				java.util.Date time = new java.util.Date();
				if(rs.getTimestamp("date_time").getDay() == time.getDay() && rs.getTimestamp("date_time").getMonth() == time.getMonth() && rs.getTimestamp("date_time").getYear() == time.getYear())
				{
					
					if((rs.getTimestamp("date_time").getTime() < time.getTime()) && (time.getTime() < (rs.getTimestamp("date_time").getTime() + TimeUnit.MINUTES.toMillis(rs.getInt("duration")))))
					{
						s = Integer.toString(rs.getInt("appointment_id"));
					
					}
					
				}
				
			}
			connection.close();
			ps.close();
			rs.close();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return s;
}

	public String canAccess(String email, Integer id) {
		String s = "nope";
		try {
			Patient p = pr.findByEmail(email);
			p.setMedicalRecord(initializeAndUnproxy.initAndUnproxy(p.getMedicalRecord()));
			// Connection conn =
			// DriverManager.getConnection("jdbc:postgresql://ec2-54-247-89-181.eu-west-1.compute.amazonaws.com:5432/d1d2a9u0egu6ja",
			// "xslquaksjvvetl",
			// "791a6dd69c36471adccf1118066dae6841cf2b7145d82831471fdd6640e5d99a");
			Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");

			PreparedStatement st = conn
					.prepareStatement("SELECT * FROM appointments WHERE doctor = ? and patient = ? and finished = true");
			st.setInt(1, id);
			st.setInt(2, p.getId());
			ResultSet rs = st.executeQuery();
			
			
			if(rs.next())
				return s = "yep";
			rs.close();
			st.close();
			conn.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int addAnotherApp(AppointmentRequestDTO adto) {
		Appointment ap = new Appointment();
		Appointment old_ap = as.getAppointmentById(Integer.parseInt(adto.getApp_id()));
		Doctor dr = this.dr.findById(old_ap.getDoctor().getId()).orElse(null);
		AppointmentType at = dr.getAppointmentType();
		Clinic c = dr.getClinic();
		Patient p = pr.findByEmail(adto.getPatient());
	
		if (dr == null || at == null) {
			return 1;
		}
		
		//provera da li je doktor zauzet
		
		int dr_start = Integer.parseInt(dr.getWorkStart().split(":")[0]) * 60
				+ Integer.parseInt(dr.getWorkStart().split(":")[1]);
		int dr_end = Integer.parseInt(dr.getWorkEnd().split(":")[0]) * 60
				+ Integer.parseInt(dr.getWorkEnd().split(":")[1]);

		int app_start = Integer.parseInt(adto.getTime().split(":")[0])*60 + Integer.parseInt(adto.getTime().split(":")[1]);
		int app_end = app_start + Integer.parseInt(adto.getDuration());
		
		
		if ((app_start < dr_start || app_start > dr_end) || (app_end < dr_start || app_end > dr_end)) {
			return 1;
		}		

		SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-MM-dd");


		java.util.Date date = null;
		java.util.Date wc_date = null;
		try {
			date = formatter1.parse(adto.getDate() + " " + adto.getTime());
			wc_date = formatter2.parse(adto.getDate());
		} catch (ParseException e) {
			return 1;
		}
		
		for(WorkCalendar wc : dr.getWorkCalendar()) {
			try {
				if(formatter3.parse(wc.getDate().toString().split(" ")[0]).getTime()!=wc_date.getTime()) {
					continue;
				} 
				else {
					if(wc.getLeave()==true) {
						return 1;
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int wc_start = Integer.parseInt(wc.getStart_time().split(":")[0]) * 60
					+ Integer.parseInt(wc.getStart_time().split(":")[1]);
			int wc_end = Integer.parseInt(wc.getEnd_time().split(":")[0]) * 60
					+ Integer.parseInt(wc.getEnd_time().split(":")[1]);
			if ((app_start >= wc_start && app_start <= wc_end) || (app_end >= wc_start && app_end <= wc_end)
					|| (app_start == wc_start && app_end == wc_end)) {
				return 1;
			}
		}
		
		
		
		
		
		

		ap.setDateTime(date);
		ap.setDuration(Integer.parseInt(adto.getDuration()));
		ap.setPrice(old_ap.getPrice());
		ap.setRequest(true);
		ap.setPredefined(false);
		ap.setDoctor(dr);
		ap.setAppointmentType(at);
		ap.setClinic(c);
		ap.setPatient(p);
		

		int flag = as.addAppointment(ap);
		//TODO u doktoru treba da se doda appointment
		

		WorkCalendar wc = new WorkCalendar();
		wc.setDate(wc_date);
		wc.setStart_time(adto.getTime());
		
		
		//racunanje minuta od pocetka dana
		String[] res = wc.getStart_time().split(":");
		int start_minutes = Integer.parseInt(res[0])*60 + Integer.parseInt(res[1]);
		//racunanje krajnjeg broja minuta od pocetka dana
		int end_minutes = start_minutes + Integer.parseInt(adto.getDuration());
		//transliranje krajnjeg broja minuta nazad u oblik "hh:mm"
		//uzima se broj minuta i ostatak pri deljenju sa 60 predstavlja broj minuta koji je preko punog sata
		//a sati se dobijaju tako sto se uzme broj minuta i bez ostatka se podeli sa 60, tako dobijamo sati:minuti
		int end_minute = end_minutes%60;
		int end_hour = end_minutes/60;
		
		dr.getAppointments().add(ap);
		
		String end_h = String.valueOf(end_hour);
		String end_m = String.valueOf(end_minute);
		if(end_hour < 10) {
			end_h = "0" + end_hour;
		}
		if(end_minute < 10) {
			end_m = "0" + end_minute;
		}
		
		wc.setEnd_time(end_h + ":" + end_m);
		wc.setDoctor(dr);
		wc.setLeave(false);
		wc.setRequest(false);
		
		dr.getWorkCalendar().add(wc);
		
		wcs.addCalendar(wc);
		
		for(Appointment a : dr.getAppointments()) {
			System.out.println(a.getAppointmentType().getName());
		}

		if (flag == 0)
			return 1;
		else
			return 0;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int sendRequest(AppointmentRequestDTO adto) {
		Appointment ap = new Appointment();
		Clinic c = cs.getClinicbyId(Integer.parseInt(adto.getClinic()));
		Doctor d =dr.findById(Integer.parseInt(adto.getDoctor()));
		Patient p = ps.getPatientById(Integer.parseInt(adto.getPatient()));
		AppointmentType at = ats.getAppointmentTypebyId(Integer.parseInt(adto.getApp_type()));
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
		
		java.util.Date wc_date = null;
		java.util.Date date = null;
		try {
			date = formatter1.parse(adto.getDate() + " " + adto.getTime());
			wc_date = formatter2.parse(adto.getDate());
		} catch (ParseException e) {
			return 1;
		}
		
		java.sql.Date dt = new java.sql.Date(date.getTime());
		
		List<String> available_times = ds.getAvailableTime(dt, d);
		System.out.println(available_times.toString());
		if(!available_times.contains(adto.getTime())) {
			return 1;

		}
		
		if(adto.getType().equalsIgnoreCase("opp")) {
			ap.setOperation(true);
		}
		
		

		ap.setDateTime(date);
		ap.setDuration(30);
		ap.setPrice(0);
		ap.setRequest(true);
		ap.setPredefined(false);
		ap.setDoctor(d);
		ap.setAppointmentType(at);
		ap.setClinic(c);
		ap.setPatient(p);
		
		WorkCalendar wc = new WorkCalendar();
		wc.setDate(wc_date);
		wc.setStart_time(adto.getTime());
		
		//racunanje minuta od pocetka dana
		String[] res = wc.getStart_time().split(":");
		int start_minutes = Integer.parseInt(res[0])*60 + Integer.parseInt(res[1]);
		//racunanje krajnjeg broja minuta od pocetka dana
		int end_minutes = start_minutes + 30;
		//transliranje krajnjeg broja minuta nazad u oblik "hh:mm"
		//uzima se broj minuta i ostatak pri deljenju sa 60 predstavlja broj minuta koji je preko punog sata
		//a sati se dobijaju tako sto se uzme broj minuta i bez ostatka se podeli sa 60, tako dobijamo sati:minuti
		int end_minute = end_minutes%60;
		int end_hour = end_minutes/60;
		
		
		
		Set<Appointment> a = d.getAppointments();
		a.add(ap);
		d.setAppointments(a);
		
		System.out.println(d.getAppointments());
		
		String end_h = String.valueOf(end_hour);
		String end_m = String.valueOf(end_minute);
		if(end_hour < 10) {
			end_h = "0" + end_hour;
		}
		if(end_minute < 10) {
			end_m = "0" + end_minute;
		}

		wc.setEnd_time(end_h + ":" + end_m);
		wc.setDoctor(d);
		wc.setLeave(false);
		wc.setRequest(true);
		
		wcs.addCalendar(wc);
		
		d.getWorkCalendar().add(wc);
		
		/*try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		//EntityManagerFactory emf = EntityManagerFactory();
		//EntityManager em = emf.createEntityManager()
		//dr.saveAndFlush(d);
		
		int flag = as.addAppointment(ap);
		//TODO u doktoru treba da se doda appointment
		

		if (flag == 0)
			return 1;
		else
		{
			
			//TODO uzmi listu admina koji pripadaju klinici i salji svima mejl
			List<String> emails = cas.getClinicAdminsClinic(c.getId());
			
			try {
				String text = "Patient: " + ap.getPatient().getName() + " " + ap.getPatient().getSurname() + 
						"requested an appointment!";
				for (String email : emails) {
					es.sendAppointmentNotificationAdmin(email, text);
				}
				
				
				
			} catch (Exception e) {
				return 1;
			}
			
			
			
			return 0;
		}
	}
	
}
