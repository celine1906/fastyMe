import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter



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

    fun updateDatabase(lists:String) {
        val data = hashMapOf(
            "prompt" to lists,
//            "fat",
//            "protein",
//            "carbs",
//            "fiber",
//            "totalCalorieIntake" to total,
            "date" to todayString
        )
        db.collection("Calorie Intake")
            .document("${userId}_$todayString")
            .set(data)
            .addOnSuccessListener {
                Log.d("Firebase", "Data updated successfully")
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error updating data: ${exception.message}")
            }
    }

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
        suspend fun modelCall(prompt:String): String {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = "AIzaSyBqK46pVY1OzQkdbKiW39kBVKYfuTc1yiU"
            )
            val generatePrompt = "Hitung kalori dari bahan-bahan makanan berikut ${prompt} dengan detail, berikan output berupa list jumlah lemak, protein, karbohidrat, serat, serta total kalori atau jumlah dari lemak, protein, karbohidrat, dan serat. contoh output = 200, 300, 500, 50, 1050"
            return try {
                val response = generativeModel.generateContent(generatePrompt)
                val text = response.text ?: ""
                Log.d("GeminiAPI", "Response: $text")
                text
            } catch (e: Exception) {
                Log.e("GeminiAPI", "Error: ${e.message}")
                "Error: ${e.message}"
            }
        }



//        class CalorieIntakeViewModel : ViewModel() {
//            private val _isLoading = MutableLiveData(false)
//            val isLoading: LiveData<Boolean> = _isLoading
//
//            fun submitCalorie(inputCalorie: String) {
//                _isLoading.value = true
//                viewModelScope.launch {
//                    val list = modelCall(inputCalorie)
//                    updateDatabase(list)
//                    // Update UI or navigate to the next screen
//                    _isLoading.value = false
//                }
//            }
//        }
//
//        val viewModel = viewModel<CalorieIntakeViewModel>()

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
                           MainScope().launch {
                               val list = modelCall(inputCalorie) // Menunggu hasil dari Gemini API
                               updateDatabase(list) // Menyimpan data ke Firebase
                               navController.navigate("detailCalorie/$typeofMeal")
                               showDialog = false
                           }
                        })
                        {
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${name} Detail", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF673AB7))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Your breakfast calories are excessive, please reduce your intake at lunch",
                fontWeight = FontWeight.Bold
            )

            // Example Meal Entries
            listOf("08.00 a.m" to "200 kcal", "10.00 a.m" to "200 kcal").forEachIndexed { index, meal ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3E5F5), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
//                        Text("${index + 1}. ${meal.first}", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("2 siung bawang putih, 1 sendok makan minyak, 1 butir telur diorak-arik, 100 gram nasi putih")
                        Spacer(modifier = Modifier.height(8.dp))
//                        Text("Total: ${meal.second}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
