package com.gavrilov.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@RestController
public class StatisticController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public StatisticController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    //region Использовался для тестирования программы
    @MessageMapping("/cpu")
    @SendTo("/topic/statistic")
    public String getStatisticsCpu(@Payload String message) throws Exception {
        return "test";
    }
    //endregion

    @GetMapping(value = "/send/message/websocket")
    public ResponseEntity<?> getImgTest(@RequestParam(value = "result") String result) {
        messagingTemplate.convertAndSend("/topic/statistic", result);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/img/{value}")
    public void getImgTest(HttpServletRequest request, HttpServletResponse response, @PathVariable String value) {
        try (OutputStream outputStream = response.getOutputStream()) {
            response.setContentType("multipart/x-mixed-replace; boundary=--BoundaryString");
            byte[] bytes = getImageAsByte(value);
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

    private byte[] getImageAsByte(String innerText) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            File image = new File(System.getProperty("user.dir") + File.separator + "img" + File.separator + "fon.jpg");
            BufferedImage originalImage = ImageIO.read(image);
            Graphics graphics = originalImage.getGraphics();
            graphics.setFont(graphics.getFont().deriveFont(20f));
            graphics.setColor(Color.black);
            graphics.drawString(innerText + "%", 15, 30);
            graphics.dispose();
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
