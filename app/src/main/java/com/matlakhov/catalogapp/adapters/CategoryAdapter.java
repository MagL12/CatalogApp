package com.matlakhov.catalogapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.matlakhov.catalogapp.R;
import com.matlakhov.catalogapp.models.Category;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Адаптер для RecyclerView, отображающий список категорий или товаров.
 * <p>
 * Адаптер поддерживает два типа элементов: категории (с подкатегориями) и товары.
 * Для каждого типа используется соответствующий layout ({@code item_category} или {@code item_product}).
 * Адаптер также обрабатывает клики по элементам через интерфейс {@link OnCategoryClickListener}.
 * </p>
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    /**
     * Максимальная длина описания категории (в символах).
     */
    private static final int MAX_DESCRIPTION_LENGTH = 50;

    /**
     * Список категорий или товаров для отображения.
     */
    private final List<Category> categories;

    /**
     * Контекст приложения для доступа к ресурсам.
     */
    private final Context context;

    /**
     * Слушатель кликов по элементам списка.
     */
    private final OnCategoryClickListener listener;

    /**
     * Интерфейс для обработки кликов по элементам списка.
     */
    public interface OnCategoryClickListener {
        /**
         * Вызывается при клике по категории или товару.
         *
         * @param category выбранная категория или товар
         */
        void onCategoryClick(Category category);
    }

    /**
     * Создаёт новый адаптер с заданным контекстом, списком категорий и слушателем.
     *
     * @param context    контекст приложения
     * @param categories список категорий или товаров
     * @param listener   слушатель кликов
     */
    public CategoryAdapter(Context context, List<Category> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories != null ? categories : new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return categories.get(position).isProduct() ? R.layout.item_product : R.layout.item_category;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(viewType, parent, false);
        return new CategoryViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (position >= 0 && position < categories.size()) {
            Category category = categories.get(position);
            holder.bind(category);
        }
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    /**
     * ViewHolder для отображения элемента категории или товара.
     * <p>
     * Поддерживает два типа макетов: {@code item_category} для категорий и {@code item_product} для товаров.
     * </p>
     */
    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameTextView;
        private final TextView descriptionTextView;
        private final TextView priceTextView;
        private final int viewType;

        /**
         * Создаёт новый ViewHolder для заданного макета.
         *
         * @param itemView элемент макета
         * @param viewType тип макета (R.layout.item_category или R.layout.item_product)
         */
        public CategoryViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            if (viewType == R.layout.item_product) {
                imageView = itemView.findViewById(R.id.product_image);
                nameTextView = itemView.findViewById(R.id.product_name);
                descriptionTextView = itemView.findViewById(R.id.product_description);
                priceTextView = itemView.findViewById(R.id.product_price);
            } else {
                imageView = itemView.findViewById(R.id.category_image);
                nameTextView = itemView.findViewById(R.id.category_name);
                descriptionTextView = itemView.findViewById(R.id.category_description);
                priceTextView = null;
            }

            if (imageView == null || nameTextView == null) {
                Log.e("CategoryAdapter", "Ошибка инициализации ViewHolder: imageView=" + imageView + ", nameTextView=" + nameTextView);
                return;
            }

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCategoryClick(categories.get(position));
                }
            });
        }

        /**
         * Привязывает данные категории или товара к элементам интерфейса.
         *
         * @param category категория или товар для отображения
         */
        public void bind(Category category) {
            if (category == null || nameTextView == null) {
                Log.w("CategoryAdapter", "bind: Категория или nameTextView равны null");
                return;
            }

            nameTextView.setText(category.getName() != null ? category.getName() : "");

            if (viewType == R.layout.item_product) {
                descriptionTextView.setVisibility(View.GONE);
                if (priceTextView != null) {
                    priceTextView.setText(category.getPrice() != null ? String.format("%.2f ₽", category.getPrice()) : "N/A");
                }
            } else {
                descriptionTextView.setVisibility(View.VISIBLE);
                String description = category.getDescription();
                descriptionTextView.setText(description != null ?
                        (description.length() > MAX_DESCRIPTION_LENGTH ? description.substring(0, MAX_DESCRIPTION_LENGTH) + "..." : description) :
                        "");
            }

            int imageResource = getImageResourceForId(category.getId());
            Glide.with(context)
                    .load(imageResource)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imageView);
        }

        /**
         * Возвращает ресурс изображения на основе ID категории или товара.
         *
         * @param id ID категории или товара
         * @return ID ресурса изображения
         */
        private int getImageResourceForId(int id) {
            Map<Integer, Integer> imageMap = new HashMap<>();
            imageMap.put(1, R.drawable.instruments);
            imageMap.put(101, R.drawable.examination);
            imageMap.put(102, R.drawable.burs);
            imageMap.put(1001, R.drawable.mirror);
            imageMap.put(1002, R.drawable.probe);
            imageMap.put(1003, R.drawable.round_bur);
            imageMap.put(2, R.drawable.materials);
            imageMap.put(201, R.drawable.filling);
            imageMap.put(2001, R.drawable.composite);
            imageMap.put(202, R.drawable.cement);
            imageMap.put(2002, R.drawable.glass_ionomer);
            imageMap.put(3, R.drawable.equipment);
            imageMap.put(301, R.drawable.unit);
            imageMap.put(3001, R.drawable.dental_unit);
            imageMap.put(302, R.drawable.xray);
            imageMap.put(3002, R.drawable.visiograph);
            imageMap.put(4001, R.drawable.handpiece);
            imageMap.put(4002, R.drawable.scaler);

            return imageMap.getOrDefault(id, R.drawable.error_image);
        }
    }
}