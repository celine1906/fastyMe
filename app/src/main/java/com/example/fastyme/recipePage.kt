package com.example.fastyme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun RecipeApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "recipe_page") {
        composable("recipe_page") {
            RecipePage(navController)
        }
        composable("category/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            RecipeCategoryScreen(navController, categoryName)
        }
        composable("detail_recipe/{recipeTitle}") { backStackEntry ->
            val recipeTitle = backStackEntry.arguments?.getString("recipeTitle") ?: ""
            DetailRecipePage(navController = navController, recipeTitle = recipeTitle)
        }
    }
}

@Composable
fun RecipePage(navController: androidx.navigation.NavController) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Header()
                Spacer(modifier = Modifier.height(16.dp))
                RecipeCategories(navController)
                PopularRecipes(navController)
            }
        }
    }
}

@Composable
fun Header() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.header_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.fastylogo),
                contentDescription = "FastyLogo",
                modifier = Modifier
                    .height(50.dp),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(Color.Black)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Specially tailored for your fasting success",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun RecipeCategories(navController: androidx.navigation.NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Recipe Categories",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 265.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories.size) { index ->
                CategoryCard(category = categories[index], iconRes = categoryIcons[index]) {
                    navController.navigate("category/${categories[index]}")
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: String, iconRes: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5E5F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCategoryScreen(navController: androidx.navigation.NavController, categoryName: String) {
    val recipes = getRecipesForCategory(categoryName)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        categoryName,
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
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp) // Add horizontal padding
                .background(Color.White) // Ensure LazyColumn background is white
        ) {
            item {
                Text(
                    text = "Recipes for $categoryName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp) // Padding to separate header from content
                )
            }
            items(recipes.size) { index ->
                val recipe = recipes[index]
                RecipeCard(
                    imageRes = recipe.imageRes,
                    title = recipe.title,
                    description = recipe.description,
                    tags = recipe.tags,
                    onClick = {
                        navController.navigate("detail_recipe/${recipe.title}")
                    }
                )
            }
        }
    }
}




data class Recipe(
    val imageRes: Int,
    val title: String,
    val description: String,
    val tags: List<String>
)

fun getRecipesForCategory(categoryName: String): List<Recipe> {
    return when (categoryName) {
        "Breakfast" -> listOf(
            Recipe(
                imageRes = R.drawable.chocolate_smoothie,
                title = "Chocolate Avocado Smoothie Bowl",
                description = "A healthy chocolate temptation to start your day.",
                tags = listOf("Vegetarian", "Gluten Free", "Sweet")
            ),
            Recipe(
                imageRes = R.drawable.oatmeal_berries,
                title = "Oatmeal with Fresh Berries",
                description = "Delicious and nutritious breakfast option.",
                tags = listOf("Fiber Rich", "Low Sugar")
            ),
            Recipe(
                imageRes = R.drawable.chocolate_smoothie,
                title = "Chocolate Avocado Smoothie Bowl",
                description = "A healthy chocolate temptation to start your day.",
                tags = listOf("Vegetarian", "Gluten Free", "Sweet")
            ),
            Recipe(
                imageRes = R.drawable.oatmeal_berries,
                title = "Oatmeal with Fresh Berries",
                description = "Delicious and nutritious breakfast option.",
                tags = listOf("Fiber Rich", "Low Sugar")
            )
        )

        "Main Dishes" -> listOf(
            Recipe(
                imageRes = R.drawable.chocolate_smoothie,
                title = "Chocolate Avocado Smoothie Bowl",
                description = "A healthy chocolate temptation to start your day.",
                tags = listOf("Vegetarian", "Gluten Free", "Sweet")
            ),
            Recipe(
                imageRes = R.drawable.oatmeal_berries,
                title = "Oatmeal with Fresh Berries",
                description = "Delicious and nutritious breakfast option.",
                tags = listOf("Fiber Rich", "Low Sugar")
            ),
            Recipe(
                imageRes = R.drawable.chocolate_smoothie,
                title = "Chocolate Avocado Smoothie Bowl",
                description = "A healthy chocolate temptation to start your day.",
                tags = listOf("Vegetarian", "Gluten Free", "Sweet")
            ),
            Recipe(
                imageRes = R.drawable.oatmeal_berries,
                title = "Oatmeal with Fresh Berries",
                description = "Delicious and nutritious breakfast option.",
                tags = listOf("Fiber Rich", "Low Sugar")
            )
        )

        "Soup & Salads" -> listOf(
            Recipe(
                imageRes = R.drawable.chocolate_smoothie,
                title = "Chocolate Avocado Smoothie Bowl",
                description = "A healthy chocolate temptation to start your day.",
                tags = listOf("Vegetarian", "Gluten Free", "Sweet")
            ),
            Recipe(
                imageRes = R.drawable.oatmeal_berries,
                title = "Oatmeal with Fresh Berries",
                description = "Delicious and nutritious breakfast option.",
                tags = listOf("Fiber Rich", "Low Sugar")
            ),
            Recipe(
                imageRes = R.drawable.chocolate_smoothie,
                title = "Chocolate Avocado Smoothie Bowl",
                description = "A healthy chocolate temptation to start your day.",
                tags = listOf("Vegetarian", "Gluten Free", "Sweet")
            ),
            Recipe(
                imageRes = R.drawable.oatmeal_berries,
                title = "Oatmeal with Fresh Berries",
                description = "Delicious and nutritious breakfast option.",
                tags = listOf("Fiber Rich", "Low Sugar")
            )
        )

        "Desserts" -> listOf(
            Recipe(
                imageRes = R.drawable.chocolate_smoothie,
                title = "Chocolate Avocado Smoothie Bowl",
                description = "A healthy chocolate temptation to start your day.",
                tags = listOf("Vegetarian", "Gluten Free", "Sweet")
            ),
            Recipe(
                imageRes = R.drawable.oatmeal_berries,
                title = "Oatmeal with Fresh Berries",
                description = "Delicious and nutritious breakfast option.",
                tags = listOf("Fiber Rich", "Low Sugar")
            ),
            Recipe(
                imageRes = R.drawable.chocolate_smoothie,
                title = "Chocolate Avocado Smoothie Bowl",
                description = "A healthy chocolate temptation to start your day.",
                tags = listOf("Vegetarian", "Gluten Free", "Sweet")
            ),
            Recipe(
                imageRes = R.drawable.oatmeal_berries,
                title = "Oatmeal with Fresh Berries",
                description = "Delicious and nutritious breakfast option.",
                tags = listOf("Fiber Rich", "Low Sugar")
            )
        )

        "Snacks" -> listOf(
            Recipe(
                imageRes = R.drawable.chocolate_smoothie,
                title = "Chocolate Avocado Smoothie Bowl",
                description = "A healthy chocolate temptation to start your day.",
                tags = listOf("Vegetarian", "Gluten Free", "Sweet")
            ),
            Recipe(
                imageRes = R.drawable.oatmeal_berries,
                title = "Oatmeal with Fresh Berries",
                description = "Delicious and nutritious breakfast option.",
                tags = listOf("Fiber Rich", "Low Sugar")
            ),
            Recipe(
                imageRes = R.drawable.chocolate_smoothie,
                title = "Chocolate Avocado Smoothie Bowl",
                description = "A healthy chocolate temptation to start your day.",
                tags = listOf("Vegetarian", "Gluten Free", "Sweet")
            ),
            Recipe(
                imageRes = R.drawable.oatmeal_berries,
                title = "Oatmeal with Fresh Berries",
                description = "Delicious and nutritious breakfast option.",
                tags = listOf("Fiber Rich", "Low Sugar")
            )
        )

        "Favourites" -> listOf(
            Recipe(
                imageRes = R.drawable.chocolate_smoothie,
                title = "Chocolate Avocado Smoothie Bowl",
                description = "A healthy chocolate temptation to start your day.",
                tags = listOf("Vegetarian", "Gluten Free", "Sweet")
            ),
            Recipe(
                imageRes = R.drawable.oatmeal_berries,
                title = "Oatmeal with Fresh Berries",
                description = "Delicious and nutritious breakfast option.",
                tags = listOf("Fiber Rich", "Low Sugar")
            ),
            Recipe(
                imageRes = R.drawable.chocolate_smoothie,
                title = "Chocolate Avocado Smoothie Bowl",
                description = "A healthy chocolate temptation to start your day.",
                tags = listOf("Vegetarian", "Gluten Free", "Sweet")
            ),
            Recipe(
                imageRes = R.drawable.oatmeal_berries,
                title = "Oatmeal with Fresh Berries",
                description = "Delicious and nutritious breakfast option.",
                tags = listOf("Fiber Rich", "Low Sugar")
            )
        )
        else -> emptyList()
    }
}

@Composable
fun PopularRecipes(navController: androidx.navigation.NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Popular Recipes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        RecipeCard(
            imageRes = R.drawable.chocolate_smoothie,
            title = "Chocolate Avocado Smoothie Bowl",
            description = "Satisfy your morning appetite with this chocolate temptation",
            tags = listOf("Vegetarian", "Gluten Free", "After Fasting", "Sweet"),
            onClick = { navController.navigate("detail_recipe/Chocolate Avocado Smoothie Bowl") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        RecipeCard(
            imageRes = R.drawable.oatmeal_berries,
            title = "Oatmeal with Fresh Berries",
            description = "A healthy and tasty choice for your breakfast.",
            tags = listOf("Vegetarian", "Low Sugar", "Fiber Rich"),
            onClick = { navController.navigate("detail_recipe/Oatmeal with Fresh Berries") }
        )
    }
}

@Composable
fun DetailRecipePage(navController: androidx.navigation.NavController, recipeTitle: String) {
    // Data resep berdasarkan recipeTitle
    val recipeData = mapOf(
        "Chocolate Avocado Smoothie Bowl" to Pair(
            "Ingredients:\n- 1 ripe avocado\n- 1 banana\n- 1 cup milk\n- 1 tbsp honey\n- Ice cubes (optional)\n..." +
                    "\n\nInstructions:\n1. Peel and slice the avocado and banana.\n2. Add all ingredients to a blender.\n3. Blend until smooth.\n4. Pour into a bowl and add toppings of your choice.\n...",
            R.drawable.chocolate_smoothie
        ),
        "Oatmeal with Fresh Berries" to Pair(
            "Ingredients:\n- 2 cups rolled oats\n- 2 cups milk\n- 1 tbsp honey\n- 1 cup mixed berries (strawberries, blueberries, raspberries)\n..." +
                    "\n\nInstructions:\n1. Bring the milk to a boil in a saucepan.\n2. Add oats and reduce heat to simmer.\n3. Cook for 5-7 minutes until thickened.\n4. Top with fresh berries and honey.\n...",
            R.drawable.oatmeal_berries
        ),
        // Other recipes...
    )

    // Nutritional data for each recipe
    val nutritionalData = mapOf(
        "Chocolate Avocado Smoothie Bowl" to NutritionalInfo(
            carbohydrates = 35f, fat = 15f, protein = 5f, calories = 300
        ),
        "Oatmeal with Fresh Berries" to NutritionalInfo(
            carbohydrates = 50f, fat = 8f, protein = 10f, calories = 350
        ),
        // Add nutritional data for other recipes...
    )

    // Retrieve the recipe data based on the recipe title
    val (recipeText, imageResource) = recipeData[recipeTitle] ?: Pair("Recipe not found.", R.drawable.chocolate_smoothie)
    val nutritionalInfo = nutritionalData[recipeTitle] ?: NutritionalInfo(0f, 0f, 0f, 0)

    // Split the recipe text into ingredients and instructions
    val (ingredients, instructions) = recipeText.split("\n\nInstructions:").let {
        if (it.size == 2) it[0] to it[1] else "" to ""
    }

    // Description for each recipe
    val recipeDescription = mapOf(
        "Chocolate Avocado Smoothie Bowl" to "This smoothie bowl combines the creamy texture of avocado with the sweetness of banana and honey. It's a nutritious and delicious breakfast or snack option packed with healthy fats and fiber.",
        "Oatmeal with Fresh Berries" to "A warm and hearty bowl of oatmeal topped with fresh berries. This dish is full of antioxidants and makes for a satisfying and wholesome breakfast.",
        // Add descriptions for other recipes...
    )

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            item {
                // Back button
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                        contentDescription = "Back",
                        tint = Color.Black  // Set the icon color to black
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Recipe title
                Text(
                    text = recipeTitle,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Food image with more descriptive contentDescription
                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = "Food image of $recipeTitle",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Recipe description
                Text(
                    text = recipeDescription[recipeTitle] ?: "No description available for this recipe.",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5E5F7))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Nutritional Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black // Menambahkan warna hitam pada teks judul
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "Carbs:",
                                fontWeight = FontWeight.Medium,
                                color = Color.Black // Menambahkan warna hitam pada teks "Carbs"
                            )
                            Text(
                                text = "${nutritionalInfo.carbohydrates}g",
                                color = Color.Black // Menambahkan warna hitam pada teks nilai carbs
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "Fat:",
                                fontWeight = FontWeight.Medium,
                                color = Color.Black // Menambahkan warna hitam pada teks "Fat"
                            )
                            Text(
                                text = "${nutritionalInfo.fat}g",
                                color = Color.Black // Menambahkan warna hitam pada teks nilai fat
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "Protein:",
                                fontWeight = FontWeight.Medium,
                                color = Color.Black // Menambahkan warna hitam pada teks "Protein"
                            )
                            Text(
                                text = "${nutritionalInfo.protein}g",
                                color = Color.Black // Menambahkan warna hitam pada teks nilai protein
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "Calories:",
                                fontWeight = FontWeight.Medium,
                                color = Color.Black // Menambahkan warna hitam pada teks "Calories"
                            )
                            Text(
                                text = "${nutritionalInfo.calories} kcal",
                                color = Color.Black // Menambahkan warna hitam pada teks nilai calories
                            )
                        }
                    }
                }

                // Combined ingredients and instructions
                Text(
                    text = buildAnnotatedString {
                        append(ingredients)  // Ingredients text
                        append("\n\n") // Spacer between ingredients and instructions
                        append("Instructions:\n") // Label for instructions
                        append(instructions)  // Instructions text
                    },
                    fontSize = 14.sp, // Adjust font size for both ingredients and instructions
                    color = Color.Black
                )
            }
        }
    }
}

// NutritionalInfo data class
data class NutritionalInfo(
    val carbohydrates: Float,
    val fat: Float,
    val protein: Float,
    val calories: Int
)



@Composable
fun RecipeCard(
    imageRes: Int,
    title: String,
    description: String,
    tags: List<String>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5E5F7))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    tags.forEach { tag ->
                        Tag(tag)
                    }
                }
            }
        }
    }
}


@Composable
fun Tag(tag: String) {
    Surface(
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(50),
        color = Color(0xFF5624C4),
        tonalElevation = 2.dp
    ) {
        Text(
            text = tag,
            fontSize = 12.sp,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

val categories =
    listOf("Breakfast", "Main Dishes", "Soup & Salads", "Desserts", "Snacks", "Favorites")
val categoryIcons = listOf(
    R.drawable.ic_breakfast,
    R.drawable.ic_main_dishes,
    R.drawable.ic_soup_salads,
    R.drawable.ic_desserts,
    R.drawable.ic_snacks,
    R.drawable.ic_favorites
)
