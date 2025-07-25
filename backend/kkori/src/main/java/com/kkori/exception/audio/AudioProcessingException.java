package com.kkori.exception.audio;

import com.kkori.exception.CustomRuntimeException;
import com.kkori.exception.ExceptionCode;

public class AudioProcessingException extends CustomRuntimeException {
    public AudioProcessingException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public static AudioProcessingException audioTranscriptionFailed() {
        return new AudioProcessingException(ExceptionCode.AUDIO_TRANSCRIPTION_FAILED);
    }

    public static AudioProcessingException audioApiCallFailed() {
        return new AudioProcessingException(ExceptionCode.AUDIO_API_CALL_FAILED);
    }
}