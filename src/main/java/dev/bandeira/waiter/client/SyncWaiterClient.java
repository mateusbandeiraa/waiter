package dev.bandeira.waiter.client;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class SyncWaiterClient {

	private static final String URI_BASE = "http://localhost:7070";

	protected HttpResponse<String> getCustomSeconds(Long timeout) {
		return Unirest.get(URI_BASE + "/wait") //
				.queryString("timeout", String.valueOf(timeout)) //
				.asString(); //
	}

	public HttpResponse<String> get5Seconds() {
		return getCustomSeconds(5000l);
	}

	public HttpResponse<String> get10Seconds() {
		return getCustomSeconds(10_000l);
	}
}
