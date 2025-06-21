package com.root7325.voicy.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author root7325 on 13.06.2025
 */
@Slf4j
public class AudioConverter {
    private static final int FFMPEG_CONVERSION_TIMEOUT_MS = 10_000;
    private static final int AUDIO_SAMPLE_RATE = 16000;
    private static final int AUDIO_CHANNELS = 1;

    public static byte[] convertOggToWav(byte[] oggData) throws IOException, InterruptedException, TimeoutException {
        Process ffmpegProcess = new ProcessBuilder(buildFfmpegCommand()).start();

        try (OutputStream ffmpegInput = ffmpegProcess.getOutputStream();
             InputStream ffmpegOutput = ffmpegProcess.getInputStream()) {

            CompletableFuture.runAsync(() -> {
                try {
                    ffmpegInput.write(oggData);
                    ffmpegInput.close();
                } catch (IOException e) {
                    Thread.currentThread().interrupt();
                }
            });

            ByteArrayOutputStream wavOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = ffmpegOutput.read(buffer)) != -1) {
                wavOutputStream.write(buffer, 0, bytesRead);
            }

            if (!ffmpegProcess.waitFor(FFMPEG_CONVERSION_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                ffmpegProcess.destroyForcibly();
                throw new TimeoutException("FFmpeg conversion timed out");
            }

            if (ffmpegProcess.exitValue() != 0) {
                throw new IOException("FFmpeg process failed with exit code " + ffmpegProcess.exitValue());
            }

            return wavOutputStream.toByteArray();
        }
    }

    private static String[] buildFfmpegCommand() {
        return new String[] {
                "ffmpeg",
                "-i", "pipe:0",
                "-f", "wav",
                "-acodec", "pcm_s16le",
                "-ar", String.valueOf(AUDIO_SAMPLE_RATE),
                "-ac", String.valueOf(AUDIO_CHANNELS),
                "pipe:1"
        };
    }
}
