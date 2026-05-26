package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.OrderEntity
import com.example.ui.viewmodel.TailorViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderNumber: String,
    viewModel: TailorViewModel,
    onNavigateBack: () -> Unit
) {
    val order by viewModel.getOrderDetails(orderNumber).collectAsState(initial = null)
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Order Details", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val orderDetails = order!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Highlighted ID and Customer Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "ORDER #${orderDetails.orderNumber}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("detail_order_number_badge")
                                )
                            }
                            if (orderDetails.balanceAmount > 0.0) {
                                Surface(
                                    color = Color(0xFFF9DEDC),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "UNPAID",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8C1D18),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            } else {
                                Surface(
                                    color = Color(0xFFE6F4EA),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "PAID IN FULL",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF137333),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = orderDetails.customerName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.testTag("detail_customer_name")
                        )

                        // Info details
                        DetailMetaRow(
                            icon = Icons.Default.Phone, 
                            text = orderDetails.mobileNumber, 
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            testTag = "detail_customer_phone"
                        )
                        if (!orderDetails.address.isNullOrBlank()) {
                            DetailMetaRow(
                                icon = Icons.Default.LocationOn, 
                                text = orderDetails.address,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Dates Timetable Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE6E1E5))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("GIVEN DATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF49454F))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, size = 14.dp, tint = Color(0xFF49454F))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(orderDetails.givenDate, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B1E))
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("DELIVERY DUE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF49454F))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, size = 14.dp, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(orderDetails.deliveryDate, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                // Payment summary section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE6E1E5))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Payment Ledger", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PaymentStatItem(label = "Total Amount", amount = orderDetails.totalAmount)
                            PaymentStatItem(label = "Advance Paid", amount = orderDetails.advancePaid)
                            PaymentStatItem(
                                label = "Balance Due",
                                amount = orderDetails.balanceAmount,
                                highlightError = orderDetails.balanceAmount > 0.0
                            )
                        }
                    }
                }

                // MEASUREMENTS SECTION
                val shirtList = getShirtList(orderDetails)
                val pantList = getPantList(orderDetails)

                if (shirtList.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Color(0xFFE6E1E5))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Shirt Measurements", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                shirtList.forEach { (label, value) ->
                                    MeasurementDetailRow(label = label, value = value)
                                }
                            }
                        }
                    }
                }

                if (pantList.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Color(0xFFE6E1E5))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Pant Measurements", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                pantList.forEach { (label, value) ->
                                    MeasurementDetailRow(label = label, value = value)
                                }
                            }
                        }
                    }
                }

                if (shirtList.isEmpty() && pantList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3EDF7), RoundedCornerShape(24.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No measurements recorded for this customer", fontSize = 13.sp, color = Color(0xFF49454F))
                    }
                }

                // Images section
                if (orderDetails.images.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Color(0xFFE6E1E5))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Cloth Images", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(orderDetails.images) { path ->
                                    Box(
                                        modifier = Modifier
                                            .size(140.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .border(1.dp, Color(0xFFE6E1E5), RoundedCornerShape(16.dp))
                                    ) {
                                        AsyncImage(
                                            model = File(path),
                                            contentDescription = "Stored cloth snapshot",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailMetaRow(icon: ImageVector, text: String, color: Color = Color(0xFF49454F), testTag: String = "") {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier
    ) {
        Icon(icon, contentDescription = null, size = 16.dp, tint = color)
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun Modifier.size(size: Int): Modifier = this.then(Modifier.size(size.dp))

@Composable
fun Icon(imageVector: ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.Dp, tint: Color) {
    Icon(imageVector, contentDescription, modifier = Modifier.size(size), tint = tint)
}

@Composable
fun PaymentStatItem(
    label: String,
    amount: Double,
    highlightError: Boolean = false
) {
    Column {
        Text(label, fontSize = 11.sp, color = Color(0xFF49454F), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "₹${amount.toInt()}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = if (highlightError) Color(0xFFB3261E) else Color(0xFF2E7D32)
        )
    }
}

@Composable
fun MeasurementDetailRow(label: String, value: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF49454F))
        Text(text = "$value \"", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B1E))
    }
}

// Logic to parse and filter NON-EMPTY fields
private fun getShirtList(order: OrderEntity): List<Pair<String, Double>> {
    val list = mutableListOf<Pair<String, Double>>()
    order.shirtChest?.let { list.add("Chest" to it) }
    order.shirtShoulder?.let { list.add("Shoulder" to it) }
    order.shirtSleeve?.let { list.add("Sleeve" to it) }
    order.shirtLength?.let { list.add("Length" to it) }
    order.shirtNeck?.let { list.add("Neck" to it) }
    return list
}

private fun getPantList(order: OrderEntity): List<Pair<String, Double>> {
    val list = mutableListOf<Pair<String, Double>>()
    order.pantWaist?.let { list.add("Waist" to it) }
    order.pantHip?.let { list.add("Hip" to it) }
    order.pantThigh?.let { list.add("Thigh" to it) }
    order.pantKnee?.let { list.add("Knee" to it) }
    order.pantBottom?.let { list.add("Bottom" to it) }
    order.pantLength?.let { list.add("Length" to it) }
    return list
}
