package entity;


public class Cow {

    private String tagId;
    private String breed;
    private String age;
    private String yield; 


    public Cow(String tagId, String breed, String age, String yield) {
        this.tagId = tagId;
        this.breed = breed;
        this.age = age;
        this.yield = yield;
    }

    public String getTagId() { return tagId; }
    public void setTagId(String tagId) { this.tagId = tagId; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }

    public String getYield() { return yield; }
    public void setYield(String yield) { this.yield = yield; }

    public String toLine() {
        return tagId + "," + breed + "," + age + "," + yield;
    }

    
    public static Cow fromLine(String line) {
        if (line == null) return null;

        String[] data = line.split(",", -1);
        if (data.length != 4) return null; // Skip malformed lines

        return new Cow(data[0], data[1], data[2], data[3]);
    }

    
    public Object[] toRow() {
        return new Object[] { tagId, breed, age, yield + " L" };
    }
}