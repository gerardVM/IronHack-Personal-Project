package bank.services;

import bank.models.StudentChecking;
import bank.models.roles.AccountHolder;
import bank.repositories.StudentCheckingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentCheckingService {
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    public Optional<StudentChecking> findByPrimaryOwner(AccountHolder user) {
        return studentCheckingRepository.findByPrimaryOwner(user);
    }
}
