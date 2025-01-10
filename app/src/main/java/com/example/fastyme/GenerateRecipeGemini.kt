package com.example.fastyme

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun UploadPhoto() {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Generate Low-Calorie Recipe",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Upload an image or take a photo of your ingredients to generate a healthy, low-calorie recipe.",
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

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap.value, 800, 800, false) // Resize to smaller dimensions

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                bitmap?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val responseJson = GeminiRecipeRecommendation(resizedBitmap.asImageBitmap())
                            if(responseJson!=null) {


                            saveResponseToFirestore(responseJson)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Recipe generated and saved successfully!", Toast.LENGTH_SHORT).show()
                            }
                            } else {
                                Toast.makeText(context, "Failed to generate. Please submit more clear image", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Log.e("GeminiAPI", "Error: ${e.message}")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Failed to generate recipe.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } ?: Toast.makeText(context, "Please select an image first.", Toast.LENGTH_SHORT).show()
            }
        )
                {
            Text("Submit")
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
        val jsonResponse = response.text ?: "{}"
        Log.d("GeminiAPI", "Response: $jsonResponse") // Log the response
        jsonResponse
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
suspend fun saveResponseToFirestore(response: String) {
    try {
        val jsonObject = cleanAndParseJsonResponse(response)
        if (jsonObject != null) {
            val menuName = jsonObject.get("menuName")?.asString
            val ingredients = jsonObject.get("ingredients")?.asString
            val cookingInstructions = jsonObject.get("cookingInstructions")?.asString
            val totalCalories = jsonObject.get("totalCalories")?.asString
            val fat = jsonObject.get("fat")?.asString
            val protein = jsonObject.get("protein")?.asString
            val carbs = jsonObject.get("carbs")?.asString
            val fiber = jsonObject.get("fiber")?.asString


            val collectionRef = db.collection("Diet Menus").document(userId).collection("Saved Menus").document(menuName.toString())
            val dietMenu = DietMenu(menuName.toString(), ingredients.toString(), cookingInstructions.toString(), totalCalories.toString(), fat.toString(), protein.toString(), carbs.toString(), fiber.toString())

            collectionRef.set(dietMenu).await()
        } else {
            Log.e("FirebaseSave", "No valid JSON extracted.")
        }
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
