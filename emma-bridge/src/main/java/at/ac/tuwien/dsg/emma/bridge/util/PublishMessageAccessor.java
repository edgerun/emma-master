package at.ac.tuwien.dsg.emma.bridge.util;

import java.lang.reflect.Field;

import net.xenqtt.client.PublishMessage;
import net.xenqtt.message.PubMessage;

/**
 * Reflection hack to get PubMessage from Xenqtt PublishMessage (required for getting the message id).
 * TODO: use a different MQTT client!
 */
public class PublishMessageAccessor {

    private static final Field pubMessage;

    static {
        try {
            pubMessage = PublishMessage.class.getDeclaredField("pubMessage");
            pubMessage.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static PubMessage getPubMessage(PublishMessage message) {
        try {
            return (PubMessage) pubMessage.get(message);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
