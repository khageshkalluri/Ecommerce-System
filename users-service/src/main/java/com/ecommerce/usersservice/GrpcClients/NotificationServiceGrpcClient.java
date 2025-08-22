package com.ecommerce.usersservice.GrpcClients;

import Notification.NotificationRequest;
import Notification.NotificationResponse;
import Notification.NotificationServiceGrpc;
import Notification.NotificationServiceGrpc.NotificationServiceBlockingStub;
import com.ecommerce.usersservice.Dto.UserRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceGrpcClient extends NotificationServiceGrpc.NotificationServiceImplBase {
    public static final Logger log = LoggerFactory.getLogger(NotificationServiceGrpcClient.class);
    NotificationServiceBlockingStub notificationServiceBlockingStub;

    public NotificationServiceGrpcClient(@Value("${notification.server.address:localhost}") String address,@Value("${notification.server.port:8001}") int port) {

        ManagedChannel channel= ManagedChannelBuilder.forAddress(address, port).usePlaintext().build();
        notificationServiceBlockingStub=NotificationServiceGrpc.newBlockingStub(channel);
    }

    public NotificationResponse createNotification(UserRequest request) {
       NotificationRequest notificationRequest= NotificationRequest.newBuilder().setId(request.getName().toString()+","+request.getEmail().toString()).setName(request.getName()).setEmail(request.getEmail()).build();
       log.info("Sending Notification Request to Notification Service "+request.toString());
       return notificationServiceBlockingStub.createNotification(notificationRequest);
    }
}
