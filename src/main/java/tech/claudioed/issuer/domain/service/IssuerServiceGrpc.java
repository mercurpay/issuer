package tech.claudioed.issuer.domain.service;

import io.grpc.stub.StreamObserver;
import issuer.RequestPayment;
import issuer.Transaction;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
public class IssuerServiceGrpc extends issuer.IssuerServiceGrpc.IssuerServiceImplBase {

    @Override
    public void requestPayment(RequestPayment request, StreamObserver<Transaction> responseObserver) {
        super.requestPayment(request, responseObserver);
    }


}
