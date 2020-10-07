package com.vsn.config.rest.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vsn.config.rest.resources.Statuses;
import lombok.Getter;
import lombok.Setter;


import java.util.HashMap;
import java.util.Map;



@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class MvcResponseMap extends MvcResponse{
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("response")
    private Map<String, Object> response;

    public MvcResponseMap() {
        super(0);
        response = new HashMap<>();
    }

    public MvcResponseMap(Statuses status, Map<?, ?> response) {
        this(status.getStatus(), response);
    }

    public MvcResponseMap(int status, Map<?, ?> response) {
        super(status);
        this.response = new HashMap<>();
        response.forEach((key, elem) -> this.response.put(key.toString(), elem));
    }

    public MvcResponseMap(int status, String paramName, Object object) {
        super(status);
        this.status = status;
        response = new HashMap<>();
        response.put(paramName, object);
    }

    public MvcResponseMap(int status, Object... objects) {
        super(status);
        this.status = status;
        response = new HashMap<>();
        for (Object object : objects) {
            response.put(object.getClass().getName(), object);
        }
    }

    public MvcResponseMap(Statuses status, String paramName, Object object) {
        super(status.ordinal());
        this.status = status.getStatus();
        response = new HashMap<>();
        response.put(paramName, object);
    }

    public MvcResponseMap(Statuses status, Object... objects) {
        super(status.ordinal());
        this.status = status.getStatus();
        response = new HashMap<>();
        for (Object object : objects) {
            response.put(object.getClass().getName(), object);
        }
    }

    public MvcResponseMap(int status, ResponseObject response) {
        super(status);
        this.status = status;
        this.response = response.getValues();
    }

    public MvcResponseMap(Statuses status, ResponseObject response) {
        super(status.ordinal());
        this.status = status.getStatus();
        this.response = response.getValues();
    }

    public MvcResponseMap addParam(Object object) {
        response.put(object.getClass().getName(), object);
        return this;
    }

    public MvcResponseMap addParam(String paramName, Object object) {
        response.put(paramName, object);
        return this;
    }

    public MvcResponseMap addParam(Object... objects) {
        for (Object object : objects) {
            response.put(object.getClass().getName(), object);
        }
        return this;
    }

    public Map<String, Object> getParams() {
        return response;
    }

    public void clearParams() {
        response = new HashMap<>();
    }

    @Override
    public String toString() {
        return "{\"status\":" + status + ", \"response\":" + response + "}";
    }
}
