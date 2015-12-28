package cn.vstore.appserver.service;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ServiceResult<D> {

    private D data;
    private ResultCode result;
    private Throwable throwable;
    private String code;
    private String des;

    ServiceResult() {
    }

    ServiceResult(ResultCode result) {
        this.result = result;
    }

    ServiceResult(ResultCode result, D data) {
        this.result = result;
        this.data = data;
    }

    ServiceResult(ResultCode result, D data, Throwable throwable) {
        this.result = result;
        this.data = data;
        this.throwable = throwable;
    }

    ServiceResult(ResultCode result, D data, String code, String des) {
        this.code = code;
        this.des = des;
        this.data = data;
        this.result = result;
    }

    ServiceResult(ResultCode result, D data, String code, String des, Throwable throwable) {
        this.code = code;
        this.des = des;
        this.data = data;
        this.result = result;
        this.throwable = throwable;
    }

    public ResultCode getResult() {
        return result;
    }

    void setResult(ResultCode result) {
        this.result = result;
    }

    void setData(D data) {
        this.data = data;
    }

    public D getData() {
        return data;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public boolean isError() {
        return throwable != null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

}
