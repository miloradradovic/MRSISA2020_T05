package com.project.tim05.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.project.tim05.model.Patient;

public class AppointmentDTO {

	@NotBlank
	@NotNull
	private String date;
	@NotBlank
	@NotNull
	private String time;
	@NotNull
	private int duration;
	@NotNull
	private double price;
	@NotNull
	private boolean request;
	@NotNull
	private boolean finished;
	@NotNull
	private boolean predefined;
	
	private String description;

	@NotNull
	private int doctor_id;
	@NotNull
	private int hall_id;
	@NotNull
	private int appointmentType_id;
	@NotNull
	private int clinic_id;
	
	private boolean ratedDoctor;
	private boolean ratedClinic;

	public boolean isRatedDoctor() {
		return ratedDoctor;
	}

	public void setRatedDoctor(boolean ratedDoctor) {
		this.ratedDoctor = ratedDoctor;
	}

	public boolean isRatedClinic() {
		return ratedClinic;
	}

	public void setRatedClinic(boolean ratedClinic) {
		this.ratedClinic = ratedClinic;
	}

	private int id;

	private PatientDTO patient;
	private ClinicDTO clinic;
	private HallDTO hall;
	private DoctorDTO doctor;
	private AppointmentTypeDTO appointmentType;
	private DiagnosisDTO diagnosis;

	public AppointmentDTO() {
		super();
	}

	public AppointmentDTO(String date, String time, int duration, double price, boolean request, boolean finished,
			int clinic_id, ClinicDTO clinic, boolean predefined, HallDTO hall, DoctorDTO doctor,
			AppointmentTypeDTO appointmentType) {
		super();
		this.date = date;
		this.time = time;
		this.duration = duration;
		this.price = price;
		this.request = request;
		this.finished = finished;
		this.predefined = predefined;
		this.clinic = clinic;
		this.hall = hall;
		this.doctor = doctor;
		this.appointmentType = appointmentType;
	}

	public AppointmentDTO(String date, String time, int duration, double price, boolean request, boolean predefined,
			int doctor_id, int hall_id, int appointmentType_id) {
		super();
		this.date = date;
		this.time = time;
		this.duration = duration;
		this.price = price;
		this.request = request;
		this.finished = false;
		this.predefined = predefined;
		this.doctor_id = doctor_id;
		this.hall_id = hall_id;
		this.appointmentType_id = appointmentType_id;
	}
	
	public AppointmentDTO(String date, AppointmentTypeDTO type, PatientDTO pat) {
		this.date = date;
		this.appointmentType = type;
		this.patient = pat;
		
	}

	public AppointmentDTO(String date, HallDTO hallDTO, AppointmentTypeDTO appointmentTypeDTO, DoctorDTO doctorDTO,
			double price) {
		this.date = date;
		this.hall = hallDTO;
		this.appointmentType = appointmentTypeDTO;
		this.doctor = doctorDTO;
		this.price = price;
	}

	public PatientDTO getPatient() {
		return patient;
	}

	public void setPatient(PatientDTO patient) {
		this.patient = patient;
	}
	
	

	public DiagnosisDTO getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(DiagnosisDTO diagnosis) {
		this.diagnosis = diagnosis;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getClinic_id() {
		return clinic_id;
	}

	public void setClinic_id(int clinic_id) {
		this.clinic_id = clinic_id;
	}

	public int getDoctor_id() {
		return doctor_id;
	}

	public void setDoctor_id(int doctor_id) {
		this.doctor_id = doctor_id;
	}

	public int getHall_id() {
		return hall_id;
	}

	public void setHall_id(int hall_id) {
		this.hall_id = hall_id;
	}

	public int getAppointmentType_id() {
		return appointmentType_id;
	}

	public void setAppointmentType_id(int appointmentType_id) {
		this.appointmentType_id = appointmentType_id;
	}

	public boolean isPredefined() {
		return predefined;
	}

	public void setPredefined(boolean predefined) {
		this.predefined = predefined;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public boolean isRequest() {
		return request;
	}

	public void setRequest(boolean request) {
		this.request = request;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public ClinicDTO getClinic() {
		return clinic;
	}

	public void setClinic(ClinicDTO clinic) {
		this.clinic = clinic;
	}

	public HallDTO getHall() {
		return hall;
	}

	public void setHall(HallDTO hall) {
		this.hall = hall;
	}

	public DoctorDTO getDoctor() {
		return doctor;
	}

	public void setDoctor(DoctorDTO doctor) {
		this.doctor = doctor;
	}

	public AppointmentTypeDTO getAppointmentType() {
		return appointmentType;
	}

	public void setAppointmentType(AppointmentTypeDTO appointmentType) {
		this.appointmentType = appointmentType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	

}