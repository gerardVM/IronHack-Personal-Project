package bank.services;

import bank.models.accounts.Checking;
import bank.models.accounts.CreditCard;
import bank.models.accounts.Savings;
import bank.models.accounts.StudentChecking;
import bank.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class NewAccounts {
    @Autowired
    private AccountRepository accountRepository;

    public CreditCard newCreditCard(CreditCard creditCardAccount) { return accountRepository.save(creditCardAccount); }

    public Savings newSavings(Savings savingsAccount) {
        return accountRepository.save(savingsAccount);
    }

    public void newChecking(Checking checkingAccount) {
        if (getAge(checkingAccount.getPrimaryOwner().getBirthDate()) < 24) {
            accountRepository.save(newStudentChecking(checkingAccount));
        } else {
            accountRepository.save(checkingAccount);
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
