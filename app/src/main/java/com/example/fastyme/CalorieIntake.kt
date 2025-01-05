import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fastyme.DetailRecipePage
import com.example.fastyme.R
import com.example.fastyme.RecipeCategoryScreen
import com.example.fastyme.RecipePage
import com.example.fastyme.db
import com.example.fastyme.fillPercentage
import com.example.fastyme.targetIntake
import com.example.fastyme.todayString
import com.example.fastyme.totalIntake
import com.example.fastyme.userId
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import com.google.gson.JsonSyntaxException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun extractJson(response: String): JsonObject? {
    return try {
        // Ekstrak JSON dengan regex
        val jsonRegex = "\\{.*?\\}".toRegex()
        val jsonString = jsonRegex.find(response)?.value

        if (jsonString != null) {
            // Parse JSON menggunakan Gson
            Gson().fromJson(jsonString, JsonObject::class.java)
        } else {
            null // Jika JSON tidak ditemukan
        }
    } catch (e: JsonSyntaxException) {
        Log.e("JSONParsing", "Error parsing JSON: ${e.message}")
        null
    }
}

fun parseToFloat(input: String?): Float? {
    if (input == null) return null
    // Regex untuk menangkap angka, termasuk angka desimal
    val regex = Regex("[-+]?[0-9]*\\.?[0-9]+")
    val matchResult = regex.find(input)
    return matchResult?.value?.toFloatOrNull() // Mengonversi hasil regex ke Float
}

fun getCurrentTimeFormatted(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun updateDatabaseCalorie(jsonObject: JsonObject, type:String, inputValue:String) {
    try {
        // Debug respons
        Log.d("Calorie Response", "Data received: $jsonObject")

        val fat = parseToFloat(jsonObject.get("fat")?.asString)
        val protein = parseToFloat(jsonObject.get("protein")?.asString)
        val carbs = parseToFloat(jsonObject.get("carbs")?.asString)
        val fiber = parseToFloat(jsonObject.get("fiber")?.asString)
        val totalCalories = parseToFloat(jsonObject.get("totalCalories")?.asString)
        val currentTime = getCurrentTimeFormatted()


        if (jsonObject!=null) {
            val firebaseData = hashMapOf(
                "fat" to fat,
                "protein" to protein,
                "carbs" to carbs,
                "fiber" to fiber,
                "totalCalories" to totalCalories,
                "prompt" to inputValue,
                "time" to currentTime
            )

            db.collection("Calorie Intake")
                .document("${userId}_$todayString")
                .collection("${type}")
                .add(firebaseData)
                .addOnSuccessListener {
                    Log.d("Firebase", "New entry added successfully to sub-collection")
                }
                .addOnFailureListener { exception ->
                    Log.d("Firebase", "Error adding entry: ${exception.message}")
                }
        } else {
            Log.e("JSONValidation", "Invalid data format: $jsonObject")
        }
    } catch (e: JsonSyntaxException) {
        Log.e("JSONValidation", "Error parsing JSON: ${e.message}")
    } catch (e: Exception) {
        Log.e("DatabaseUpdate", "Unexpected error: ${e.message}")
    }
}

fun updateDatabaseIntakeCalorie(total: Int, totalB:Int, totalL:Int, totalD:Int, totalS:Int) {
    val data = hashMapOf(
        "totalBreakfastIntake" to totalB,
        "totalLunchIntake" to totalL,
        "totalDinnerIntake" to totalD,
        "totalSnackIntake" to totalS,
        "totalCalorieIntake" to total,
    )
    db.collection("Calorie Intake")
        .document("${userId}_$todayString")
        .set(data, SetOptions.merge())
        .addOnSuccessListener {
            Log.d("Firebase", "Data updated successfully")
        }
        .addOnFailureListener { exception ->
            Log.d("Firebase", "Error updating data: ${exception.message}")
        }
}

data class totalCalorie (
    var totalBreakfast:Int,
    var totalLunch:Int,
    var totalDinner:Int,
    var totalSnack:Int,
    var totalCalories:Int,
)


@Composable
fun fetchDataCalorie(calorieState: MutableState<totalCalorie>) {
//    LaunchedEffect(Unit) {
        db.collection("Calorie Intake")
            .document("${userId}_$todayString")
            .addSnapshotListener {
                    snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: ${snapshot.data}")
                    calorieState.value = totalCalorie(
                        totalBreakfast = (snapshot["totalBreakfastIntake"] as? Number)?.toInt() ?: 0,
                        totalLunch = (snapshot["totalLunchIntake"] as? Number)?.toInt() ?: 0,
                        totalDinner = (snapshot["totalDinnerIntake"] as? Number)?.toInt() ?: 0,
                        totalSnack = (snapshot["totalSnackIntake"] as? Number)?.toInt() ?: 0,
                        totalCalories = (snapshot["totalCalorieIntake"] as? Number)?.toInt() ?: 0
                    )
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
//    }
}


data class calorie (
    val fat:String,
    val carbs:String,
    val protein:String,
    val fiber:String,
    val totalCalories:String,
    val prompt:String,
    val time:String
)

fun retrieveDataCalorie(listData: SnapshotStateList<calorie>, type:String) {
    db.collection("Calorie Intake")
        .document("${userId}_$todayString")
        .collection("${type}")
        .get()
        .addOnSuccessListener { data ->
            listData.clear() // Bersihkan list sebelumnya
            for (d in data) {
                listData.add(
                    calorie(
                        d.data["fat"].toString(),
                        d.data["carbs"].toString(),
                        d.data["protein"].toString(),
                        d.data["fiber"].toString(),
                        d.data["totalCalories"].toString(),
                        d.data["prompt"].toString(),
                        d.data["time"].toString()
                    )
                )
            }
        }
        .addOnFailureListener { e -> Log.d("fail", "${e}") }
}

fun normalizeNumberString(input: String): String {
    return input.trimStart('0').ifEmpty { "0" }
}

@Composable
fun progressCircle(size:Int, progress:Float) {
    Box(modifier = Modifier.size(size.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            progress = progress / 100,
            strokeWidth = 10.dp,
            color = Color(0xFF673AB7),
            trackColor = Color.LightGray
        )
        Text("${progress.toInt()}%", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieIntake(userId: String, navController: NavController) {
    val calorieState = remember { mutableStateOf(totalCalorie(0,0,0,0,0)) }
    fetchDataCalorie(calorieState)
    val targetCalorie = 2000
    val progress = (calorieState.value.totalCalories.toFloat() / targetCalorie) * 100
    var showDialog by remember { mutableStateOf(false) }
    var inputCalorie by remember { mutableStateOf("") }
    var typeofMeal by remember { mutableStateOf("") }



    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                title = { Text("Calorie Intake", color = Color.White) },

                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF673AB7))
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Header
                Text("You are on the way!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Progress Circle
                progressCircle(200, progress)

                Spacer(modifier = Modifier.height(16.dp))

                // Intake Text
                Text("You have consumed", fontSize = 16.sp)
                Text("${calorieState.value.totalCalories} / $targetCalorie kcal", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("today", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(24.dp))

                // Meal Tracker List
                MealTrackerItem("Breakfast", calorieState.value.totalBreakfast.toInt(), onPlusClick = { showDialog = true; typeofMeal="Breakfast" }, navController, R.drawable.breakfast, 500)
                Spacer(modifier = Modifier.height(8.dp))
                MealTrackerItem("Lunch", calorieState.value.totalLunch.toInt(), onPlusClick = { showDialog = true; typeofMeal="Lunch" }, navController, R.drawable.lunch, 700)
                Spacer(modifier = Modifier.height(8.dp))
                MealTrackerItem("Dinner", calorieState.value.totalDinner.toInt(), onPlusClick = { showDialog = true; typeofMeal="Dinner" }, navController, R.drawable.dinner, 600)
                Spacer(modifier = Modifier.height(8.dp))
                MealTrackerItem("Snack", calorieState.value.totalSnack.toInt(), onPlusClick = { showDialog = true; typeofMeal="Snack" }, navController, R.drawable.snack, 200)
            }

        }

//        API Gemini
suspend fun modelCall(prompt: String): String {
    return try {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyBqK46pVY1OzQkdbKiW39kBVKYfuTc1yiU",
            generationConfig { mapOf("response_mime_type" to "application/json") }
        )
        val generatePrompt = """
            Hitung kalori dari bahan-bahan makanan berikut ${prompt} dengan detail.
            Berikan output berupa list jumlah lemak, protein, karbohidrat, serat, serta total kalori.
            Gunakan format JSON sebagai berikut: {"fat":str, "protein":str, "carbs":str, "fiber":str, "totalCalories":str}
        """.trimIndent()

        val response = generativeModel.generateContent(generatePrompt)
        response.text ?: "{}" // Default ke JSON kosong jika respons null
    } catch (e: Exception) {
        Log.e("GeminiAPI", "Error: ${e.message}")
        "{}" // Kembalikan JSON kosong saat terjadi error
    }
}





        // Modal Dialog for Input
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Box(
                    modifier = Modifier
                        .height(600.dp).width(500.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Enter your ${typeofMeal}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputCalorie,
                            onValueChange = { inputCalorie = it },
                            label = { Text("Enter calorie value") },
                            maxLines = 100,
                            modifier = Modifier.fillMaxWidth().height(480.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
//                        val coroutineScope = rememberCoroutineScope()
                        Button(onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val list = modelCall(inputCalorie) // Menunggu hasil dari Gemini API
                                    withContext(Dispatchers.Main) {
                                        val response = extractJson(list)
                                        if (response != null) {
                                            updateDatabaseCalorie(response, typeofMeal, inputCalorie)
//                                            val totalCalories = parseToFloat(response.get("totalCalories")?.asString)
                                            val totalCalories = (parseToFloat(normalizeNumberString(response.get("totalCalories")?.asString ?: "0")) ?: 0f).toInt()


                                            calorieState.value.totalCalories += totalCalories
                                            if(typeofMeal=="Breakfast") {
                                                calorieState.value.totalBreakfast += totalCalories
                                            } else if(typeofMeal=="Lunch") {
                                                calorieState.value.totalLunch += totalCalories
                                            } else if(typeofMeal=="Dinner") {
                                                calorieState.value.totalDinner += totalCalories
                                            } else if(typeofMeal=="Snack") {
                                                calorieState.value.totalSnack += totalCalories
                                            }
                                            updateDatabaseIntakeCalorie(calorieState.value.totalCalories.toInt(), calorieState.value.totalBreakfast.toInt(), calorieState.value.totalLunch.toInt(), calorieState.value.totalDinner.toInt(), calorieState.value.totalSnack.toInt())

                                        } // Menyimpan data ke Firebase di MainThread
                                        navController.navigate("detailCalorie/$typeofMeal")
                                        showDialog = false
                                    }
                                } catch (e: Exception) {
                                    Log.e("GeminiAPI", "Error: ${e.message}")
                                }
                            }
                        }) {
                            Text("Submit")
                        }

                    }
                }
            }
        }

        // Detail Navigation Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
            ) {
                Text("Detail", color = Color.White)
            }
        }

    }
}

@Composable
fun MealTrackerItem(name: String, calorie: Int, onPlusClick: () -> Unit, navController: NavController, imageResId:Int, amountLimit:Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFD9C2EC), RoundedCornerShape(20.dp))
            .padding(12.dp),
//        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = name,
//                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                )
                Column {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("$calorie / $amountLimit kcal", fontSize = 14.sp)
                }
            }

            Button(
                onClick = onPlusClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Warna latar belakang menjadi transparan
                    contentColor = Color.White          // Warna teks atau ikon menjadi hitam
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp),
                modifier = Modifier
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Show Dialog")
                }
            }
        }
        if (calorie>0) {
            Button(onClick = {
                navController.navigate("detailCalorie/$name")
            },
                modifier = Modifier
                    .fillMaxWidth()) {
                Text("Detail")
            }
        }
    }

}

// Extension function to convert pixels to dp
@Composable
fun Float.toDp(): Dp {
    val density = LocalDensity.current
    return with(density) { this@toDp.toDp() }
}

@Composable
fun ProgressNutrition(name: String, amount:Float, amountLimit:Int, color: Color) {
    val progress = (amount / amountLimit).coerceIn(0f, 1f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name)
            Text("${amount} / ${amountLimit} g")
        }
        // Canvas for progress bar
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
        ) {
            // Draw the progress bar
            val barWidth = size.width * progress
            drawRect(
                color = color,
                size = androidx.compose.ui.geometry.Size(width = barWidth, height = size.height)
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailCalorieScreen(name: String, navController: NavController) {
    val listData = remember { mutableStateListOf<calorie>() }
    retrieveDataCalorie(listData, name)
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.White // Ensure the back icon is black
                        )
                    }
                },
                title = { Text("${name} Detail", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF673AB7))
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
//            Text(
//                "Your breakfast calories are excessive, please reduce your intake at lunch",
//                fontWeight = FontWeight.Bold
//            )
            itemsIndexed(listData) {
                    index, d -> Column(
                modifier = Modifier
                        .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color(0xFFD9C2EC), RoundedCornerShape(20.dp))
                    .padding(12.dp),
            ){
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row {
                        Box() {
                            Text("${index+1}.")
                        }
                        Text("${d.time}")
                    }
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${d.prompt}")
                        IconButton(onClick = {

                        }) {
                            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit", modifier = Modifier.size(25.dp))
                        }
                    }
                    var fatLimit by remember { mutableStateOf(0) }
                    var proteinLimit by remember { mutableStateOf(0) }
                    var carbsLimit by remember { mutableStateOf(0) }
                    var fiberLimit by remember { mutableStateOf(0) }
                    if(name=="Breakfast") {
                        fatLimit=15
                        proteinLimit=19
                        carbsLimit=75
                        fiberLimit=7
                    } else if(name=="Lunch") {
                        fatLimit=21
                        proteinLimit=26
                        carbsLimit=105
                        fiberLimit=10
                    }
                    else if(name=="Dinner") {
                        fatLimit=18
                        proteinLimit=23
                        carbsLimit=90
                        fiberLimit=8
                    } else if(name=="Snack") {
                        fatLimit=6
                        proteinLimit=7
                        carbsLimit=30
                        fiberLimit=3
                    }
                    ProgressNutrition("Fat", d.fat.toFloat(), fatLimit, Color(0xFFF5B03E))
                    ProgressNutrition("Protein", d.protein.toFloat(), proteinLimit, Color(0xFFCB1C35))
                    ProgressNutrition("Carbs", d.carbs.toFloat(), carbsLimit, Color(0xFF5624C4))
                    ProgressNutrition("Fiber", d.fiber.toFloat(), fiberLimit, Color(0xFF307D31))
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total calories : ${d.totalCalories} kcal")
                        Button(onClick = {}) {
                            Text("Delete")
                        }
                    }

                }
            }
            }

        }
    }
}
