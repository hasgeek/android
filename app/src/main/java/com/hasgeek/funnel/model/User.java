package com.hasgeek.funnel.model;

import io.realm.RealmObject;

/**
 * Created by karthikbalakrishnan on 15/04/15.
 */
public class User extends RealmObject{
    String userId;
    String authToken;
}
