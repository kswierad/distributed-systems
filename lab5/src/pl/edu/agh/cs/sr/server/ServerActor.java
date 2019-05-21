package pl.edu.agh.cs.sr.server;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import pl.edu.agh.cs.sr.commands.*;
import pl.edu.agh.cs.sr.server.order.OrderActor;
import pl.edu.agh.cs.sr.server.order.SaveActor;
import pl.edu.agh.cs.sr.server.search.SearchManager;
import pl.edu.agh.cs.sr.server.stream.StreamActor;
import scala.concurrent.duration.Duration;

import java.io.FileNotFoundException;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

public class ServerActor extends AbstractActor {
//    static public Props props(String clientPath) {
//        return Props.create(ServerActor.class, () -> new ServerActor(clientPath));
//    }

    private String clientPath= "";
    private LoggingAdapter log;

//    public ServerActor(String bookstore_name){
//        this.log = Logging.getLogger(getContext().getSystem(), this);
//        this.clientPath = bookstore_name;
//
//
//
//
//    }

    @Override
    public void preStart() throws Exception {
        ActorRef searchManager = context().actorOf(Props.create(SearchManager.class), "searchManager");
        ActorRef saveActor = context().actorOf(Props.create(SaveActor.class), "saveActor");
        context().actorOf(Props.create(OrderActor.class, saveActor, searchManager), "orderActor");
        context().actorOf(Props.create(StreamActor.class), "streamActor");

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Order.class, order -> {
                    context().child("orderActor").get().tell(order, getSelf());
                    System.out.println("Passed order into orderActor");
                })
                .match(Stream.class, stream -> {
                    context().child("streamActor").get().tell(stream, getSelf());
                    System.out.println("Passed stream into streamActor");
                })
                .match(Search.class, search -> {
                    context().child("searchManager").get().tell(search, getSelf());
                    System.out.println("Passed search into searchManager");
                })
                .matchAny(o -> System.out.println("Server Actor got unknown message") )
                .build();
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
