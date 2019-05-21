package pl.edu.agh.cs.sr.commands;

import java.io.Serializable;

public class SearchResponse implements Serializable {
    private String name;
    private Boolean inDatabase;
    private String clientPath;
    private Double price;

    public SearchResponse(String name, Boolean inDatabase, String clientPath, Double price) {
        this.name = name;
        this.inDatabase = inDatabase;
        this.clientPath = clientPath;
        this.price = price;
    }

    public Boolean inDatabase() {
        return inDatabase;
    }

    public String getName() {
        return name;
    }

    public String getClientPath() {
        return clientPath;
    }

    public Double getPrice() {
        return price;
    }
}
