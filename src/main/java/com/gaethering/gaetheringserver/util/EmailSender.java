package com.gaethering.gaetheringserver.util;

public interface EmailSender {

    void sendAuthMail(String email, String authCode);

    void confirmAuthCode(String code);

}
