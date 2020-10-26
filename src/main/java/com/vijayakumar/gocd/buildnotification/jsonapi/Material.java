package com.vijayakumar.gocd.buildnotification.jsonapi;

import com.google.gson.annotations.SerializedName;

public class Material {
    @SerializedName("id")
    public int id;

    // Format: "Pipeline", etc.
    @SerializedName("type")
    public String type;

    // Format: "zoo" or "git@github.com:foo/bar.git, Branch: master"
    @SerializedName("description")
    public String description;

    //"fingerprint": "d22ec438c20be7f700e2aca7f4f416eef11e5ec2bbcf201c6f03f02ed8b2a6e0",

    public boolean isPipeline() {
        return type.equals("Pipeline");
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
        Material other = (Material) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
