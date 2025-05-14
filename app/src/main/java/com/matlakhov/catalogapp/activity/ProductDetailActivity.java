package com.matlakhov.catalogapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.matlakhov.catalogapp.R;

/**
 * Активность для отображения деталей товара.
 * <p>
 * Эта активность получает данные товара (ID, название, описание, цену) через Intent,
 * отображает их в пользовательском интерфейсе и загружает изображение товара с помощью Glide.
 * </p>
 */
public class ProductDetailActivity extends AppCompatActivity {

    /** Тег для целей логирования. */
    private static final String TAG = "ProductDetailActivity";

    /** Значение по умолчанию для недействительного ID товара. */
    private static final int INVALID_ID = -1;

    /** Значение по умолчанию для недействительной цены. */
    private static final double INVALID_PRICE = -1.0;

    /** Ключи для сохранения состояния. */
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_PRODUCT_NAME = "product_name";
    private static final String KEY_PRODUCT_DESCRIPTION = "product_description";
    private static final String KEY_PRODUCT_PRICE = "product_price";

    /** ImageView для отображения изображения товара. */
    private ImageView imageView;

    /** TextView для отображения названия товара. */
    private TextView nameTextView;

    /** TextView для отображения цены товара. */
    private TextView priceTextView;

    /** TextView для отображения описания товара. */
    private TextView descriptionTextView;

    /** ID товара. */
    private int productId;

    /** Название товара. */
    private String productName;

    /** Описание товара. */
    private String productDescription;

    /** Цена товара. */
    private double productPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Восстановление состояния, если оно существует
        if (savedInstanceState != null) {
            productId = savedInstanceState.getInt(KEY_PRODUCT_ID, INVALID_ID);
            productName = savedInstanceState.getString(KEY_PRODUCT_NAME);
            productDescription = savedInstanceState.getString(KEY_PRODUCT_DESCRIPTION);
            productPrice = savedInstanceState.getDouble(KEY_PRODUCT_PRICE, INVALID_PRICE);
        } else {
            // Получение данных товара из Intent
            Intent intent = getIntent();
            productId = intent.getIntExtra("PRODUCT_ID", INVALID_ID);
            productName = intent.getStringExtra("PRODUCT_NAME");
            productDescription = intent.getStringExtra("PRODUCT_DESCRIPTION");
            productPrice = intent.getDoubleExtra("PRODUCT_PRICE", INVALID_PRICE);
        }

        // Проверка обязательных данных товара
        if (productId == INVALID_ID || productName == null) {
            Log.e(TAG, "Недействительные данные товара: id=" + productId + ", name=" + productName);
            Toast.makeText(this, "Ошибка: данные товара не переданы", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Настройка статус-бара
        configureStatusBar();

        // Настройка тулбара
        if (!configureToolbar()) {
            return;
        }

        // Инициализация компонентов интерфейса
        imageView = findViewById(R.id.product_image);
        nameTextView = findViewById(R.id.product_name);
        priceTextView = findViewById(R.id.product_price);
        descriptionTextView = findViewById(R.id.product_description);

        // Проверка на null для компонентов интерфейса
        if (imageView == null || nameTextView == null || priceTextView == null || descriptionTextView == null) {
            Log.e(TAG, "Один или несколько компонентов не найдены: " +
                    "imageView=" + imageView + ", nameTextView=" + nameTextView +
                    ", priceTextView=" + priceTextView + ", descriptionTextView=" + descriptionTextView);
            Toast.makeText(this, "Ошибка: элементы интерфейса не найдены", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Логирование данных для отладки
        Log.d(TAG, "Товар: id=" + productId + ", name=" + productName +
                ", price=" + productPrice + ", description=" + productDescription);

        // Отображение данных
        displayProductDetails();

        // Загрузка изображения
        loadProductImage();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_PRODUCT_ID, productId);
        outState.putString(KEY_PRODUCT_NAME, productName);
        outState.putString(KEY_PRODUCT_DESCRIPTION, productDescription);
        outState.putDouble(KEY_PRODUCT_PRICE, productPrice);
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
     * Настраивает тулбар с заголовком, кнопкой возврата и стилями.
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
        getSupportActionBar().setTitle(productName);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_main));
        return true;
    }

    /**
     * Отображает данные товара в пользовательском интерфейсе.
     * <p>
     * Устанавливает название, описание и цену товара в соответствующие TextView.
     * Если описание отсутствует, отображается пустая строка. Если цена недействительна,
     * отображается "N/A".
     * </p>
     */
    private void displayProductDetails() {
        nameTextView.setText(productName);
        descriptionTextView.setText(productDescription != null ? productDescription : "");
        priceTextView.setText(productPrice != INVALID_PRICE ? String.format("%.2f ₽", productPrice) : "N/A");
    }

    /**
     * Загружает изображение товара с помощью Glide.
     * <p>
     * Выбирает ресурс изображения на основе ID товара и загружает его в ImageView,
     * используя Glide с placeholder и error изображениями.
     * </p>
     */
    private void loadProductImage() {
        int imageResource;
        switch (productId) {
            case 1001:
                imageResource = R.drawable.mirror;
                break;
            case 1002:
                imageResource = R.drawable.probe;
                break;
            case 1003:
                imageResource = R.drawable.round_bur;
                break;
            case 2001:
                imageResource = R.drawable.composite;
                break;
            case 2002:
                imageResource = R.drawable.glass_ionomer;
                break;
            case 3001:
                imageResource = R.drawable.dental_unit;
                break;
            case 3002:
                imageResource = R.drawable.visiograph;
                break;
            case 4001:
                imageResource = R.drawable.handpiece;
                break;
            case 4002:
                imageResource = R.drawable.scaler;
                break;
            default:
                imageResource = R.drawable.error_image;
                break;
        }

        Glide.with(this)
                .load(imageResource)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imageView);
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