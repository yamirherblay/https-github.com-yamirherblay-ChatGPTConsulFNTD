package com.example.application.net;

import com.apiclient.consultor.ApiClient;
import com.apiclient.consultor.api.ConsultaApi;

public enum Apiconect {
    INSTANCE;
    final ConsultaApi consultaApi;

    Apiconect() {
        ApiClient apiClient = new ApiClient();
        apiClient.updateBaseUri("http://127.0.0.1:8080");
        consultaApi = new ConsultaApi(apiClient);
    }

    public ConsultaApi getConsultaApi() {
        return consultaApi;
    }
}
