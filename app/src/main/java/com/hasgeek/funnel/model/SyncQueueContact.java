package com.hasgeek.funnel.model;

import io.realm.RealmObject;

/**
 * Created by karthikbalakrishnan on 31/03/15.
 */
public class SyncQueueContact extends RealmObject {

    public String userId;
    public String userPuk;
    public String userKey;
    public String spaceId;

    public SyncQueueContact() {
    }

    public SyncQueueContact(String userId, String userPuk, String userKey, String spaceId) {
        this.userId = userId;
        this.userPuk = userPuk;
        this.userKey = userKey;
        this.spaceId = spaceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPuk() {
        return userPuk;
    }

    public void setUserPuk(String userPuk) {
        this.userPuk = userPuk;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }
}
