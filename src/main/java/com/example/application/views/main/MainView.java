package com.example.application.views.main;

import com.apiclient.consultor.ApiException;
import com.apiclient.consultor.model.Consulta;
import com.apiclient.consultor.model.HistoryConversation;
import com.example.application.service.ConsultaService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@PageTitle("Main")
@Route(value = "/")

public class MainView extends AppLayout {
    ConsultaService consultService = new ConsultaService();
    MessageList messages = new MessageList();
    MessageInput inputBtn = new MessageInput();
    private final Button historyConversation = new Button("History Chat");

    public MainView() throws Exception {

        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("GPT CHAT");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "2");
        Button chatbtn = new Button("Chat with GPT");
        chatbtn.addClickListener(submitEvent -> createChatWindows());
        historyConversation.addClickListener(submitEvent -> {
            try {
                historyPanel();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        historyPanel();
        toggle.getThemeNames().add(Lumo.DARK);
        addToDrawer(chatbtn, historyConversation);
        addToNavbar(toggle, title);

    }

    public Consulta messageCHAT(String message) throws ApiException {
        Consulta response;
        response = consultService.chatCompletion(message);
        return response;
    }

    public void createChatWindows() {

        inputBtn.addSubmitListener(submitEvent -> {
            MessageListItem newMessage = new MessageListItem(
                    submitEvent.getValue(),
                    Instant.now(),
                    "User"
            );
            newMessage.setUserColorIndex(3);

            Consulta consult;
            MessageListItem chatgptResponse = null;
            try {
                consult = this.messageCHAT(submitEvent.getValue());
                chatgptResponse = new MessageListItem(consult.getMessages(),
                        Instant.now(),
                        "ChatGPT");
                chatgptResponse.addThemeNames("current-user");

            } catch (ApiException e) {

                Notification.show("Sorry, it takes a long time to respond.",
                        6000, Notification.Position.MIDDLE);
            }

            List<MessageListItem> items = new ArrayList<>(messages.getItems());
            items.add(newMessage);
            items.add(chatgptResponse);
            messages.setItems(items);

        });
        VerticalLayout chatLayout = new VerticalLayout(messages, inputBtn);

        chatLayout.setHeight("90vh");
        chatLayout.setWidth("100%");
        inputBtn.setWidth("100%");
        chatLayout.expand(messages);
        chatLayout.getThemeList().add(Lumo.DARK);
        setContent(chatLayout);
    }

    public void historyPanel() throws Exception {
        List<HistoryConversation> hist;
        VerticalLayout content = new VerticalLayout();
        HorizontalLayout deleteBtn = new HorizontalLayout();

        try {
            hist = consultService.historyConversationList();
            for (HistoryConversation historyConversation : hist) {
                Details details = new Details();
                Div contenidoPLusBTN = new Div();
                Span respuesta = new Span(historyConversation.getAnswer());
                details.setSummaryText(historyConversation.getQuestion());
                contenidoPLusBTN.add(respuesta, deleteQuestionButton(Math.toIntExact(historyConversation.getId())));

                details.setContent(contenidoPLusBTN);
                content.add(details);
            }
            content.setHeight("90vh");
            content.setWidth("100%");
            content.getThemeList().add(Lumo.DARK);
            setContent(content);
        } catch (Exception e) {

            Notification.show("Sorry, We can't load any history conversation.");
        }

    }

    public Button deleteQuestionButton(int id) {
        Button deleteButton = new Button("Delete");

        deleteButton.addClickListener(e -> {
            try {
                consultService.deletequestion(id);
                historyPanel();

            } catch (Exception ex) {
                Notification.show("We can't connect to database");
            }
        });
        return deleteButton;
    }

}
