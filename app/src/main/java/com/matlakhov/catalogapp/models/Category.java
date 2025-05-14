package com.matlakhov.catalogapp.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Модель данных для категории или товара.
 * <p>
 * Этот класс представляет собой категорию или товар, десериализуемый из JSON с помощью Gson.
 * Категория может содержать вложенные подкатегории или товары (поле {@code items}).
 * Класс реализует интерфейс {@link Parcelable} для передачи данных между активностями.
 * </p>
 */
public class Category implements Parcelable {

    /** Тег для целей логирования. */
    private static final String TAG = "Category";

    /** Уникальный идентификатор категории или товара. */
    @SerializedName("id")
    private int id;

    /** Название категории или товара. */
    @SerializedName("name")
    private String name;

    /** URL изображения категории или товара. */
    @SerializedName("image")
    private String imageUrl;

    /** Описание категории или товара. */
    @SerializedName("description")
    private String description;

    /** Цена (для товаров). */
    @SerializedName("price")
    private Double price;

    /** Список вложенных подкатегорий или товаров. */
    @SerializedName("items")
    private List<Category> items;

    /**
     * Создаёт новый пустой объект категории.
     */
    public Category() {
        this.items = new ArrayList<>();
    }

    /**
     * Создаёт объект категории из данных, прочитанных из {@link Parcel}.
     *
     * @param in объект {@link Parcel} с данными
     */
    protected Category(Parcel in) {
        id = in.readInt();
        name = in.readString();
        imageUrl = in.readString();
        description = in.readString();

        // Обработка null для price
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }

        // Создаем пустой список сначала, чтобы избежать проблем с большими структурами
        items = new ArrayList<>();

        // Чтение размера списка
        int size = in.readInt();

        // Если размер > 0, инициализируем список элементов
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Category item = in.readParcelable(Category.class.getClassLoader());
                if (item != null) {
                    items.add(item);
                }
            }
        }
        Log.d(TAG, "Создан объект Category из Parcel: id=" + id + ", name=" + name);
    }

    /**
     * Создатель объекта {@link Category} для реализации {@link Parcelable}.
     */
    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    /**
     * Возвращает идентификатор категории или товара.
     *
     * @return идентификатор
     */
    public int getId() {
        return id;
    }

    /**
     * Возвращает название категории или товара.
     *
     * @return название, или null, если не задано
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает URL изображения категории или товара.
     *
     * @return URL изображения, или null, если не задано
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Возвращает описание категории или товара.
     *
     * @return описание, или null, если не задано
     */
    public String getDescription() {
        return description;
    }

    /**
     * Возвращает цену товара.
     *
     * @return цена, или null, если это категория, а не товар
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Возвращает неизменяемый список вложенных подкатегорий или товаров.
     *
     * @return неизменяемый список, или пустой список, если элементы отсутствуют
     */
    public List<Category> getItems() {
        return items != null ? Collections.unmodifiableList(items) : Collections.emptyList();
    }

    /**
     * Устанавливает идентификатор категории или товара.
     *
     * @param id идентификатор
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Устанавливает название категории или товара.
     *
     * @param name название
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Устанавливает URL изображения категории или товара.
     *
     * @param imageUrl URL изображения
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Устанавливает описание категории или товара.
     *
     * @param description описание
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Устанавливает цену товара.
     *
     * @param price цена
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Устанавливает список вложенных подкатегорий или товаров.
     *
     * @param items список подкатегорий или товаров
     */
    public void setItems(List<Category> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    /**
     * Проверяет, является ли объект категорией (содержит вложенные элементы).
     *
     * @return true, если объект содержит вложенные элементы, false в противном случае
     */
    public boolean isCategory() {
        return items != null && !items.isEmpty();
    }

    /**
     * Проверяет, является ли объект товаром (имеет цену).
     *
     * @return true, если объект имеет цену, false в противном случае
     */
    public boolean isProduct() {
        return price != null;
    }

    /**
     * Возвращает список товаров на текущем уровне вложенности.
     *
     * @return список товаров, или пустой список, если товаров нет
     */
    public List<Category> getProducts() {
        List<Category> products = new ArrayList<>();
        if (items != null) {
            for (Category item : items) {
                if (item != null && item.isProduct()) {
                    products.add(item);
                }
            }
        }
        return products;
    }

    /**
     * Рекурсивно собирает все товары из всех уровней вложенности.
     * <p>
     * Этот метод проходит по всем вложенным категориям и собирает товары.
     * При больших структурах может быть неэффективным из-за рекурсии.
     * </p>
     *
     * @return список всех товаров из всех уровней вложенности
     */
    public List<Category> getAllProducts() {
        List<Category> allProducts = new ArrayList<>();
        if (items != null) {
            for (Category item : items) {
                if (item != null) {
                    if (item.isProduct()) {
                        allProducts.add(item);
                    } else if (item.isCategory()) {
                        allProducts.addAll(item.getAllProducts());
                    }
                }
            }
        }
        return allProducts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(description);

        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price);
        }

        // Запись размера списка
        if (items == null || items.isEmpty()) {
            dest.writeInt(0);
        } else {
            dest.writeInt(items.size());
            // Запись каждого элемента отдельно
            for (Category item : items) {
                dest.writeParcelable(item, flags);
            }
        }
    }
}