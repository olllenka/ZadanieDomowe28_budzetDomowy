package com.example.demo.transaction;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionDao {

    private static final String URL = "jdbc:mysql://localhost:3306/homebudget?serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "admin";

    private Connection connection;

    public TransactionDao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException exception) {
            System.err.println("Nie udało się znaleźć sterownika");
        } catch (SQLException exception) {
            System.err.println("Nie udało się nawiązać połączenia");
        }
    }

    public void add(Transaction transaction){
        String insertTransactionSql = "INSERT INTO transaction(type, description, amount, date) VALUES(?, ?, ?, ?)";
        try {
            PreparedStatement insertStatement = connection.prepareStatement(insertTransactionSql);
            insertStatement.setString(1, String.valueOf(transaction.getType()));
            insertStatement.setString(2, transaction.getDescription());
            insertStatement.setDouble(3, transaction.getAmount());
            insertStatement.setDate(4, transaction.getDate());
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Nie udało się dodać transakcji");
            e.printStackTrace();
        }
    }

    public void update(Transaction transaction){
        String updateTransactionSql = "UPDATE transaction SET type = ?, description = ?, amount = ?, date = ? WHERE id = ?";
        try {
            PreparedStatement updateStatement = connection.prepareStatement(updateTransactionSql);
            updateStatement.setString(1, String.valueOf(transaction.getType()));
            updateStatement.setString(2, transaction.getDescription());
            updateStatement.setDouble(3, transaction.getAmount());
            updateStatement.setDate(4, transaction.getDate());
            updateStatement.setLong(5, transaction.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Nie udało się zmodyfikować transakcji");
            e.printStackTrace();
        }
    }

    public void delete(long id){
        String deleteTransactionSql = "DELETE FROM transaction WHERE id = ?";
        try {
            PreparedStatement deleteStatement = connection.prepareStatement(deleteTransactionSql);
            deleteStatement.setLong(1, id);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Nie udało się usunąć transakcji");
            e.printStackTrace();
        }
    }

    public List<Transaction> selectAllIncomes(){
        return selectAll(TransactionType.INCOME);
    }

    public List<Transaction> selectAllExpenses(){
        return selectAll(TransactionType.EXPENSE);
    }

    private List<Transaction> selectAll(TransactionType transactionType) {
        List<Transaction> transactions = new ArrayList<>();
        String selectSql = "SELECT id, description, amount, type, date FROM transaction WHERE type = ? ORDER BY date DESC";
        try {
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, String.valueOf(transactionType));
            ResultSet transactionsSet = selectStatement.executeQuery();
            while(transactionsSet.next()){
                Transaction transaction = new Transaction();
                transaction.setId(transactionsSet.getLong("id"));
                transaction.setType(transactionType);
                transaction.setDescription(transactionsSet.getString("description"));
                transaction.setAmount(transactionsSet.getDouble("amount"));
                transaction.setDate(transactionsSet.getDate("date"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.err.println("Nie udało się wyświetlić transakcji");
            e.printStackTrace();
        }
        return transactions;
    }

    public Transaction findById(Long id) {
        Transaction transaction = new Transaction();
        String selectSql = "SELECT id, description, amount, type, date FROM transaction WHERE id = ?";
        try {
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setLong(1, id);
            ResultSet transactionsSet = selectStatement.executeQuery();
            if(transactionsSet.next()){
                transaction.setId(transactionsSet.getLong("id"));
                transaction.setType(getTransactionType(transactionsSet.getString("type")));
                transaction.setDescription(transactionsSet.getString("description"));
                transaction.setAmount(transactionsSet.getDouble("amount"));
                transaction.setDate(transactionsSet.getDate("date"));
            }
        } catch (SQLException e) {
            System.err.println("Nie udało się wyświetlić transakcji");
            e.printStackTrace();
        }
        return transaction;
    }

    private TransactionType getTransactionType(String typeString) {
        TransactionType type;
        if(TransactionType.INCOME.toString().equalsIgnoreCase(typeString))
            type = TransactionType.INCOME;
        else
            type = TransactionType.EXPENSE;
        return type;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException exception) {
            System.err.println("Nie udało się zamknąć połączenia");
        }
    }
}
