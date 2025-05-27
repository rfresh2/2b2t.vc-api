package vc.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import vc.controller.FeedController;

@Component
public class FeedShutdownHook implements ApplicationListener<ContextClosedEvent> {

    private final FeedController feedController;

    public FeedShutdownHook(FeedController feedController) {
        this.feedController = feedController;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        feedController.shutdownFeeds();
    }
}
