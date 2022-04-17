package com.technical.test.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Country implements Serializable {

    private String cca3;
    private Set<String> borders;

    public String getCca3() {
        return cca3;
    }

    public void setCca3(String cca3) {
        this.cca3 = cca3;
    }

    public Set<String> getBorders() {
        return borders;
    }

    public void setBorders(Set<String> borders) {
        this.borders = borders;
    }
}