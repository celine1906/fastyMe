package com.example.fastyme

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import kotlinx.coroutines.*
import extractJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.StringReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPhoto(navController: NavController) {
    val isLoading = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val defaultImg: Bitmap = BitmapFactory.decodeResource(context.resources, android.R.drawable.ic_menu_report_image)
    val bitmap = remember { mutableStateOf(defaultImg) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) {
        if (it != null) {
            bitmap.value = it
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream)
            if (selectedBitmap != null) {
                bitmap.value = selectedBitmap
            }
        }
    }




    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Generate Low-Calorie Recipe",
                        color = Color.Black // Make the title text black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.Black // Ensure the back icon is black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White // Set the top bar background to white
                )
            )
        },
        containerColor = Color.White // Set Scaffold background to white
    ) {
        innerPadding->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Your existing UI elements here


                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )

             }
            }
            Text(
                text = "Upload an image of your ingredients or take a photo to generate a healthy, low-calorie recipe.",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Image(
                bitmap = bitmap.value.asImageBitmap(),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { cameraLauncher.launch() }) {
                    Text("Open Camera")
                }
                Button(onClick = { galleryLauncher.launch("image/*") }) {
                    Text("Upload Photo")
                }
            }

            val resizedBitmap =
                Bitmap.createScaledBitmap(
                    bitmap.value,
                    800,
                    800,
                    false
                ) // Resize to smaller dimensions

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                bitmap?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        isLoading.value = true
                        try {
                            val responseJson =
                                GeminiRecipeRecommendation(resizedBitmap.asImageBitmap())

                            withContext(Dispatchers.Main) {
                                val response = cleanAndParseJsonResponse(responseJson)
                                isLoading.value = false
                                if (response != null) {
                                    val gson = Gson()
                                    val jsonString = gson.toJson(response)
                                    val encodedJson = Uri.encode(jsonString)
                                    navController.navigate("geminiResponse/$encodedJson")
                                } // Menyimpan data ke Firebase di MainThread
                                else {
                                    Toast.makeText(
                                        context,
                                        "Gagal generate",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                            }
                        } catch (e: Exception) {
                            Log.e("GeminiAPI", "Error: ${e.message}")
                        }

                } ?: Toast.makeText(context, "Please select an image first.", Toast.LENGTH_SHORT).show()
                    }

                }
            ) {
                Text("Submit")
            }
        }
    }
}

fun imageBitmapToByteArray(imageBitmap: ImageBitmap): ByteArray {
    val bitmap = imageBitmap.asAndroidBitmap()
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return outputStream.toByteArray()
}

fun byteArrayToBase64(byteArray: ByteArray): String {
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

suspend fun GeminiRecipeRecommendation(img: ImageBitmap): String {
    val byteArray = imageBitmapToByteArray(img)
    val base64Image = byteArrayToBase64(byteArray)
    return try {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyBqK46pVY1OzQkdbKiW39kBVKYfuTc1yiU",
            generationConfig { mapOf("response_mime_type" to "application/json")}
        )
        val generatePrompt = """
            Based on the provided image, recommend a low-calorie diet menu.
            The image is encoded in Base64 below:
            $base64Image
            
            The output should include:
            - Menu name
            - Ingredients
            - Cooking instructions
            - Total calories
            - Nutritional breakdown (fat, protein, carbohydrates, and fiber)

            Please format the response as JSON:
            {
                "menuName": "string",
                "ingredients": "string",
                "cookingInstructions": "string",
                "totalCalories": "string",
                "fat": "string",
                "protein": "string",
                "carbs": "string",
                "fiber": "string"
            }
        """.trimIndent()

        val response = generativeModel.generateContent(generatePrompt)
        val jsonResponse = response.text
        if (jsonResponse != null && jsonResponse.isNotEmpty()) {
            Log.d("GeminiAPI", "Response: $jsonResponse") // Log the response
            jsonResponse
        } else {
            Log.e("GeminiAPI", "Response is null or empty")
            "{}"
        }
    } catch (e: Exception) {
        Log.e("GeminiAPI", "Error: ${e.message}")
        "{}"
    }
}

data class DietMenu(
    val menuName: String = "",
    val ingredients: String = "",
    val cookingInstructions: String = "",
    val totalCalories: String = "",
    val fat: String = "",
    val protein: String = "",
    val carbs: String = "",
    val fiber: String = ""
)
fun saveResponseToFirestore(menuName: String, ingredients: String, cookingInstructions: String, totalCalories: String, fat: String, protein: String, carbs: String,fiber: String) {
    try {
            val collectionRef = db.collection("Diet Menus").document(userId).collection("Saved Menus").document(menuName.toString())
            val dietMenu = DietMenu(menuName.toString(), ingredients.toString(), cookingInstructions.toString(), totalCalories.toString(), fat.toString(), protein.toString(), carbs.toString(), fiber.toString())
            collectionRef.set(dietMenu)
    } catch (e: Exception) {
        Log.e("FirebaseSave", "Error saving to Firestore: ${e.message}")
    }
}


fun cleanAndParseJsonResponse(response: String): JsonObject? {
    return try {
        // Remove backticks and trim unwanted characters
        val cleanedResponse = response.trim()
            .removePrefix("```json")
            .removeSuffix("```")
            .replace("\n", " ")
            .replace("*", "")
            .trim() // Memastikan tidak ada spasi tambahan

        val jsonRegex = "\\{.*?\\}".toRegex()
        val jsonString = jsonRegex.find(cleanedResponse)?.value

        // Jika ada karakter tambahan di akhir JSON, coba hapus whitespace atau karakter yang tidak diperlukan
//        val cleanedAndTrimmedResponse = cleanedResponse.replace(Regex("\\s+$"), "")

        Log.d("CleanedResponse", cleanedResponse)


        // Use GsonBuilder with lenient parsing
        val gson = GsonBuilder().setLenient().create()

        // Parse the cleaned JSON string to JsonObject using lenient parsing
        val jsonReader = JsonReader(StringReader(jsonString))
        jsonReader.isLenient = true  // Enable lenient parsing
        val jsonObject: JsonObject = gson.fromJson(jsonReader, JsonObject::class.java)

        // Ensure the response is fully consumed
        if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
            Log.e("JSONParsing", "Extra characters found after JSON.")
            return null
        }

        jsonObject
    } catch (e: JsonSyntaxException) {
        Log.e("JSONParsing", "Error parsing JSON: ${e.message}")
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuListScreen(navController: NavController) {
    val context = LocalContext.current
    val dietMenus = remember { mutableStateListOf<DietMenu>() }

    // Ambil data dari Firestore
    LaunchedEffect(Unit) {
        try {
            // Ambil data dari Firestore
            val querySnapshot = db.collection("Diet Menus")
                .document(userId)
                .collection("Saved Menus")
                .get()
                .await()

            dietMenus.clear()  // Clear previous data

            // Parse data Firestore dan masukkan ke dalam daftar dietMenus
            for (document in querySnapshot.documents) {
                val dietMenu = document.toObject(DietMenu::class.java)
                dietMenu?.let { dietMenus.add(it) }
            }
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error fetching data: ${e.message}")
        }
    }

    // Tampilan daftar menu
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Saved Recipe",
                        color = Color.Black // Make the title text black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.Black // Ensure the back icon is black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White // Set the top bar background to white
                )
            )
        },
        containerColor = Color.White // Set Scaffold background to white
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            items(dietMenus) { dietMenu ->
                MenuItem(dietMenu, navController)
            }
        }
    }
}


@Composable
fun MenuItem(dietMenu: DietMenu, navController: NavController) {

    Column(
        modifier = Modifier
            .padding(16.dp)
            .clickable {
                // Navigasi ke halaman detail
                navController.navigate("menuDetail/${dietMenu.menuName}")
            }
    ) {
        Text(text = dietMenu.menuName, fontWeight = FontWeight.Bold)
    }
    Divider(
        color = Color.Gray, // Menentukan warna garis
        thickness = 1.dp, // Menentukan ketebalan garis
        modifier = Modifier.fillMaxWidth() // Membuat garis memenuhi lebar container
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDetailScreen(navController: NavController, menuName: String) {
    val context = LocalContext.current
    val dietMenu = remember { mutableStateOf<DietMenu?>(null) }

    // Ambil data menu berdasarkan menuName
    LaunchedEffect(menuName) {
        try {
            val documentSnapshot = db.collection("Diet Menus")
                .document(userId)
                .collection("Saved Menus")
                .document(menuName)
                .get()
                .await()

            // Jika dokumen ditemukan, parse data
            val menu = documentSnapshot.toObject(DietMenu::class.java)
            dietMenu.value = menu
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error fetching menu detail: ${e.message}")
        }
    }

    dietMenu.value?.let { menu ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            menu.menuName,
                            color = Color.Black // Make the title text black
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                                contentDescription = "Back",
                                tint = Color.Black // Ensure the back icon is black
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White // Set the top bar background to white
                    )
                )
            },
            containerColor = Color.White // Set Scaffold background to white
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize().padding(innerPadding)
                    .padding(16.dp)
            ) {
                item {
                    Text(text = menu.menuName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Ingredients", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${menu.ingredients}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cooking Instructions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${menu.cookingInstructions}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Nutrition", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Total Calories: ${menu.totalCalories}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Fat: ${menu.fat}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Protein: ${menu.protein}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Carbs: ${menu.carbs}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Fiber: ${menu.fiber}")
                    Button(
                        onClick = {
// Menentukan koleksi dan dokumen yang ingin dihapus
                            val documentRef = db.collection("Diet Menus")
                                .document(userId)
                                .collection("Saved Menus").document("${menu.menuName}")

// Menghapus dokumen
                            documentRef.delete()
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Dokumen berhasil dihapus")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firestore", "Gagal menghapus dokumen", e)
                                }
                            navController.navigate("savedRecipe")
                                  },
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Text("Delete Recipe")
                    }
                }

            }
        }
    } ?: run {
        Text(text = "Loading...")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponseGemini(navController: NavController, backStackEntry: NavBackStackEntry) {
    val context = LocalContext.current
    val encodedJson = backStackEntry.arguments?.getString("jsonObject") ?: ""
    val jsonString = Uri.decode(encodedJson)
    val gson = Gson()
    val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)

    // Extract values from jsonObject
    val menuName = jsonObject.get("menuName")?.asString ?: ""
    val ingredients = jsonObject.get("ingredients")?.asString ?: ""
    val cookingInstructions = jsonObject.get("cookingInstructions")?.asString ?: ""
    val totalCalories = jsonObject.get("totalCalories")?.asString ?: ""
    val fat = jsonObject.get("fat")?.asString ?: ""
    val protein = jsonObject.get("protein")?.asString ?: ""
    val carbs = jsonObject.get("carbs")?.asString ?: ""
    val fiber = jsonObject.get("fiber")?.asString ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Suggested Recipe",
                        color = Color.Black // Make the title text black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.Black // Ensure the back icon is black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White // Set the top bar background to white
                )
            )
        },
        containerColor = Color.White // Set Scaffold background to white
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize().padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                Text(text = "${menuName}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Ingredients", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "${ingredients}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Cooking Instructions", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "${cookingInstructions}")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Nutrition", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Total Calories: ${totalCalories}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Fat: ${fat}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Protein: ${protein}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Carbs: ${carbs}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Fiber: ${fiber}")
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        saveResponseToFirestore(
                            menuName,
                            ingredients,
                            cookingInstructions,
                            totalCalories,
                            fat,
                            protein,
                            carbs,
                            fiber
                        )
                        Toast.makeText(
                            context,
                            "Recipe Saved",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text("Save Recipe")
                }
            }
        }


    }
}

