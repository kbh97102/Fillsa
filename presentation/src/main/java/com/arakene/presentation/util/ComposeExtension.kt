package com.arakene.presentation.util

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arakene.presentation.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import androidx.core.graphics.scale

@Composable
fun Modifier.noEffectClickable(enable: Boolean = true, click: () -> Unit) = this.clickable(
    enabled = enable, onClick = click, interactionSource =
        remember { MutableInteractionSource() }, indication = null
)

@Composable
fun HandleViewEffect(
    effect: Flow<Effect>,
    lifecycleOwner: LifecycleOwner,
    compositionScope: CoroutineScope = rememberCoroutineScope(),
    effectHandler: suspend (Effect) -> Unit
) = LaunchedEffect(effect, lifecycleOwner) {

    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            effect
                .onEach {
                    effectHandler(it)
                }
                .launchIn(compositionScope)
        }
    }
}

val LocalSnackbarHost = compositionLocalOf { SnackbarHostState() }


fun copyToClipboard(
    context: Context,
    scope: CoroutineScope,
    clipBoard: Clipboard,
    snackbarHostState: SnackbarHostState,
    quote: String,
    author: String
) {
    scope.launch {
        val copyText = "$quote - $author"
        clipBoard.setClipEntry(ClipEntry(ClipData.newPlainText(copyText, copyText)))

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            snackbarHostState.showSnackbar(context.getString(R.string.copied))
        }
    }
}

fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Uri? {
    val filename = "quote_${System.currentTimeMillis()}.jpg"
    val fos: OutputStream

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyQuotes")
    }

    val contentResolver = context.contentResolver
    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        ?: return null

    fos = contentResolver.openOutputStream(uri) ?: return null
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    fos.flush()
    fos.close()

    return uri
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "shared_image_${System.currentTimeMillis()}.png")
    file.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }

    file.deleteOnExit()

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

fun uriToCacheFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile("temp", null, context.cacheDir)
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.deleteOnExit()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun shouldResizeBitmap(
    bitmap: Bitmap,
    maxSize: Long = 1 * 1024 * 1024 // 5MB
): Boolean {
    val originalWidth = bitmap.width
    val originalHeight = bitmap.height

    // Bitmap을 압축하지 않고 계산할 수 있는 최대 해상도 비율을 대략적으로 계산
    val originalSize = originalWidth * originalHeight * 4 // ARGB (4 bytes per pixel) 기준

    return originalSize >= maxSize
}

private fun calculateResizeRatio(
    bitmap: Bitmap,
    maxSize: Long = 1 * 1024 * 1024 // 5MB
): Float {
    // Bitmap을 JPEG로 압축했을 때의 크기를 예상할 수 없으므로, 해상도 비율을 계산해야 합니다.
    val originalWidth = bitmap.width
    val originalHeight = bitmap.height

    // Bitmap을 압축하지 않고 계산할 수 있는 최대 해상도 비율을 대략적으로 계산
    val originalSize = originalWidth * originalHeight * 4 // ARGB (4 bytes per pixel) 기준

    val ratio = Math.sqrt((maxSize / originalSize.toFloat()).toDouble()).toFloat()

    // 1보다 작은 비율을 반환하여 파일 크기를 줄입니다.
    return ratio.coerceIn(0.1f, 1f) // 너무 작은 비율을 방지하기 위해 최소 10% 크기로 제한
}

fun resizeImageToMaxSize(originalFile: File, cacheDir: File, maxSize: Long = 1 * 1024 * 1024): File? {
    return try {
        // 원본 이미지를 Bitmap으로 디코딩
        val originalBitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
        originalBitmap?.let { bitmap ->
            // 5MB 이하여서 라사이즈가 필요 없는 경우
            if (shouldResizeBitmap(bitmap).not()) {
                return originalFile
            }

            // 원본 이미지의 크기와 해상도를 기반으로 리사이즈 비율 계산
            val ratio = calculateResizeRatio(bitmap, maxSize)

            // 해상도 비율에 맞춰 리사이즈된 Bitmap 생성
            val resizedBitmap =
                bitmap.scale((bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt())

            // 리사이즈된 Bitmap을 임시 파일로 저장
            val name = "background_scaled.jpeg"
            val cacheFile = File(cacheDir, name)

            if (cacheFile.exists()) {
                cacheFile.delete()
            }

            val outputStream = FileOutputStream(cacheFile)

            // 100% 품질로 저장
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            cacheFile.deleteOnExit()

            // 리사이즈된 파일 반환
            cacheFile
        }
    } catch (e: Exception) {
        null
    }
}