package tech.claudioed.issuer.domain.service;

import io.grpc.stub.StreamObserver;
import issuer.Account;
import issuer.CardBalance;
import issuer.RequestCard;
import issuer.RequestCharge;
import issuer.RequestPayment;
import issuer.Transaction;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
public class IssuerServiceGrpc extends issuer.IssuerServiceGrpc.IssuerServiceImplBase {

  @Override
  public void requestPayment(RequestPayment request, StreamObserver<Transaction> responseObserver) {
    super.requestPayment(request, responseObserver);
  }

  @Override
  public void requestCard(RequestCard request, StreamObserver<Account> responseObserver) {
    super.requestCard(request, responseObserver);
  }

  @Override
  public void requestCharge(RequestCharge request, StreamObserver<CardBalance> responseObserver) {
    super.requestCharge(request, responseObserver);
  }
}
