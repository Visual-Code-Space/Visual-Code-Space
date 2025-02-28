/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.compose.ui.graphics

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.createBitmap
import com.caverock.androidsvg.SVG
import kotlin.math.max

@Composable
fun rememberSvgAssetImageBitmap(path: String): ImageBitmap {
    val context = LocalContext.current
    return remember(path) {
        val svg = SVG.getFromAsset(context.assets, path)
        getBitmapFromSvg(svg).asImageBitmap()
    }
}

private fun getBitmapFromSvg(svg: SVG): Bitmap {
    val bitmap = createBitmap(max(svg.documentWidth.toInt(), 24), max(svg.documentHeight.toInt(), 24))
    svg.renderToCanvas(Canvas(bitmap))
    return bitmap
}
