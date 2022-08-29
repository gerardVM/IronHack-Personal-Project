package bank.services;

import bank.models.roles.Admin;
import bank.models.roles.ThirdParty;
import bank.repositories.ThirdPartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class ThirdPartyService {
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    public Optional<ThirdParty> findByUsername(String username) {
        return thirdPartyRepository.findByUsername(username);
    }
}
