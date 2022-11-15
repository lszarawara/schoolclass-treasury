INSERT INTO `user` VALUES
    (1000,'lukasz.szarawara@gmail.com','Łukasz','Szarawara','admin','$2a$10$ieW19wwyp4CJXTSuqX2U8evHnvV0gP5UzLqWSN6V0TEEV1HD.jB3u','ROLE_ADMIN','Y'),
    (1001,'drugi@wp.pl','Drugi','Skarbnik','drugi','$2a$10$fNk3gusLtiMND7adPkRV0O2lXCDCl.r1qLSGhmhU/VXh2crTo9iMy','ROLE_SUPERUSER','Y'),
    (1002,'trzeci@wp.pl','Trzeci','Trzeci','Trzeci','$2a$10$DmWhvPg7AJB512iAhdulBuqSvTsKzr7ZQz7LN.W3mwOUM8T83jN16','ROLE_USER','Y'),
    (1003,'czwarty@wp.pl','Czwarty','Czwarty','Czwarty','$2a$10$UQtMFtW97Zm.WJul4WlBNOpi4KoQ3z9PqGCtsQ0O3gXWdzvs1LUl6','ROLE_SUPERUSER','Y'),
    (1004,'jan.piaty@gmail.com','Jan','Piaty','piaty','$2a$10$6iNjHFtNkigULFRDJlJCS.9Q.iPpL.bgvdqKJfJ6n9j.lqsPe3Ha2','ROLE_USER','N');

INSERT INTO `school_class` VALUES
    (1,'SP261 1a', 1002),
    (2,'SP261 1b', 1002);

INSERT INTO `child` VALUES
    (1,'techniczne','Y','konto zaokrągleń',1,'Y'),
    (2,'techniczne','Y','konto zaokrągleń',2,'Y'),
    (3,'Adam','N','Abackij',1,'Y'),
    (4,'Benek','N','Barnaba',1,'Y'),
    (5,'Celina','N','Cośka',1,'Y'),
    (6,'Daniel','N','Darko',1,'N'),
    (7,'Edek','N','Enotko',2,'N');

INSERT INTO `transaction` VALUES
    (1,100.00,'2022-11-01','Składka',NULL,'DUE',3),
    (2,100.00,'2022-11-01','Składka',NULL,'DUE',4),
    (3,100.00,'2022-11-01','Składka',NULL,'DUE',5),
    (4,100.00,'2022-11-01','Składka',NULL,'DUE',6),
    (5,100.00,'2022-11-02','Wpłata',NULL,'PAYMENT',3),
    (6,100.00,'2022-11-02','Wpłata',NULL,'PAYMENT',4),
    (7,100.00,'2022-11-02','Wpłata',NULL,'PAYMENT',5),
    (8,0.01,'2022-11-02','Kwiaty',NULL,'DEBIT',1),
    (9,20.08,'2022-11-02','Kwiaty',NULL,'DEBIT',3),
    (10,20.08,'2022-11-02','Kwiaty',NULL,'DEBIT',4),
    (11,20.08,'2022-11-02','Kwiaty',NULL,'DEBIT',5),
    (12,20.08,'2022-11-02','Kwiaty',NULL,'DEBIT',6),
    (13,30.00,'2022-11-03','Wycieczka','Y','DEBIT',3),
    (14,30.00,'2022-11-03','Wycieczka','Y','DEBIT',4),
    (15,30.00,'2022-11-03','Wycieczka','Y','DEBIT',6);

INSERT INTO `user_child` VALUES
    (1001,3),
    (1001,6),
    (1002,4),
    (1003,6);