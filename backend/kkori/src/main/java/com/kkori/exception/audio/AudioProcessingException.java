package com.kkori.exception.audio;

import com.kkori.exception.CustomRuntimeException;
import com.kkori.exception.ExceptionCode;

public class AudioProcessingException extends CustomRuntimeException {
    public AudioProcessingException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public static AudioProcessingException audioProcessingFailed() {
        return new AudioProcessingException(ExceptionCode.AUDIO_PROCESSING_FAILED);
    }

    public static AudioProcessingException audioTranscriptionFailed() {
        return new AudioProcessingException(ExceptionCode.AUDIO_TRANSCRIPTION_FAILED);
    }

    public static AudioProcessingException apiCallFailed() {
        return new AudioProcessingException(ExceptionCode.API_CALL_FAILED);
    }
}