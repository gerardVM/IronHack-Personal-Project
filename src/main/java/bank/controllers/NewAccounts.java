package bank.controllers;

import bank.models.Checking;
import bank.models.CreditCard;
import bank.models.Savings;
import bank.models.StudentChecking;
import bank.repositories.CheckingRepository;
import bank.repositories.CreditCardRepository;
import bank.repositories.SavingsRepository;
import bank.repositories.StudentCheckingRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class NewAccounts {
    @Autowired
    CheckingRepository checkingRepository;
    @Autowired
    StudentCheckingRepository studentCheckingRepository;
    @Autowired
    CreditCardRepository creditCardRepository;
    @Autowired
    SavingsRepository savingsRepository;

    public CreditCard newCreditCard(CreditCard creditCardAccount) {
        return creditCardRepository.save(creditCardAccount);
    }

    public Savings newSavings(Savings savingsAccount) {
        return savingsRepository.save(savingsAccount);
    }

    public void newChecking(Checking checkingAccount) {
        if (getAge(checkingAccount.getPrimaryOwner().getBirthDate()) < 24) {
            studentCheckingRepository.save(newStudentChecking(checkingAccount));
        } else {
            checkingRepository.save(checkingAccount);
        }
    }

    public StudentChecking newStudentChecking(Checking checkingAccount) {
        StudentChecking studentChecking = new StudentChecking();
        studentChecking.setId(checkingAccount.getId());
        studentChecking.setBalance(checkingAccount.getBalance());
        studentChecking.setPrimaryOwner(checkingAccount.getPrimaryOwner());
        studentChecking.setSecondaryOwner(checkingAccount.getSecondaryOwner());
        studentChecking.setSecretKey(checkingAccount.getSecretKey());
        studentChecking.setCreationDate(checkingAccount.getCreationDate());
        studentChecking.setAccountStatus(checkingAccount.getAccountStatus());
        return studentCheckingRepository.save(studentChecking);
    }

    private static int getDiffYears(LocalDate first, LocalDate last) {
        int diff = first.getYear() - last.getYear();
        if (first.getMonthValue() < last.getMonthValue() ||
                first.getMonthValue() == last.getMonthValue() && first.getDayOfMonth() < last.getDayOfMonth()) {
            diff--;
        }
        return diff;
    }

    private static int getAge(LocalDate birthDate) {
        LocalDate now = LocalDate.now();
        return getDiffYears(birthDate, now);
    }
}
