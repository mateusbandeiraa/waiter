package dev.bandeira.waiter.client;

import java.util.concurrent.CompletableFuture;

import kong.unirest.HttpResponse;

@AsyncWrapper(SyncWaiterClient.class)
public interface AsyncWaiterClient {

	public CompletableFuture<HttpResponse<String>> get5Seconds();

	public CompletableFuture<HttpResponse<String>> get10Seconds();
}
