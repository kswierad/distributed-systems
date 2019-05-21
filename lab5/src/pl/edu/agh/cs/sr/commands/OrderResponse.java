package pl.edu.agh.cs.sr.commands;

import akka.actor.ActorRef;

import java.io.Serializable;

public class OrderResponse implements Serializable {
    private Boolean isSuccessful;
    private String name;
    private Double price;

    public OrderResponse(Boolean isSuccessful, String name, Double price) {
        this.isSuccessful = isSuccessful;
        this.name = name;
        this.price = price;
    }

    public Boolean isSuccessful() {
        return isSuccessful;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

}
