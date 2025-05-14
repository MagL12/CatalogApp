package com.matlakhov.catalogapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.matlakhov.catalogapp.R;
import com.matlakhov.catalogapp.adapters.CategoryAdapter;
import com.matlakhov.catalogapp.adapters.ProductAdapter;
import com.matlakhov.catalogapp.models.CatalogResponse;
import com.matlakhov.catalogapp.models.Category;
import com.matlakhov.catalogapp.models.Product;
import com.matlakhov.catalogapp.network.ApiClient;
import com.matlakhov.catalogapp.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Главная активность приложения, отображающая каталог категорий и рекомендуемые товары.
 * <p>
 * Эта активность загружает данные каталога и рекомендуемых товаров через API, отображает их
 * в двух RecyclerView (вертикальный список категорий и горизонтальный список рекомендуемых товаров)
 * и обрабатывает клики по элементам для перехода в {@link CategoryDetailActivity} или {@link ProductDetailActivity}.
 * </p>
 *
 * @see CategoryAdapter
 * @see ProductAdapter
 * @see CategoryDetailActivity
 * @see ProductDetailActivity
 */
public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {

    /**
     * Тег для целей логирования.
     */
    private static final String TAG = "MainActivity";

    /**
     * Значение по умолчанию для недействительной цены.
     */
    private static final double INVALID_PRICE = -1.0;

    /**
     * RecyclerView для отображения списка категорий.
     */
    private RecyclerView recyclerViewCategories;

    /**
     * RecyclerView для отображения списка рекомендуемых товаров.
     */
    private RecyclerView recyclerViewRecommended;

    /**
     * Адаптер для управления списком категорий.
     */
    private CategoryAdapter categoryAdapter;

    /**
     * Адаптер для управления списком рекомендуемых товаров.
     */
    private ProductAdapter productAdapter;

    /**
     * Список всех категорий каталога.
     */
    private List<Category> catalogList = new ArrayList<>();

    /**
     * Список рекомендуемых товаров.
     */
    private List<Category> recommendedProducts = new ArrayList<>();

    /**
     * ProgressBar для индикации состояния загрузки.
     */
    private ProgressBar progressBar;

    /**
     * SwipeRefreshLayout для обновления данных притягиванием вниз.
     */
    private SwipeRefreshLayout swipeRefresh;

    /**
     * Сервис API для загрузки данных каталога.
     */
    private ApiService apiService;

    /**
     * Ключ для сохранения ID активити в savedInstanceState.
     */
    private static final String KEY_CATALOG_LIST = "catalog_list";

    /**
     * Ключ для сохранения списка рекомендуемых товаров в savedInstanceState.
     */
    private static final String KEY_RECOMMENDED_PRODUCTS = "recommended_products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            catalogList = savedInstanceState.getParcelableArrayList(KEY_CATALOG_LIST);
            recommendedProducts = savedInstanceState.getParcelableArrayList(KEY_RECOMMENDED_PRODUCTS);
        }

        configureStatusBar();

        if (!configureToolbar()) {
            return;
        }

        recyclerViewCategories = findViewById(R.id.recycler_main);
        recyclerViewRecommended = findViewById(R.id.recycler_recommended);
        progressBar = findViewById(R.id.progress_loading);
        swipeRefresh = findViewById(R.id.swipe_refresh);

        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(this, catalogList, this);
        recyclerViewCategories.setAdapter(categoryAdapter);

        LinearLayoutManager recommendedLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRecommended.setLayoutManager(recommendedLayoutManager);
        recyclerViewRecommended.setNestedScrollingEnabled(false); // Отключаем вложенную прокрутку
        productAdapter = new ProductAdapter(this, recommendedProducts);
        recyclerViewRecommended.setAdapter(productAdapter);

        swipeRefresh.setOnRefreshListener(this::loadCatalogData);

        apiService = ApiClient.getClient().create(ApiService.class);
        loadCatalogData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_CATALOG_LIST, new ArrayList<>(catalogList));
        outState.putParcelableArrayList(KEY_RECOMMENDED_PRODUCTS, new ArrayList<>(recommendedProducts));
    }

    /**
     * Настраивает внешний вид статус-бара, устанавливая его цвет и флаги.
     */
    private void configureStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.purple_dark));
    }

    /**
     * Настраивает тулбар с заголовком и стилями.
     *
     * @return true, если тулбар успешно настроен, false в противном случае
     */
    private boolean configureToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            Log.e(TAG, "Тулбар не найден");
            Toast.makeText(this, "Ошибка: тулбар не найден", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Каталог");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_main));
        return true;
    }

    /**
     * Загружает данные каталога и рекомендуемых товаров через API.
     * <p>
     * При успешном ответе обновляет списки категорий и рекомендуемых товаров,
     * а при ошибке отображает соответствующее сообщение пользователю.
     * </p>
     */
    private void loadCatalogData() {
        progressBar.setVisibility(View.VISIBLE);
        swipeRefresh.setRefreshing(false);

        apiService.getCatalog().enqueue(new Callback<CatalogResponse>() {
            @Override
            public void onResponse(@NonNull Call<CatalogResponse> call, @NonNull Response<CatalogResponse> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    catalogList.clear();
                    recommendedProducts.clear();

                    catalogList.addAll(response.body().getCatalog());

                    List<Product> products = response.body().getProducts();
                    if (products != null) {
                        for (Product product : products) {
                            Category productCategory = new Category();
                            productCategory.setId(product.getId());
                            productCategory.setName(product.getName());
                            productCategory.setImageUrl(product.getImageUrl());
                            productCategory.setDescription(product.getDescription());
                            productCategory.setPrice(product.getPrice());
                            recommendedProducts.add(productCategory);
                        }
                    }

                    Log.d(TAG, "Размер каталога: " + catalogList.size());
                    Log.d(TAG, "Размер списка рекомендуемых товаров: " + recommendedProducts.size());
                    for (Category product : recommendedProducts) {
                        Log.d(TAG, "Товар: id=" + product.getId() + ", name=" + product.getName());
                    }

                    categoryAdapter.notifyDataSetChanged();
                    productAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Ошибка ответа: " + response.code());
                    Toast.makeText(MainActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CatalogResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                Log.e(TAG, "Сетевая ошибка: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Обрабатывает клики по категории или товару в RecyclerView.
     * <p>
     * Если выбранный элемент является товаром, выполняется переход в {@link ProductDetailActivity}.
     * Если выбранная категория является подкатегорией, открывается {@link CategoryDetailActivity}.
     * </p>
     *
     * @param category выбранная категория или товар
     */
    @Override
    public void onCategoryClick(Category category) {
        if (category == null) {
            Log.w(TAG, "onCategoryClick: Категория равна null");
            return;
        }

        if (category.isProduct()) {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", category.getId());
            intent.putExtra("PRODUCT_NAME", category.getName());
            intent.putExtra("PRODUCT_DESCRIPTION", category.getDescription());
            intent.putExtra("PRODUCT_PRICE", category.getPrice() != null ? category.getPrice() : INVALID_PRICE);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, CategoryDetailActivity.class);
            intent.putExtra("CATEGORY_NAME", category.getName());
            intent.putExtra("CATEGORY_ID", category.getId());
            intent.putExtra("CATEGORY_DESCRIPTION", category.getDescription());
            intent.putParcelableArrayListExtra("CATEGORY_ITEMS", new ArrayList<>(category.getItems()));
            startActivity(intent);
        }
    }
}