package com.gavrilov.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MjpegService extends AbstractService {

    @Value("${max.value.cpu}")
    private String maxValue;

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MjpegService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void run() {
        int processCpuLoad = getProcessCpuLoad();
        if (processCpuLoad != 0) {
            String result = "";
            if (processCpuLoad < Integer.valueOf(maxValue)) {
                result = "The CPU load is " + processCpuLoad + "%";
            } else {
                result = "CPU loaded";
            }
           /* byte[] decodedBytes = Base64.getDecoder().decode("test");
            String pathSaveImg = System.getProperty("user.dir") + File.separator + "img" + File.separator + "statisticsImg.jpg";
            try {
                FileUtils.writeByteArrayToFile(new File(pathSaveImg), decodedBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            /*byte[] bytes = Base64.decodeBase64(result);
            String pathSaveImg = System.getProperty("user.dir") + File.separator + "img" + File.separator + "statisticsImg.jpg";
            try (FileOutputStream imgOutFile = new FileOutputStream(pathSaveImg)) {
                imgOutFile.write(bytes);
                imgOutFile.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }
}
