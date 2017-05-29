package com.saladevs.changelogclone.utils

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.widget.ImageView


fun ImageView.setDisabled(b: Boolean) {
    if (b) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        val cf = ColorMatrixColorFilter(matrix)
        this.setColorFilter(cf)
        this.setImageAlpha(128)
    } else {
        this.setColorFilter(null)
        this.setImageAlpha(255)
    }

}