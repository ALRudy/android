package com.example.digi_move

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.support.media.ExifInterface
import android.util.Log

import java.io.IOException

/**
 * Created by User on 10/29/2017.
 */

class BitmapRotate {

    private var mContext: Context? = null

    @Throws(IOException::class)
    fun HandleSamplingAndRotationBitmap(context: Context, selectedImage: Uri): Bitmap? {
        mContext = context
        val MAX_HEIGHT = 1024
        val MAX_WIDTH = 1024

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var imageStream = context.contentResolver.openInputStream(selectedImage)
        BitmapFactory.decodeStream(imageStream, null, options)
        imageStream!!.close()

        // Calculate inSampleSize
        options.inSampleSize =
            calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        imageStream = context.contentResolver.openInputStream(selectedImage)
        var img = BitmapFactory.decodeStream(imageStream, null, options)

        img = rotateImageIfRequired(img, selectedImage)
        return img
    }

    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    @Throws(IOException::class)
    private fun rotateImageIfRequired(img: Bitmap?, selectedImage: Uri): Bitmap? {

        val input = mContext!!.contentResolver.openInputStream(selectedImage)
        val ei: ExifInterface
        try {
            ei = ExifInterface(input!!)

            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return rotateImage(
                    img,
                    90
                )
                ExifInterface.ORIENTATION_ROTATE_180 -> return rotateImage(
                    img,
                    180
                )
                ExifInterface.ORIENTATION_ROTATE_270 -> return rotateImage(
                    img,
                    270
                )
                else -> return img
            }
        } catch (e: NullPointerException) {
            Log.e(TAG, "rotateImageIfRequired: Could not read file." + e.message)
        }

        return img
    }

    companion object {

        private val TAG = "RotateBitmap"

        /*
    ----------------------------- Image Rotation --------------------------------------------------
     */

        private fun rotateImage(img: Bitmap?, degree: Int): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            val rotatedImg = Bitmap.createBitmap(img!!, 0, 0, img.width, img.height, matrix, true)
            img.recycle()
            return rotatedImg
        }


        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int, reqHeight: Int
        ): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                // Calculate ratios of height and width to requested height and width
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

                // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
                // with both dimensions larger than or equal to the requested height and width.
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio

                // This offers some additional logic in case the image has a strange
                // aspect ratio. For example, a panorama may have a much larger
                // width than height. In these cases the total pixels might still
                // end up being too large to fit comfortably in memory, so we should
                // be more aggressive with sample down the image (=larger inSampleSize).

                val totalPixels = (width * height).toFloat()

                // Anything more than 2x the requested pixels we'll sample down further
                val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

                while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                    inSampleSize++
                }
            }
            return inSampleSize
        }
    }
}