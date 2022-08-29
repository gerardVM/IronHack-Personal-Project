package bank.models.roles;

import bank.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

/**
 * This class represents an Admin Account.
 * In this project, Admins have name and password.
 */
@Getter
@Setter
@Entity
public class Admin extends User {

}
