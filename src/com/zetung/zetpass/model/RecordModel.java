package com.zetung.zetpass.model;

import java.io.Serializable;

public class RecordModel implements Serializable {

    private String owner;
    private String name;
    private String login;
    private String password;
    private String description;

    public RecordModel(String owner, String name, String login, String password, String description) {
        this.owner = owner;
        this.name = name;
        this.login = login;
        this.password = password;
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "RecordModel{" +
                "idowner=" + owner +
                ", name='" + name + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
