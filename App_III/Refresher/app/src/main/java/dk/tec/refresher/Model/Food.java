package dk.tec.refresher.Model;

public class Food {
    public Food() {
    }

    public Food(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
