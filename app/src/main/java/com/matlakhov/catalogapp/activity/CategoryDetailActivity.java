package com.matlakhov.catalogapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.matlakhov.catalogapp.R;
import com.matlakhov.catalogapp.adapters.CategoryAdapter;
import com.matlakhov.catalogapp.models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Активность для отображения деталей категории, включая подкатегории или товары.
 * <p>
 * Эта активность получает данные категории (название, ID, описание, цену и список элементов)
 * через Intent и отображает список подкатегорий или товаров с помощью RecyclerView.
 * Если категория представляет собой товар (т.е. имеет цену и описание, но нет подкатегорий),
 * пользователь перенаправляется в {@link ProductDetailActivity}.
 * </p>
 *
 * @see CategoryAdapter
 * @see ProductDetailActivity
 */
public class CategoryDetailActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {

    /**
     * Тег для целей логирования.
     */
    private static final String TAG = "CategoryDetailActivity";

    /**
     * Значение по умолчанию для недействительного ID категории.
     */
    private static final int INVALID_ID = -1;

    /**
     * Значение по умолчанию для недействительной цены.
     */
    private static final double INVALID_PRICE = -1.0;

    /**
     * ID категории.
     */
    private int categoryId;

    /**
     * Название категории.
     */
    private String categoryName;

    /**
     * Цена категории, если она представляет товар.
     */
    private Double categoryPrice;

    /**
     * Описание категории.
     */
    private String categoryDescription;

    /**
     * RecyclerView для отображения подкатегорий или товаров.
     */
    private RecyclerView recyclerView;

    /**
     * ProgressBar для индикации состояния загрузки.
     */
    private ProgressBar progressBar;

    /**
     * Список подкатегорий или товаров в данной категории.
     */
    private List<Category> items;

    /**
     * Адаптер для управления отображением подкатегорий или товаров в RecyclerView.
     */
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        Intent intent = getIntent();
        categoryName = intent.getStringExtra("CATEGORY_NAME");
        categoryId = intent.getIntExtra("CATEGORY_ID", INVALID_ID);
        categoryPrice = intent.getDoubleExtra("CATEGORY_PRICE", INVALID_PRICE);
        categoryDescription = intent.getStringExtra("CATEGORY_DESCRIPTION");
        items = intent.getParcelableArrayListExtra("CATEGORY_ITEMS");

        if (categoryName == null || categoryId == INVALID_ID) {
            Log.e(TAG, "Недействительные данные категории: name=" + categoryName + ", id=" + categoryId);
            Toast.makeText(this, "Ошибка: данные категории не переданы", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        configureStatusBar();

        if (!configureToolbar()) {
            return;
        }

        recyclerView = findViewById(R.id.recycler_detail);
        progressBar = findViewById(R.id.progress_loading_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (items == null || items.isEmpty()) {
            if (categoryPrice != INVALID_PRICE && categoryDescription != null) {
                // Перенаправление в ProductDetailActivity, если категория представляет товар
                Intent productIntent = new Intent(this, ProductDetailActivity.class);
                productIntent.putExtra("PRODUCT_ID", categoryId);
                productIntent.putExtra("PRODUCT_NAME", categoryName);
                productIntent.putExtra("PRODUCT_DESCRIPTION", categoryDescription);
                productIntent.putExtra("PRODUCT_PRICE", categoryPrice);
                startActivity(productIntent);
                finish();
                return;
            } else {
                Log.e(TAG, "Недействительные данные товара: price=" + categoryPrice + ", description=" + categoryDescription);
                Toast.makeText(this, "Ошибка: данные товара не переданы", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            categoryAdapter = new CategoryAdapter(this, items, this);
            recyclerView.setAdapter(categoryAdapter);
        }

        progressBar.setVisibility(View.GONE);
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
     * Настраивает тулбар с названием, кнопкой возврата и стилями.
     *
     * @return true, если тулбар успешно настроен, false в противном случае
     */
    private boolean configureToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_detail);
        if (toolbar == null) {
            Log.e(TAG, "Тулбар не найден");
            Toast.makeText(this, "Ошибка: тулбар не найден", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(categoryName);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_main));
        return true;
    }

    /**
     * Обрабатывает клики по категории или товару в RecyclerView.
     * <p>
     * Если выбранный элемент является товаром, выполняется переход в {@link ProductDetailActivity}.
     * Если выбранная категория является подкатегорией, открывается новая инстанция {@link CategoryDetailActivity}.
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
            intent.putExtra("PRODUCT_PRICE", category.getPrice());
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

    /**
     * Обрабатывает действие навигации вверх (кнопка назад в тулбаре).
     *
     * @return true, если действие обработано, false в противном случае
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}