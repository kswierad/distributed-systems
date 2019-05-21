package pl.edu.agh.cs.sr.commands;

import akka.actor.ActorPath;

import java.io.Serializable;

public class Order implements Serializable {
    private String name;
    private String clientPath;

    public Order(String name, String clientPath){
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
