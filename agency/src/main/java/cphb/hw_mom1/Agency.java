package cphb.hw_mom1;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Connection;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import com.google.gson.Gson;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

public class Agency implements Closeable {

    private static final String EXCHANGE_NAME = "ads_exchange";
    private static final String BOOKING_EXCHANGE_NAME = "booking_exchange";

    private Connection connection;
    private Channel channel;
    private ArrayList<Ad> ads;
    private Gson serializer;

    public Agency() throws Exception {
        initAds();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        this.serializer = new Gson();
        // exchange to publish ads
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        // Queue to receive bookings
        channel.exchangeDeclare(BOOKING_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, BOOKING_EXCHANGE_NAME, "");
        channel.basicConsume(queueName, true, (tag, delivery) -> this.onBookingReceived(tag, delivery), consumerTag -> {
            System.out.println("Cancelled");
        });
        

    }

    private void initAds() {
        ads = new ArrayList<Ad>();
        ads.add(new Ad("Haloween campaign", 3.99, Instant.now()));
        ads.add(new Ad("Dreams come true this autumn", 5.65, Instant.parse("2019-11-30T00:00:00.00Z")));
        ads.add(new Ad("Dreams come true this autumn", 510.99, Instant.parse("2019-11-30T00:00:00.00Z")));
    }

    private void onBookingReceived(String tag, Delivery delivery) {
        System.out.println("booking received " + tag);
        Booking b = serializer.fromJson(new String(delivery.getBody(), Charset.forName("utf8")), Booking.class);
        Optional<Ad> ad = ads.stream().filter(a -> a.getUuid() == b.getAdId()).findFirst();
        if (ad.isPresent()) {
            ads.remove(ad.get());
        }
    }

    public void publishAll() throws IOException {
        for (Ad ad : ads) {
            String json = serializer.toJson(ad);
            this.channel.basicPublish(EXCHANGE_NAME, "", null, json.getBytes());
            System.out.println("Published " + json);
        }
    }

    @Override
    public void close() throws IOException {
        connection.close();
    }

    public static void main(String[] args) throws Exception {
        try (Agency a = new Agency()) {
            a.publishAll();

            while (true) {
                Thread.sleep(100);
            }
        }
        
    }

   
}
