/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 *
 * @author c0646039
 */
public class ProductList {
     private List<Product> productList;

    public ProductList() {
        productList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String Query = "SELECT * FROM product";
            PreparedStatement pstmt = conn.prepareStatement(Query);
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                Product p = new Product(res.getInt("productID"),
                        res.getString("name"),
                        res.getString("description"),
                        res.getInt("quantity"));
                productList.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JsonArray toJSON() {
        JsonArrayBuilder json = Json.createArrayBuilder();
        for (Product p : productList) {
            json.add(p.toJSON());
        }
        return json.build();
    }

    public Product get(int productID) {
        Product result = null;
        for (Product p : productList) {
            if (p.getProductID() == productID) {
                result = p;
            }
        }
        return result;
    }

    public void set(int productID, Product p) {
        int result = doUpdate(
                "update product SET name = ?, description = ?, quantity = ? where productID = ?",
                p.getName(),
                p.getDescription(),
                String.valueOf(p.getQuantity()),
                String.valueOf(productID));
        if (result > 0) {
            Product original = get(productID);
            original.setName(p.getName());
            original.setDescription(p.getDescription());
            original.setQuantity(p.getQuantity());
        }

    }

    public void add(Product m) throws Exception {
        int result = doUpdate(
                "INSERT into product (productID, name, description, quantity) values (?, ?, ?, ?)",
                String.valueOf(m.getProductID()),
                m.getName(),
                m.getDescription(),
                String.valueOf(m.getQuantity()));
        if (result > 0) {
            productList.add(m);
        } else {
            throw new Exception("Error Inserting");
        }
    }
    


    public void remove(Product m) throws Exception {
        remove(m.getProductID());
    }

    public void remove(int productID) throws Exception {
        int result = doUpdate("DELETE from product where productID = ?",
                String.valueOf(productID));
        if (result > 0) {
            Product original = get(productID);
            productList.remove(original);
        } else {
            throw new Exception("Delete failed");
        }

    }

    private Connection getConnection() throws SQLException {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String jdbc = "jdbc:mysql://localhost/fastenlt";
            con = (Connection) DriverManager.getConnection(jdbc, "root", "");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }


    private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection con = getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }

    
    
}
