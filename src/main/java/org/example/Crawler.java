package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import javax.swing.text.Document;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;

public class Crawler {
    public HashSet<String> urlHash;
    public int max_depth = 2;
    public static Connection connection = null;
    public Crawler(){
        urlHash = new HashSet<>();
        connection = DatabaseConnectionn.getConnection();
    }
    public void getPageTextAndLinks(String url, int depth){
        if(!urlHash.contains(url)){
             if(urlHash.add(url)){
                 System.out.println(url);
             }
             try{
                 Document document = (Document) Jsoup.connect(url).timeout(5000).get();
                 String text = document.text().length()>1000?document.text().substring(0,999):document.text();
                 String title = document.title();

                 //System.out.println(title +"/n"+text);
                 PreparedStatement preparedStatement = connection.prepareStatement("Insert into pages values(?,?,?)");
                 preparedStatement.setString(1,title);
                 preparedStatement.setString(1,url);
                 preparedStatement.setString(1,text);

                 depth++;
                 if(depth ==2 ){
                     return;
                 }
                 Elements availableLinksOnPage  = document.select("a[href]");
                 for(Element currentLink:availableLinksOnPage ){
                     getPageTextAndLinks(currentLink.attr("abs:href"), depth);
                 }
             }
             catch (IOException ioException){
                 ioException.printStackTrace();

            }
             catch(SQLException sqlException){
                 sqlException.printStackTrace();
             }
        }
    }
    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        crawler.getPageTextAndLinks("https://www.javatpoint.com", 0);
    }
}