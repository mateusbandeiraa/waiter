package dev.bandeira.waiter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.bandeira.waiter.client.AsyncWaiterClient;
import dev.bandeira.waiter.client.SyncWaiterClient;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;

class TimingsTest {

	private static App app;

	@BeforeAll
	static void beforeAll() {
		app = new App();
		app.startApp();
	}

	@AfterAll
	static void afterAll() {
		app.stopApp();
	}

	@Test
	void testSyncSyncTakes15Seconds() {
		var syncWaiterClient = new SyncWaiterClient();

		SimpleTimer timer = SimpleTimer.start();
		HttpResponse<String> get5Seconds = syncWaiterClient.get5Seconds();
		HttpResponse<String> get10Seconds = syncWaiterClient.get10Seconds();
		timer.end();

		assertThat(timer.getDurationInSeconds()).isCloseTo(15l, byLessThan(1l));

		assertThat(get5Seconds.getStatus()).isEqualTo(HttpStatus.OK);
		assertThat(get10Seconds.getStatus()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void testSyncAsyncTakes15Seconds() throws InterruptedException, ExecutionException {
		var syncWaiterClient = new SyncWaiterClient();
		var asyncWaiterClient = AsyncWaiterClient.create(syncWaiterClient);

		SimpleTimer timer = SimpleTimer.start();
		HttpResponse<String> get5Seconds = syncWaiterClient.get5Seconds();
		HttpResponse<String> get10Seconds = asyncWaiterClient.get10Seconds().get();
		timer.end();

		assertThat(timer.getDurationInSeconds()).isCloseTo(15l, byLessThan(1l));
		assertThat(get5Seconds.getStatus()).isEqualTo(HttpStatus.OK);
		assertThat(get10Seconds.getStatus()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void testAsyncSyncTakes10Seconds() throws InterruptedException, ExecutionException {
		var syncWaiterClient = new SyncWaiterClient();
		var asyncWaiterClient = AsyncWaiterClient.create(syncWaiterClient);

		SimpleTimer timer = SimpleTimer.start();
		
		CompletableFuture<HttpResponse<String>> get5SecondsFuture = asyncWaiterClient.get5Seconds();
		HttpResponse<String> get10Seconds = syncWaiterClient.get10Seconds();

		HttpResponse<String> get5Seconds = get5SecondsFuture.get();
		timer.end();

		assertThat(timer.getDurationInSeconds()).isCloseTo(10l, byLessThan(1l));

		assertThat(get5Seconds.getStatus()).isEqualTo(HttpStatus.OK);
		assertThat(get10Seconds.getStatus()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void testAsyncAsyncTakes10Seconds() throws InterruptedException, ExecutionException {
		var asyncWaiterClient = AsyncWaiterClient.create(new SyncWaiterClient());

		SimpleTimer timer = SimpleTimer.start();
		
		CompletableFuture<HttpResponse<String>> get5SecondsFuture = asyncWaiterClient.get5Seconds();
		CompletableFuture<HttpResponse<String>> get10SecondsFuture = asyncWaiterClient.get10Seconds();
		
		HttpResponse<String> get5Seconds = get5SecondsFuture.get();
		HttpResponse<String> get10Seconds = get10SecondsFuture.get();
		timer.end();

		assertThat(timer.getDurationInSeconds()).isCloseTo(10l, byLessThan(1l));

		assertThat(get5Seconds.getStatus()).isEqualTo(HttpStatus.OK);
		assertThat(get10Seconds.getStatus()).isEqualTo(HttpStatus.OK);
	}

	static class SimpleTimer {
		private Instant start;
		private Instant end;

		protected SimpleTimer() {

		}

		public static SimpleTimer start() {
			var simpleTimer = new SimpleTimer();
			simpleTimer.start = Instant.now();
			return simpleTimer;
		}

		public SimpleTimer end() {
			this.end = Instant.now();
			return this;
		}

		public Duration getDuration() {
			return Duration.between(start, end);
		}

		public long getDurationInSeconds() {
			return this.getDuration().getSeconds();
		}
	}
}
