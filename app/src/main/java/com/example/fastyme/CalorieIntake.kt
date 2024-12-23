import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.fastyme.todayString
import com.example.fastyme.userId
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import com.google.gson.JsonSyntaxException

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

//suspend fun parseGeminiResponse(response: String): Map<String, Float?> {
//    return try {
//        // Parse JSON response to JsonObject
//        val jsonObject = Gson().fromJson(response, JsonObject::class.java)
//
//        // Extract the "Calorie" object
//        val calorieObject = jsonObject.getAsJsonObject("Calorie")
//
//        // Parse individual fields and convert to Float
//        mapOf(
//            "fat" to calorieObject.get("fat")?.asString?.toFloatOrNull(),
//            "protein" to calorieObject.get("protein")?.asString?.toFloatOrNull(),
//            "carbs" to calorieObject.get("carbs")?.asString?.toFloatOrNull(),
//            "fiber" to calorieObject.get("fiber")?.asString?.toFloatOrNull(),
//            "totalCalories" to calorieObject.get("totalCalorie")?.asString?.toFloatOrNull()
//        )
//    } catch (e: JsonSyntaxException) {
//        // Log the error for debugging
//        Log.e("JSONParsing", "Error parsing JSON: ${e.message}")
//        emptyMap() // Return empty map if parsing fails
//    } catch (e: Exception) {
//        Log.e("JSONParsing", "Unexpected error: ${e.message}")
//        emptyMap() // Return empty map for other errors
//    }
//}

fun parseToFloat(input: String?): Float? {
    if (input == null) return null
    // Regex untuk menangkap angka, termasuk angka desimal
    val regex = Regex("[-+]?[0-9]*\\.?[0-9]+")
    val matchResult = regex.find(input)
    return matchResult?.value?.toFloatOrNull() // Mengonversi hasil regex ke Float
}

fun updateDatabaseCalorie(jsonObject: JsonObject, type:String) {
    try {
        // Debug respons
        Log.d("Calorie Response", "Data received: $jsonObject")

        // Parsing respons
//        val calorieData = if (data.startsWith("{")) {
//            // Jika formatnya JSON
//            Gson().fromJson(data, Map::class.java)
//        } else {
//            // Jika formatnya string biasa
//            parseGeminiResponse(data)
//        }

        val fat = parseToFloat(jsonObject.get("fat")?.asString)
        val protein = parseToFloat(jsonObject.get("protein")?.asString)
        val carbs = parseToFloat(jsonObject.get("carbs")?.asString)
        val fiber = parseToFloat(jsonObject.get("fiber")?.asString)
        val totalCalories = parseToFloat(jsonObject.get("totalCalories")?.asString)

        if (jsonObject!=null) {
            val firebaseData = hashMapOf(
                "fat" to fat,
                "protein" to protein,
                "carbs" to carbs,
                "fiber" to fiber,
                "totalCalories" to totalCalories,
                "date" to todayString
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


data class calorie (
    val prompt:String,
    val date:String
)

fun retrieveDataCalorie(listData: SnapshotStateList<calorie>) {
    db.collection("Calorie Intake")
        .get()
        .addOnSuccessListener { data ->
            listData.clear() // Bersihkan list sebelumnya
            for (d in data) {
                listData.add(
                    calorie(
                        d.data["prompt"].toString(),
                        d.data["date"].toString()
                    )
                )
            }
        }
        .addOnFailureListener { e -> Log.d("fail", "${e}") }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieIntake(userId: String, navController: NavController) {
    var totalCalorie by remember { mutableStateOf(0) }
    val targetCalorie = 2100
    val progress = (totalCalorie.toFloat() / targetCalorie) * 100
    var showDialog by remember { mutableStateOf(false) }
    var inputCalorie by remember { mutableStateOf("") }
    var typeofMeal by remember { mutableStateOf("") }
    val today = LocalDate.now()
    val todayString = today.format(DateTimeFormatter.ISO_DATE)
//    var list by remember { mutableStateOf("") }


//    fun fetchData() {
//        db.collection("Calorie Intake")
//            .document("${userId}_$todayString")
//            .get()
//            .addOnSuccessListener { document ->
//                if (document != null && document.exists()) {
//                    totalCalorie = document.getLong("totalWaterIntake")?.toInt() ?: 0
////                    fillPercentage = (totalIntake.toFloat() / targetIntake * 100).coerceAtMost(100f)
//                } else {
//                    totalCalorie = 0 // Reset if no data for today
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d("Firebase", "Error fetching data: ${exception.message}")
//            }
//    }

//    fun updateDatabase(lists:String) {
//        val data = hashMapOf(
//            "prompt" to lists,
////            "fat",
////            "protein",
////            "carbs",
////            "fiber",
////            "totalCalorieIntake" to total,
//            "date" to todayString
//        )
//        db.collection("Calorie Intake")
//            .document("${userId}_$todayString")
//            .set(data)
//            .addOnSuccessListener {
//                Log.d("Firebase", "Data updated successfully")
//            }
//            .addOnFailureListener { exception ->
//                Log.d("Firebase", "Error updating data: ${exception.message}")
//            }
//    }

    // Fetch initial data on load
//    fetchData()

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
                Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize(),
                        progress = progress / 100,
                        strokeWidth = 10.dp,
                        color = Color(0xFF673AB7)
                    )
                    Text("${progress.toInt()}%", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Intake Text
                Text("You have consumed", fontSize = 16.sp)
                Text("$totalCalorie / $targetCalorie kcal", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("today", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(24.dp))

                // Meal Tracker List
                MealTrackerItem("Breakfast", totalCalorie, onPlusClick = { showDialog = true; typeofMeal="Breakfast" })
                Spacer(modifier = Modifier.height(8.dp))
                MealTrackerItem("Lunch", totalCalorie, onPlusClick = { showDialog = true; typeofMeal="Lunch" })
                Spacer(modifier = Modifier.height(8.dp))
                MealTrackerItem("Dinner", totalCalorie, onPlusClick = { showDialog = true; typeofMeal="Dinner" })
                Spacer(modifier = Modifier.height(8.dp))
                MealTrackerItem("Snack", totalCalorie, onPlusClick = { showDialog = true; typeofMeal="Snack" })
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
                                            updateDatabaseCalorie(response, typeofMeal)
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

//                        if (viewModel.isLoading.value == true) {
//                            CircularProgressIndicator()
//                        }
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
fun MealTrackerItem(name: String, calorie: Int, onPlusClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFEDE7F6), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("$calorie / 300 kcal", fontSize = 14.sp)
        }
        Button(
            onClick = onPlusClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, // Warna latar belakang menjadi transparan
                contentColor = Color.Black          // Warna teks atau ikon menjadi hitam
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp),
            modifier = Modifier
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Show Dialog")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailCalorieScreen(name: String) {
    val listData = remember { mutableStateListOf<calorie>() }
    retrieveDataCalorie(listData)
    Scaffold(
        topBar = {
            TopAppBar(
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
            items(listData) {
                    d -> Column(
                modifier = Modifier.fillMaxWidth()
            ){
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Prompt : ${d.prompt.toString()}")
                    Text("Date : ${d.date.toString()}")
                }



                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            }
//            Text(
//                "Your breakfast calories are excessive, please reduce your intake at lunch",
//                fontWeight = FontWeight.Bold
//            )
//
//            // Example Meal Entries
//            listOf("08.00 a.m" to "200 kcal", "10.00 a.m" to "200 kcal").forEachIndexed { index, meal ->
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color(0xFFF3E5F5), RoundedCornerShape(8.dp))
//                        .padding(16.dp)
//                ) {
//                    Column {
////                        Text("${index + 1}. ${meal.first}", fontWeight = FontWeight.Bold)
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Text("2 siung bawang putih, 1 sendok makan minyak, 1 butir telur diorak-arik, 100 gram nasi putih")
//                        Spacer(modifier = Modifier.height(8.dp))
////                        Text("Total: ${meal.second}", fontWeight = FontWeight.Bold)
//                    }
//                }
//            }
        }
    }
}
