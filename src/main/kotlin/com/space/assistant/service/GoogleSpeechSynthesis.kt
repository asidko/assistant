package com.space.assistant.service

import com.google.cloud.texttospeech.v1beta1.*
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

@Component
class GoogleSpeechSynthesis {
    private val player = AudioPlayer()

    private val client = TextToSpeechClient.create()

    private val langCode = "ru-RU"

    private val synthesis = object {
        val voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode(langCode)
                .setSsmlGender(SsmlVoiceGender.MALE)
                .build()

        val audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.LINEAR16)
                .build()
    }

    fun say(text: String) {

        val synthesisText = SynthesisInput.newBuilder()
                .setText(text)
                .build()

        val response = client.synthesizeSpeech(
                synthesisText,
                synthesis.voice,
                synthesis.audioConfig)

        player.play(ByteArrayInputStream(response.audioContent.toByteArray()))
    }


    private class AudioPlayer {
        private val clip: Clip = AudioSystem.getClip()

        fun play(audioContentsInputStream: ByteArrayInputStream) {
            AudioSystem.getAudioInputStream(audioContentsInputStream).use {
                clip.open(it)
                clip.start()
            }
        }
    }
}

