package com.example.ui.screens

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.ui.viewmodel.TailorViewModel
import com.example.utils.ImageUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderScreen(
    viewModel: TailorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Screen states
    var customerName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Date Format Helper
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val today = remember { Date() }
    val calendar = remember { Calendar.getInstance() }
    
    var givenDate by remember { mutableStateOf(dateFormatter.format(today)) }
    
    // Default delivery date to 7 days from today
    val defaultDelivery = remember {
        calendar.time = today
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        calendar.time
    }
    var deliveryDate by remember { mutableStateOf(dateFormatter.format(defaultDelivery)) }

    // Payments
    var totalAmountText by remember { mutableStateOf("") }
    var advancePaidText by remember { mutableStateOf("") }

    val totalAmount = totalAmountText.toDoubleOrNull() ?: 0.0
    val advancePaid = advancePaidText.toDoubleOrNull() ?: 0.0
    val balanceAmount = if (totalAmount - advancePaid < 0.0) 0.0 else (totalAmount - advancePaid)

    // Shirt Measurements
    var shirtChest by remember { mutableStateOf("") }
    var shirtShoulder by remember { mutableStateOf("") }
    var shirtSleeve by remember { mutableStateOf("") }
    var shirtLength by remember { mutableStateOf("") }
    var shirtNeck by remember { mutableStateOf("") }

    // Pant Measurements
    var pantWaist by remember { mutableStateOf("") }
    var pantHip by remember { mutableStateOf("") }
    var pantThigh by remember { mutableStateOf("") }
    var pantKnee by remember { mutableStateOf("") }
    var pantBottom by remember { mutableStateOf("") }
    var pantLength by remember { mutableStateOf("") }

    // Images
    var imagePaths by remember { mutableStateOf<List<String>>(emptyList()) }
    var tempPhotoFile by remember { mutableStateOf<File?>(null) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempPhotoUri?.let { uri ->
                val permPath = ImageUtils.saveImageToInternalStorage(context, uri)
                if (permPath != null) {
                    imagePaths = imagePaths + permPath
                }
            }
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            val permPath = ImageUtils.saveImageToInternalStorage(context, uri)
            if (permPath != null) {
                imagePaths = imagePaths + permPath
            }
        }
    }

    // Auto Order ID Generator display
    var previewOrderNumber by remember { mutableStateOf("......") }
    LaunchedEffect(Unit) {
        previewOrderNumber = viewModel.getNextOrderNumber()
    }

    // Calendar Handlers
    val givenDatePicker = remember {
        val calendarInstance = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendarInstance.set(year, month, dayOfMonth)
                givenDate = dateFormatter.format(calendarInstance.time)
            },
            calendarInstance.get(Calendar.YEAR),
            calendarInstance.get(Calendar.MONTH),
            calendarInstance.get(Calendar.DAY_OF_MONTH)
        )
    }

    val deliveryDatePicker = remember {
        val calendarInstance = Calendar.getInstance()
        calendarInstance.add(Calendar.DAY_OF_YEAR, 7)
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendarInstance.set(year, month, dayOfMonth)
                deliveryDate = dateFormatter.format(calendarInstance.time)
            },
            calendarInstance.get(Calendar.YEAR),
            calendarInstance.get(Calendar.MONTH),
            calendarInstance.get(Calendar.DAY_OF_MONTH)
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("New Order Form", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("add_order_back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            
            // Auto Generated ID Indicator card banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AUTO-GENERATED ID",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Order Number: #$previewOrderNumber",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "READ-ONLY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Customer Identity Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE6E1E5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Customer Information", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)

                    TextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer Name *") },
                        placeholder = { Text("Enter client name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_name_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF3EDF7),
                            unfocusedContainerColor = Color(0xFFF3EDF7),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    TextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = { Text("Mobile Number *") },
                        placeholder = { Text("Enter phone number") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_phone_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF3EDF7),
                            unfocusedContainerColor = Color(0xFFF3EDF7),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    TextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address (Optional)") },
                        placeholder = { Text("Shop or residential address") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_address_input"),
                        maxLines = 2,
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF3EDF7),
                            unfocusedContainerColor = Color(0xFFF3EDF7),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }

            // Timeline Dates Card
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Given date text input
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF3EDF7))
                            .clickable { givenDatePicker.show() }
                            .padding(12.dp)
                            .testTag("given_date_picker")
                    ) {
                        Column {
                            Text("Given Date", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF49454F))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF49454F))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(givenDate, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B1E))
                            }
                        }
                    }

                    // Delivery date layout box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF3EDF7))
                            .clickable { deliveryDatePicker.show() }
                            .padding(12.dp)
                            .testTag("delivery_date_picker")
                    ) {
                        Column {
                            Text("Delivery Due", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF49454F))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(deliveryDate, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B1E))
                            }
                        }
                    }
                }
            }

            // Ledger Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE6E1E5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Payment Section", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextField(
                            value = totalAmountText,
                            onValueChange = { totalAmountText = it },
                            label = { Text("Total Amount") },
                            placeholder = { Text("0") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("total_amount_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF3EDF7),
                                unfocusedContainerColor = Color(0xFFF3EDF7),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        TextField(
                            value = advancePaidText,
                            onValueChange = { advancePaidText = it },
                            label = { Text("Advance Paid") },
                            placeholder = { Text("0") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("advance_paid_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF3EDF7),
                                unfocusedContainerColor = Color(0xFFF3EDF7),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }

                    // Read-only Balance Banner with high color priority
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = if (balanceAmount > 0) Color(0xFFF2B8B5).copy(alpha = 0.2f) else Color(0xFFC8E6C9).copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Remaining Balance",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF49454F)
                            )
                            Text(
                                text = "₹${balanceAmount.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (balanceAmount > 0) Color(0xFFB3261E) else Color(0xFF2E7D32),
                                modifier = Modifier.testTag("balance_display")
                            )
                        }
                    }
                }
            }

            // Measurements (Shirt)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE6E1E5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Shirt Measurements", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SleekMeasurementInput(label = "Chest (inches)", value = shirtChest, onValueChange = { shirtChest = it }, tag = "shirt_chest")
                        SleekMeasurementInput(label = "Shoulder (inches)", value = shirtShoulder, onValueChange = { shirtShoulder = it }, tag = "shirt_shoulder")
                        SleekMeasurementInput(label = "Sleeve (inches)", value = shirtSleeve, onValueChange = { shirtSleeve = it }, tag = "shirt_sleeve")
                        SleekMeasurementInput(label = "Length (inches)", value = shirtLength, onValueChange = { shirtLength = it }, tag = "shirt_length")
                        SleekMeasurementInput(label = "Neck (inches)", value = shirtNeck, onValueChange = { shirtNeck = it }, tag = "shirt_neck")
                    }
                }
            }

            // Measurements (Pant)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE6E1E5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Pant Measurements", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SleekMeasurementInput(label = "Waist (inches)", value = pantWaist, onValueChange = { pantWaist = it }, tag = "pant_waist")
                        SleekMeasurementInput(label = "Hip (inches)", value = pantHip, onValueChange = { pantHip = it }, tag = "pant_hip")
                        SleekMeasurementInput(label = "Thigh (inches)", value = pantThigh, onValueChange = { pantThigh = it }, tag = "pant_thigh")
                        SleekMeasurementInput(label = "Knee (inches)", value = pantKnee, onValueChange = { pantKnee = it }, tag = "pant_knee")
                        SleekMeasurementInput(label = "Bottom (inches)", value = pantBottom, onValueChange = { pantBottom = it }, tag = "pant_bottom")
                        SleekMeasurementInput(label = "Length (inches)", value = pantLength, onValueChange = { pantLength = it }, tag = "pant_length")
                    }
                }
            }

            // Cloth Snapshot Storage
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFE6E1E5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Add Cloth Images", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
                                    tempPhotoFile = file
                                    val authority = "${context.packageName}.fileprovider"
                                    val uri = FileProvider.getUriForFile(context, authority, file)
                                    tempPhotoUri = uri
                                    cameraLauncher.launch(uri)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Camera Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("camera_capture_button"),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFF79747E)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF49454F))
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Camera", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                galleryLauncher.launch("image/*")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("gallery_upload_button"),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFF79747E)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF49454F))
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gallery", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    // Attachment scroll row
                    if (imagePaths.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(top = 4.dp)
                        ) {
                            items(imagePaths) { path ->
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(1.dp, Color(0xFFE6E1E5), RoundedCornerShape(16.dp))
                                ) {
                                    AsyncImage(
                                        model = File(path),
                                        contentDescription = "Cloth snapshot",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { imagePaths = imagePaths - path },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove snapshot",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(Color(0xFFF3EDF7), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No photos selected. Attach cloth textures.",
                                fontSize = 13.sp,
                                color = Color(0xFF49454F)
                            )
                        }
                    }
                }
            }

            // Save & export FAB banner call-to-action
            Button(
                onClick = {
                    val sChest = shirtChest.toDoubleOrNull()
                    val sShoulder = shirtShoulder.toDoubleOrNull()
                    val sSleeve = shirtSleeve.toDoubleOrNull()
                    val sLength = shirtLength.toDoubleOrNull()
                    val sNeck = shirtNeck.toDoubleOrNull()
                    
                    val pWaist = pantWaist.toDoubleOrNull()
                    val pHip = pantHip.toDoubleOrNull()
                    val pThigh = pantThigh.toDoubleOrNull()
                    val pKnee = pantKnee.toDoubleOrNull()
                    val pBottom = pantBottom.toDoubleOrNull()
                    val pLength = pantLength.toDoubleOrNull()

                    viewModel.createOrder(
                        customerName = customerName,
                        mobileNumber = mobileNumber,
                        address = address,
                        givenDate = givenDate,
                        deliveryDate = deliveryDate,
                        totalAmount = totalAmount,
                        advancePaid = advancePaid,
                        shirtChest = sChest,
                        shirtShoulder = sShoulder,
                        shirtSleeve = sSleeve,
                        shirtLength = sLength,
                        shirtNeck = sNeck,
                        pantWaist = pWaist,
                        pantHip = pHip,
                        pantThigh = pThigh,
                        pantKnee = pKnee,
                        pantBottom = pBottom,
                        pantLength = pLength,
                        images = imagePaths,
                        onSuccess = {
                            Toast.makeText(context, "Order Saved Successfully", Toast.LENGTH_SHORT).show()
                            onNavigateToDashboard()
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_order_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Save & Export Order",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SleekMeasurementInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF49454F),
            modifier = Modifier.weight(1.2f)
        )
        TextField(
            value = value,
            onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                    onValueChange(it)
                }
            },
            placeholder = { Text("nil", color = Color(0xFF49454F)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(100.dp)
                .testTag("input_$tag"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3EDF7),
                unfocusedContainerColor = Color(0xFFF3EDF7),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}
