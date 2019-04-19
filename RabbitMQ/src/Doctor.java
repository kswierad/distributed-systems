import com.rabbitmq.client.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Doctor {


    private String name;
    private Channel requestChannel;
    private Channel responseChannel;
    private Channel adminChannel;
    private Channel logChannel;

    public static void main(String[] argv) throws Exception {


        Scanner scanner = new Scanner(System.in);

        System.out.print("Name: ");
        new Doctor(scanner.nextLine()).examine();

    }


    Doctor(String name) throws Exception{
        this.name = name;
        prepareChannels();
    }

    public void examine() throws Exception{



        Scanner scanner = new Scanner(System.in);


        while(true){
            System.out.println("Type the injury: ");
            String injury = scanner.nextLine();
            if(injury.equals("exit")) System.exit(0);
            //ExaminationType examinationType = ExaminationType.valueOf(br.readLine().toUpperCase());

            String message = name + "." + injury;
            requestChannel.basicPublish(Admin.getNormalExchange(), "request."+injury+"."+name, null, (message).getBytes(StandardCharsets.UTF_8));
            logChannel.basicPublish( Admin.getNormalExchange(), "log.request."+injury+"."+name, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("Sent: " + message);
        }
//
    }

    private void prepareChannels() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();

        responseChannel = connection.createChannel();
        String queueName = responseChannel.queueDeclare().getQueue();
        responseChannel.exchangeDeclare(Admin.getNormalExchange(), "topic");
        responseChannel.queueBind(queueName, Admin.getNormalExchange(), "response.*."+name);

        Consumer consumer = new DefaultConsumer(responseChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("Received from Tech: " + message);
            }
        };

        responseChannel.basicConsume(queueName,true, consumer);

        requestChannel = connection.createChannel();
        requestChannel.exchangeDeclare(Admin.getNormalExchange(), "topic");

        adminChannel = connection.createChannel();
        queueName = adminChannel.queueDeclare().getQueue();
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
