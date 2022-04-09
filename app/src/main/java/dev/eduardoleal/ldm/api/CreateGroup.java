package dev.eduardoleal.ldm.api;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class CreateGroup {

    @SerializedName("group")
    @Expose
    private Group group;
    @SerializedName("op")
    @Expose
    private String op;
    @SerializedName("result")
    @Expose
    private Boolean result;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

}