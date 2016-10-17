package com.hasgeek.funnel.model.wrapper;

import com.google.gson.annotations.SerializedName;
import com.hasgeek.funnel.model.Proposal;
import com.hasgeek.funnel.model.Session;
import com.hasgeek.funnel.model.Space;

import org.json.JSONObject;

import java.util.List;

/**
 * Author: @karthikb351
 * Project: zalebi
 */
public class SpaceWrapper {

    public Space space;

    @SerializedName("proposals")
    public List<Proposal> proposals;

}
