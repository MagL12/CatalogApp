package com.matlakhov.catalogapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Модель данных для десериализации JSON-ответа от API, содержащего каталог и список товаров.
 * <p>
 * Этот класс используется для парсинга JSON-ответа, где поле {@code "catalog"} содержит список категорий,
 * а поле {@code "products"} — список товаров, не вложенных в категории.
 * </p>
 */
public class CatalogResponse {

    /** Корневой список категорий из JSON-поля "catalog". */
    @SerializedName("catalog")
    private List<Category> catalog;

    /** Список «отдельных» товаров из JSON-поля "products". */
    @SerializedName("products")
    private List<Product> products;

    /**
     * Создаёт новый объект с пустыми списками категорий и товаров.
     */
    public CatalogResponse() {
        this.catalog = new ArrayList<>();
        this.products = new ArrayList<>();
    }

    /**
     * Возвращает неизменяемый список категорий.
     * <p>
     * Каждая категория может содержать вложенные элементы (items). Если список категорий не инициализирован,
     * возвращается пустой список.
     * </p>
     *
     * @return неизменяемый список категорий
     */
    public List<Category> getCatalog() {
        return catalog != null ? Collections.unmodifiableList(catalog) : Collections.emptyList();
    }

    /**
     * Возвращает неизменяемый список товаров.
     * <p>
     * Содержит товары, лежащие в корне JSON (не вложенные в категории). Если список товаров не инициализирован,
     * возвращается пустой список.
     * </p>
     *
     * @return неизменяемый список товаров
     */
    public List<Product> getProducts() {
        return products != null ? Collections.unmodifiableList(products) : Collections.emptyList();
    }

    /**
     * Устанавливает список категорий.
     *
     * @param catalog список категорий
     */
    public void setCatalog(List<Category> catalog) {
        this.catalog = catalog != null ? new ArrayList<>(catalog) : new ArrayList<>();
    }

    /**
     * Устанавливает список товаров.
     *
     * @param products список товаров
     */
    public void setProducts(List<Product> products) {
        this.products = products != null ? new ArrayList<>(products) : new ArrayList<>();
    }
}
