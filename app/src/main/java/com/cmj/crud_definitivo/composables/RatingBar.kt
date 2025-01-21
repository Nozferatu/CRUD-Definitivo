package com.cmj.crud_definitivo.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarHalf
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Float = 0f,
    stars: Int = 5,
    canChangeRating: Boolean = false,
    onRatingChanged: (Float) -> Unit,
    starsColor: Color = Color.Yellow
) {
    var isHalfStar = (rating % 1) != 0f

    Row(modifier = modifier) {
        for (index in 1..stars) {
            Icon(
                imageVector =
                if (index <= rating) {
                    Icons.Rounded.Star
                } else {
                    if (isHalfStar) {
                        isHalfStar = false
                        Icons.Rounded.StarHalf
                    } else {
                        Icons.Rounded.StarOutline
                    }
                },
                contentDescription = null,
                tint = starsColor,
                modifier = modifier
                    .clickable {
                        if(canChangeRating){
                            onRatingChanged(index.toFloat())
                        }
                    }
            )
        }
    }
}