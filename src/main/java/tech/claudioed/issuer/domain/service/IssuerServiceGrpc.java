package tech.claudioed.issuer.domain.service;

import io.grpc.stub.StreamObserver;
import issuer.Account;
import issuer.CardBalance;
import issuer.RequestCard;
import issuer.RequestCharge;
import issuer.RequestPayment;
import issuer.Transaction;
import java.math.BigDecimal;
import org.lognet.springboot.grpc.GRpcService;
import tech.claudioed.issuer.domain.OperationType;
import tech.claudioed.issuer.domain.service.data.TokenData;
import tech.claudioed.issuer.domain.service.data.TransactionRequest;

@GRpcService
public class IssuerServiceGrpc extends issuer.IssuerServiceGrpc.IssuerServiceImplBase {

  private final ChargeCardService chargeCardService;

  private final PurchaseService purchaseService;

  private final RegisterCardService registerCardService;

  public IssuerServiceGrpc(ChargeCardService chargeCardService,
      PurchaseService purchaseService,
      RegisterCardService registerCardService) {
    this.chargeCardService = chargeCardService;
    this.purchaseService = purchaseService;
    this.registerCardService = registerCardService;
  }

  @Override
  public void requestPayment(RequestPayment request, StreamObserver<Transaction> responseObserver) {
    final TransactionRequest transactionRequest = TransactionRequest.builder().data(
        TokenData.builder().data(request.getToken()).build()).value(BigDecimal.valueOf(request.getValue())).type(
        OperationType.PURCHASE).build();
    final tech.claudioed.issuer.domain.Transaction transaction = this.purchaseService
        .acquire(transactionRequest);
    final Transaction transactionData = Transaction.newBuilder().setId(transaction.getId()).build();
    responseObserver.onNext(transactionData);
    responseObserver.onCompleted();
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
