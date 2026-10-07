package gytis.courier.adapter.webscoket;

public class WebSocketDestinations {
    private WebSocketDestinations() {}

    public static final String ADMIN_TASK_CARD = "/topic/task-cards";
    public static final String NOTIFICATIONS = "/topic/notifications/";
    public static final String NOTIFICATION_QUEUE_SUB = "/user/queue/notifications";
    public static final String NOTIFICATION_QUEUE_SEND = "/queue/notifications";
    public static final String TICKET = "/topic/tickets/";
}
