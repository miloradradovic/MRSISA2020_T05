import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Component({
    selector: 'app-add-clinic-form',
    templateUrl: './add-clinic-form.component.html',
    styleUrls: ['./add-clinic-form.component.css']
})
export class AddClinicFromComponent implements OnInit{

    model: clinicModel = {
        name: ''
    }

    ngOnInit(): void{

    }

   
    constructor(private http: HttpClient){

    }

    AddClinic(): void{
        let url = "http://localhost:8081/clinic/addClinic"
        this.http.post(url, this.model).subscribe(
            res => {
                alert("Clinic added successfully");
                location.reload();
            },
            err => {
                alert("Error has occured while adding clinic");
                console.log(err);
            } 
        );
    
    }
}

export interface clinicModel
{
    name: string | RegExp;
}