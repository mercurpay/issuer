package tech.claudioed.issuer.domain.service;

import io.grpc.stub.StreamObserver;
import io.opentracing.Tracer;
import issuer.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.lognet.springboot.grpc.GRpcService;
import tech.claudioed.issuer.domain.Balance;
import tech.claudioed.issuer.domain.OperationType;
import tech.claudioed.issuer.domain.service.data.CardChargeRequest;
import tech.claudioed.issuer.domain.service.data.RequestCardRequest;
import tech.claudioed.issuer.domain.service.data.TokenData;
import tech.claudioed.issuer.domain.service.data.TransactionRequest;

import java.math.BigDecimal;

@Slf4j
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
    log.info("Requesting payment for token {} ... ",request.getToken());
    val paymentSpan = tracer.buildSpan("request-payment").withTag("token", request.getToken()).startActive();

      final TransactionRequest transactionRequest =
              TransactionRequest.builder()
                      .data(TokenData.builder().data(request.getToken()).build())
                      .value(BigDecimal.valueOf(request.getValue()))
                      .type(OperationType.PURCHASE)
                      .build();
      final tech.claudioed.issuer.domain.Transaction transaction =
              this.purchaseService.acquire(transactionRequest);
      final Transaction transactionData = Transaction.newBuilder()
          .setStatus(transaction.getStatus()).setId(transaction.getId()).build();
      paymentSpan.close();
      log.info("Payment ID {} created successfully for token {} ",transactionData.getId(),request.getToken());
      responseObserver.onNext(transactionData);
      responseObserver.onCompleted();
  }

  @Override
  public void requestCard(RequestCard request, StreamObserver<Account> responseObserver) {
    log.info("Requesting new card for customer {} ...",request.getCustomer());
    val requestCardSpan = tracer.buildSpan("request-card").withTag("customer", request.getCustomer())
            .withTag("issuer",request.getIssuer()).startActive();
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
      requestCardSpan.close();
      log.info("New card created successfully for customer {} ",request.getCustomer());
      responseObserver.onNext(newAccount);
      responseObserver.onCompleted();
  }

  @Override
  public void requestCharge(RequestCharge request, StreamObserver<CardBalance> responseObserver) {
    log.info("Requesting new charge for token {} ...",request.getToken());
    val requestChargeSpan = tracer.buildSpan("request-charge").withTag("token", request.getToken()).startActive();
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
      requestChargeSpan.close();
      log.info("Charge executed successfully for token {} ...",request.getToken());
      responseObserver.onNext(cardBalance);
      responseObserver.onCompleted();
    }

}
