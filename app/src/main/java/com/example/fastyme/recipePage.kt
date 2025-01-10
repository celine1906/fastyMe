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
import androidx.navigation.NavController
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
        composable("uploadPhoto") {
            UploadPhoto()
        }
        composable("savedRecipe") {
            UploadPhoto()
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
                ButtonGenerateRecipe(navController)
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Specially tailored for your fasting success",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ButtonGenerateRecipe(navController: NavController) {
    Button(
        onClick = {
            navController.navigate("uploadPhoto")
        },
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    )
    {
        Text("Scan Ingredients for Low-Calorie Recipe")
    }
}

@Composable
fun ButtonSavedRecipe(navController: NavController) {
    Button(
        onClick = {
            navController.navigate("savedRecipe")
        },
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    )
    {
        Text("Saved recipe")
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
                imageRes = R.drawable.overnight_oats,
                title = "Overnight Oats with Chia Seeds",
                description = "A quick and healthy breakfast option with chia seeds and fresh fruits.",
                tags = listOf("Fiber Rich", "Low Sugar", "Vegetarian")
            ),
            Recipe(
                imageRes = R.drawable.classic_pancakes,
                title = "Classic Pancakes",
                description = "Fluffy and delicious pancakes for a great start to your day.",
                tags = listOf("Sweet", "Comfort Food")
            )
        )

        "Main Dishes" -> listOf(
            Recipe(
                imageRes = R.drawable.grilled_lemon_chicken,
                title = "Grilled Lemon Herb Chicken",
                description = "Juicy chicken breasts marinated with lemon and herbs, grilled to perfection.",
                tags = listOf("High Protein", "Low Carb")
            ),
            Recipe(
                imageRes = R.drawable.spaghetti_aglio_olio,
                title = "Spaghetti Aglio e Olio",
                description = "A simple yet flavorful Italian pasta dish with garlic and olive oil.",
                tags = listOf("Vegetarian", "Quick Meal")
            )
        )

        "Soup & Salads" -> listOf(
            Recipe(
                imageRes = R.drawable.tomato_basil_soup,
                title = "Tomato Basil Soup",
                description = "A comforting bowl of fresh tomato soup garnished with basil.",
                tags = listOf("Low Calorie", "Vegetarian")
            ),
            Recipe(
                imageRes = R.drawable.caesar_salad,
                title = "Caesar Salad",
                description = "Crisp romaine lettuce with creamy Caesar dressing and croutons.",
                tags = listOf("Light Meal", "Vegetarian")
            )
        )

        "Desserts" -> listOf(
            Recipe(
                imageRes = R.drawable.chocolate_lava_cake,
                title = "Chocolate Lava Cake",
                description = "Decadent chocolate cake with a gooey molten center.",
                tags = listOf("Sweet", "Indulgent")
            ),
            Recipe(
                imageRes = R.drawable.mango_sticky_rice,
                title = "Mango Sticky Rice",
                description = "A classic Thai dessert featuring sweet sticky rice and fresh mango slices.",
                tags = listOf("Sweet", "Exotic")
            )
        )

        "Snacks" -> listOf(
            Recipe(
                imageRes = R.drawable.sweet_potato_fries,
                title = "Sweet Potato Fries",
                description = "Crispy and flavorful sweet potato fries seasoned with paprika.",
                tags = listOf("Vegetarian", "Quick Snack")
            ),
            Recipe(
                imageRes = R.drawable.trail_mix_bites,
                title = "Trail Mix Energy Bites",
                description = "Nutritious and delicious no-bake bites packed with dried fruits and nuts.",
                tags = listOf("Healthy", "Energy Boost")
            )
        )

        "Favorites" -> listOf(
            Recipe(
                imageRes = R.drawable.margherita_pizza,
                title = "Margherita Pizza",
                description = "A classic pizza topped with fresh tomatoes, mozzarella, and basil.",
                tags = listOf("Vegetarian", "Comfort Food")
            ),
            Recipe(
                imageRes = R.drawable.teriyaki_chicken_bowl,
                title = "Teriyaki Chicken Bowl",
                description = "Savory teriyaki chicken served over a bowl of steamed rice.",
                tags = listOf("High Protein", "Comfort Food")
            )
        )
        else -> emptyList()
    }
}

//INI BELUM DIEDIT PUPULERNYAA
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
            imageRes = R.drawable.spaghetti_aglio_olio,
            title = "Spaghetti Aglio e Olio",
            description = "A simple yet elegant Italian pasta dish featuring garlic, olive oil, and a touch of chili flakes for a delightful kick.",
            tags = listOf("Vegetarian", "Quick Meal", "Comfort Food"),
            onClick = { navController.navigate("detail_recipe/Spaghetti Aglio e Olio") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        RecipeCard(
            imageRes = R.drawable.margherita_pizza,
            title = "Margherita Pizza",
            description = "A timeless pizza classic with a crispy crust, tangy tomato sauce, melted mozzarella, and fresh basil leaves.",
            tags = listOf("Vegetarian", "Comfort Food"),
            onClick = { navController.navigate("detail_recipe/Margherita Pizza") }
        )
    }
}

@Composable
fun DetailRecipePage(navController: androidx.navigation.NavController, recipeTitle: String) {
    // ISI RESEP KALAU DI KLIK
    val recipeData = mapOf(
        "Overnight Oats with Chia Seeds" to Pair(
            "Ingredients:\n- 1/2 cup rolled oats\n- 1 cup almond milk (or any milk of choice)\n- 1 tablespoon chia seeds\n- 1 tablespoon honey or maple syrup\n- Fresh fruits (e.g., berries, banana slices)\n..." +
                    "\n\nInstructions:\n1. In a mason jar, combine oats, almond milk, chia seeds, and honey. Mix well.\n2. Cover and refrigerate overnight, or for at least 6 hours.\n3. In the morning, stir the mixture and top it with fresh fruits before serving.",
            R.drawable.overnight_oats
        ),
        "Classic Pancakes" to Pair(
            "Ingredients:\n- 1 cup all-purpose flour\n- 1 cup milk\n- 1 egg\n- 2 tablespoons sugar\n- 1 teaspoon baking powder\n- 2 tablespoons melted butter\n..." +
                    "\n\nInstructions:\n1. In a mixing bowl, whisk together flour, sugar, and baking powder.\n2. Add milk, egg, and melted butter. Mix until the batter is smooth.\n3. Heat a non-stick pan over medium heat and lightly grease it.\n4. Pour a ladle of batter onto the pan and cook until bubbles form on the surface. Flip and cook the other side until golden brown.\n5. Serve warm with syrup, fruits, or butter.",
            R.drawable.classic_pancakes
        ),
        "Grilled Lemon Herb Chicken" to Pair(
            "Ingredients:\n- 2 chicken breasts\n- 2 tablespoons lemon juice\n- 1 tablespoon olive oil\n- 1 teaspoon minced garlic\n- 1 teaspoon dried rosemary\n- Salt and pepper to taste\n..." +
                    "\n\nInstructions:\n1. In a bowl, mix lemon juice, olive oil, garlic, rosemary, salt, and pepper to make the marinade.\n2. Coat the chicken breasts with the marinade and let sit for 30 minutes.\n3. Preheat the grill or grill pan. Cook the chicken for 6-8 minutes on each side, or until fully cooked.\n4. Serve with vegetables or rice.",
            R.drawable.grilled_lemon_chicken
        ),
        "Spaghetti Aglio e Olio" to Pair(
            "Ingredients:\n- 200g spaghetti\n- 3 tablespoons olive oil\n- 3 garlic cloves (thinly sliced)\n- 1/2 teaspoon chili flakes\n- Fresh parsley (chopped)\n- Parmesan cheese (optional)\n..." +
                    "\n\nInstructions:\n1. Boil spaghetti in salted water until al dente. Reserve some pasta water and drain the rest.\n2. Heat olive oil in a frying pan and sauté garlic until golden. Add chili flakes.\n3. Toss the spaghetti into the pan and mix well. Add a splash of reserved pasta water for extra moisture.\n4. Garnish with parsley and Parmesan cheese before serving.",
            R.drawable.spaghetti_aglio_olio
        ),
        "Chocolate Lava Cake" to Pair(
            "Ingredients:\n- 150g dark chocolate\n- 100g unsalted butter\n- 2 eggs\n- 1/3 cup sugar\n- 1/4 cup all-purpose flour\n..." +
                    "\n\nInstructions:\n1. Preheat oven to 200°C (390°F). Grease ramekins with butter.\n2. Melt dark chocolate and butter in a microwave or double boiler.\n3. In a bowl, whisk eggs and sugar until frothy. Add melted chocolate mixture and mix well.\n4. Gently fold in flour until just combined.\n5. Pour batter into ramekins and bake for 10-12 minutes. The edges should be firm, but the center should be gooey.\n6. Let cool slightly before serving.",
            R.drawable.chocolate_lava_cake
        ),
        "Mango Sticky Rice" to Pair(
            "Ingredients:\n- 1 cup glutinous rice\n- 1 cup coconut milk\n- 1/4 cup sugar\n- 1 ripe mango, sliced\n..." +
                    "\n\nInstructions:\n1. Steam the glutinous rice until fully cooked (about 20 minutes).\n2. Heat coconut milk and sugar in a saucepan over low heat until the sugar dissolves.\n3. Mix half of the coconut milk into the cooked rice. Let it rest for 10 minutes to absorb the flavor.\n4. Serve rice with sliced mango and drizzle the remaining coconut milk on top.",
            R.drawable.mango_sticky_rice
        ),
        "Tomato Basil Soup" to Pair(
            "Ingredients:\n- 5 ripe tomatoes, chopped\n- 1 medium onion, diced\n- 2 garlic cloves, minced\n- 2 cups vegetable broth\n- 1 tablespoon olive oil\n- Fresh basil leaves (for garnish)\n- Salt and pepper to taste\n..." +
                    "\n\nInstructions:\n1. Heat olive oil in a pot. Sauté onion and garlic until fragrant.\n2. Add chopped tomatoes and cook for 5 minutes. Season with salt and pepper.\n3. Pour in vegetable broth and simmer for 20 minutes.\n4. Blend the mixture until smooth using a blender. Return to the pot to heat through.\n5. Serve in a bowl, garnished with fresh basil leaves.",
            R.drawable.tomato_basil_soup
        ),
        "Caesar Salad" to Pair(
            "Ingredients:\n- 1 head romaine lettuce, chopped\n- 1/2 cup croutons\n- 1/4 cup Parmesan cheese, grated\n- 3 tablespoons Caesar dressing\n..." +
                    "\n\nInstructions:\n1. In a large bowl, toss romaine lettuce with Caesar dressing until evenly coated.\n2. Add croutons and grated Parmesan cheese. Toss gently.\n3. Serve chilled in individual portions.",
            R.drawable.caesar_salad
        ),
        "Sweet Potato Fries" to Pair(
            "Ingredients:\n- 2 sweet potatoes, cut into thin strips\n- 2 tablespoons olive oil\n- 1/2 teaspoon salt\n- 1/2 teaspoon paprika\n..." +
                    "\n\nInstructions:\n1. Preheat oven to 200°C (390°F). Line a baking tray with parchment paper.\n2. Toss sweet potato strips with olive oil, salt, and paprika in a bowl.\n3. Spread the fries evenly on the tray and bake for 20-25 minutes, flipping halfway through.\n4. Serve warm with your favorite dip.",
            R.drawable.sweet_potato_fries
        ),
        "Trail Mix Energy Bites" to Pair(
            "Ingredients:\n- 1 cup rolled oats\n- 1/2 cup peanut butter\n- 1/4 cup honey\n- 1/4 cup dried fruits (e.g., raisins, cranberries)\n- 1/4 cup chocolate chips\n..." +
                    "\n\nInstructions:\n1. Combine all ingredients in a mixing bowl. Stir until a sticky dough forms.\n2. Roll the mixture into small balls and place them on a baking sheet.\n3. Refrigerate for 1 hour to set.\n4. Store in an airtight container for up to a week.",
            R.drawable.trail_mix_bites
        ),
        "Margherita Pizza" to Pair(
            "Ingredients:\n- 1 pizza base\n- 1/2 cup tomato sauce\n- 1 cup mozzarella cheese, shredded\n- Fresh basil leaves\n..." +
                    "\n\nInstructions:\n1. Preheat oven to 220°C (430°F).\n2. Spread tomato sauce evenly over the pizza base.\n3. Sprinkle mozzarella cheese and add basil leaves on top.\n4. Bake for 10-12 minutes, or until the cheese is melted and bubbly.\n5. Slice and serve warm.",
            R.drawable.margherita_pizza
        ),
        "Teriyaki Chicken Bowl" to Pair(
            "Ingredients:\n- 2 chicken thighs, cut into strips\n- 1/3 cup teriyaki sauce\n- 2 cups steamed rice\n- 1 tablespoon sesame seeds\n- 2 green onions, sliced\n..." +
                    "\n\nInstructions:\n1. Heat a skillet and cook chicken strips until browned.\n2. Add teriyaki sauce and simmer for 5 minutes, coating the chicken.\n3. Serve over steamed rice, garnished with sesame seeds and green onions.",
            R.drawable.teriyaki_chicken_bowl
        )
    )


    // NUTRISI TIAP RESEP
    val nutritionalData = mapOf(
        "Overnight Oats with Chia Seeds" to NutritionalInfo(
            carbohydrates = 50f, fat = 7f, protein = 8f, calories = 250
        ),
        "Classic Pancakes" to NutritionalInfo(
            carbohydrates = 30f, fat = 8f, protein = 6f, calories = 220
        ),
        "Grilled Lemon Herb Chicken" to NutritionalInfo(
            carbohydrates = 2f, fat = 8f, protein = 40f, calories = 250
        ),
        "Spaghetti Aglio e Olio" to NutritionalInfo(
            carbohydrates = 60f, fat = 10f, protein = 12f, calories = 400
        ),
        "Chocolate Lava Cake" to NutritionalInfo(
            carbohydrates = 25f, fat = 18f, protein = 4f, calories = 290
        ),
        "Mango Sticky Rice" to NutritionalInfo(
            carbohydrates = 60f, fat = 5f, protein = 4f, calories = 300
        ),
        "Tomato Basil Soup" to NutritionalInfo(
            carbohydrates = 18f, fat = 4f, protein = 2f, calories = 100
        ),
        "Caesar Salad" to NutritionalInfo(
            carbohydrates = 10f, fat = 12f, protein = 5f, calories = 180
        ),
        "Sweet Potato Fries" to NutritionalInfo(
            carbohydrates = 20f, fat = 7f, protein = 2f, calories = 150
        ),
        "Trail Mix Energy Bites" to NutritionalInfo(
            carbohydrates = 15f, fat = 9f, protein = 5f, calories = 180
        ),
        "Margherita Pizza" to NutritionalInfo(
            carbohydrates = 40f, fat = 12f, protein = 8f, calories = 280
        ),
        "Teriyaki Chicken Bowl" to NutritionalInfo(
            carbohydrates = 50f, fat = 7f, protein = 25f, calories = 350
        )
    )
    // Retrieve the recipe data based on the recipe title
    val (recipeText, imageResource) = recipeData[recipeTitle] ?: Pair("Recipe not found.", R.drawable.chocolate_smoothie)
    val nutritionalInfo = nutritionalData[recipeTitle] ?: NutritionalInfo(0f, 0f, 0f, 0)

    // Split the recipe text into ingredients and instructions
    val (ingredients, instructions) = recipeText.split("\n\nInstructions:").let {
        if (it.size == 2) it[0] to it[1] else "" to ""
    }

    // DESCRIPSI TIAP RESEP
    val recipeDescription = mapOf(
        "Overnight Oats with Chia Seeds" to "A perfect blend of oats, chia seeds, and almond milk, this breakfast is packed with fiber and topped with fresh fruits for a delightful start to your day.",
        "Classic Pancakes" to "Soft and fluffy pancakes, perfect with syrup, fruits, or butter for a sweet and satisfying breakfast.",
        "Grilled Lemon Herb Chicken" to "Tender chicken breasts marinated in a zesty lemon herb blend, grilled to perfection for a flavorful and healthy main dish.",
        "Spaghetti Aglio e Olio" to "A simple yet elegant Italian pasta dish featuring garlic, olive oil, and a touch of chili flakes for a delightful kick.",
        "Chocolate Lava Cake" to "Indulge in a rich and gooey chocolate dessert with a molten center that melts in your mouth.",
        "Mango Sticky Rice" to "A classic Thai dessert with sweet sticky rice, rich coconut milk, and fresh mango slices for a tropical delight.",
        "Tomato Basil Soup" to "A comforting bowl of soup made with ripe tomatoes, fresh basil, and a hint of garlic, perfect for any season.",
        "Caesar Salad" to "Crispy romaine lettuce tossed with creamy Caesar dressing, crunchy croutons, and grated Parmesan cheese for a classic salad experience.",
        "Sweet Potato Fries" to "Golden and crispy sweet potato fries seasoned with paprika for a healthy and flavorful snack option.",
        "Trail Mix Energy Bites" to "Nutritious and delicious no-bake bites made with oats, peanut butter, dried fruits, and chocolate chips, perfect for an energy boost.",
        "Margherita Pizza" to "A timeless pizza classic with a crispy crust, tangy tomato sauce, melted mozzarella, and fresh basil leaves.",
        "Teriyaki Chicken Bowl" to "Juicy chicken strips glazed in teriyaki sauce, served over steamed rice and garnished with sesame seeds and green onions for an irresistible meal."
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
