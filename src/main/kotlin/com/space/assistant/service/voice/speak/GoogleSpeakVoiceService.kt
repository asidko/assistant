package com.space.assistant.service.voice.speak

import com.google.cloud.texttospeech.v1beta1.*
import com.space.assistant.core.service.SpeakVoiceService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineEvent
import javax.sound.sampled.LineListener

@Service
class GoogleSpeakVoiceService : SpeakVoiceService {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val googleSpeech = GoogleSpeechSynthesis()

    @Synchronized
    override fun say(text: String) {
        log.info("Speaking: $text")
        googleSpeech.say(text)
    }
}


@Suppress("NAME_SHADOWING")
class GoogleSpeechSynthesis {
    private val player = AudioPlayer()
    private val log = LoggerFactory.getLogger(this.javaClass)

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

        log.debug("Got GoogleSpeech response of size {}", response.audioContent.size())

        player.play(response.audioContent.toByteArray())
    }

    private class AudioPlayer {
        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        class AudioListener : LineListener {
            private var done = false

            @Synchronized
            override fun update(event: LineEvent) {
                if (event.type === LineEvent.Type.STOP || event.type === LineEvent.Type.CLOSE) {
                    done = true
                    (this as java.lang.Object).notifyAll()
                }
            }

            @Synchronized
            fun waitUntilDone() {
                while (!done) (this as java.lang.Object).wait()
            }
        }

        fun play(audio: ByteArray) {
            val listener = AudioListener()
            AudioSystem.getAudioInputStream(audio.inputStream())
                    .use { audioInputStream ->
                        val clip = AudioSystem.getClip()
                        clip.addLineListener(listener)
                        clip.open(audioInputStream)

                        clip.use { clip ->
                            clip.start()
                            listener.waitUntilDone()
                        }
                    }
        }
    }
}

