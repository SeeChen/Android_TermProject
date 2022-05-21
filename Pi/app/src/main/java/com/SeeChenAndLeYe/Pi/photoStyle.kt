package com.SeeChenAndLeYe.Pi

import android.graphics.Bitmap
import android.graphics.Color

class photoStyle {

    fun styleOne(sourceBitmap: Bitmap, size: Int): Bitmap? {

        if (sourceBitmap.width == 0 || sourceBitmap.height == 0 || sourceBitmap.isRecycled) {
            return null
        }

        val width = sourceBitmap.width
        val height = sourceBitmap.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val rows = width / size
        val cols = height / size
        val blocks = IntArray(rows * cols)
        for (i in 0..rows) {
            for (j in 0..cols) {
                var length = blocks.size
                var flag = 0
                if (i == rows && j != cols) {
                    length = 100
                    if (length == 0) {
                        break
                    }
                    sourceBitmap.getPixels(
                        blocks,
                        0,
                        size,
                        i * size,
                        j * size,
                        width - i * size,
                        size
                    )
                    flag = 1

                } else if (i != rows && j == cols) {
                    length = (height - j * size) * size
                    if (length == 0) {
                        break
                    }
                    sourceBitmap.getPixels(
                        blocks,
                        0,
                        size,
                        i * size,
                        j * size,
                        size,
                        height - j * size
                    )
                    flag = 2
                } else if (i == rows && j == cols) {
                    length = (width - i * size) * (height - j * size)
                    if (length == 0) {
                        break
                    }
                    sourceBitmap.getPixels(
                        blocks,
                        0,
                        size,
                        i * size,
                        j * size,
                        width - i * size,
                        height - j * size
                    )
                    flag = 3
                } else {
                    sourceBitmap.getPixels(blocks, 0, size, i * size, j * size, size, size)
                }
                var r = 0
                var g = 0
                var b = 0
                var a = 0

                for (k in 0 until length) {
                    r += Color.red(blocks[k])
                    g += Color.green(blocks[k])
                    b += Color.blue(blocks[k])
                    a += Color.alpha(blocks[k])
                }

                var color = Color.argb(a / length, r / length, g / length, b / length)
                for (k in 0 until length) {
                    blocks[k] = color
                }

                if (flag == 1) {
                    bitmap.setPixels(
                        blocks,
                        0,
                        width - i * size,
                        i * size,
                        j * size,
                        width - i * size,
                        size
                    )
                } else if (flag == 2) {
                    bitmap.setPixels(blocks, 0, size, i * size, j * size, size, height - j * size)
                } else if (flag == 3) {
                    bitmap.setPixels(
                        blocks,
                        0,
                        size,
                        i * size,
                        j * size,
                        width - i * size,
                        height - j * size
                    )
                } else {
                    bitmap.setPixels(blocks, 0, size, i * size, j * size, size, size)
                }
            }
        }
        return bitmap
    }

    fun styleTwo(sourceBitmap: Bitmap): Bitmap? {
        val bm = sourceBitmap
        val width = bm.width
        val height = bm.height
        val size = width * height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        var newPixels = IntArray(size)
        var oldPixels = IntArray(size)

        bm.getPixels(oldPixels, 0, width, 0, 0, width, height)

        for (i in 1..size - 1) {
            //前一个像素
            val preColor = oldPixels[i - 1]
            var preA = Color.alpha(preColor)
            var preR = Color.red(preColor)
            var preG = Color.green(preColor)
            var preB = Color.blue(preColor)

            //后一个像素
            val nextColor = oldPixels[i]
            val newA = Color.alpha(nextColor)
            val newR = Color.red(nextColor)
            val newG = Color.green(nextColor)
            val newB = Color.blue(nextColor)

            preA = (preA - newA + 127)
            preR = (preR - newR + 127)
            preG = (preG - newG + 127)
            preB = (preB - newB + 127)

            if (preA > 255) {
                preA = 255
            }
            if (preR > 255) {
                preR = 255
            }
            if (preG > 255) {
                preG = 255
            }
            if (preB > 255) {
                preB = 255
            }

            newPixels[i] = Color.argb(preA, preR, preG, preB)

        }

        bmp.setPixels(newPixels, 0, width, 0, 0, width, height)

        return bmp
    }
}