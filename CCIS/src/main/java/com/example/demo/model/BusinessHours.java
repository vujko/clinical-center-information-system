package com.example.demo.model;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Table(name = "business_hours")
public class BusinessHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique = true, nullable = false, columnDefinition = "serial")
    private Integer id;

    @Column(name="started", unique = false, nullable = false)
    private Time started;

    @Column(name="ended", unique = false, nullable = false)
    private Time ended;


    public BusinessHours(){}

    public BusinessHours(Integer id, Time started, Time ended) {
        this.id = id;
        this.started = started;
        this.ended = ended;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Time getStarted() {
        return started;
    }

    public void setStarted(Time started) {
        this.started = started;
    }

    public Time getEnded() {
        return ended;
    }

    public void setEnded(Time ended) {
        this.ended = ended;
    }
}