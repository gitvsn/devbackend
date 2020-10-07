package com.vsn.config.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vsn.config.rest.resources.Statuses;
import lombok.Getter;
import lombok.Setter;


public class MvcResponseError extends MvcResponse {
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("error")
    protected String error;

    MvcResponseError(int status) {
        super(status);
    }

    public MvcResponseError(int status, String error) {
        super(status);
        this.error=error;
    }

    public MvcResponseError(Statuses status, String error) {
        super(status.ordinal());
        this.error=error;
    }


    public static MvcResponse getMvcErrorResponse(int status, String errorMsg) {
        MvcResponseError mvc = new MvcResponseError(status);
        mvc.setError(errorMsg);
        return mvc;
    }

    public static MvcResponse getMvcErrorResponse(Statuses status, String errorMsg) {
        MvcResponseError mvc = new MvcResponseError(status.ordinal());
        mvc.setError(errorMsg);
        return mvc;
    }

    @Override
    public String toString() {
        return "{\"status\":" + status + ", \"error\":" + error + "}";
    }
}
