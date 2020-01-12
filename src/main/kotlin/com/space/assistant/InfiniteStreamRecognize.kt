package com.space.assistant

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

/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// [START speech_transcribe_infinite_streaming]
object InfiniteStreamRecognize {
    private const val STREAMING_LIMIT = 290000 // ~5 minutes
    const val RED = "\u001b[0;31m"
    const val GREEN = "\u001b[0;32m"
    const val YELLOW = "\u001b[0;33m"
    // Creating shared object
    @Volatile
    private var sharedQueue = LinkedBlockingQueue<Any?>()
    private var targetDataLine: TargetDataLine? = null
    private const val BYTES_PER_BUFFER = 6400 // buffer size in bytes
    private var restartCounter = 0
    private var audioInput = ArrayList<ByteString?>()
    private var lastAudioInput: ArrayList<ByteString?>? = ArrayList()
    private var resultEndTimeInMS = 0
    private var isFinalEndTime = 0
    private var finalRequestEndTime = 0
    private var newStream = true
    private var bridgingOffset = 0.0
    private var lastTranscriptWasFinal = false
    private var referenceToStreamController: StreamController? = null
    private var tempByteString: ByteString? = null
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            infiniteStreamingRecognize("en-US")
        } catch (e: Exception) {
            println("Exception caught: $e")
        }
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

    /** Performs infinite streaming speech recognition  */
    @Throws(Exception::class)
    fun infiniteStreamingRecognize(languageCode: String?) { // Microphone Input buffering
        class MicBuffer : Runnable {
            override fun run() {
                println(YELLOW)
                println("Start speaking...Press Ctrl-C to stop")
                targetDataLine!!.start()
                val data = ByteArray(BYTES_PER_BUFFER)
                while (targetDataLine!!.isOpen) {
                    try {
                        val numBytesRead = targetDataLine!!.read(data, 0, data.size)
                        if (numBytesRead <= 0 && targetDataLine!!.isOpen) {
                            continue
                        }
                        sharedQueue.put(data.clone())
                    } catch (e: InterruptedException) {
                        println("Microphone input buffering interrupted : " + e.message)
                    }
                }
            }
        }
        // Creating microphone input buffer thread
        val micrunnable = MicBuffer()
        val micThread = Thread(micrunnable)
        var responseObserver: ResponseObserver<StreamingRecognizeResponse?>? = null
        SpeechClient.create().use { client ->
            var clientStream: ClientStream<StreamingRecognizeRequest?>
            responseObserver = object : ResponseObserver<StreamingRecognizeResponse?> {
                var responses = ArrayList<StreamingRecognizeResponse>()
                override fun onStart(controller: StreamController) {
                    referenceToStreamController = controller
                }

                override fun onResponse(response: StreamingRecognizeResponse?) {
                    responses.add(response!!)
                    val result = response.resultsList[0]
                    val resultEndTime = result.resultEndTime
                    resultEndTimeInMS = (resultEndTime.seconds * 1000
                            + resultEndTime.nanos / 1000000).toInt()
                    val correctedTime = (resultEndTimeInMS - bridgingOffset
                            + STREAMING_LIMIT * restartCounter)
                    val alternative = result.alternativesList[0]
                    if (result.isFinal) {
                        print(GREEN)
                        print("\u001b[2K\r")
                        System.out.printf("%s: %s [confidence: %.2f]\n",
                                convertMillisToDate(correctedTime),
                                alternative.transcript,
                                alternative.confidence
                        )
                        isFinalEndTime = resultEndTimeInMS
                        lastTranscriptWasFinal = true
                    } else {
                        print(RED)
                        print("\u001b[2K\r")
                        System.out.printf("%s: %s", convertMillisToDate(correctedTime),
                                alternative.transcript
                        )
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
                    System.exit(0)
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
                        lastAudioInput = null
                        lastAudioInput = audioInput
                        audioInput = ArrayList()
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
                        if (newStream && lastAudioInput!!.size > 0) { // if this is the first audio from a new request
// calculate amount of unfinalized audio from last request
// resend the audio to the speech client before incoming audio
                            val chunkTime = STREAMING_LIMIT / lastAudioInput!!.size.toDouble()
                            // ms length of each chunk in previous request audio arrayList
                            if (chunkTime != 0.0) {
                                if (bridgingOffset < 0) { // bridging Offset accounts for time of resent audio
// calculated from last request
                                    bridgingOffset = 0.0
                                }
                                if (bridgingOffset > finalRequestEndTime) {
                                    bridgingOffset = finalRequestEndTime.toDouble()
                                }
                                val chunksFromMS = Math.floor((finalRequestEndTime
                                        - bridgingOffset) / chunkTime).toInt()
                                // chunks from MS is number of chunks to resend
                                bridgingOffset = floor((lastAudioInput!!.size
                                        - chunksFromMS) * chunkTime)
                                // set bridging offset for next request
                                for (i in chunksFromMS until lastAudioInput!!.size) {
                                    request = StreamingRecognizeRequest.newBuilder()
                                            .setAudioContent(lastAudioInput!![i])
                                            .build()
                                    clientStream.send(request)
                                }
                            }
                            newStream = false
                        }
                        tempByteString = ByteString.copyFrom(sharedQueue.take() as ByteArray)
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