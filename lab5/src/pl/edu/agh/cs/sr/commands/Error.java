package pl.edu.agh.cs.sr.commands;

import java.io.Serializable;

public class Error implements Serializable {
    private String reason;

    public Error(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
