package dev.bandeira.waiter.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import kong.unirest.HttpResponse;

public interface AsyncWaiterClient {

	public static AsyncWaiterClient create(SyncWaiterClient client) {
		return (AsyncWaiterClient) Proxy.newProxyInstance(client.getClass().getClassLoader(),
				new Class[] { AsyncWaiterClient.class }, (proxy, proxyMethod, args) -> {
					Method originalMethod = client.getClass().getMethod(proxyMethod.getName());
					return CompletableFuture.supplyAsync(() -> {
						try {
							return originalMethod.invoke(client, args);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new CompletionException(e);
						}
					});
				});
	}

	public CompletableFuture<HttpResponse<String>> get5Seconds();

	public CompletableFuture<HttpResponse<String>> get10Seconds();
}
