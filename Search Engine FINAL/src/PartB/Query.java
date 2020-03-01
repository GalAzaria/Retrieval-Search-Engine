package PartB;

public class Query {
    private String number;
    private String title;
    private String description;

    public Query(String number, String title, String description) {
        this.number = number;
        this.title = title;
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
