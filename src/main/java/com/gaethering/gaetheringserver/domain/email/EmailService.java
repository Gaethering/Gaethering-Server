package com.gaethering.gaetheringserver.domain.email;

public interface EmailService {

    void sendAuthMail(String email, String authCode);

    void confirmAuthCode(String code);

}
