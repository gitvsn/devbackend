package com.vsn.config.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vsn.config.rest.resources.Statuses;
import lombok.Getter;
import lombok.Setter;


import java.util.List;


public class MvcResponseList<T> extends MvcResponse{
    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("response")
    private List<T> response;

    public MvcResponseList(Statuses status, List<T> response) {
        super(status.ordinal());
        this.response = response;
    }

    public void addParameter(T object){
        response.add(object);
    }

    @Override
    public String toString() {
        return "{\"status\":" + status + ", \"response\":" + response + "}";
    }
}
