package dev.bandeira.waiter.client;

import java.util.concurrent.CompletableFuture;

import kong.unirest.HttpResponse;

public class AsyncWaiterClient {

	private SyncWaiterClient syncWaiterClient = new SyncWaiterClient();

	protected CompletableFuture<HttpResponse<String>> getCustomSeconds(Long timeout) {
		return CompletableFuture.supplyAsync(() -> syncWaiterClient.getCustomSeconds(timeout));
	}

	public CompletableFuture<HttpResponse<String>> get5Seconds() {
		return CompletableFuture.supplyAsync(syncWaiterClient::get5Seconds);
	}

	public CompletableFuture<HttpResponse<String>> get10Seconds() {
		return CompletableFuture.supplyAsync(syncWaiterClient::get10Seconds);
	}

}
