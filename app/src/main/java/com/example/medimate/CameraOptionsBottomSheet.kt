// CameraOptionsBottomSheet.kt
package com.example.medimate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.medimate.databinding.BottomSheetCameraOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CameraOptionsBottomSheet : BottomSheetDialogFragment() {

    // --- ▼▼ 1. 통신을 위한 인터페이스와 리스너 변수 추가 ▼▼ ---
    interface CameraOptionsListener {
        fun onTakePhotoClicked()
        fun onOpenGalleryClicked()
    }
    private var listener: CameraOptionsListener? = null
    fun setListener(listener: CameraOptionsListener) {
        this.listener = listener
    }
    // --- ▲▲ 여기까지 ▲▲ ---

    private var _binding: BottomSheetCameraOptionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCameraOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCloseSheet.setOnClickListener { dismiss() }

        // --- ▼▼ 2. Toast 메시지 대신 리스너 호출로 변경 ▼▼ ---
        binding.btnTakePhoto.setOnClickListener {
            listener?.onTakePhotoClicked() // HomeActivity에 "사진 촬영" 버튼 눌렸다고 알림
            dismiss()
        }

        binding.btnOpenGallery.setOnClickListener {
            listener?.onOpenGalleryClicked() // HomeActivity에 "앨범 선택" 버튼 눌렸다고 알림
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CameraOptionsBottomSheet"
    }
}