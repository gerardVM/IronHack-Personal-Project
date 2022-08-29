package bank.models;

import bank.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

/**
 *  This class represents a Student Checking Account.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class StudentChecking extends Account {
    @NotNull
    private String secretKey;
    @NotNull
    @Past
    private LocalDate creationDate;
    @NotNull
    private Status accountStatus;
}
