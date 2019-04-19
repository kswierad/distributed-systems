import com.rabbitmq.client.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Tech {

    private String name;
    private Channel responseChannel;
    private Channel requestChannel;
    private List<String> skills;
    private Channel adminChannel;
    private Channel logChannel;


    public static void main(String[] argv) throws Exception {

        System.out.println("Type the name of a technician");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        System.out.println("Type the first skill: ");
        LinkedList<String> skills = new LinkedList<>();
        skills.add(scanner.nextLine());
        System.out.println("Type the second skill: ");
        skills.add(scanner.nextLine());
        new Tech(name, skills);
    }

    public Tech(String name, List<String> skills) throws Exception {
        this.name = name;
        this.skills = skills;
        prepareChannels();
    }

    private void prepareChannels() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        responseChannel = connection.createChannel();
        responseChannel.exchangeDeclare(Admin.getNormalExchange(), "topic");

        requestChannel = connection.createChannel();
        //requestChannel.queueDeclare(Topics.getRequestQueueName(), false, false, false, null);
        //requestChannel.exchangeDeclare(Topics.getExchangeName(), BuiltinExchangeType.TOPIC);

        for (String skill : skills) {
            String queueName = requestChannel.queueDeclare(skill, false, false, false, null).getQueue();
            requestChannel.exchangeDeclare(Admin.getNormalExchange(), "topic");
            requestChannel.queueBind(queueName, Admin.getNormalExchange(), "request." + skill + ".*");
            Consumer consumer = new DefaultConsumer(requestChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println(name + ": got(" + envelope.getRoutingKey() + "): " + message);
                    message += " done by " + name;
                    String responseKey = envelope.getRoutingKey().replaceFirst("request", "response");
                    String logKey = "log.".concat(responseKey);
                    responseChannel.basicPublish(Admin.getNormalExchange(), responseKey, null, message.getBytes(StandardCharsets.UTF_8));
                    logChannel.basicPublish( Admin.getNormalExchange(), logKey, null, message.getBytes(StandardCharsets.UTF_8));
                    System.out.println("Responded: " + message);
                }
            };
            //requestChannel.basicQos(1);
            requestChannel.basicConsume(queueName, true, consumer);
            //System.out.println("Ended working");

        }

        adminChannel = connection.createChannel();
        String queueName = adminChannel.queueDeclare().getQueue();
        adminChannel.exchangeDeclare(Admin.getAdminExchange(), "fanout");
        adminChannel.queueBind(queueName, Admin.getAdminExchange(), "");

        Consumer consumer1 = new DefaultConsumer(adminChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("Admin says: " + message);
            }
        };
        adminChannel.basicConsume(queueName, consumer1);

        logChannel = connection.createChannel();
        logChannel.exchangeDeclare(Admin.getNormalExchange(), "topic");
    }
}