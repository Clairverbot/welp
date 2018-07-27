package com.example.clair.welp.Objects;

import java.util.List;

public class Notification {
    private String NotificationString, NotificationType, SendingUsername, SendingEmail, ReceivingEmail, DateSent;

    public Notification(){}
    public Notification(String notificationString, String notificationType, String sendingUsername,String sendingEmail, String receivingEmail, String dateSent) {
        NotificationString = notificationString;
        NotificationType = notificationType;
        SendingUsername = sendingUsername;
        SendingEmail = sendingEmail;
        ReceivingEmail = receivingEmail;
        DateSent = dateSent;
    }

    public String getNotificationString() {
        return NotificationString;
    }

    public void setNotificationString(String notificationString) {
        NotificationString = notificationString;
    }

    public String getNotificationType() {
        return NotificationType;
    }

    public void setNotificationType(String notificationType) {
        NotificationType = notificationType;
    }

    public String getSendingUsername() {
        return SendingUsername;
    }

    public void setSendingUsername(String sendingUsername) {
        SendingUsername = sendingUsername;
    }

    public String getSendingEmail() {
        return SendingEmail;
    }

    public void setSendingEmail(String sendingEmail) {
        SendingEmail = sendingEmail;
    }

    public String getReceivingEmail() {
        return ReceivingEmail;
    }

    public void setReceivingEmail(String receivingEmail) {
        ReceivingEmail = receivingEmail;
    }

    public String getDateSent() {
        return DateSent;
    }

    public void setDateSent(String dateSent) {
        DateSent = dateSent;
    }
}
