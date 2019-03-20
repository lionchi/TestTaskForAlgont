package com.gavrilov.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

@RestController
public class StatisticController {

    //region Использовался для тестирования программы
    @MessageMapping("/cpu")
    @SendTo("/topic/statistic")
    public String getStatisticsCpu(@Payload String message) throws Exception {
        return "test";
    }
    //endregion

    @RequestMapping(value = "/img/{value}")
    public void getImgTest(HttpServletResponse response, @PathVariable String value) {
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
