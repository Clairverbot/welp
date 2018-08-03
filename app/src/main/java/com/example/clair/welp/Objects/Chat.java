package com.example.clair.welp.Objects;

public class Chat {
    private String Message, ReceivingEmail, ReceivingUsername, SendingEmail, SendingUsername, DateSent;

    public Chat(){}
    public Chat(String message, String receivingEmail, String receivingUsername, String sendingEmail, String sendingUsername, String dateSent) {
        Message = message;
        ReceivingEmail = receivingEmail; //current user
        ReceivingUsername = receivingUsername; //current user
        SendingEmail = sendingEmail;
        SendingUsername = sendingUsername;
        DateSent = dateSent;
    }

    public String getMessage() {
        return Message;
    }
    public void setMessage(String message) { Message = message; }

    public String getReceivingEmail() {
        return ReceivingEmail;
    }
    public void setReceivingEmail(String receivingEmail) { ReceivingEmail = receivingEmail; }

    public String getReceivingUsername() {
        return ReceivingUsername;
    }
    public void setReceivingUsername(String receivingUsername) { ReceivingUsername = receivingUsername; }

    public String getSendingEmail() {
        return SendingEmail;
    }
    public void setSendingEmail(String sendingEmail) {
        SendingEmail = sendingEmail;
    }

    public String getSendingUsername() {
        return SendingUsername;
    }
    public void setSendingUsername(String sendingUsername) {
        SendingUsername = sendingUsername;
    }

    public String getDateSent() {
        return DateSent;
    }
    public void setDateSent(String dateSent) {
        DateSent = dateSent;
    }
}
