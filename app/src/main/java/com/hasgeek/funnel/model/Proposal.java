package com.hasgeek.funnel.model;

/**
 * Created by karthik on 23-12-2014.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Proposal extends RealmObject{

    @Expose
    private String bio;
    @Expose
    private Integer comments;
    @Expose
    private Boolean confirmed;
    @Expose
    private String description;
    @Expose
    private String fullname;
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("json_url")
    @Expose
    private String jsonUrl;
    @Expose
    private String level;
    @Expose
    private String links;
    @Expose
    private String name;
    @Expose
    private String objective;
    @Expose
    private String proposer;
    @Expose
    private String requirements;
    @Expose
    private String section;
    @Expose
    private String slides;
    @Expose
    private String speaker;
    @Expose
    private String submitted;
    @Expose
    private String title;
    @Expose
    private String type;
    @Expose
    private String url;
    @Expose
    private Integer votes;

    private Space space;

    /**
     *
     * @return
     * The bio
     */
    public String getBio() {
        return bio;
    }

    /**
     *
     * @param bio
     * The bio
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     *
     * @return
     * The comments
     */
    public Integer getComments() {
        return comments;
    }

    /**
     *
     * @param comments
     * The comments
     */
    public void setComments(Integer comments) {
        this.comments = comments;
    }

    /**
     *
     * @return
     * The confirmed
     */
    public Boolean getConfirmed() {
        return confirmed;
    }

    /**
     *
     * @param confirmed
     * The confirmed
     */
    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The fullname
     */
    public String getFullname() {
        return fullname;
    }

    /**
     *
     * @param fullname
     * The fullname
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The proposalId
     */
    public void setlId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The jsonUrl
     */
    public String getJsonUrl() {
        return jsonUrl;
    }

    /**
     *
     * @param jsonUrl
     * The json_url
     */
    public void setJsonUrl(String jsonUrl) {
        this.jsonUrl = jsonUrl;
    }

    /**
     *
     * @return
     * The level
     */
    public String getLevel() {
        return level;
    }

    /**
     *
     * @param level
     * The level
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     *
     * @return
     * The links
     */
    public String getLinks() {
        return links;
    }

    /**
     *
     * @param links
     * The links
     */
    public void setLinks(String links) {
        this.links = links;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The objective
     */
    public String getObjective() {
        return objective;
    }

    /**
     *
     * @param objective
     * The objective
     */
    public void setObjective(String objective) {
        this.objective = objective;
    }

    /**
     *
     * @return
     * The proposer
     */
    public String getProposer() {
        return proposer;
    }

    /**
     *
     * @param proposer
     * The proposer
     */
    public void setProposer(String proposer) {
        this.proposer = proposer;
    }

    /**
     *
     * @return
     * The requirements
     */
    public String getRequirements() {
        return requirements;
    }

    /**
     *
     * @param requirements
     * The requirements
     */
    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    /**
     *
     * @return
     * The section
     */
    public String getSection() {
        return section;
    }

    /**
     *
     * @param section
     * The section
     */
    public void setSection(String section) {
        this.section = section;
    }

    /**
     *
     * @return
     * The slides
     */
    public String getSlides() {
        return slides;
    }

    /**
     *
     * @param slides
     * The slides
     */
    public void setSlides(String slides) {
        this.slides = slides;
    }

    /**
     *
     * @return
     * The speaker
     */
    public String getSpeaker() {
        return speaker;
    }

    /**
     *
     * @param speaker
     * The speaker
     */
    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    /**
     *
     * @return
     * The submitted
     */
    public String getSubmitted() {
        return submitted;
    }

    /**
     *
     * @param submitted
     * The submitted
     */
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The votes
     */
    public Integer getVotes() {
        return votes;
    }

    /**
     *
     * @param votes
     * The votes
     */
    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }
}