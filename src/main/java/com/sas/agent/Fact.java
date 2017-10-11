package com.sas.agent;

import lombok.Getter;
import lombok.Setter;

public class Fact {
    @Getter
    @Setter
    private String factName;

    @Getter
    @Setter
    private String factTtl;

    private Properties properties;
}
