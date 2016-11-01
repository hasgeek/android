package com.hasgeek.funnel.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by karthikbalakrishnan on 30/03/15.
 */
public class Attendee extends RealmObject {

    @PrimaryKey
    @SerializedName("_id")
    @Expose
    private String id;

    @Expose
    private String company;

    @Expose
    private String fullname;

    @SerializedName("job_title")
    @Expose
    private String jobTitle;

    @SerializedName("puk")
    @Expose
    private String puk;

    private Space space;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPuk() {
        return puk;
    }

    public void setPuk(String puk) {
        this.puk = puk;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

}
