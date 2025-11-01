# Documentación de Solución - Aplicación de Gestión de Recetas Android (JAVA)

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

#### Dependencias (build.gradle app)
```java
implementation 'com.google.firebase:firebase-auth:22.3.0'
implementation 'com.google.firebase:firebase-bom:32.7.0'
```

#### AuthRepository.java
```java
public class AuthRepository {
    private FirebaseAuth auth;
    
    public AuthRepository() {
        auth = FirebaseAuth.getInstance();
    }
    
    public void login(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }
    
    public void register(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }
    
    public void logout() {
        auth.signOut();
    }
    
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
}
```

#### LoginActivity.java
```java
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private AuthRepository authRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        authRepository = new AuthRepository();
        setupClickListeners();
    }
    
    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> performLogin());
        binding.btnRegister.setOnClickListener(v -> goToRegister());
    }
    
    private void performLogin() {
        String email = binding.editEmail.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();
        
        if (validateInput(email, password)) {
            authRepository.login(email, password, task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    showError("Error de autenticación");
                }
            });
        }
    }
}
```

### 2. Base de Datos Local (SQLite + Room)

#### Dependencias
```java
implementation "androidx.room:room-runtime:2.6.1"
annotationProcessor "androidx.room:room-compiler:2.6.1"
```

#### Recipe.java (Entidad)
```java
@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey
    @NonNull
    public String id;
    
    public String name;
    public String category;
    public String area;
    public String instructions;
    public String imageUrl;
    public String ingredients; // JSON string
    public String personalNotes;
    public boolean isPersonal;
    public long dateAdded;
    public long dateModified;
    
    public Recipe(@NonNull String id, String name, String category, String area, 
                  String instructions, String imageUrl, String ingredients) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.area = area;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients;
        this.personalNotes = "";
        this.isPersonal = false;
        this.dateAdded = System.currentTimeMillis();
        this.dateModified = System.currentTimeMillis();
    }
    
    // Getters y setters
}
```

#### RecipeDao.java
```java
@Dao
public interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY dateModified DESC")
    LiveData<List<Recipe>> getAllRecipes();
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);
    
    @Delete
    void deleteRecipe(Recipe recipe);
    
    @Update
    void updateRecipe(Recipe recipe);
    
    @Query("SELECT * FROM recipes ORDER BY dateModified DESC LIMIT 1")
    Recipe getLastModifiedRecipe();
}
```

#### AppDatabase.java
```java
@Database(entities = {Recipe.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    
    public abstract RecipeDao recipeDao();
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "recipe_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
```

### 3. API Externa (TheMealDB)

#### MealApiService.java
```java
public interface MealApiService {
    @GET("search.php")
    Call<MealResponse> searchByName(@Query("s") String name);
    
    @GET("filter.php")
    Call<MealResponse> searchByCategory(@Query("c") String category);
    
    @GET("filter.php")
    Call<MealResponse> searchByArea(@Query("a") String area);
    
    @GET("categories.php")
    Call<CategoryResponse> getCategories();
    
    @GET("list.php?a=list")
    Call<AreaResponse> getAreas();
}
```

#### MealDto.java
```java
public class MealDto {
    @SerializedName("idMeal")
    public String idMeal;
    
    @SerializedName("strMeal")
    public String strMeal;
    
    @SerializedName("strCategory")
    public String strCategory;
    
    @SerializedName("strArea")
    public String strArea;
    
    @SerializedName("strInstructions")
    public String strInstructions;
    
    @SerializedName("strMealThumb")
    public String strMealThumb;
    
    // strIngredient1-20, strMeasure1-20
    @SerializedName("strIngredient1")
    public String strIngredient1;
    // ... hasta strIngredient20
    
    public Recipe toRecipe() {
        return new Recipe(idMeal, strMeal, strCategory, strArea, 
                         strInstructions, strMealThumb, buildIngredientsJson());
    }
    
    private String buildIngredientsJson() {
        // Construir JSON con ingredientes y medidas
        return "{}"; // Implementar lógica
    }
}
```

#### ApiClient.java
```java
public class ApiClient {
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private static Retrofit retrofit;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    public static MealApiService getApiService() {
        return getClient().create(MealApiService.class);
    }
}
```

### 4. Pantallas Principales

#### MainActivity.java
```java
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PreferencesManager preferencesManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        preferencesManager = new PreferencesManager(this);
        setupBottomNavigation();
        showLastRecipe();
    }
    
    private void setupBottomNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }
    
    private void showLastRecipe() {
        String[] lastRecipe = preferencesManager.getLastRecipe();
        if (lastRecipe != null) {
            String message = getString(R.string.last_recipe_added, lastRecipe[1]);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
}
```

#### HomeFragment.java
```java
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private RecipeAdapter adapter;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        setupRecyclerView();
        observeRecipes();
        
        return binding.getRoot();
    }
    
    private void setupRecyclerView() {
        adapter = new RecipeAdapter(recipe -> {
            Bundle bundle = new Bundle();
            bundle.putString("recipeId", recipe.id);
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_home_to_detail, bundle);
        });
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }
    
    private void observeRecipes() {
        viewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            adapter.setRecipes(recipes);
        });
    }
}
```

#### RecipeAdapter.java
```java
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipes = new ArrayList<>();
    private OnRecipeClickListener listener;
    
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }
    
    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeBinding binding = ItemRecipeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RecipeViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(recipes.get(position));
    }
    
    @Override
    public int getItemCount() {
        return recipes.size();
    }
    
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }
    
    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private ItemRecipeBinding binding;
        
        public RecipeViewHolder(ItemRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        public void bind(Recipe recipe) {
            binding.textName.setText(recipe.name);
            binding.textCategory.setText(recipe.category);
            
            Glide.with(binding.getRoot().getContext())
                    .load(recipe.imageUrl)
                    .into(binding.imageRecipe);
            
            binding.getRoot().setOnClickListener(v -> listener.onRecipeClick(recipe));
        }
    }
}
```

### 5. ViewModels

#### HomeViewModel.java
```java
public class HomeViewModel extends ViewModel {
    private RecipeRepository repository;
    private LiveData<List<Recipe>> recipes;
    
    public HomeViewModel(@NonNull Application application) {
        super();
        repository = new RecipeRepository(application);
        recipes = repository.getAllRecipes();
    }
    
    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }
    
    public void deleteRecipe(Recipe recipe) {
        repository.deleteRecipe(recipe);
    }
}
```

#### SearchViewModel.java
```java
public class SearchViewModel extends ViewModel {
    private MutableLiveData<List<MealDto>> searchResults = new MutableLiveData<>();
    private MutableLiveData<List<String>> categories = new MutableLiveData<>();
    private MutableLiveData<List<String>> areas = new MutableLiveData<>();
    private MealApiService apiService;
    private RecipeRepository repository;
    
    public SearchViewModel(@NonNull Application application) {
        super();
        apiService = ApiClient.getApiService();
        repository = new RecipeRepository(application);
        loadCategories();
        loadAreas();
    }
    
    public LiveData<List<MealDto>> getSearchResults() {
        return searchResults;
    }
    
    public LiveData<List<String>> getCategories() {
        return categories;
    }
    
    public LiveData<List<String>> getAreas() {
        return areas;
    }
    
    public void searchRecipes(String query, SearchType type) {
        Call<MealResponse> call;
        
        switch (type) {
            case NAME:
                call = apiService.searchByName(query);
                break;
            case CATEGORY:
                call = apiService.searchByCategory(query);
                break;
            case AREA:
                call = apiService.searchByArea(query);
                break;
            default:
                return;
        }
        
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchResults.setValue(response.body().meals);
                }
            }
            
            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                // Handle error
            }
        });
    }
    
    public void addToCollection(MealDto meal) {
        new Thread(() -> {
            Recipe recipe = meal.toRecipe();
            repository.insertRecipeSync(recipe);
        }).start();
    }
}
```

### 6. Repository

#### RecipeRepository.java
```java
public class RecipeRepository {
    private RecipeDao recipeDao;
    private LiveData<List<Recipe>> allRecipes;
    
    public RecipeRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        recipeDao = database.recipeDao();
        allRecipes = recipeDao.getAllRecipes();
    }
    
    public LiveData<List<Recipe>> getAllRecipes() {
        return allRecipes;
    }
    
    public void insertRecipe(Recipe recipe) {
        new Thread(() -> recipeDao.insertRecipe(recipe)).start();
    }
    
    public void insertRecipeSync(Recipe recipe) {
        recipeDao.insertRecipe(recipe);
    }
    
    public void deleteRecipe(Recipe recipe) {
        new Thread(() -> recipeDao.deleteRecipe(recipe)).start();
    }
    
    public void updateRecipe(Recipe recipe) {
        new Thread(() -> {
            recipe.dateModified = System.currentTimeMillis();
            recipeDao.updateRecipe(recipe);
        }).start();
    }
    
    public Recipe getLastModifiedRecipe() {
        return recipeDao.getLastModifiedRecipe();
    }
}
```

### 7. SharedPreferences

#### PreferencesManager.java
```java
public class PreferencesManager {
    private static final String PREF_NAME = "recipe_prefs";
    private static final String KEY_LAST_RECIPE_ID = "last_recipe_id";
    private static final String KEY_LAST_RECIPE_NAME = "last_recipe_name";
    private static final String KEY_LAST_RECIPE_TIME = "last_recipe_time";
    
    private SharedPreferences prefs;
    
    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveLastRecipe(String recipeId, String recipeName) {
        prefs.edit()
                .putString(KEY_LAST_RECIPE_ID, recipeId)
                .putString(KEY_LAST_RECIPE_NAME, recipeName)
                .putLong(KEY_LAST_RECIPE_TIME, System.currentTimeMillis())
                .apply();
    }
    
    public String[] getLastRecipe() {
        String id = prefs.getString(KEY_LAST_RECIPE_ID, null);
        String name = prefs.getString(KEY_LAST_RECIPE_NAME, null);
        long time = prefs.getLong(KEY_LAST_RECIPE_TIME, 0);
        
        if (id != null && name != null) {
            return new String[]{id, name, String.valueOf(time)};
        }
        return null;
    }
}
```

### 8. Validaciones

#### InputValidator.java
```java
public class InputValidator {
    
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Email no puede estar vacío");
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return new ValidationResult(false, "Formato de email inválido");
        }
        
        return new ValidationResult(true, "");
    }
    
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return new ValidationResult(false, "Contraseña no puede estar vacía");
        }
        
        if (password.length() < 6) {
            return new ValidationResult(false, "Contraseña debe tener al menos 6 caracteres");
        }
        
        return new ValidationResult(true, "");
    }
    
    public static ValidationResult validateRecipeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "Nombre de receta no puede estar vacío");
        }
        
        if (name.length() < 3) {
            return new ValidationResult(false, "Nombre debe tener al menos 3 caracteres");
        }
        
        return new ValidationResult(true, "");
    }
    
    public static class ValidationResult {
        public boolean isValid;
        public String errorMessage;
        
        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }
    }
}
```

### 9. Enums y Utilidades

#### SearchType.java
```java
public enum SearchType {
    NAME,
    CATEGORY,
    AREA
}
```

## Configuración de Dependencias (build.gradle app)

```java
dependencies {
    // Core Android
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Navigation
    implementation 'androidx.navigation:navigation-fragment:2.7.6'
    implementation 'androidx.navigation:navigation-ui:2.7.6'
    
    // ViewModel & LiveData
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'
    
    // Room
    implementation 'androidx.room:room-runtime:2.6.1'
    annotationProcessor 'androidx.room:room-compiler:2.6.1'
    
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-auth'
    
    // Retrofit
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Image loading
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    
    // View Binding
    buildFeatures {
        viewBinding true
    }
}
```

## Flujo de Desarrollo Recomendado

1. **Setup inicial**: Configurar Firebase, dependencias, estructura
2. **Entidades y Database**: Crear Recipe.java, RecipeDao.java, AppDatabase.java
3. **API**: Configurar Retrofit, MealApiService.java, DTOs
4. **Repository**: Implementar RecipeRepository.java
5. **Autenticación**: LoginActivity.java, RegisterActivity.java, AuthRepository.java
6. **UI Principal**: MainActivity.java, HomeFragment.java, RecipeAdapter.java
7. **ViewModels**: HomeViewModel.java, SearchViewModel.java
8. **Búsqueda**: SearchFragment.java con spinners
9. **Detalle**: RecipeDetailFragment.java
10. **Validaciones**: InputValidator.java
11. **SharedPreferences**: PreferencesManager.java
12. **Testing y refinamiento**

Esta versión en Java mantiene toda la funcionalidad requerida pero usando sintaxis y patrones de Java tradicional.
