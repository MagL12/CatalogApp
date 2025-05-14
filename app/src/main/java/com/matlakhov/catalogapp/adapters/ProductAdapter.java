package com.matlakhov.catalogapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.matlakhov.catalogapp.R;
import com.matlakhov.catalogapp.activity.ProductDetailActivity;
import com.matlakhov.catalogapp.models.Category;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Адаптер для RecyclerView, отображающий список рекомендуемых товаров.
 * <p>
 * Этот адаптер используется для отображения товаров в горизонтальном списке.
 * Каждый элемент отображается с использованием макета {@code item_product}.
 * При клике на товар пользователь перенаправляется в {@link ProductDetailActivity}.
 * </p>
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    /** Тег для целей логирования. */
    private static final String TAG = "ProductAdapter";

    /** Значение по умолчанию для недействительной цены. */
    private static final double INVALID_PRICE = -1.0;

    /** Маппинг ID товаров на ресурсы изображений. */
    private static final Map<Integer, Integer> IMAGE_MAP;

    static {
        IMAGE_MAP = new HashMap<>();
        IMAGE_MAP.put(1001, R.drawable.mirror);
        IMAGE_MAP.put(1002, R.drawable.probe);
        IMAGE_MAP.put(1003, R.drawable.round_bur);
        IMAGE_MAP.put(2001, R.drawable.composite);
        IMAGE_MAP.put(2002, R.drawable.glass_ionomer);
        IMAGE_MAP.put(3001, R.drawable.dental_unit);
        IMAGE_MAP.put(3002, R.drawable.visiograph);
        IMAGE_MAP.put(4001, R.drawable.handpiece);
        IMAGE_MAP.put(4002, R.drawable.scaler);
    }

    /** Список товаров для отображения. */
    private final List<Category> products;

    /** Контекст приложения для доступа к ресурсам. */
    private final Context context;

    /**
     * Создаёт новый адаптер с заданным контекстом и списком товаров.
     *
     * @param context  контекст приложения
     * @param products список товаров
     */
    public ProductAdapter(Context context, List<Category> products) {
        this.context = context;
        this.products = products != null ? products : new ArrayList<>();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        if (position >= 0 && position < products.size()) {
            Category product = products.get(position);
            holder.bind(product);
        }
    }

    /**
     * Возвращает количество элементов в списке товаров.
     *
     * @return размер списка товаров, или 0, если список пуст или равен null
     */
    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    /**
     * ViewHolder для отображения элемента товара.
     * <p>
     * Использует макет {@code item_product} для отображения изображения, названия,
     * описания и цены товара. При клике на элемент открывается {@link ProductDetailActivity}.
     * </p>
     */
    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameTextView;
        private final TextView descriptionTextView;
        private final TextView priceTextView;

        /**
         * Создаёт новый ViewHolder для заданного макета.
         *
         * @param itemView элемент макета
         */
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_image);
            nameTextView = itemView.findViewById(R.id.product_name);
            descriptionTextView = itemView.findViewById(R.id.product_description);
            priceTextView = itemView.findViewById(R.id.product_price);

            if (imageView == null || nameTextView == null || priceTextView == null) {
                Log.e(TAG, "Ошибка инициализации ViewHolder: imageView=" + imageView +
                        ", nameTextView=" + nameTextView + ", priceTextView=" + priceTextView);
                return;
            }

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Category product = products.get(position);
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("PRODUCT_ID", product.getId());
                    intent.putExtra("PRODUCT_NAME", product.getName());
                    intent.putExtra("PRODUCT_DESCRIPTION", product.getDescription());
                    intent.putExtra("PRODUCT_PRICE", product.getPrice() != null ? product.getPrice() : INVALID_PRICE);
                    context.startActivity(intent);
                }
            });
        }

        /**
         * Привязывает данные товара к элементам интерфейса.
         *
         * @param product товар для отображения
         */
        public void bind(Category product) {
            if (product == null || nameTextView == null || priceTextView == null) {
                Log.w(TAG, "bind: Товар или элементы интерфейса равны null");
                return;
            }

            nameTextView.setText(product.getName() != null ? product.getName() : "");
            descriptionTextView.setText(product.getDescription() != null ? product.getDescription() : "");
            priceTextView.setText(product.getPrice() != null ? String.format("%.2f ₽", product.getPrice()) : "N/A");

            int imageResource = IMAGE_MAP.getOrDefault(product.getId(), R.drawable.error_image);
            Glide.with(context)
                    .load(imageResource)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
    }
}