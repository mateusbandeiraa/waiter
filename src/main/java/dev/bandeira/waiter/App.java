package dev.bandeira.waiter;

import java.time.Instant;

import io.javalin.Javalin;

public class App {

	private Javalin app;
	
	public static void main(String[] args) {
		var app = new App();
		app.startApp();
	}

	public void startApp() {
		this.app = Javalin.create().start(7070);

		app.get("/wait", ctx -> {
			Instant start = Instant.now();
			Integer timeout = ctx.queryParamAsClass("timeout", Integer.class).getOrDefault(0);
			Thread.sleep(timeout);
			Instant end = Instant.now();
			ctx.result("Waited " + timeout + "ms between" + start.toString() + " and " + end.toString());
		});
	}

	public void stopApp() {
		this.app.close();
	}
}
