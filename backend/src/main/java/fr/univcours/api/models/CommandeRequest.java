package fr.univcours.api.models;

import java.util.List;

public class CommandeRequest {
    private List<CommandeItem> items;

    public CommandeRequest() {
    }

    public List<CommandeItem> getItems() {
        return items;
    }

    public void setItems(List<CommandeItem> items) {
        this.items = items;
    }
}
