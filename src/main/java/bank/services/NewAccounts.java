package bank.services;

import bank.models.*;
import bank.repositories.CheckingRepository;
import bank.repositories.CreditCardRepository;
import bank.repositories.SavingsRepository;
import bank.repositories.StudentCheckingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

// How to use static methods smoothly

@Service
public class NewAccounts {
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private SavingsRepository savingsRepository;

    public CreditCard newCreditCard(CreditCard creditCardAccount) { return creditCardRepository.save(creditCardAccount); }

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
        return studentChecking;
    }

    private int getDiffYears(LocalDate first, LocalDate last) {
        int diff = first.getYear() - last.getYear();
        if (first.getMonthValue() < last.getMonthValue() ||
                first.getMonthValue() == last.getMonthValue() && first.getDayOfMonth() < last.getDayOfMonth()) {
            diff--;
        }
        return diff;
    }

    public int getAge(LocalDate birthDate) {
        LocalDate now = LocalDate.now();
        return getDiffYears(now, birthDate);
    }
}
