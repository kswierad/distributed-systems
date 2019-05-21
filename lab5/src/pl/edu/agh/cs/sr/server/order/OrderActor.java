package pl.edu.agh.cs.sr.server.order;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import pl.edu.agh.cs.sr.commands.Order;
import pl.edu.agh.cs.sr.commands.OrderResponse;
import pl.edu.agh.cs.sr.commands.SearchResponse;

public class OrderActor extends AbstractActor {
    private Integer nr = 0;
    private ActorRef saver;
    private ActorRef searchManager;
    private String client;

    public OrderActor(ActorRef saver, ActorRef searchManager){
        this.saver = saver;
        this.searchManager = searchManager;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Order.class, request -> {
                    client = request.getClientPath();

                    searchManager.tell(request, getSelf());

                })
                .match(SearchResponse.class, response -> {
                    if(response.inDatabase()) {
                        saver.tell(response, getSelf());

                    } else {
                        OrderResponse orderResponse = new OrderResponse(false, response.getName(), 0.0);
                        context().actorSelection(client).tell(orderResponse, getSelf());
                    }
                })
                .matchAny(o -> System.out.println("Order supervisor received unknown message"))
                .build();
    }

}
