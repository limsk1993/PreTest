package com.sumkim.pretest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sumkim.pretest.Extensions.collectWithLifecycle
import com.sumkim.pretest.response.SectionInfo
import com.sumkim.pretest.response.SectionProduct
import com.sumkim.pretest.ui.theme.PreTestTheme
import com.sumkim.pretest.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PreTestTheme {
                MainRoute()
            }
        }
    }
}

@Composable
fun MainRoute(
    vm: MainViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        if (vm.ensureInit()) return@LaunchedEffect
        vm.getSection()
    }

    val context = LocalContext.current
    val isLoading by vm.isLoading.collectAsStateWithLifecycle()
    val sectionInfos by vm.sectionInfos.collectAsStateWithLifecycle()
    val sectionProducts by vm.sectionProducts.collectAsStateWithLifecycle()
    val wishedIds by vm.wishedIds.collectAsStateWithLifecycle()
    vm.eventChannel.collectWithLifecycle {
        when (it) {
            is MainEvent.Toast -> {
                if (it.msg != null) Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        MainScreen(
            modifier = Modifier.padding(innerPadding),
            sectionInfos = sectionInfos ?: listOf(),
            sectionProducts = sectionProducts ?: mapOf(),
            wishedIds = wishedIds,
            clickWished = vm::toggleWish,
            isRefreshing = isLoading,
            onRefresh = vm::refresh
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    sectionInfos: List<SectionInfo>,
    sectionProducts: Map<Int, List<SectionProduct>>,
    wishedIds: Set<String>,
    clickWished: (String) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .pullToRefresh(
                state = refreshState,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(sectionInfos) { index, section ->
                val items = sectionProducts[section.id] ?: emptyList()
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    if (index > 0) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color.Gray))
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        text = section.title ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    when (section.type) {
                        "horizontal" -> HorizontalProductList(items, wishedIds, clickWished)
                        "vertical" -> VerticalProductList(items, wishedIds, clickWished)
                        "grid" -> GridProductList(items, wishedIds, clickWished)
                    }
                }
            }
        }

        PullToRefreshDefaults.Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = refreshState
        )
    }
}

@Composable
fun HorizontalProductList(
    items: List<SectionProduct>,
    wishedIds: Set<String>,
    clickWished: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            ProductCard(item, wishedIds, clickWished = clickWished)
        }
    }
}

@Composable
fun VerticalProductList(
    items: List<SectionProduct>,
    wishedIds: Set<String>,
    clickWished: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        items.forEach { item ->
            ProductCard(item, wishedIds, isVertical = true, clickWished = clickWished)
        }
    }
}

@Composable
fun GridProductList(
    items: List<SectionProduct>,
    wishedIds: Set<String>,
    clickWished: (String) -> Unit
) {
    val rows = items.chunked(3).take(2) // 3x2 고정

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        rows.forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { item ->
                    ProductCard(item, wishedIds, modifier = Modifier.weight(1f), clickWished = clickWished)
                }
                if (rowItems.size < 3) {
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    item: SectionProduct,
    wishedIds: Set<String>,
    modifier: Modifier = Modifier,
    isVertical: Boolean = false,
    clickWished: (String) -> Unit
) {
    val isWished = wishedIds.contains(item.id.toString())
    Column(
        modifier = if (isVertical) modifier else modifier
            .width(170.dp)
            .height(280.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            AsyncImage(
                model = item.image,
                contentDescription = item.name,
                modifier = if (isVertical) {
                    Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .aspectRatio(6f / 4f)
                        .clip(RoundedCornerShape(8.dp))
                } else {
                    Modifier
                        .align(Alignment.Center)
                        .width(150.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                },
                contentScale = ContentScale.Crop
            )
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(24.dp),
                onClick = { clickWished(item.id.toString()) }
            ) {
                Image(
                    painter = painterResource(
                        if (isWished) R.drawable.ic_btn_heart_on
                        else R.drawable.ic_btn_heart_off
                    ),
                    contentDescription = "찜하기",
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            item.name ?: "",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (isVertical) 1 else 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = buildAnnotatedString {
                    if (item.discountedPrice != null) {
                        withStyle(style = SpanStyle(color = Color(0xFFFA622F))) {
                            append("${calculateDiscountRate(item.discountedPrice, item.originalPrice ?: 0)}%")
                        }
                        append(" ")
                    }
                    append(if (item.discountedPrice != null) "${item.discountedPrice}원" else "${item.originalPrice}원")
                },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
            if (item.discountedPrice != null && isVertical) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${item.originalPrice}원",
                    style = MaterialTheme.typography.labelSmall,
                    textDecoration = TextDecoration.LineThrough,
                    color = Color.Gray
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (item.discountedPrice != null && !isVertical) {
                Text(
                    text = "${item.originalPrice}원",
                    style = MaterialTheme.typography.labelSmall,
                    textDecoration = TextDecoration.LineThrough,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            if (item.isSoldOut == true) {
                Text(
                    "품절",
                    color = Color.Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
    }
}

fun calculateDiscountRate(discountedPrice: Int, originalPrice: Int): Int {
    return round(((originalPrice - discountedPrice).toDouble() / originalPrice) * 100).toInt()
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    PreTestTheme {
        MainScreen(
            sectionInfos = listOf(),
            sectionProducts = mapOf(),
            wishedIds = setOf(),
            clickWished = {},
            isRefreshing = false,
            onRefresh = {}
        )
    }
}