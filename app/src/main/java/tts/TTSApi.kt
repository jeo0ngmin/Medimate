package com.example.medimate.tts

import android.content.Context

object TTSApi {
    /** 앱 시작 시 1회 호출 (초기화 + 음색/속도 설정 + 자동 오디오 포커스 설정) */
    fun init(context: Context, rate: Float = 1.0f, pitch: Float = 1.0f, autoFocus: Boolean = true, onReady: (() -> Unit)? = null) {
        TTSHelper.enableAutoFocus(autoFocus)
        TTSHelper.init(context) {
            TTSHelper.setVoice(rate, pitch)
            onReady?.invoke()
        }
    }

    /** 이전 발화를 끊고 새 문장부터 */
    fun speak(text: String) {
        TTSHelper.speakFlush(requireNotNull(TTSHelper.appContext), text)
    }

    /** 뒤에 이어붙이기 */
    fun speakAdd(text: String) {
        TTSHelper.speakAdd(requireNotNull(TTSHelper.appContext), text)
    }

    /** 중간 쉬기(ms) */
    fun pause(ms: Long) {
        TTSHelper.pause(ms)
    }

    /** 시스템 미디어 볼륨 %로 설정 (0~100) */
    fun setVolumePercent(percent: Int) {
        VolumeHelper.setPercent(requireNotNull(TTSHelper.appContext), percent)
    }

    /** 정리(포커스 반납 포함) */
    fun shutdown() {
        TTSHelper.shutdown(TTSHelper.appContext)
    }
}
