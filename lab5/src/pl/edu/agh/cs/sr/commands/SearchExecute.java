package pl.edu.agh.cs.sr.commands;

import java.io.Serializable;

public class SearchExecute implements Serializable {
    private Search search;
    private String databasePath;

    public SearchExecute(Search search, String databasePath) {
        this.search = search;
        this.databasePath = databasePath;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public Search getSearch() {
        return search;
    }
}
