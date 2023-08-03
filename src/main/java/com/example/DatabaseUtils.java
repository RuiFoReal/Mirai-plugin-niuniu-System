package com.example;


import java.sql.*;
import java.util.Random;

public final class DatabaseUtils {

    private static final String url = "jdbc:sqlite:D:\\sqlite\\rtq.db";
    private static final String select_all = "SELECT * FROM niuzi order by long desc";

    private static final String select_user = "SELECT * FROM niuzi where qq=?";

    private static final String insert_user = "INSERT INTO niuzi (qq,long,name) VALUES (?, ?, ?)";
    private static final String update_username = "UPDATE niuzi SET name =? WHERE qq=?";

    private static final String check_user_female = "SELECT * FROM niuzi where qq=? and sex='女'";
    private static final String check_user_has_mate = "SELECT mate FROM niuzi where qq=?";
    private static final String update_user_female = "UPDATE niuzi SET sex ='女' WHERE qq=?";
    private static final String update_user_mate = "UPDATE niuzi SET mate=? WHERE qq=?";

    private static final String     delete_user_mate = "UPDATE niuzi SET mate=0 WHERE qq=?";
    private static final Connection connection;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("创建成功");
    }

    private DatabaseUtils() {
    }

    public static void test() {
    }

    public static ResultSet QueryAllData() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(select_all);
        return preparedStatement.executeQuery();
    }

    public static ResultSet QueryOneUserData(long qq) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(select_user);
        preparedStatement.setLong(1, qq);
        return preparedStatement.executeQuery();
    }


    public static boolean CheckUserExists(long qq) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(select_user);
        preparedStatement.setLong(1, qq);
        ResultSet resultSet1 = preparedStatement.executeQuery();
        boolean next = resultSet1.next();
        resultSet1.close();
        preparedStatement.close();
        return next;
    }

    public static void InsertUser(long qq, String name) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(insert_user);
        preparedStatement.setLong(1, qq);
        preparedStatement.setFloat(2, new Random().nextFloat()*1000);
        preparedStatement.setString(3, name);
        preparedStatement.executeUpdate();
    }

    public static void UpdateUserName(long qq,String name) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(update_username);
        preparedStatement.setString(1, name);
        preparedStatement.setLong(2, qq);
        preparedStatement.executeUpdate();
    }

    public static void UpdateLong(long qq,float damage,String symbol) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE niuzi SET long=long"+symbol+"? WHERE qq=?");
        preparedStatement.setFloat(1, damage);
        preparedStatement.setLong(2,qq);
        preparedStatement.executeUpdate();
    }

    public static boolean CheckUserIsFemale(long qq) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(check_user_female);
        preparedStatement.setLong(1, qq);
        ResultSet resultSet1 = preparedStatement.executeQuery();
        boolean next = resultSet1.next();
        resultSet1.close();
        preparedStatement.close();
        return next;
    }

    public static void UpdateUserFemale(long qq) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(update_user_female);
        preparedStatement.setLong(1, qq);
        preparedStatement.executeUpdate();
        UpdateLong(qq,50,"-");
    }

    public static boolean CheckUserHasMate(long qq) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(check_user_has_mate);
        preparedStatement.setLong(1, qq);
        ResultSet resultSet1 = preparedStatement.executeQuery();
        long mate = resultSet1.getLong("mate");
        resultSet1.close();
        preparedStatement.close();
        return mate != 0;
    }

    public static void UpdateUserMate(long qq,long mate) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(update_user_mate);
        preparedStatement.setLong(1, mate);
        preparedStatement.setLong(2, qq);
        preparedStatement.executeUpdate();
    }

    public static long QueryUserMate(long qq) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(check_user_has_mate);
        preparedStatement.setLong(1, qq);
        ResultSet resultSet1 = preparedStatement.executeQuery();
        return resultSet1.getLong("mate");
    }

    public static void DeleteUserMate(long qq) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(delete_user_mate);
        preparedStatement.setLong(1, qq);
        preparedStatement.executeUpdate();
    }
}