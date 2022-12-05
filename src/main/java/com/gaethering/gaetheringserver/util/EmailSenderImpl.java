package com.gaethering.gaetheringserver.util;

import com.gaethering.gaetheringserver.member.exception.FailedSendEmailException;
import com.gaethering.gaetheringserver.member.exception.InvalidEmailAuthCodeException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {

    private static final String EMAIL_ENCODING = "utf-8";
    private static final String EMAIL_SUBJECT = "Gaethering 인증코드";

    private final RedisUtil redisUtil;
    private final JavaMailSender javaMailSender;

    @Override
    @Transactional
    public void sendAuthMail(String email, String authCode) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            makeAuthEmail(email, authCode, message);

            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("[이메일 전송 실패] {}", e.getMessage());

            throw new FailedSendEmailException();
        }

        redisUtil.setDataExpire(authCode, email, 60 * 5L);
    }

    @Override
    @Transactional
    public void confirmAuthCode(String code) {

        String authEmail = redisUtil.getData(code);

        if (ObjectUtils.isEmpty(authEmail)) {
            throw new InvalidEmailAuthCodeException();
        }
    }


    private static void makeAuthEmail(String email, String authCode, MimeMessage message)
        throws MessagingException {

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, EMAIL_ENCODING);

        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(EMAIL_SUBJECT);
        mimeMessageHelper.setText(authCode);
    }

}
