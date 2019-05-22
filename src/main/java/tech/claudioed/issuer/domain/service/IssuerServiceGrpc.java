package tech.claudioed.issuer.domain.service;

import io.grpc.stub.StreamObserver;
import io.opentracing.Tracer;
import issuer.Account;
import issuer.CardBalance;
import issuer.RequestCard;
import issuer.RequestCharge;
import issuer.RequestPayment;
import issuer.Transaction;
import java.math.BigDecimal;

import lombok.val;
import org.lognet.springboot.grpc.GRpcService;
import tech.claudioed.issuer.domain.Balance;
import tech.claudioed.issuer.domain.OperationType;
import tech.claudioed.issuer.domain.service.data.CardChargeRequest;
import tech.claudioed.issuer.domain.service.data.RequestCardRequest;
import tech.claudioed.issuer.domain.service.data.TokenData;
import tech.claudioed.issuer.domain.service.data.TransactionRequest;

@GRpcService
public class IssuerServiceGrpc extends issuer.IssuerServiceGrpc.IssuerServiceImplBase {

  private final ChargeCardService chargeCardService;

  private final PurchaseService purchaseService;

  private final RegisterCardService registerCardService;

  private final Tracer tracer;

  public IssuerServiceGrpc(ChargeCardService chargeCardService,
      PurchaseService purchaseService,
      RegisterCardService registerCardService,
      Tracer tracer) {
    this.chargeCardService = chargeCardService;
    this.purchaseService = purchaseService;
    this.registerCardService = registerCardService;
    this.tracer = tracer;
  }

  @Override
  public void requestPayment(RequestPayment request, StreamObserver<Transaction> responseObserver) {
    val paymentSpan = tracer.buildSpan("request-payment").start().setTag("token", request.getToken())
            .setBaggageItem("token",request.getToken());
    try (val scope = tracer.scopeManager().activate(paymentSpan, true)) {
      final TransactionRequest transactionRequest =
              TransactionRequest.builder()
                      .data(TokenData.builder().data(request.getToken()).build())
                      .value(BigDecimal.valueOf(request.getValue()))
                      .type(OperationType.PURCHASE)
                      .build();
      final tech.claudioed.issuer.domain.Transaction transaction =
              this.purchaseService.acquire(transactionRequest);
      final Transaction transactionData = Transaction.newBuilder().setId(transaction.getId()).build();
      paymentSpan.finish();
      responseObserver.onNext(transactionData);
      responseObserver.onCompleted();
    }
  }

  @Override
  public void requestCard(RequestCard request, StreamObserver<Account> responseObserver) {
    val requestCardSpan = tracer.buildSpan("request-card").start().setTag("customer", request.getCustomer())
            .setTag("issuer",request.getIssuer())
            .setBaggageItem("customer",request.getCustomer());
    try (val scope = tracer.scopeManager().activate(requestCardSpan, true)) {
      final RequestCardRequest cardRequest =
              RequestCardRequest.builder()
                      .card(request.getCard())
                      .customer(request.getCustomer())
                      .issuer(request.getIssuer())
                      .balance(BigDecimal.valueOf(request.getBalance()))
                      .build();
      final tech.claudioed.issuer.domain.Account account =
              this.registerCardService.newCard(cardRequest);
      final Account newAccount = Account.newBuilder().setId(account.getId()).build();
      requestCardSpan.finish();
      responseObserver.onNext(newAccount);
      responseObserver.onCompleted();
    }
  }

  @Override
  public void requestCharge(RequestCharge request, StreamObserver<CardBalance> responseObserver) {
    val requestChargeSpan = tracer.buildSpan("request-charge").start().setTag("token", request.getToken())
            .setBaggageItem("token",request.getToken());
    try (val scope = tracer.scopeManager().activate(requestChargeSpan, true)) {
      final CardChargeRequest cardChargeRequest =
              CardChargeRequest.builder()
                      .data(TokenData.builder().data(request.getToken()).build())
                      .value(BigDecimal.valueOf(request.getValue()))
                      .build();
      final Balance balance = this.chargeCardService.charge(cardChargeRequest);
      final CardBalance cardBalance =
              CardBalance.newBuilder()
                      .setBalance(balance.getBalance().doubleValue())
                      .setToken(request.getToken())
                      .build();
      requestChargeSpan.finish();
      responseObserver.onNext(cardBalance);
      responseObserver.onCompleted();
    }
  }

}
