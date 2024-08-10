package com.github.linarusakova.mytracker.util;

public class OneDayRecord {
    private String date;
    private String weight;
    private String medical;
    private String dayDetails;

    public OneDayRecord() {
    }

    public OneDayRecord(String date, String weight, String medical, String dayDetails) {
        this.date = date;
        this.weight = weight;
        this.medical = medical;
        this.dayDetails = dayDetails;
    }

    @Override
    public String toString() {
        return "OneDayRecord{" +
                "date='" + date + '\'' +
                ", weight='" + weight + '\'' +
                ", medical='" + medical + '\'' +
                ", dayDetails='" + dayDetails + '\'' +
                '}';
    }

    public OneDayRecord(String timeInMillis, int weight, String number, Object dayDetails) {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getMedical() {
        return medical;
    }

    public void setMedical(String medical) {
        this.medical = medical;
    }

    public String getDayDetails() {
        return dayDetails;
    }

    public void setDayDetails(String dayDetails) {
        this.dayDetails = dayDetails;
    }
}
