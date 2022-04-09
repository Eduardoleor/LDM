package dev.eduardoleal.ldm.api;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class CreateUser {

    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("op")
    @Expose
    private String op;
    @SerializedName("result")
    @Expose
    private Boolean result;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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