# Requirements

## Introduction and general management

### User interface

- [x] [R68] The system should have a text-based user interface.
- [x] [R69] The system should keep track of the time spent from the moment the application is started until the user decides to exit the application. When exiting the application, the system should display the total time spent in the application in the following format:
```
Process start:  Tuesday; 2023-03-01 11:21:11
Process end:    Tuesday; 2023-03-01 11:21:56
Total time:     45132 milliseconds (45 seconds; 0 minutes; 0 hours)
```

### Access monitoring

- [ ] [R70] The system should keep track of generic data like the date of the last time the application was ran and the number of times the application was ran.
- [ ] [R71] There should be a user log in this format: `<date> <time> <username> <action>`. The log should be stored in a relational database.
- [ ] [R72] It should be possible to list the log from inside the application.
- [ ] [R73] It should be possible to search for user records in the log.

### Object oriented programming

- [x] [R74] The application has to be implemented using object-oriented programming, using tha Java programming language.
- [x] [R75] Implement the necessary storage structures trying to optimize the resource usage and the performance of the application.

### Data validation and user notification

- [ ] [R76] The system should validate all data entered by the user.
- [x] [R77] The system should display informative messages when necessary.
> [!TIP]
> Informative messages includes user actions, errors, warnings, etc.

###  Database management

- [ ] [R65] The system should use a relational database to store the data needed for the application.
- [ ] [R66] The access to the system should be restricted with a `username` and a `password`. Both should be stored in a relational database.
- [ ] [R67] Database access parameters like `ip`, `port`, `database name`, `login` and `password` should be stored in a text file `Properties`. There should be an interface to change these parameters from inside the application when it starts, before the user login process. This interface should only appear when the user asks for it.

## Application global description

### General Requirements

- [ ] [R1] Let users register and login to the system.
- [ ] [R2] Let access to the system by 3 types of `Users`: `Admin`, `Reviewer` and `Author`.
- [ ] [R3] `Users` are characterized by the attributes: `name`, `username`, `email`, `password`, `type` and `status`.
> [!TIP]
> `status` can be `active` or `inactive`.
- [ ] [R4] Attribute `username` and `email` must be unique.
- [ ] [R5] `email` has to have a valid format.
> [!TIP]
> Format must be `[description]@[entity].[domain]`.


### Basic user management and access control

- [ ] [R6] Each user only can alter its own data, not being able to alter or view data from other users.
- [ ] [R7] The exception to R6 are the `Admin` users, that can view and alter data from all users, including other `Admin` users. `Admin` users can also create new users.
- [ ] [R8] Both `Author` and `Reviewer` users, additionally to R3, have the attributes `nif`, `phone` and `address`. `Author` users have the attributes `literary style` and `date of activity start`. `Reviewer` users have the attributes `area of expertise` and `academic background`.
- [ ] [R9] `nif` has to be a unique number and has 9 digits.
- [ ] [R10] `phone` has to be a number with 9 digits and start with `9`, `2` or `3`.
- [ ] [R11] `Author` and `Reviewer` users have to register in the system. `Admin` users are registered by other `Admin` users.
- [ ] [R12] Every registraton request has to be notified to the `Admin` users inside the system.
- [ ] [R13] `Admin` users can accept or reject registration requests. Only accepted users can login to the system.
- [ ] [R14] If a registration request is rejected or was not yet responded, the user has to be notified when attempting to login.
- [ ] [R15] At any time, `Admin` users can activate or deactivate users.
- [ ] [R16] A user can request to delete its account. A notification has to be sent to the `Admin` users who can accept or reject the request. If the request is accepted, the user's personal data must be omitted from the system, although the records associated with the user must be kept.
- [ ] [R17] If there are no `Admin` users in the system, the system must request the registration of a new `Admin` user before allowing any other operation.
- [ ] [R18] After login, the system should display the message `Welcome, [name]!`.
- [ ] [R19] The system should display the message `Goodbye, [name]!` when the user logs out.