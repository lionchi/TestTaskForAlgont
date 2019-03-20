package com.gavrilov.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;

@RestController
public class StatisticController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public StatisticController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Использовался для тестирования программы
    @MessageMapping("/cpu")
    @SendTo("/topic/statistic")
    public String getStatisticsCpu(@Payload String message) throws Exception {
        return "test";
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/imgSuccess")
    public void getImgSuccess(HttpServletRequest request, HttpServletResponse response) {
        try (OutputStream outputStream = response.getOutputStream()) {
            response.setContentType("multipart/x-mixed-replace; boundary=--BoundaryString");
            byte[] bytes = getImageAsByte("success");
            outputStream.write((
                    "--BoundaryString\r\n" +
                            "Content-type: image/jpeg\r\n" +
                            "Content-Length: " +
                            bytes.length +
                            "\r\n\r\n").getBytes());
            outputStream.write(bytes);
            outputStream.write("\r\n\r\n".getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/imgError")
    public void getImgError(HttpServletRequest request, HttpServletResponse response) {
        try (OutputStream outputStream = response.getOutputStream()) {
            response.setContentType("multipart/x-mixed-replace; boundary=--BoundaryString");
            byte[] bytes = getImageAsByte("error");
            outputStream.write((
                    "--BoundaryString\r\n" +
                            "Content-type: image/jpeg\r\n" +
                            "Content-Length: " +
                            bytes.length +
                            "\r\n\r\n").getBytes());
            outputStream.write(bytes);
            outputStream.write("\r\n\r\n".getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/send/message/websocket")
    public ResponseEntity<?> getImgTest(@RequestParam(value = "result") String result) {
        messagingTemplate.convertAndSend("/topic/statistic", result);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/img")
    public void getImgTest(HttpServletRequest request, HttpServletResponse response) {
        final String testText = "value 3%";
        try (OutputStream outputStream = response.getOutputStream()) {
            response.setContentType("multipart/x-mixed-replace; boundary=--BoundaryString");
            byte[] bytes = testText.getBytes();
            outputStream.write((
                    "--BoundaryString\r\n" +
                            "Content-type: image/jpeg\r\n" +
                            "Content-Length: " +
                            bytes.length +
                            "\r\n\r\n").getBytes());
            outputStream.write(bytes);
            outputStream.write("\r\n\r\n".getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getImageAsByte(String nameImg) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            File image = new File(System.getProperty("user.dir") + File.separator + "img" + File.separator + nameImg + ".jpg");
            BufferedImage originalImage = ImageIO.read(image);
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
