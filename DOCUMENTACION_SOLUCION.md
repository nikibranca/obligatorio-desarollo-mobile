# Documentación de Solución - Aplicación de Gestión de Recetas Android

## Resumen del Proyecto
Aplicación nativa Android para organizar y gestionar colecciones de recetas con autenticación de usuarios, búsqueda externa de recetas, almacenamiento local y funcionalidades de gestión.

## Arquitectura Propuesta

### Patrón MVVM (Model-View-ViewModel)
- **Model**: Entidades de datos, repositorios, base de datos
- **View**: Activities, Fragments, layouts
- **ViewModel**: Lógica de presentación y estado de UI

### Estructura de Paquetes
```
com.app.recetas/
├── data/
│   ├── local/
│   │   ├── database/
│   │   ├── dao/
│   │   └── entities/
│   ├── remote/
│   │   ├── api/
│   │   └── dto/
│   └── repository/
├── domain/
│   ├── model/
│   └── usecase/
├── presentation/
│   ├── ui/
│   │   ├── auth/
│   │   ├── home/
│   │   ├── search/
│   │   ├── detail/
│   │   └── profile/
│   └── viewmodel/
└── utils/
```

## Componentes Principales

### 1. Autenticación (Firebase Authentication)

#### Dependencias
```kotlin
// build.gradle (app)
implementation 'com.google.firebase:firebase-auth:22.3.0'
implementation 'com.google.firebase:firebase-bom:32.7.0'
```

#### AuthRepository
```kotlin
class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    
    suspend fun login(email: String, password: String): Result<FirebaseUser?>
    suspend fun register(email: String, password: String): Result<FirebaseUser?>
    suspend fun logout()
    fun getCurrentUser(): FirebaseUser?
}
```

#### Pantallas de Autenticación
- **LoginActivity**: Email/password, validación de campos
- **RegisterActivity**: Registro de nuevos usuarios
- **SplashActivity**: Verificar estado de autenticación

### 2. Base de Datos Local (SQLite + Room)

#### Dependencias
```kotlin
implementation "androidx.room:room-runtime:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"
kapt "androidx.room:room-compiler:2.6.1"
```

#### Entidades
```kotlin
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val imageUrl: String?,
    val ingredients: String, // JSON string
    val personalNotes: String = "",
    val isPersonal: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis(),
    val dateModified: Long = System.currentTimeMillis()
)
```

#### DAO
```kotlin
@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY dateModified DESC")
    fun getAllRecipes(): Flow<List<Recipe>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)
    
    @Delete
    suspend fun deleteRecipe(recipe: Recipe)
    
    @Update
    suspend fun updateRecipe(recipe: Recipe)
    
    @Query("SELECT * FROM recipes ORDER BY dateModified DESC LIMIT 1")
    suspend fun getLastModifiedRecipe(): Recipe?
}
```

### 3. API Externa (TheMealDB)

#### Retrofit Setup
```kotlin
interface MealApiService {
    @GET("search.php")
    suspend fun searchByName(@Query("s") name: String): MealResponse
    
    @GET("filter.php")
    suspend fun searchByCategory(@Query("c") category: String): MealResponse
    
    @GET("filter.php")
    suspend fun searchByArea(@Query("a") area: String): MealResponse
    
    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse
    
    @GET("list.php?a=list")
    suspend fun getAreas(): AreaResponse
}
```

#### DTOs
```kotlin
data class MealResponse(
    val meals: List<MealDto>?
)

data class MealDto(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String,
    val strArea: String,
    val strInstructions: String,
    val strMealThumb: String,
    // ingredientes strIngredient1-20, strMeasure1-20
)
```

### 4. Pantallas Principales

#### MainActivity (Navigation Host)
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()
        showLastRecipe() // SharedPreferences
    }
}
```

#### HomeFragment (Lista de Recetas)
```kotlin
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: RecipeAdapter
    
    override fun onCreateView(...): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeRecipes()
        return binding.root
    }
    
    private fun setupRecyclerView() {
        adapter = RecipeAdapter { recipe ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeToDetail(recipe.id)
            )
        }
        binding.recyclerView.adapter = adapter
    }
}
```

#### SearchFragment
```kotlin
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    
    private fun setupSpinners() {
        // Spinner para categorías
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val adapter = ArrayAdapter(requireContext(), 
                android.R.layout.simple_spinner_item, categories)
            binding.spinnerCategory.adapter = adapter
        }
        
        // Spinner para áreas
        viewModel.areas.observe(viewLifecycleOwner) { areas ->
            val adapter = ArrayAdapter(requireContext(), 
                android.R.layout.simple_spinner_item, areas)
            binding.spinnerArea.adapter = adapter
        }
    }
}
```

#### RecipeDetailFragment
```kotlin
class RecipeDetailFragment : Fragment() {
    private lateinit var binding: FragmentRecipeDetailBinding
    private lateinit var viewModel: RecipeDetailViewModel
    
    private fun setupUI(recipe: Recipe) {
        binding.apply {
            textName.text = recipe.name
            textCategory.text = recipe.category
            textInstructions.text = recipe.instructions
            editPersonalNotes.setText(recipe.personalNotes)
            
            // Cargar imagen con Glide
            Glide.with(this@RecipeDetailFragment)
                .load(recipe.imageUrl)
                .into(imageRecipe)
        }
    }
}
```

### 5. ViewModels

#### HomeViewModel
```kotlin
class HomeViewModel(private val repository: RecipeRepository) : ViewModel() {
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> = _recipes
    
    init {
        viewModelScope.launch {
            repository.getAllRecipes().collect {
                _recipes.value = it
            }
        }
    }
    
    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }
}
```

#### SearchViewModel
```kotlin
class SearchViewModel(
    private val apiRepository: MealApiRepository,
    private val localRepository: RecipeRepository
) : ViewModel() {
    
    private val _searchResults = MutableLiveData<List<MealDto>>()
    val searchResults: LiveData<List<MealDto>> = _searchResults
    
    fun searchRecipes(query: String, type: SearchType) {
        viewModelScope.launch {
            try {
                val result = when(type) {
                    SearchType.NAME -> apiRepository.searchByName(query)
                    SearchType.CATEGORY -> apiRepository.searchByCategory(query)
                    SearchType.AREA -> apiRepository.searchByArea(query)
                }
                _searchResults.value = result.meals ?: emptyList()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun addToCollection(meal: MealDto) {
        viewModelScope.launch {
            val recipe = meal.toRecipe()
            localRepository.insertRecipe(recipe)
            updateLastRecipe(recipe)
        }
    }
}
```

### 6. SharedPreferences para Última Receta

#### PreferencesManager
```kotlin
class PreferencesManager(context: Context) {
    private val prefs = context.getSharedPreferences("recipe_prefs", Context.MODE_PRIVATE)
    
    fun saveLastRecipe(recipeId: String, recipeName: String) {
        prefs.edit()
            .putString("last_recipe_id", recipeId)
            .putString("last_recipe_name", recipeName)
            .putLong("last_recipe_time", System.currentTimeMillis())
            .apply()
    }
    
    fun getLastRecipe(): Triple<String?, String?, Long>? {
        val id = prefs.getString("last_recipe_id", null)
        val name = prefs.getString("last_recipe_name", null)
        val time = prefs.getLong("last_recipe_time", 0)
        
        return if (id != null && name != null) {
            Triple(id, name, time)
        } else null
    }
}
```

### 7. Validaciones

#### InputValidator
```kotlin
object InputValidator {
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error("Email no puede estar vacío")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> 
                ValidationResult.Error("Formato de email inválido")
            else -> ValidationResult.Success
        }
    }
    
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error("Contraseña no puede estar vacía")
            password.length < 6 -> ValidationResult.Error("Contraseña debe tener al menos 6 caracteres")
            else -> ValidationResult.Success
        }
    }
    
    fun validateRecipeName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("Nombre de receta no puede estar vacío")
            name.length < 3 -> ValidationResult.Error("Nombre debe tener al menos 3 caracteres")
            else -> ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
```

### 8. Manejo de Hilos (Coroutines)

#### Repository con Dispatchers
```kotlin
class RecipeRepository(
    private val localDataSource: RecipeDao,
    private val remoteDataSource: MealApiService
) {
    fun getAllRecipes(): Flow<List<Recipe>> = localDataSource.getAllRecipes()
    
    suspend fun insertRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        localDataSource.insertRecipe(recipe)
    }
    
    suspend fun searchRemoteRecipes(query: String) = withContext(Dispatchers.IO) {
        remoteDataSource.searchByName(query)
    }
}
```

## Configuración de Recursos

### strings.xml
```xml
<resources>
    <string name="app_name">Mis Recetas</string>
    <string name="login_title">Iniciar Sesión</string>
    <string name="register_title">Registrarse</string>
    <string name="email_hint">Correo electrónico</string>
    <string name="password_hint">Contraseña</string>
    <string name="search_recipes">Buscar recetas</string>
    <string name="my_recipes">Mis recetas</string>
    <string name="add_recipe">Agregar receta</string>
    <string name="personal_notes">Notas personales</string>
    <string name="delete_recipe">Eliminar receta</string>
    <string name="last_recipe_added">Última receta: %s</string>
</resources>
```

### Navigation Graph
```xml
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    app:startDestination="@id/homeFragment">
    
    <fragment
        android:id="@+id/homeFragment"
        android:name="com.app.recetas.presentation.ui.home.HomeFragment" />
    
    <fragment
        android:id="@+id/searchFragment"
        android:name="com.app.recetas.presentation.ui.search.SearchFragment" />
    
    <fragment
        android:id="@+id/recipeDetailFragment"
        android:name="com.app.recetas.presentation.ui.detail.RecipeDetailFragment">
        <argument
            android:name="recipeId"
            app:argType="string" />
    </fragment>
</navigation>
```

## Funcionalidades Extras (Opcionales)

### 1. Planificador de Menú Semanal
```kotlin
@Entity(tableName = "meal_plan")
data class MealPlan(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val recipeId: String,
    val dayOfWeek: Int, // 1-7
    val mealType: String, // "breakfast", "lunch", "dinner"
    val weekStartDate: String // "2025-11-01"
)
```

### 2. Sincronización en la Nube (Firebase Firestore)
```kotlin
class CloudSyncRepository {
    private val firestore = FirebaseFirestore.getInstance()
    
    suspend fun syncRecipes(userId: String) {
        // Subir recetas locales a Firestore
        // Descargar recetas de Firestore
        // Resolver conflictos por timestamp
    }
}
```

## Consideraciones de Implementación

### Dependencias Principales (build.gradle)
```kotlin
dependencies {
    // Core Android
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Navigation
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.6'
    implementation 'androidx.navigation:navigation-ui-ktx:2.7.6'
    
    // ViewModel & LiveData
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
    
    // Room
    implementation 'androidx.room:room-runtime:2.6.1'
    implementation 'androidx.room:room-ktx:2.6.1'
    kapt 'androidx.room:room-compiler:2.6.1'
    
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-auth'
    implementation 'com.google.firebase:firebase-firestore' // opcional
    
    // Retrofit
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Image loading
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
}
```

### Permisos (AndroidManifest.xml)
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Flujo de Desarrollo Recomendado

1. **Setup inicial**: Configurar Firebase, dependencias, estructura de paquetes
2. **Autenticación**: Implementar login/register con validaciones
3. **Base de datos**: Crear entidades Room, DAOs, database
4. **API externa**: Configurar Retrofit, DTOs, servicios
5. **UI básica**: Crear layouts, navigation, RecyclerView
6. **ViewModels**: Implementar lógica de presentación
7. **Funcionalidades core**: CRUD de recetas, búsqueda, notas
8. **SharedPreferences**: Última receta modificada
9. **Validaciones**: Input validation en todos los formularios
10. **Testing**: Pruebas unitarias y de integración
11. **Funcionalidades extras**: Planificador, sync en la nube

Esta documentación proporciona una base sólida para implementar todos los requerimientos del trabajo obligatorio de manera estructurada y escalable.
