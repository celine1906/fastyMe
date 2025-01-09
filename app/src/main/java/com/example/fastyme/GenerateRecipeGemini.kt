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
import com.google.gson.JsonSyntaxException
import extractJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream

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

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap.value, 800, 800, false) // Resize to smaller dimensions

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                bitmap?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val responseJson = GeminiRecipeRecommendation(resizedBitmap.asImageBitmap())
                            val cleanedResponse = cleanJsonResponse(responseJson)
                            saveResponseToFirestore(cleanedResponse)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Recipe generated and saved successfully!", Toast.LENGTH_SHORT).show()
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

fun cleanJsonResponse(response: String): String {
    // Menghapus tanda backticks (```) dari respons JSON
    return response.replace("\n", "").replace("\r", "").replace("```", "")
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
    try{

        val jsonObject = extractJsonn(response) // Ensure this is a valid JSON
        Log.e("JSONOBJECT", "${jsonObject}")

        if (jsonObject != null) {
//        val menuName = jsonObject.getString("menuName")
//        val ingredients = jsonObject.getString("ingredients")
//        val cookingInstructions = jsonObject.getString("cookingInstructions")
//        val totalCalories = jsonObject.getString("totalCalories")
//        val fat = jsonObject.getString("fat")
//        val protein = jsonObject.getString("protein")
//        val carbs = jsonObject.getString("carbs")
//        val fiber = jsonObject.getString("fiber")

//        val jsonObject = extractJson(response)

            val menuName = jsonObject.get("menuName") ?: ""
            val ingredients = jsonObject.get("ingredients")?: ""
            val cookingInstructions = jsonObject.get("cookingInstructions") ?: ""
            val totalCalories = jsonObject.get("totalCalories") ?: ""
            val fat = jsonObject.get("fat") ?: ""
            val protein = jsonObject.get("protein") ?: ""
            val carbs = jsonObject.get("carbs") ?: ""
            val fiber = jsonObject.get("fiber") ?: ""
    val collectionRef = db.collection("Diet Menus").document(userId.toString()).collection("Saved Menus").document("${menuName}")


    val dietMenu = DietMenu(
        menuName = menuName.toString(),
        ingredients = ingredients.toString(),
        cookingInstructions = cookingInstructions.toString(),
        totalCalories = totalCalories.toString(),
        fat = fat.toString(),
        protein = protein.toString(),
        carbs = carbs.toString(),
        fiber = fiber.toString()
    )

    collectionRef.set(dietMenu).await()
        } else {
            Log.e("FirebaseSave", "No valid JSON extracted.")
        }
} catch (e: JSONException) {
    Log.e("FirebaseSave", "Invalid JSON format: ${e.message}")
} catch (e: Exception) {
    Log.e("FirebaseSave", "Error saving to Firestore: ${e.message}")
}
}


fun extractJsonn(response: String): JsonObject? {
    return try {
        // Clean the response to ensure it's properly formatted
        val cleanedResponse = cleanJsonResponse(response)
        // Now parse it into a JsonObject
        Gson().fromJson(cleanedResponse, JsonObject::class.java)
    } catch (e: JsonSyntaxException) {
        Log.e("JSONParsing", "Error parsing JSON: ${e.message}")
        null
    }
}
