package com.qst.dms.domain;

import lombok.Data;

@Data
public class DbInfo {
    private String databaseProductName;
    private String databaseProductVersion;
    private String driverName;
}
