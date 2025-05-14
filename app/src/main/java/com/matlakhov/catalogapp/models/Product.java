package com.matlakhov.catalogapp.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

/**
 * Модель данных для товара.
 * <p>
 * Этот класс представляет собой товар, десериализуемый из JSON с помощью Gson.
 * Класс реализует интерфейс {@link Parcelable} для передачи данных между активностями.
 * </p>
 */
public class Product implements Parcelable {

    /** Тег для целей логирования. */
    private static final String TAG = "Product";

    /** Уникальный идентификатор товара. */
    @SerializedName("id")
    private int id;

    /** Название товара. */
    @SerializedName("name")
    private String name;

    /** URL изображения товара. */
    @SerializedName("image")
    private String imageUrl;

    /** Описание товара. */
    @SerializedName("description")
    private String description;

    /** Цена товара. */
    @SerializedName("price")
    private double price;

    /**
     * Создаёт новый пустой объект товара.
     */
    public Product() {
    }

    /**
     * Создаёт объект товара из данных, прочитанных из {@link Parcel}.
     *
     * @param in объект {@link Parcel} с данными
     */
    protected Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        imageUrl = in.readString();
        description = in.readString();
        price = in.readDouble();
        Log.d(TAG, "Создан объект Product из Parcel: id=" + id + ", name=" + name);
    }

    /**
     * Создатель объекта {@link Product} для реализации {@link Parcelable}.
     */
    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    /**
     * Возвращает идентификатор товара.
     *
     * @return идентификатор
     */
    public int getId() {
        return id;
    }

    /**
     * Возвращает название товара.
     *
     * @return название, или null, если не задано
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает URL изображения товара.
     *
     * @return URL изображения, или null, если не задано
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Возвращает описание товара.
     *
     * @return описание, или null, если не задано
     */
    public String getDescription() {
        return description;
    }

    /**
     * Возвращает цену товара.
     *
     * @return цена
     */
    public double getPrice() {
        return price;
    }

    /**
     * Устанавливает идентификатор товара.
     *
     * @param id идентификатор
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Устанавливает название товара.
     *
     * @param name название
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Устанавливает URL изображения товара.
     *
     * @param imageUrl URL изображения
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Устанавливает описание товара.
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
    public void setPrice(double price) {
        this.price = price;
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
        dest.writeDouble(price);
    }
}