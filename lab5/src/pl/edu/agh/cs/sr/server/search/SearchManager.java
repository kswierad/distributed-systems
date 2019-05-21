package pl.edu.agh.cs.sr.server.search;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import pl.edu.agh.cs.sr.commands.Order;
import pl.edu.agh.cs.sr.commands.Search;
import scala.concurrent.duration.Duration;

import java.io.FileNotFoundException;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

public class SearchManager extends AbstractActor {

    private Integer counter = 0;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Search.class, search -> {
                    ActorRef child = context().actorOf(Props.create(SearchSupervisor.class), getName());
                    child.tell(search, getSelf());
                    System.out.println("search manager got search for" + search.getName());
                })
                .match(Order.class, order -> {
                    ActorRef child = context().actorOf(Props.create(SearchSupervisor.class), getName());
                    child.tell(order, getSender());
                    System.out.println("search manager got search for" + order.getName());
                })
                .matchAny( o -> System.out.println("manager got wrong object."))
                .build();
    }

    private String getName(){
        counter++;
        return "searchSupervisor" + counter;
    }

    private static SupervisorStrategy strategy
            = new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
            match(FileNotFoundException.class, e -> resume()).
            matchAny(o -> restart()).
            build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

}
