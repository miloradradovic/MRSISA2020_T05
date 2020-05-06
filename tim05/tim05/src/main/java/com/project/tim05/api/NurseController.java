package com.project.tim05.api;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.tim05.dto.NurseDTO;
import com.project.tim05.dto.PatientDTO;
import com.project.tim05.model.ClinicAdministrator;
import com.project.tim05.model.MedicalStaff;
import com.project.tim05.model.Nurse;
import com.project.tim05.model.Patient;
import com.project.tim05.model.RegistrationRequest;
import com.project.tim05.service.ClinicAdministratorService;
import com.project.tim05.service.NurseService;
import com.project.tim05.service.PatientService;
import com.project.tim05.service.RegistrationRequestService;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/nurse")
@RestController
public class NurseController {
	
	private final PatientService ps;
	private final NurseService ns;
	private final ClinicAdministratorService cas;
	private final RegistrationRequestService rs;
	
	@Autowired
	public NurseController(PatientService ps, NurseService ns, ClinicAdministratorService cas, RegistrationRequestService rs) {
		this.ps = ps;
		this.ns = ns;
		this.cas = cas;
		this.rs = rs;
	}
	

	
	@PostMapping("/addNurse")
	@PreAuthorize("hasRole('CLINIC_ADMIN')")
	public ResponseEntity<Object> addNurse(@Valid @RequestBody NurseDTO nurse) {
		Nurse n = new Nurse();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		ClinicAdministrator user = (ClinicAdministrator) authentication.getPrincipal();
		
		ClinicAdministrator currentUser = cas.getClinicAdmin(user.getEmail());
		
		
		n.setClinic(currentUser.getClinic());
		n.setName(nurse.getName());
		n.setSurname(nurse.getSurname());
		n.setEmail(nurse.getEmail());
		n.setPassword(nurse.getPassword());
		n.setWorkStart(nurse.getWorkStart());
		n.setWorkEnd(nurse.getWorkEnd());
		n.setEnabled(true);
		
		RegistrationRequest existUser = this.rs.findByEmail(nurse.getEmail());
		if (existUser != null) {
			 ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		}
		
		int flag = ns.addNurse(n);
				
		if(flag != 0) {
			return ResponseEntity.status(HttpStatus.OK).body(null);
		}else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		}
	
		
	}

}
