package com.matlakhov.catalogapp.network;

import com.matlakhov.catalogapp.models.CatalogResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Интерфейс для взаимодействия с API через Retrofit.
 * <p>
 * Определяет методы для выполнения HTTP-запросов к серверу.
 * Текущая реализация включает запрос каталога, но может быть расширена для других операций.
 * </p>
 * <p>
 * <b>Примечание:</b> Текущий эндпоинт ({@code uc?export=download&id=...}) указывает на Google Drive.
 * Убедитесь, что это правильный URL для API, и что сервер возвращает JSON в формате {@link CatalogResponse}.
 * </p>
 */
public interface ApiService {

    /**
     * Выполняет GET-запрос для получения каталога.
     * <p>
     * Запрашивает данные каталога и товаров с сервера. Ответ десериализуется в объект
     * {@link CatalogResponse}, содержащий список категорий и товаров.
     * </p>
     *
     * @return объект {@link Call} для асинхронного выполнения запроса
     */
    @GET("uc?export=download&id=1bIgYsUcdnXbPVD-euR5juTck8HHkrZvM")
    Call<CatalogResponse> getCatalog();
}
