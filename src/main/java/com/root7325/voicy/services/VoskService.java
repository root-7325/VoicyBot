package com.root7325.voicy.services;

import com.google.gson.JsonObject;
import com.root7325.voicy.utils.Config;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;

/**
 * @author root7325 on 13.06.2025
 */
@Slf4j
public class VoskService {
    private final Model model;

    public VoskService() {
        Config.MiscConfig miscConfig = Config.getInstance().getMiscConfig();
        String modelPath = miscConfig.getVoskModelPath();

        try {
            this.model = new Model(modelPath);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String recognizeSpeech(byte[] audioData) {
        try (Recognizer recognizer = new Recognizer(model, 16000);
             ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
             AudioInputStream ais = AudioSystem.getAudioInputStream(bais)) {

            int nbytes;
            byte[] buffer = new byte[4096];
            while ((nbytes = ais.read(buffer)) >= 0) {
                if (recognizer.acceptWaveForm(buffer, nbytes)) {
                    String result = recognizer.getResult();
                    return (String) new JSONObject(result).get("text");
                }
            }

            String result = recognizer.getFinalResult();
            return (String) new JSONObject(result).get("text");

        } catch (Exception e) {
            log.error("Error recognizing speech", e);
            throw new RuntimeException("Speech recognition failed", e);
        }
    }
}