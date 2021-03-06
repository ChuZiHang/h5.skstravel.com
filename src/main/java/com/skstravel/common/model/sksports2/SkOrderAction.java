package com.skstravel.common.model.sksports2;

import java.io.Serializable;

public class SkOrderAction implements Serializable {
    private Integer actionId;

    private Integer orderId;

    private String actionUser;

    private Boolean orderStatus;

    private Boolean shippingStatus;

    private Boolean payStatus;

    private Boolean actionPlace;

    private String actionNote;

    private Integer logTime;

    private static final long serialVersionUID = 1L;

    public Integer getActionId() {
        return actionId;
    }

    public void setActionId(Integer actionId) {
        this.actionId = actionId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getActionUser() {
        return actionUser;
    }

    public void setActionUser(String actionUser) {
        this.actionUser = actionUser;
    }

    public Boolean getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Boolean orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Boolean getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(Boolean shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public Boolean getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Boolean payStatus) {
        this.payStatus = payStatus;
    }

    public Boolean getActionPlace() {
        return actionPlace;
    }

    public void setActionPlace(Boolean actionPlace) {
        this.actionPlace = actionPlace;
    }

    public String getActionNote() {
        return actionNote;
    }

    public void setActionNote(String actionNote) {
        this.actionNote = actionNote;
    }

    public Integer getLogTime() {
        return logTime;
    }

    public void setLogTime(Integer logTime) {
        this.logTime = logTime;
    }
}