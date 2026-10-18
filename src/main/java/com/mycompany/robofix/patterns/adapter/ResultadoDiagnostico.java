package com.mycompany.robofix.patterns.adapter;

public class ResultadoDiagnostico {
    private String robotCode;
    private String component;
    private String status;
    private int reportedValue;

    public ResultadoDiagnostico(String robotCode, String component, String status, int reportedValue) {
        this.robotCode = robotCode;
        this.component = component;
        this.status = status;
        this.reportedValue = reportedValue;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public String getComponent() {
        return component;
    }

    public String getStatus() {
        return status;
    }

    public int getReportedValue() {
        return reportedValue;
    }

    public boolean isValid() {
        return robotCode != null && !robotCode.isEmpty() &&
               component != null && !component.isEmpty() &&
               status != null && !status.isEmpty();
    }
}
