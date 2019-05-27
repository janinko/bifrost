package org.jboss.pnc.bifrost.common.scheduler;

import org.jboss.pnc.bifrost.test.Wait;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class SubscriptionsTest {

    @Test
    public void shouldSubcribeTaskAndRunIt() throws TimeoutException, InterruptedException {
        BackOffRunnableConfig backOffRunnableConfig = new BackOffRunnableConfig(400, 10, 5000, 400);

        Subscriptions subscriptions = new Subscriptions();

        List<String> results = new ArrayList<>();
        Consumer<String> onResult = line -> {
            results.add(line);
        };

        Subscription subscription = new Subscription("1", "A");

        Consumer<Subscriptions.TaskParameters<String>> task = (parameters) -> {
            for (int i = 0; i < 5; i++) {
                parameters.getResultConsumer().accept("Result " + i + ". Last was: " + parameters.getLastResult());
            }
        };
        subscriptions.subscribe(
                subscription,
                task,
                Optional.empty(),
                onResult,
                backOffRunnableConfig
        );


        Wait.forCondition(()->results.size() == 10, 3, ChronoUnit.SECONDS);

        results.forEach(System.out::println);

        subscriptions.unsubscribeAll();
    }

}