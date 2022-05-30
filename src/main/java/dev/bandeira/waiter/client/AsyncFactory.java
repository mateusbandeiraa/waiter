package dev.bandeira.waiter.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class AsyncFactory {

	private AsyncFactory() {

	}

	@SuppressWarnings("unchecked")
	public static <T> T makeAsync(Class<T> clazz) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		AsyncWrapper annotation = clazz.getAnnotation(AsyncWrapper.class);
		Class<?> wrappedClass = annotation.value();
		Object wrappedInstance = wrappedClass.getDeclaredConstructor().newInstance();

		return (T) Proxy.newProxyInstance(wrappedClass.getClassLoader(), new Class[] { AsyncWaiterClient.class },
				(proxy, proxyMethod, args) -> {
					Method originalMethod = wrappedClass.getMethod(proxyMethod.getName());
					return CompletableFuture.supplyAsync(() -> {
						try {
							return originalMethod.invoke(wrappedInstance, args);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new CompletionException(e);
						}
					});
				});
	}

	public static void main(String[] args)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, InterruptedException, ExecutionException {
		AsyncWaiterClient makeAsync = AsyncFactory.makeAsync(AsyncWaiterClient.class);

		makeAsync.get5Seconds().thenAccept(response -> System.out.println(response.getBody())).get();
	}
}
