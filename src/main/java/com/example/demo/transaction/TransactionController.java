package com.example.demo.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TransactionController {

    private TransactionDao transactionDao;

    @Autowired
    public TransactionController(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("mode", "add");
        return "home";
    }

    @PostMapping("/")
    public String add(Transaction transaction){
        transactionDao.add(transaction);
        return "success";
    }

    @GetMapping("/edit")
    public String editForm(Model model, @RequestParam Long id){
        Transaction transaction = transactionDao.findById(id);
        model.addAttribute("transaction", transaction);
        model.addAttribute("mode", "edit");
        return "home";
    }

    @PostMapping("/edit")
    public String edit(Transaction transaction){
        transactionDao.update(transaction);
        return "success";
    }

    @GetMapping("/remove")
    public String remove(@RequestParam Long id){
        transactionDao.delete(id);
        return "success";
    }

    @GetMapping("incomes")
    public String showAllIncomes(Model model){
        List<Transaction> transactions = transactionDao.selectAllIncomes();
        model.addAttribute("transactions", transactions);
        model.addAttribute("sumOfIncomes", TransactionUtil.sumOfTransactionsAmount(transactions));
        return "incomes";
    }

    @GetMapping("expenses")
    public String showAllExpenses(Model model){
        List<Transaction> transactions = transactionDao.selectAllExpenses();
        model.addAttribute("transactions", transactions);
        model.addAttribute("sumOfExpenses", TransactionUtil.sumOfTransactionsAmount(transactions));
        return "expenses";
    }
}
