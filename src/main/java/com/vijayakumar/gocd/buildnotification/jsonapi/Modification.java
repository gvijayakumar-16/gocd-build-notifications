package com.vijayakumar.gocd.buildnotification.jsonapi;

import com.google.gson.annotations.SerializedName;

public class Modification {
    @SerializedName("id")
    public int id;

    // Format: "cucumber/102/BuildAndPublish/1" for pipelines, and
    // "2d110a724f3e716f801b6e87d420d7f0c32a208f" for git commits.
    @SerializedName("revision")
    public String revision;

    @SerializedName("comment")
    public String comment;

    @SerializedName("user_name")
    public String userName;

    //"modified_time": 1436365681065,
    //"email_address": null

    /**
     * Return a shortened form of the comment, or null if we have no comment.
     * Designed to reduce git commit messages to just their summary line.
     */
    public String summarizeComment() {
        if (comment == null)
            return null;

        String[] lines = comment.split("\\r?\\n");
        return lines[0];
    }

    // Override hashCode and equals with implementations generated by
    // Eclipse so we can compare MaterialRevision objects.

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Modification other = (Modification) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
