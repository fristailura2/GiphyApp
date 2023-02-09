package com.test.giphyapp.presentation.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    internetIndicatorSize: Dp = 24.dp,
    internetIndicatorAvailableColor: Color = Color.Green,
    internetIndicatorUnAvailableColor: Color = Color.Red,
    searchText: String,
    hasInternetConnection: Boolean,
    listener: (String) -> Unit
) {
    TextField(
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        modifier = modifier,
        trailingIcon = {
            Box(
                modifier = Modifier
                    .size(internetIndicatorSize)
                    .background(
                        color = if (hasInternetConnection)
                            internetIndicatorAvailableColor
                        else
                            internetIndicatorUnAvailableColor,
                        shape = CircleShape
                    )
            )
        },
        value = searchText,
        onValueChange = listener
    )
}
