package pl.edu.agh.cs.sr.server.stream;

import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import pl.edu.agh.cs.sr.commands.Stream;
import scala.concurrent.duration.Duration;

import java.io.FileNotFoundException;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

public class StreamActor extends AbstractActor {


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Stream.class, request -> {
                    context().actorOf(Props.create(StreamWorker.class)).tell(request, getSelf());
                })
                .matchAny( o -> System.out.println("Stream actor go unknown object."))
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
