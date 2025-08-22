package com.ecommerce.notificationservice.grpc;

import Notification.NotificationRequest;
import Notification.NotificationResponse;
import Notification.NotificationServiceGrpc.NotificationServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class NotificationGrpcService extends NotificationServiceImplBase {

   private static Logger logger = LoggerFactory.getLogger(NotificationGrpcService.class);
    @Override
    public void createNotification(NotificationRequest request, StreamObserver<NotificationResponse> responseStreamObserver){
        logger.debug("Creating Notification "+request.toString());
        NotificationResponse response = NotificationResponse.newBuilder().setName(request.getName()).setEmail(request.getEmail()).build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }
}
