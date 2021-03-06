package es.kleiren.madclimb.data_classes;

import java.io.Serializable;

public class Route implements Serializable {

    private String zoneName;
    private String sectorName;
    private int resource;
    private String grade;
    private int qd;
    private int height;
    private String doneAttempt;
    private String name;
    private String description;
    private String ref;
    private String doneDate;
    private String eightADotNu = "";

    public String getEightADotNu() {
        return eightADotNu;
    }

    public void setEightADotNu(String eightADotNu) {
        this.eightADotNu = eightADotNu;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public int getQd() {
        return qd;
    }

    public void setQd(int qd) {
        this.qd = qd;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setReference(String ref) {
        this.ref = ref;
    }

    public String getRef() {
        return ref;
    }

    public void setDoneDate(String doneDate) {
        this.doneDate = doneDate;
    }

    public String getDoneDate() {
        return doneDate;
    }

    public String getDoneAttempt() {
        return doneAttempt;
    }

    public void setDoneAttempt(String doneAttempt) {
        this.doneAttempt = doneAttempt;
    }
}
