package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderEntity
import com.example.ui.viewmodel.TailorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TailorViewModel,
    onNavigateToAddOrder: () -> Unit,
    onNavigateToOrderDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val orders by viewModel.filteredOrders.collectAsState()
    val user by viewModel.loggedUser.collectAsState()

    var selectedFilter by remember { mutableStateOf("All Orders") }

    // Dynamic Filter Categories Chips: "All Orders", "Pending", "Ready", "Payment Due"
    val categories = listOf("All Orders", "Pending", "Ready", "Payment Due")

    val initials = remember(user) {
        user?.name?.trim()?.split("\\s+".toRegex())
            ?.mapNotNull { it.firstOrNull() }
            ?.take(2)
            ?.joinToString("")
            ?.uppercase() ?: "JD"
    }

    val filteredOrdersList = remember(orders, selectedFilter) {
        when (selectedFilter) {
            "Pending" -> orders.filter { it.balanceAmount > 0.0 }
            "Ready" -> orders.filter { it.balanceAmount == 0.0 }
            "Payment Due" -> orders.filter { it.balanceAmount > 0.0 }
            else -> orders
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddOrder,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .testTag("add_order_fab")
                    .padding(bottom = 12.dp, end = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Order")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // App Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kolathala Book",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Tailor Notebook",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Profile Avatar Initials Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEADDFF))
                        .border(1.dp, Color(0xFFD0BCFF), CircleShape)
                        .clickable { onNavigateToProfile() }
                        .testTag("profile_icon_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color(0xFF21005D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Search Bar Input with high custom rounding of 28.dp matching HTML
            TextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search Name, Mobile or Order #") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = Color(0xFF49454F)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 4.dp)
                    .testTag("search_bar"),
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF3EDF7),
                    unfocusedContainerColor = Color(0xFFF3EDF7),
                    disabledContainerColor = Color(0xFFF3EDF7),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
                    focusedPlaceholderColor = Color(0xFF49454F),
                    unfocusedPlaceholderColor = Color(0xFF49454F)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Filter Chips List Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedFilter == category
                    if (isSelected) {
                        Surface(
                            onClick = { selectedFilter = category },
                            color = Color(0xFFEADDFF),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF21005D)
                                )
                            }
                        }
                    } else {
                        Surface(
                            onClick = { selectedFilter = category },
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFF79747E)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF49454F)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENTLY ADDED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = Color(0xFF49454F)
                )
                Text(
                    text = "${filteredOrdersList.size} Orders",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredOrdersList.isEmpty()) {
                // Friendly Elegant Empty State UI
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = Color(0xFFE6E1E5),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No orders found" else "No orders recorded yet",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "Try refining your search text" else "Tap '+' button below to save order details.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("orders_list"),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(filteredOrdersList, key = { it.orderNumber }) { order ->
                        OrderCardItem(
                            order = order,
                            onCardClick = { onNavigateToOrderDetail(order.orderNumber) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCardItem(
    order: OrderEntity,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("order_card_${order.orderNumber}"),
        shape = RoundedCornerShape(24.dp), // Styled as rounded-[24px] in HTML
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFE6E1E5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = order.customerName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B1E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // NEW/Badge Indicator style from HTML
                    Surface(
                        color = Color(0xFFF3EDF7),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "NEW",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF6750A4),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "ID: ${order.orderNumber}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = Color(0xFF49454F)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFF49454F),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Del: ${order.deliveryDate}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF49454F)
                    )
                }
            }

            // Price/Remaining LEDGER pill on right matching design HTML exactly
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                if (order.balanceAmount > 0.0) {
                    Surface(
                        color = Color(0xFFF2B8B5).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp) // rounded-full pill
                    ) {
                        Text(
                            text = "₹${order.balanceAmount.toInt()} Bal",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB3261E),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    Surface(
                        color = Color(0xFFC8E6C9).copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp) // rounded-full pill
                    ) {
                        Text(
                            text = "Ready",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
