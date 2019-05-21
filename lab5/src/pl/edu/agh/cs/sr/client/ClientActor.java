package pl.edu.agh.cs.sr.client;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import pl.edu.agh.cs.sr.commands.*;
import pl.edu.agh.cs.sr.commands.Error;

public class ClientActor extends AbstractActor{

    private LoggingAdapter log;
    private ActorSelection server;

    public ClientActor(String serverPath){
        this.log = Logging.getLogger(getContext().getSystem(), this);

        server = getContext().actorSelection(serverPath);

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Order.class, order -> {
                    server.tell(order, getSelf());
                    System.out.println("Sent out an Order request to server");
                })
                .match(Search.class, search -> {
                    server.tell(search, getSelf());
                    System.out.println("Sent out a Search request to server");
                })
                .match(Stream.class, order -> {
                    server.tell(order, getSelf());
                    System.out.println("Sent out a Stream request to server");
                })
                .match(OrderResponse.class, response -> {
                    if (response.isSuccessful()){
                        System.out.println("Ordering of " + response.getName() + " was successful,\n" +
                                           "the price is: " + response.getPrice());
                    } else {
                        System.out.println("Order was unsuccessful.");
                    }
                })
                .match(SearchResponse.class, response -> {
                    System.out.print(response.getName());
                    System.out.println(response.inDatabase()?
                            " is in database and costs " + response.getPrice().toString():
                            " is not in database");
                })
                .match(Error.class, error -> {
                    System.out.println("Something went wrong, server says:" + error.getReason());
                })
                .match(String.class, System.out::println)
                .matchAny(o -> log.info("Invalid object was sent to ClientActor"))
                .build();
    }
}