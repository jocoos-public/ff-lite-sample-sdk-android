package com.jocoos.flipflop.sample.utils

import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

fun applyOptionsForRoundedCorners(
    radius: Int,
    cornerType: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL
): RequestOptions {
    val multi = MultiTransformation(
        CenterCrop(),
        RoundedCornersTransformation(
            radius,
            0,
            cornerType)
    )
    return RequestOptions.bitmapTransform(multi)
}
