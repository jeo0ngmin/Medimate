package com.example.medimate.tts

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlin.collections.ArrayDeque
import java.util.Locale

private data class TTSStep(val text: String? = null, val pauseMs: Long = 0L)

object TTSHelper {
    internal lateinit var appContext: Context
        private set

    private var tts: TextToSpeech? = null
    private var ready = false
    private val mainHandler = Handler(Looper.getMainLooper())

    private val queue = ArrayDeque<TTSStep>()
    private var isPlaying = false
    private var currentId: String? = null

    private var autoFocus = true
    private var focusTaken = false
    private var focusRequest: AudioFocusRequest? = null

    fun init(context: Context, onReady: (() -> Unit)? = null) {
        appContext = context.applicationContext
        if (tts != null) { if (ready) onReady?.invoke(); return }

        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val r = tts?.setLanguage(Locale.KOREAN)
                ready = r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onError(utteranceId: String?) { if (utteranceId == currentId) mainHandler.post { playNext() } }
                    override fun onDone(utteranceId: String?) { if (utteranceId == currentId) mainHandler.post { playNext() } }
                })

                if (ready && queue.isNotEmpty() && !isPlaying) playNext()
                if (ready) onReady?.invoke()
            } else ready = false
        }
    }

    fun enableAutoFocus(enable: Boolean) { autoFocus = enable }

    fun setVoice(rate: Float = 1.0f, pitch: Float = 1.0f) {
        tts?.setSpeechRate(rate)
        tts?.setPitch(pitch)
    }

    fun speakFlush(context: Context, text: String) {
        ensureInit(context)
        queue.clear()
        queue.add(TTSStep(text = text))
        if (ready && !isPlaying) playNext()
    }

    fun speakAdd(context: Context, text: String) {
        ensureInit(context)
        queue.add(TTSStep(text = text))
        if (ready && !isPlaying) playNext()
    }

    fun pause(ms: Long) {
        queue.add(TTSStep(pauseMs = ms.coerceAtLeast(0)))
    }

    fun shutdown(context: Context? = null) {
        if (autoFocus && focusTaken && context != null) abandonFocus(context)
        queue.clear()
        isPlaying = false
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
        currentId = null
    }

    private fun ensureInit(context: Context) {
        if (tts == null) init(context.applicationContext)
    }

    private fun playNext() {
        val step = if (queue.isEmpty()) null else queue.removeFirst()
        if (step == null) {
            isPlaying = false
            if (autoFocus && focusTaken) abandonFocus(appContext)
            return
        }

        isPlaying = true

        if (step.pauseMs > 0) {
            mainHandler.postDelayed({ playNext() }, step.pauseMs)
            return
        }

        if (autoFocus && !focusTaken) requestFocus(appContext)

        val id = System.nanoTime().toString()
        currentId = id
        tts?.speak(step.text ?: "", TextToSpeech.QUEUE_FLUSH, Bundle(), id)
    }

    private fun requestFocus(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(attrs).setOnAudioFocusChangeListener { }.build()
            focusTaken = am.requestAudioFocus(focusRequest!!) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            focusTaken = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    private fun abandonFocus(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { am.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            am.abandonAudioFocus(null)
        }
        focusTaken = false
    }
}
