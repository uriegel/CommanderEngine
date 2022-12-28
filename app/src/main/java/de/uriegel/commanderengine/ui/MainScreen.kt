package de.uriegel.commanderengine.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun MainScreen(start: ()->Unit) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text("Commander Engine")
        })
    },
        content = {
            ConstraintLayout(modifier =
            Modifier
                .padding(it)
                .fillMaxWidth()
                .fillMaxHeight()
            ) {
                val (buttonStart, buttonStop) = createRefs()

                Button(
                    onClick = { start() },
                    modifier = Modifier.constrainAs(buttonStart) {
                        top.linkTo(parent.top)
                        bottom.linkTo(buttonStop.top)
                        centerHorizontallyTo(parent)
                    }
                ) {
                    Text("Start")
                }
                Button(
                    onClick = {},
                    modifier = Modifier.constrainAs(buttonStop) {
                        top.linkTo(buttonStart.bottom)
                        bottom.linkTo(parent.bottom)
                        centerHorizontallyTo(parent)
                    }
                ) {
                    Text("Stop")
                }
            }
        }
    )
}
