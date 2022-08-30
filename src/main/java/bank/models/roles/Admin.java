package bank.models.roles;

import bank.models.User;
import javax.persistence.*;

/**
 * This class represents an Admin Account.
 * In this project, Admins have name and password.
 */

@Entity
public class Admin extends User { }
