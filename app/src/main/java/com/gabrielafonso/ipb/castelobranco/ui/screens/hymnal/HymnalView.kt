// `app/src/main/java/com/gabrielafonso/ipb/castelobranco/ui/screens/hymnal/HymnalView.kt`
package com.gabrielafonso.ipb.castelobranco.ui.screens.hymnal

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielafonso.ipb.castelobranco.R
import com.gabrielafonso.ipb.castelobranco.domain.model.Hymn
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseScreen

@Composable
fun HymnalView(
    viewModel: HymnalViewModel,
    onHymnClick: (String) -> Unit
) {
    val activity = LocalActivity.current ?: LocalContext.current.findActivity()
    val hymnal by viewModel.hymnal.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }

    val filtered = remember(query, hymnal) {
        val q = query.trim()
        if (q.isBlank()) hymnal
        else hymnal.filter { hymn ->
            hymn.number.contains(q, ignoreCase = true) ||
                    hymn.title.contains(q, ignoreCase = true) ||
                    hymn.lyrics.any { it.text.contains(q, ignoreCase = true) }
        }
    }

    BaseScreen(
        tabName = "Hinário",
        logo = painterResource(id = R.drawable.sarca_ipb),
        showBackArrow = true,
        onBackClick = { activity?.finish() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            SearchCard(
                query = query,
                onQueryChange = { query = it },
                resultsCount = filtered.size,
                onSearchClick = { /* opcional */ },
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered, key = { it.number }) { item ->
                    HymnRow(
                        item = item,
                        onClick = { onHymnClick(item.number) }
                    )
                    Divider(color = Color(0xFFE7E7E7))
                }
            }
        }
    }
}

@Composable
private fun SearchCard(
    query: String,
    onQueryChange: (String) -> Unit,
    resultsCount: Int,
    onSearchClick: () -> Unit,
) {
    val cardBg = Color(0xFFE9E9E9)
    val green = Color(0xFF0F6B5C)
    val blue = Color(0xFF38A6D8)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    singleLine = true,
                    placeholder = { Text(text = "") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color(0xFFBEBEBE),
                        unfocusedBorderColor = Color(0xFFBEBEBE),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.size(12.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                            .clickable(onClick = onSearchClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Pesquisar",
                            tint = green,
                            modifier = Modifier.size(30.dp)
                        )
                    }


                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Resultados:  $resultsCount",
                color = Color(0xFF2F7E6F),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun HymnRow(
    item: Hymn,
    onClick: () -> Unit
) {
    val green = Color(0xFF0F6B5C)
    val orange = Color(0xFFF2A300)

    val preview = remember(item) {
        item.lyrics.firstOrNull()?.text
            ?.replace("\n", " ")
            ?.trim()
            .orEmpty()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(width = 4.dp, height = 34.dp)
                .background(orange, RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${item.number} \u2022 ${item.title}",
                color = green,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = preview,
                color = Color(0xFF7A7A7A),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
