package com.vsn.config.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vsn.config.rest.resources.Statuses;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;



@Data
public class MvcResponse {
    @Getter
    @Setter
    @JsonProperty("status")
    protected int status;

    public MvcResponse(int status){
        this.status = status;
    }

    public MvcResponse(Statuses status){
        this.status = status.getStatus();
    }
}
