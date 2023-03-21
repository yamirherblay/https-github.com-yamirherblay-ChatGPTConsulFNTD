package com.example.application.service;

import com.apiclient.consultor.ApiException;
import com.apiclient.consultor.api.ConsultaApi;
import com.apiclient.consultor.model.Consulta;
import com.apiclient.consultor.model.HistoryConversation;
import com.example.application.net.Apiconect;
import com.vaadin.flow.component.notification.Notification;

import java.util.List;

public class ConsultaService {

    private final ConsultaApi consultaApi = Apiconect.INSTANCE.getConsultaApi();

    public Consulta chatCompletion(String question) throws ApiException {
        Consulta result;
        try {
            result = consultaApi.chat(question);

        } catch (ApiException e) {

            Notification.show("We can't make the query");
            throw new ApiException(e.getMessage());
        }
        return result;
    }

    public List<HistoryConversation> historyConversationList() throws ApiException {
        List<HistoryConversation> history = consultaApi.historyConversation();
        return history;
    }

    public int deletequestion(int id) throws ApiException {
        if (consultaApi.deleteQuestionID(id) == 1) return 1;
        return -1;
    }
}
