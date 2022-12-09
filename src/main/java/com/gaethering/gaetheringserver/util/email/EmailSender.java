package com.gaethering.gaetheringserver.util.email;

public interface EmailSender {

    void sendAuthMail(String email, String authCode);

    void confirmAuthCode(String code);

}
