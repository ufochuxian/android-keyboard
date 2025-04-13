package org.futo.inputmethod.theme

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.github.chrisbanes.photoview.PhotoView
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.databinding.FragmentPhotoEditBinding
import java.io.File

class PhotoEditFragment : BaseMviFragment<
        FragmentPhotoEditBinding,
        PhotoEditIntent,
        PhotoEditState,
        PhotoEditViewModel>() {

    companion object {
        fun newInstance(uri: String): PhotoEditFragment {
            return PhotoEditFragment().apply {
                arguments = bundleOf("uri" to uri)
            }
        }
    }

    override val viewModel: PhotoEditViewModel by viewModels()

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPhotoEditBinding.inflate(inflater, container, false)

    override fun setupViews() {
        val uri = Uri.parse(requireArguments().getString("uri"))
        binding.photoView.setImageURI(uri)
        binding.photoView.attacher.setAllowDragAtMinScale(true)

        // ✅ 设置缩放范围
        binding.photoView.minimumScale = 0.3f   // 原图比例，防止缩太小
        binding.photoView.maximumScale = 4.0f   // 可放大 4 倍，够用户拖动查看细节

        binding.btnNext.setOnClickListener {
            val croppedBitmap = cropToClipRect(binding.photoView, binding.clipOverlay)
            val croppedUri = saveBitmapToCache(croppedBitmap)
            viewModel.sendIntent(PhotoEditIntent.OnNextClicked(croppedUri))
        }

    }

    override fun render(state: PhotoEditState) {
        if (state.navigateToCustomize && state.editedUri != null) {
            parentFragmentManager.beginTransaction()
                .add(R.id.fragment_container, ThemeCustomizeFragment.newInstance(state.editedUri.toString())) // ✅ FIXED
                .addToBackStack(null)
                .commit()
        }
    }

    private fun saveBitmapToCache(bitmap: Bitmap): Uri {
        val file = File(requireContext().cacheDir, "cropped_${System.currentTimeMillis()}.png")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        return FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
    }


    fun cropToClipRect(photoView: PhotoView, overlay: ClipOverlayView): Bitmap {
        val drawable = photoView.drawable as? BitmapDrawable ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val originalBitmap = drawable.bitmap

        // 获取 PhotoView 的矩阵（缩放、平移）
        val displayMatrix = Matrix(photoView.imageMatrix)
        val inverseMatrix = Matrix()
        if (!displayMatrix.invert(inverseMatrix)) {
            // 矩阵不可逆，返回空图
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }

        // 获取裁剪框（在屏幕坐标系下）
        val clipRect = overlay.getClipRect()

        // 将裁剪框转换为原始图片坐标系（考虑缩放+平移）
        val srcPoints = floatArrayOf(
            clipRect.left, clipRect.top,
            clipRect.right, clipRect.bottom
        )
        inverseMatrix.mapPoints(srcPoints)

        val left = srcPoints[0].coerceIn(0f, originalBitmap.width.toFloat())
        val top = srcPoints[1].coerceIn(0f, originalBitmap.height.toFloat())
        val right = srcPoints[2].coerceIn(0f, originalBitmap.width.toFloat())
        val bottom = srcPoints[3].coerceIn(0f, originalBitmap.height.toFloat())

        val cropWidth = (right - left).toInt().coerceAtLeast(1)
        val cropHeight = (bottom - top).toInt().coerceAtLeast(1)

        return Bitmap.createBitmap(originalBitmap, left.toInt(), top.toInt(), cropWidth, cropHeight)
    }



}
