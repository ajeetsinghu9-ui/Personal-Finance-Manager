package model;

import java.util.Date;

/**
 * Model class representing a financial transaction.
 */
public class Transaction {

    private int    id;
    private String type;          // "Income" or "Expense"
    private int    categoryId;
    private String categoryName;  // populated via JOIN
    private double amount;
    private Date   date;
    private String description;

    // ---- Constructors ------------------------------------------------

    public Transaction() {}

    public Transaction(String type, int categoryId, double amount, Date date, String description) {
        this.type        = type;
        this.categoryId  = categoryId;
        this.amount      = amount;
        this.date        = date;
        this.description = description;
    }

    // ---- Getters & Setters -------------------------------------------

    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public String getType()                   { return type; }
    public void setType(String type)          { this.type = type; }

    public int getCategoryId()                { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName()                    { return categoryName; }
    public void setCategoryName(String categoryName)   { this.categoryName = categoryName; }

    public double getAmount()                 { return amount; }
    public void setAmount(double amount)      { this.amount = amount; }

    public Date getDate()                     { return date; }
    public void setDate(Date date)            { this.date = date; }

    public String getDescription()                     { return description; }
    public void setDescription(String description)     { this.description = description; }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", type=" + type +
               ", category=" + categoryName + ", amount=" + amount +
               ", date=" + date + ", description=" + description + "}";
    }
}
