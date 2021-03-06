package com.example.demo.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "calendars")
public class Calendar {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection
    @CollectionTable(name = "vataction_dates", joinColumns = @JoinColumn(name = "calendar_id"))
    @Column(name = "vacation_dates")
    private List<Date> vacationDates;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection()
    @CollectionTable(name = "event_start_dates", joinColumns = @JoinColumn(name = "calendar_id"), uniqueConstraints = {
            @UniqueConstraint(columnNames = {"calendar_id", "event_start_dates"})})
    @Column(name = "event_start_dates")
    private List<Date> eventStartDates;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection
    @CollectionTable(name = "event_end_dates", joinColumns = @JoinColumn(name = "calendar_id"))
    @Column(name = "event_end_dates")
    private List<Date> eventEndDates;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection
    @CollectionTable(name = "event_names", joinColumns = @JoinColumn(name = "calendar_id"))
    @Column(name = "event_names")
    private List<String> eventNames;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection
    @CollectionTable(name = "appointment_ids", joinColumns = @JoinColumn(name = "calendar_id"))
    @Column(name = "appointment_id")
    private List<Integer> appointmentIds;

    public HashMap<String,List<HashMap<Date,Date>>> formatDates(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HashMap<String,List<HashMap<Date,Date>>> map = new HashMap<String, List<HashMap<Date,Date>>>();

        for(int i = 0; i<eventStartDates.size(); i++){
            if(map.containsKey(sdf.format(eventStartDates.get(i)).substring(0,10))){
                HashMap<Date,Date> pair = new HashMap<Date,Date>();
                pair.put(eventStartDates.get(i),eventStartDates.get(i));
                map.get(sdf.format(eventStartDates.get(i)).substring(0,10)).add(pair);

            }else{
                List<HashMap<Date,Date>> ls = new ArrayList<HashMap<Date,Date>>();
                HashMap<Date,Date> pair = new HashMap<Date,Date>();
                pair.put(eventStartDates.get(i),eventEndDates.get(i));
                ls.add(pair);
                map.put(sdf.format(eventStartDates.get(i)).substring(0,10), ls);
            }
        }
        return map;
    }

    public Calendar() {
        this.eventStartDates = new ArrayList<Date>();
        this.eventEndDates = new ArrayList<Date>();
    }

    public Calendar(Integer id, List<Date> eventStartDates, List<Date> eventEndDates, List<String> eventNames, List<Integer> appointmentIds){
        this.id = id;
        this.eventStartDates = eventStartDates;
        this.eventEndDates = eventEndDates;
        this.eventNames = eventNames;
        this.appointmentIds = appointmentIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Integer> getAppointmentIds() {
        return appointmentIds;
    }

    public void setAppointmentIds(List<Integer> appointmentIds) {
        this.appointmentIds = appointmentIds;
    }

    public List<Date> getEventStartDates() {
        return this.eventStartDates;
    }

    public void setEventStartDates(List<Date> startDates) {
        this.eventStartDates = startDates;
    }

    public List<Date> getEventEndDates() {
        return this.eventEndDates;
    }

    public void setEventEndDates(List<Date> endDates) {
        this.eventEndDates = endDates;
    }

    public List<String> getEventNames() {
        return eventNames;
    }

    public void setEventNames(List<String> eventNames) {
        this.eventNames = eventNames;
    }

    public List<Date> getVacationDates() {
        return vacationDates;
    }

    public void setVacationDates(List<Date> vacationDates) {
        this.vacationDates = vacationDates;
    }

    public void addAppointment(Appointment appointment){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startTime = appointment.getTime();
            ExaminationType exType = appointment.getExaminationType();
            addEvent(sdf.parse(sdf.format(startTime)),
                    sdf.parse(sdf.format(new Date(startTime.getTime() + (long)(exType.getDuration() * 1000 * 60 * 60)))),
                    exType.getName(),
                    appointment.getId());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void addVacationDates(MedicalStaffRequest medicalStaffRequest) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            List<Date> list = new ArrayList<>();
            list.add(sdf.parse(sdf.format(medicalStaffRequest.getFromDate())));
            list.add(sdf.parse(sdf.format(medicalStaffRequest.getToDate())));
            this.setVacationDates(list);
        }catch (ParseException p){
            p.printStackTrace();
        }
    }

    private void addEvent(Date startDate, Date endDate, String eventName) {
        if (eventStartDates.size() == 0) {
            eventStartDates.add(startDate);
            eventEndDates.add(endDate);
            eventNames.add(eventName);
            return;
        }
    }
    private void addEvent(Date startDate, Date endDate, String eventName, Integer appointmentId){
        if(this.getEventStartDates().size() == 0){
            this.eventStartDates.add(startDate);
            this.eventEndDates.add(endDate);
            this.eventNames.add(eventName);
            return;
        }
        if(startDate.after(eventStartDates.get(eventStartDates.size() - 1))){
            eventStartDates.add(startDate);
            eventEndDates.add(endDate);
            eventNames.add(eventName);
            appointmentIds.add(appointmentId);
            return;
        }
        for(int i = 0; i != eventStartDates.size(); i++){
            if(startDate.before(eventStartDates.get(i))){
                eventStartDates.add(i, startDate);
                eventEndDates.add(i, endDate);
                eventNames.add(i, eventName);
                appointmentIds.add(i, appointmentId);
                return;
            }
        }
    }

    public void removeEventByAppointmentId(Integer appointmentId) {
        for(int i = 0; i != appointmentIds.size(); i++){
            if(appointmentIds.get(i).equals(appointmentId)){
                eventStartDates.remove(i);
                eventEndDates.remove(i);
                eventNames.remove(i);
                appointmentIds.remove(i);
                break;
            }
        }
    }
    public boolean areTheSameDay(Date date1, Date date2){
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR) &&
                cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR);
    }

    public Date getDayStart(Date date){
        java.util.Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }
    public Date getDayStartAfter(Date date){
        java.util.Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }
}