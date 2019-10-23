package cphb.hw_mom1;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

import com.google.gson.Gson;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;


public class App 
{
    private static final String EXCHANGE_NAME = "ads_exchange";
    private static final String BOOKING_EXCHANGE_NAME = "booking_exchange";

    public static void main( String[] args ) throws Exception
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Scanner scanner = new Scanner(System.in);
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            channel.exchangeDeclare(BOOKING_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, "");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                Gson gson = new Gson();
                Ad ad = gson.fromJson(message, Ad.class);
                System.out.println("\nReceived ad");
                printAd(ad);
                if (readAnswer(scanner, "Book(y/n)?")) {
                    sendBooking(channel, ad);
                }
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
                System.out.println("Cancelled");
            });
            //System.in.read();
            while (true) {
                Thread.sleep(100);
            }
        }
        
    }

    public static void sendBooking(Channel channel, Ad ad) throws IOException {
        Gson gson = new Gson();
        Booking booking = new Booking(ad.getUuid(), UUID.randomUUID());
        channel.basicPublish(BOOKING_EXCHANGE_NAME, "", null, gson.toJson(booking).getBytes());
        System.out.println("Booking request sent");
    }

    public static void printAd(Ad ad)
    {
        System.out.println("---------------------------");
        System.out.println("Ad: " + ad.getUuid());
        System.out.println("Campaign: " + ad.getCampaign());
        System.out.println("Price: " + ad.getPrice());
        System.out.println("Ends at: " + ad.getEndDate());
    }

    public static boolean readAnswer(Scanner s, String prompt) {
        while(true) {
            System.out.print(prompt);
            String answer = s.nextLine().toLowerCase();
            if (answer.equals("y")) return true;
            if (answer.equals("n")) return false;
            System.out.println("Unknown answer: " + answer);
        }
    }
}
