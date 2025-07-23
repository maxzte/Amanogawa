package id.anison.amanogawa

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex


//val TOP_APP_BAR_HEIGHT = 92.dp
val TOP_APP_BAR_HEIGHT = 64.dp

//val BOTTOM_APP_BAR_HEIGHT = 64.dp
val BOTTOM_APP_BAR_HEIGHT = 64.dp


@Composable
fun AScaffold(
    modifier: Modifier = Modifier,
    topBarContent: @Composable (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
    bottomBarContent: @Composable (() -> Unit)? = null,
) {
    val insets = WindowInsets.systemBars
    val topPadding = insets.asPaddingValues().calculateTopPadding()
    val bottomPadding = insets.asPaddingValues().calculateBottomPadding()

    val innerPadding = PaddingValues(
        top = topPadding + TOP_APP_BAR_HEIGHT,
        bottom = BOTTOM_APP_BAR_HEIGHT,
        start = BOTTOM_APP_BAR_HEIGHT,
    )
    Box(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            content(innerPadding)
        }

        if (topBarContent != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.TopCenter
            ) {
                topBarContent()
            }
        }

        if (bottomBarContent != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.BottomCenter
            ) {
                bottomBarContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
//@Preview
@Composable
fun TransparentBottomBarScreen() {
    val items = remember { List(50) { "Item $it" } }

    AScaffold(
        topBarContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TOP_APP_BAR_HEIGHT)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                    .zIndex(1f)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.White.copy(alpha = 0.6f))

                )

                TopAppBar(
                    title = { Text("Top") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }

        },
        content = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding()
                ),
            ) {
                items(items) {
                    Text(
                        text = it,
                        modifier = Modifier
                            .fillMaxWidth()
//                            .padding(16.dp)
                    )
                }
            }
        },
        bottomBarContent = {
            BottomAppBar(
                containerColor = Color.Gray.copy(alpha = 0.2f),
                modifier = Modifier.height(BOTTOM_APP_BAR_HEIGHT)
            ) {

            }
        }
    )
}

@Composable
fun TransparentBottomBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .navigationBarsPadding()
            .background(Color.Transparent)
            .drawWithContent {
                drawContent()
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            },
//            .padding(horizontal = 16.dp),
//            .navigationBarsPadding(), // додаємо padding тільки для gesture bar
        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.Person, contentDescription = "Profile")
        }
    }
}

@Composable
fun test1(
    items: List<String>,
    columns: Int = 3,
    modifier: Modifier = Modifier
) {
//    val items = remember { List(5000) { "Item $it" } }
        val spacing = 8.dp
        val itemModifier = Modifier
            .padding(spacing / 2)
            .fillMaxWidth() // будемо обмежувати ширину вручну нижче

        LazyColumn(modifier = modifier.padding(spacing)) {
            items(items.chunked(columns)) { rowItems ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    val itemWeight = 1f / columns
                    for (item in rowItems) {
                        Card(
                            modifier = itemModifier
                                .weight(itemWeight)
                                .aspectRatio(1f), // квадрат
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item)
                            }
                        }
                    }

                    // Якщо останній рядок неповний — заповнюємо порожніми боксами
                    if (rowItems.size < columns) {
                        repeat(columns - rowItems.size) {
                            Spacer(modifier = Modifier.weight(itemWeight))
                        }
                    }
                }
            }
        }
    }
