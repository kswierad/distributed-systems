package pl.edu.agh.cs.sr.server.order;

import akka.actor.AbstractActor;
import pl.edu.agh.cs.sr.commands.OrderResponse;
import pl.edu.agh.cs.sr.commands.SearchResponse;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class SaveActor extends AbstractActor {
    private final String orders = "data/orders.txt";

    @Override
    public Receive createReceive()  {
        return receiveBuilder()
                .match(SearchResponse.class, searchResponse -> {
                    BufferedWriter writer = null;
                    try {
                        writer = new BufferedWriter(new FileWriter(orders, true));
                        System.out.println("Saving order, title: " + searchResponse.getName());
                        String msg = searchResponse.getName() + "\n";
                        writer.write(msg);
                        writer.close();
                        System.out.println("Saving order successful");
                        OrderResponse orderResponse = new OrderResponse(true,
                                searchResponse.getName(), searchResponse.getPrice());

                        context().actorSelection(searchResponse.getClientPath()).tell(orderResponse, getSelf());
                    } catch (Exception e) {
                        OrderResponse orderResponse = new OrderResponse(false,
                                searchResponse.getName(), 0.0);

                        context().actorSelection(searchResponse.getClientPath()).tell(orderResponse, getSelf());
                    }

                })
                .matchAny(o -> System.out.println("save actor received unknown message"))
                .build();

    }
}
