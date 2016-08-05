package com.cybermkd.plugin.zbus;

public final class Topic {
    private final String mqName;
    private final String topicName;

    public Topic(String mqName, String topicName) {
        this.mqName = mqName;
        this.topicName = topicName;
    }

    public final String getMqName() {
        return mqName;
    }

    public final String getTopicName() {
        return topicName;
    }
}
