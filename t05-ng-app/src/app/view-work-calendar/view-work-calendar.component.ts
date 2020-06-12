import {Component, ChangeDetectionStrategy, ViewChild, TemplateRef, OnInit} from '@angular/core';
import {startOfDay, subDays, addDays, endOfMonth, isSameDay, isSameMonth, addHours} from 'date-fns';
import {CalendarEvent, CalendarView} from 'angular-calendar';
import {clinicModel} from "../edit-clinic/edit-clinic.component";
import {MatSnackBar} from "@angular/material/snack-bar";
import {HttpClient} from "@angular/common/http";
import {Subject} from "rxjs";

const colors: any = {
  red: {
    primary: '#ad2121',
    secondary: '#FAE3E3',
  },
  blue: {
    primary: '#1e90ff',
    secondary: '#D1E8FF',
  }
};

@Component({
  selector: 'app-view-work-calendar',
  templateUrl: './view-work-calendar.component.html',
  styleUrls: ['./view-work-calendar.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ViewWorkCalendarComponent implements OnInit {

  @ViewChild('modalContent', { static: true }) modalContent: TemplateRef<any>;

  view: CalendarView = CalendarView.Month;

  CalendarView = CalendarView;

  viewDate: Date = new Date();

  works:any[];
  apps:any[];

  constructor(private _snackBar: MatSnackBar, private http: HttpClient) {}

  async ngOnInit(){

    // @ts-ignore
    this.works = await this.http.get("http://localhost:8081/workCalendar/getWorkCalendar").toPromise();
    // @ts-ignore
    this.apps = await this.http.get("http://localhost:8081/appointment/getDoctorAppointments").toPromise();

    this.addEvents();
  }

  addEvents()
  {
    this.works.forEach(work => {
      if(work.leave == true)
      {
        this.events.push(
          {
            start: new Date(work.date),
            end: new Date(work.date),
            title: 'On Leave',
            color: colors.blue,
            allDay: true
          },
        );
      }

      this.apps.forEach(app => {
        let w = new Date(work.date);
        w.setHours(parseInt(work.startTime.split(":")[0]), parseInt(work.startTime.split(":")[1]));

        let a = new Date(app.date)

        if(w.getTime() === a.getTime())
        {
          this.events.push(
            {
              start: subDays(new Date(work.date).setHours(parseInt(work.startTime.split(":")[0]), parseInt(work.startTime.split(":")[1])), 1),
              end: subDays(new Date(work.date).setHours(parseInt(work.endTime.split(":")[0]), parseInt(work.endTime.split(":")[1])), 1),
              title: 'Start:' + work.startTime + ', End: ' + work.endTime + ', AppointmentType: ' + app.appointmentType.name + ", Patient: " + app.patient.name + " " + app.patient.surname,
              color: colors.red
            },
          );
        }
      })
    })
    this.refresh.next();
  }

  refresh: Subject<any> = new Subject();

  events: CalendarEvent[] = [];

  activeDayIsOpen: boolean = false;

  dayClicked({ date, events }: { date: Date; events: CalendarEvent[] }): void {
    if (isSameMonth(date, this.viewDate)) {
      if (
        (isSameDay(this.viewDate, date) && this.activeDayIsOpen === true) ||
        events.length === 0
      ) {
        this.activeDayIsOpen = false;
      } else {
        this.activeDayIsOpen = true;
      }
      this.viewDate = date;
    }
  }

  setView(view: CalendarView) {
    this.view = view;
  }

  closeOpenMonthViewDay() {
    this.activeDayIsOpen = false;
  }

}
