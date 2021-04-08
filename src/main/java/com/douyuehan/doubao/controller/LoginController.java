//package com.douyuehan.doubao.controller;
//
//import com.google.code.kaptcha.Constants;
//import com.google.code.kaptcha.Producer;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.imageio.ImageIO;
//import javax.servlet.ServletException;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//
//@RestController
//@Slf4j
//public class LoginController {
//    @Autowired
//    private Producer producer;
//
//    @GetMapping("captcha.jpg")
//    public void captcha(HttpServletResponse response, HttpServletRequest request) throws ServletException, IOException {
//        response.setHeader("Cache-Control", "no-store, no-cache");
//        response.setContentType("image/jpeg");
//        // 生成文字验证码
//        String text = producer.createText();
//        // 生成图片验证码
//        BufferedImage image = producer.createImage(text);
//        // 保存到验证码到 session
//        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, text);
//        ServletOutputStream out = response.getOutputStream();
//        ImageIO.write(image, "jpg", out);
//        out.close();
//    }
//
//
//}
