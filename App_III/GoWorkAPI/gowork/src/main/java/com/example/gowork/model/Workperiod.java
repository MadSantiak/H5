package com.example.gowork.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Workperiod {
    @Id
    @GeneratedValue
    int id;
    Date startDate;
    Date endDate;
    double latitude;
    double longitude;
    boolean atWorkplace;
}
