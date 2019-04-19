import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;

public class Admin {

    public static String getNormalExchange(){
        return "Exchange";
    }

    public static String getAdminExchange(){
        return "AdminExchange";
    }

    public static void main(String args[]) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel logChannel = connection.createChannel();
        String logQueue = logChannel.queueDeclare().getQueue();
        logChannel.exchangeDeclare(Admin.getNormalExchange(), "topic");
        logChannel.queueBind(logQueue, Admin.getNormalExchange(), "log.#");

        Consumer consumer = new DefaultConsumer(logChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println(new Date() + message);
            }
        };

        logChannel.basicConsume(logQueue,true, consumer);

        Channel adminChannel = connection.createChannel();
        adminChannel.exchangeDeclare(Admin.getAdminExchange(), "fanout");

        Scanner scanner = new Scanner(System.in);
        while(true){
            String input = scanner.nextLine();
            if(input.equals("exit")) System.exit(0);
            adminChannel.basicPublish(Admin.getAdminExchange(), "", null, (input).getBytes(StandardCharsets.UTF_8));
        }
    }
}
