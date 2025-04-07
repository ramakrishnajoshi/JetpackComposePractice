package com.example.jetpackcomposepractice

/*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposepractice.ui.theme.JetpackComposePracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackComposePracticeTheme {
                BuildScaffold()
            }
        }
    }
}

@Composable
fun BuildScaffold() {
    */
/*Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
        )
    }*//*


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Greeting("Android")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val items = listOf("Android", "iOS", "Windows")
    Column(
        modifier = modifier
            .padding(15.dp)
            .background(color = Color.Gray)
            .padding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (item in items) {
            Row {
                Text(
                    text = "Hello $item!",
                    modifier =
                        modifier
                            .padding(24.dp)
                            .background(color = Color.Black)
                )
                ElevatedButton(onClick = {}) {
                    Text(text = "Click Me")
                }
            }

        }
    }
}

*/
/*@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackComposePracticeTheme {
        Greeting("Android")
    }
}*//*


@Composable
@Preview(showBackground = true, widthDp = 500)
fun PreviewApp() {
    JetpackComposePracticeTheme {
        BuildScaffold()
    }
}

@Preview
@Composable
fun ShowList(names : List<String> = listOf("Android", "iOS", "Windows")) {
    JetpackComposePracticeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary
        ) {
            LazyColumn {
                items(items = names) { item: String ->
                    Text(text = item)
                }
            }
        }
    }
}*/

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.jetpackcomposepractice.ui.theme.JetpackComposePracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposePracticeTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun MyApp(modifier: Modifier = Modifier) {

    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }

    Surface(modifier) {
        if (shouldShowOnboarding) {
            OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
        } else {
            Greetings()
        }
    }
}

@Composable
fun OnboardingScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Basics Codelab!")
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onContinueClicked
        ) {
            Text("Continue")
        }
    }

}

@Composable
private fun Greetings(
    modifier: Modifier = Modifier,
    names: List<String> = List(1000) { "$it" }
) {
    LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
        items(items = names) { name ->
            Greeting(name = name)
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    JetpackComposePracticeTheme {
        OnboardingScreen(onContinueClicked = {})
    }
}

@Composable
private fun Greeting(name: String, modifier: Modifier = Modifier) {

    var expanded by rememberSaveable { mutableStateOf(false) }

    val extraPadding by animateDpAsState(
        if (expanded) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(bottom = extraPadding.coerceAtLeast(0.dp))
            ) {
                Text(text = "Hello, ")
                Text(text = name)
            }
            ElevatedButton(
                onClick = { expanded = !expanded }
            ) {
                Text(if (expanded) "Show less" else "Show more")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    JetpackComposePracticeTheme {
        Greetings()
    }
}

@Preview( uiMode = Configuration.UI_MODE_NIGHT_YES,)
@Composable
fun MyAppPreview() {
    JetpackComposePracticeTheme {
        MyApp(Modifier.fillMaxSize())
    }
}


@Preview(showBackground = true, widthDp = 250, heightDp = 500)
@Composable
fun ColumnALignmentPreview() {
    JetpackComposePracticeTheme {
        Box(
            modifier = Modifier.size(200.dp).background(Color.LightGray),
            contentAlignment = Alignment.Center // Center children by default
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_android_black_24dp),
                contentDescription = null,
                modifier = Modifier.matchParentSize() // Fill the Box
            )
            Text(
                "Text Over Image",
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp) // Override default alignment
            )
            CircularProgressIndicator(modifier = Modifier.align(Alignment.TopEnd)) // Top-right corner
        }
    }
}
@Composable
//@Preview(showBackground = true, widthDp = 350, heightDp = 700)
fun ColumnAlignment() {
//    Column {
        Column(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),

        ) {
            Text(
                text = "Hello Column, Hello Column",
                modifier = Modifier.background(color = Color.Gray)
            )
            Text(
                text = "Hello Column",
                modifier = Modifier.background(color = Color.Cyan)
            )
        }
        /*Row(
            modifier = Modifier.background(color = Color.Blue)
        ) {
            Text(
                text = "Hello Row, Hello Row",
                modifier = Modifier.background(color = Color.Gray)
            )
            Spacer(modifier = Modifier.size(size = 12.dp))
            Text(
                text = "Hello Row",
                modifier = Modifier.background(color = Color.Cyan)
            )
        }*/
   //}
}


@Composable
@Preview
fun testingConstraintLayout() {

    val isClicked = remember {
        mutableStateOf<Boolean>(true)
    }
    ConstraintLayout {
        val (text1, text2, button1) = createRef();

        Text(
            text = "This is Text 1",
            modifier = Modifier.constrainAs(text1) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )
        Text(
            text = "This is text 2",
            modifier = Modifier.constrainAs(text2) {
                top.linkTo(parent.top)
                start.linkTo(text1.top)
            }
        )
        Button(
            onClick = {},
            modifier = Modifier.constrainAs(button1) {
                top.linkTo(text1.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Text(
                text = "This is button"
            )
        }
    }
}