package de.ur.mi.android.sportsfreund;

public class ListObject {

    private String title;
    private String body;

    public ListObject (String title,String body)  {
        this.body = body;
        this.title = title;
    }

    public String getBody()  {
        return body;
    }

    public String getTitle()  {
        return title;
    }
}
