package com.rssfetcher.models;

public class RssFeedAuthor {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAttributeValue(String attribute, String value) {
        switch(attribute) {
            case "name":
                this.setName(value);
                break;
            default:
                break;
        }
    }
}
