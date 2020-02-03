package com.space.assistant.service.speech.recognition

import com.google.api.gax.rpc.ClientStream
import com.google.api.gax.rpc.ResponseObserver
import com.google.api.gax.rpc.StreamController
import com.google.cloud.speech.v1p1beta1.*
import com.google.protobuf.ByteString
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import kotlin.math.floor

class GoogleInfiniteStreamRecognize(private val onResultCallback: (List<String>) -> Unit) {
    private val STREAMING_LIMIT = 290000 // ~5 minutes
    private val BYTES_PER_BUFFER = 6400 // buffer size in bytes
    private val additionalLanguages = listOf("uk-UA")
    private val maxAlternativeCount = 3

    val RED = "\u001b[0;31m"
    val GREEN = "\u001b[0;32m"
    val YELLOW = "\u001b[0;33m"

    // Creating shared object
    @Volatile
    private var sharedQueue = LinkedBlockingQueue<ByteArray>()
    private var targetDataLine: TargetDataLine? = null
    private var restartCounter = 0
    private var audioInput = mutableListOf<ByteString>()
    private var lastAudioInput = mutableListOf<ByteString>()
    private var resultEndTimeInMS = 0
    private var isFinalEndTime = 0
    private var finalRequestEndTime = 0
    private var newStream = true
    private var bridgingOffset = 0.0
    private var lastTranscriptWasFinal = false
    private var referenceToStreamController: StreamController? = null
    private var tempByteString: ByteString = ByteString.EMPTY

    fun start(langCode: String) {
        try {
            infiniteStreamingRecognize(langCode)
        } catch (e: Exception) {
            println("Exception caught: $e")
        }
    }

    fun stop() {
        targetDataLine?.stop()
        targetDataLine?.close()
    }

    fun convertMillisToDate(milliSeconds: Double): String {
        val millis = milliSeconds.toLong()
        val format = DecimalFormat()
        format.minimumIntegerDigits = 2
        return String.format("%s:%s /",
                format.format(TimeUnit.MILLISECONDS.toMinutes(millis)),
                format.format(TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
        )
    }


    fun infiniteStreamingRecognize(languageCode: String) { // Microphone Input buffering
        class MicBuffer : Runnable {
            override fun run() {
                println(YELLOW)
                println("Start speaking...")
                targetDataLine!!.start()
                val data = ByteArray(BYTES_PER_BUFFER)
                while (targetDataLine!!.isOpen)
                    try {
                        val numBytesRead = targetDataLine!!.read(data, 0, data.size)
                        if (numBytesRead <= 0 && targetDataLine!!.isOpen) continue
                        sharedQueue.put(data.clone())
                    } catch (e: InterruptedException) {
                        println("Microphone input buffering interrupted : " + e.message)
                    }
            }
        }
        // Creating microphone input buffer thread
        val micrunnable = MicBuffer()
        val micThread = Thread(micrunnable)

        var responseObserver: ResponseObserver<StreamingRecognizeResponse>?

        SpeechClient.create().use { client ->

            var clientStream: ClientStream<StreamingRecognizeRequest?>

            responseObserver = object : ResponseObserver<StreamingRecognizeResponse> {
                var responses = ArrayList<StreamingRecognizeResponse>()

                override fun onStart(controller: StreamController) {
                    referenceToStreamController = controller
                }

                override fun onResponse(response: StreamingRecognizeResponse) {
                    responses.add(response)
                    val result = response.resultsList[0]
                    val resultEndTime = result.resultEndTime
                    resultEndTimeInMS = (resultEndTime.seconds * 1000 + resultEndTime.nanos / 1000000).toInt()
                    val correctedTime = (resultEndTimeInMS - bridgingOffset + STREAMING_LIMIT * restartCounter)
                    val alternatives = result.alternativesList
                    if (result.isFinal) {
                        print(GREEN)
                        print("\u001b[2K\r")
                        for (alternative in alternatives) {
                            print(String.format("%s: %s [confidence: %.2f]\n", convertMillisToDate(correctedTime), alternative.transcript, alternative.confidence))
                        }
                        onResultCallback(alternatives.map { it.transcript })
                        isFinalEndTime = resultEndTimeInMS
                        lastTranscriptWasFinal = true
                    } else {
                        print(RED)
                        print("\u001b[2K\r")
                        print(String.format("%s: %s", convertMillisToDate(correctedTime), alternatives.firstOrNull()?.transcript
                                ?: "<no alternative>"))
                        lastTranscriptWasFinal = false
                    }
                }

                override fun onComplete() {}
                override fun onError(t: Throwable) {}
            }


            clientStream = client.streamingRecognizeCallable().splitCall(responseObserver)
            val recognitionConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode(languageCode)
                    .addAllAlternativeLanguageCodes(additionalLanguages)
                    .setMaxAlternatives(maxAlternativeCount)
                    .setSampleRateHertz(16000)
                    .build()
            val streamingRecognitionConfig = StreamingRecognitionConfig.newBuilder()
                    .setConfig(recognitionConfig)
                    .setInterimResults(true)
                    .build()
            var request = StreamingRecognizeRequest.newBuilder()
                    .setStreamingConfig(streamingRecognitionConfig)
                    .build() // The first request in a streaming call has to be a config
            clientStream.send(request)



            try { // SampleRate:16000Hz, SampleSizeInBits: 16, Number of channels: 1, Signed: true,
// bigEndian: false
                val audioFormat = AudioFormat(16000.toFloat(), 16, 1, true, false)
                val targetInfo = DataLine.Info(
                        TargetDataLine::class.java,
                        audioFormat) // Set the system information to read from the microphone audio
                // stream
                if (!AudioSystem.isLineSupported(targetInfo)) {
                    println("Microphone not supported")
                    stop()
                    return
                }
                // Target data line captures the audio stream the microphone produces.
                targetDataLine = AudioSystem.getLine(targetInfo) as TargetDataLine
                targetDataLine!!.open(audioFormat)
                micThread.start()
                var startTime = System.currentTimeMillis()
                while (true) {
                    val estimatedTime = System.currentTimeMillis() - startTime
                    if (estimatedTime >= STREAMING_LIMIT) {
                        clientStream.closeSend()
                        referenceToStreamController!!.cancel() // remove Observer
                        if (resultEndTimeInMS > 0) {
                            finalRequestEndTime = isFinalEndTime
                        }
                        resultEndTimeInMS = 0
                        lastAudioInput = audioInput
                        audioInput = mutableListOf()
                        restartCounter++
                        if (!lastTranscriptWasFinal) {
                            print('\n')
                        }
                        newStream = true
                        clientStream = client.streamingRecognizeCallable().splitCall(responseObserver)
                        request = StreamingRecognizeRequest.newBuilder()
                                .setStreamingConfig(streamingRecognitionConfig)
                                .build()
                        println(YELLOW)
                        System.out.printf("%d: RESTARTING REQUEST\n", restartCounter * STREAMING_LIMIT)
                        startTime = System.currentTimeMillis()
                    } else {
                        if (newStream && lastAudioInput.size > 0) { // if this is the first audio from a new request
// calculate amount of unfinalized audio from last request
// resend the audio to the speech client before incoming audio
                            val chunkTime = STREAMING_LIMIT / lastAudioInput.size.toDouble()
                            // ms length of each chunk in previous request audio arrayList
                            if (chunkTime != 0.0) {
                                if (bridgingOffset < 0) { // bridging Offset accounts for time of resent audio
// calculated from last request
                                    bridgingOffset = 0.0
                                }
                                if (bridgingOffset > finalRequestEndTime) {
                                    bridgingOffset = finalRequestEndTime.toDouble()
                                }
                                val chunksFromMS = floor((finalRequestEndTime - bridgingOffset) / chunkTime).toInt()
                                // chunks from MS is number of chunks to resend
                                bridgingOffset = floor((lastAudioInput.size - chunksFromMS) * chunkTime)
                                // set bridging offset for next request
                                for (i in chunksFromMS until lastAudioInput.size) {
                                    request = StreamingRecognizeRequest.newBuilder().setAudioContent(lastAudioInput[i]).build()
                                    clientStream.send(request)
                                }
                            }
                            newStream = false
                        }
                        tempByteString = ByteString.copyFrom(sharedQueue.take())
                        request = StreamingRecognizeRequest.newBuilder()
                                .setAudioContent(tempByteString)
                                .build()
                        audioInput.add(tempByteString)
                    }
                    clientStream.send(request)
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}
