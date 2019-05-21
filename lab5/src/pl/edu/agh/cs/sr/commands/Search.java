package pl.edu.agh.cs.sr.commands;

import java.io.Serializable;

public class Search implements Serializable {
    private String name;
    private String clientPath;

    public Search(String name, String clientPath) {
        this.name = name;
        this.clientPath = clientPath;
    }

    public String getName() {
        return name;
    }

    public String getClientPath() {
        return clientPath;
    }
}
