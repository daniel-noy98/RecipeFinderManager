package com.example.recipefindermanager.data

object SampleRecipes {
    val recipes = listOf(
        Recipe(
            id = "sample-carbonara",
            name = "Creamy Pasta Carbonara",
            description = "A creamy Italian pasta dish with crispy pancetta, parmesan, eggs, and black pepper.",
            category = "Dinner",
            cookTime = "25 min",
            servings = 4,
            imageUrl = "https://images.unsplash.com/photo-1551183053-bf91a1d81141?auto=format&fit=crop&w=1400&q=80",
            ingredients = listOf(
                "400g spaghetti",
                "200g pancetta or bacon, diced",
                "4 large eggs",
                "100g Parmesan cheese, grated",
                "2 cloves garlic, minced",
                "Salt and black pepper to taste",
                "Fresh parsley for garnish"
            ),
            instructions = """
                1. Cook spaghetti in salted boiling water until al dente.
                2. In a large pan, cook pancetta until crispy. Add garlic and cook for 1 minute.
                3. In a bowl, whisk together eggs and Parmesan cheese.
                4. Drain pasta, reserving 1 cup of pasta water. Add pasta to the pan with pancetta.
                5. Remove from heat and quickly stir in the egg mixture, adding pasta water to create a creamy sauce.
                6. Season with salt and pepper. Serve immediately with extra Parmesan and parsley.
            """.trimIndent(),
            public = true
        ),
        Recipe(
            id = "sample-pancakes",
            name = "Fluffy Blueberry Pancakes",
            description = "Soft breakfast pancakes with blueberries and a light, fluffy texture.",
            category = "Breakfast",
            cookTime = "20 min",
            servings = 4,
            imageUrl = "https://images.unsplash.com/photo-1528207776546-365bb710ee93?auto=format&fit=crop&w=1400&q=80",
            ingredients = listOf(
                "1 1/2 cups flour",
                "2 tablespoons sugar",
                "1 tablespoon baking powder",
                "1 cup milk",
                "1 large egg",
                "2 tablespoons melted butter",
                "1 cup blueberries"
            ),
            instructions = """
                1. Mix flour, sugar, and baking powder in a bowl.
                2. Whisk milk, egg, and melted butter in a separate bowl.
                3. Combine wet and dry ingredients gently.
                4. Fold in blueberries.
                5. Cook pancakes on a hot pan until bubbles form, then flip.
                6. Serve warm with maple syrup.
            """.trimIndent(),
            public = true
        ),
        Recipe(
            id = "sample-quinoa-salad",
            name = "Mediterranean Quinoa Salad",
            description = "A colorful lunch bowl with quinoa, vegetables, herbs, and a fresh lemon dressing.",
            category = "Lunch",
            cookTime = "30 min",
            servings = 6,
            imageUrl = "https://images.unsplash.com/photo-1623428187969-5da2dcea5ebf?auto=format&fit=crop&w=1400&q=80",
            ingredients = listOf(
                "2 cups cooked quinoa",
                "1 cucumber, diced",
                "1 cup cherry tomatoes",
                "1/2 red onion, sliced",
                "1/2 cup feta cheese",
                "1/4 cup olive oil",
                "Fresh lemon juice",
                "Salt, pepper, and parsley"
            ),
            instructions = """
                1. Cook quinoa and let it cool.
                2. Chop cucumber, tomatoes, and red onion.
                3. Mix quinoa and vegetables in a large bowl.
                4. Add feta cheese and parsley.
                5. Whisk olive oil, lemon juice, salt, and pepper.
                6. Toss everything together and serve chilled.
            """.trimIndent(),
            public = true
        )
    )

    fun getById(id: String): Recipe? = recipes.firstOrNull { it.id == id }
}
