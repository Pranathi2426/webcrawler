package org.example;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashSet;

import org.jsoup.Jsoup;
        import org.jsoup.nodes.Document;
        import org.jsoup.nodes.Element;
        import org.jsoup.select.Elements;

        import java.io.IOException;
        import java.sql.Connection;
        import java.sql.SQLException;
        import java.util.HashSet;

public class Crawler {
    private HashSet<String> urlink;
    public Connection connection;

    public Crawler() {
//        try {
        connection = DatabaseConnection.getConnection();
//        }
//        catch (SQLException sqlException) {
//            sqlException.printStackTrace();
//        }
        urlink = new HashSet<String>();
    }

    public void getPageTextAndLinks(String URL, int depth) {
        if (!urlink.contains(URL)) {
            try {
                if (urlink.add(URL)) {
                    System.out.println(URL);
                }
                Document document = Jsoup.connect(URL).userAgent("Chrome").timeout(5000).get();
                String text = document.text().length()<501?document.text():document.text().substring(0,500);
//                System.out.println(text)
                PreparedStatement preparedStatement = connection.prepareStatement("Insert into pages values (?,?,?)");
                preparedStatement.setString(1,document.title());
                preparedStatement.setString(2,URL);
                preparedStatement.setString(3,text);
                preparedStatement.executeUpdate();


                depth++;
               if (depth == 2) {
                    return;
                }


                Elements availableLinks = document.select("a[href]");

                for (Element element : availableLinks) {
                    getPageTextAndLinks(element.attr("abs:href"), depth);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }catch (SQLException sqlException){
                sqlException.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        crawler.getPageTextAndLinks("https://www.geeksforgeeks.org/java/",0);
    }
}