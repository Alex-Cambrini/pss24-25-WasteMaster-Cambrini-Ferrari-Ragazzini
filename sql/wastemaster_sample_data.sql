

-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: wastemaster_db
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (2,'$2a$10$fYTiFZv8sEopMsEgi/u2R.YoG3mrTTurEYf6Q3z/C3NFbepAU.oGK',2),(3,'$2a$10$PzbV.Uzp9HiezsnJlRLT5ukpx1epp3tt8pwBuIPoZe.DGmV.gXICi',3),(4,'$2a$10$8mYqforW64CXiykz1MbOgeVPydC8RhwP0eKZoE/K0pp9q2N6/rxzG',4),(5,'$2a$10$vXjRt2Dy7kUAdJkHX.7k2.KnxsTsRa89o2b/6tUzHckwQXjRMZn4q',5),(6,'$2a$10$iCoWHOwLwX0ZbQ2STsP9HOJLgxlgayO1R0qEh50GQQrX/l0P9Mtey',6),(7,'$2a$10$idYukiwj09BfYcuiAEwEE.4OJCLfu3SGeapsQc7fSp3WnW/Of/p5a',7),(8,'$2a$10$4qyk4maQYYZbyd8WpfqccOCgRSlcA.aJF9WiwsaQdAxXa03Gi9dMy',8),(9,'$2a$10$a7PPbOGK9trO349tG2o4ZO/kDF390EDPdAxA2NslH5VMro4GkD3jq',9),(10,'$2a$10$CMZkgTHQsWEMBpeYkEBkGeJCoiMG9E10FGhvN5QrhDbbriokEdoPO',10),(11,'$2a$10$Xw0IYvgpR7brMRYfyd/6Bea84hUDafOzZYf0Et9.bEZIHa104N57a',11),(12,'$2a$10$ngRlDd0WujEpz31C08mxKOyhtOfcFBIc7YnfXzqAsGmKPMRg0bAHe',12),(13,'$2a$10$CoTmO0iIT55DNHH8ZT0RkuwjURJ98HvbPkbBpC3d.8oVvOVoG43Ze',13),(14,'$2a$10$l.ZBA2uvXdz6hiS64KiZzOHa0.lCrZdU8QbkfwpRcEJ7oytYP4Re.',14),(15,'$2a$10$qKc/ggVhos80S2e/PARN9.TWsqW2xZGVahmOAZUP1K5qtesLEpaba',15),(16,'$2a$10$Rb/HQXwv8ygl./tv2iQi9eDFo8W/hETS9yfQO.eAnjixIUe/7lEkq',16),(17,'$2a$10$zVUBjyOdMPtsQ4KQ0jyr/eGzXu/j20Pt2LrKGpPmz9ZGun1Fk676u',17);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `collections`
--

LOCK TABLES `collections` WRITE;
/*!40000 ALTER TABLE `collections` DISABLE KEYS */;
/*!40000 ALTER TABLE `collections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES
(1,'2025-08-05 09:00:00','lorenzo.ferrari@yahoo.com',_binary '\0','Lorenzo','3332121213','Ferrari',2),
(2,'2025-08-15 10:30:00','alex.cambrini@yahoo.com',_binary '\0','Alex','3335454645','Cambrini',3),
(3,'2025-09-01 11:45:00','manuel@ragazzini.com',_binary '\0','Manuel','3337898784','Ragazzini',4);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES
(2,'2025-08-03 10:15:00','mario.rossi@yahoo.com',_binary '\0','Mario','3336545876','Rossi','B','OFFICE_WORKER',5),
(3,'2025-08-05 11:30:00','luca.rossi@yahoo.com',_binary '\0','Luca','3331234567','Rossi','C','OFFICE_WORKER',6),
(4,'2025-08-07 09:45:00','giulia.bianchi@yahoo.com',_binary '\0','Giulia','3499876543','Bianchi','B','OFFICE_WORKER',7),
(5,'2025-08-10 14:20:00','marco.esposito@yahoo.com',_binary '\0','Marco','3201112233','Esposito','C1','OPERATOR',8),
(6,'2025-08-12 08:50:00','elena.conti@yahoo.com',_binary '\0','Elena','3285556677','Conti','B','ADMINISTRATOR',9),
(7,'2025-08-15 13:10:00','fabio.ricci@yahoo.com',_binary '\0','Fabio','3314443322','Ricci','C1','OPERATOR',10),
(8,'2025-08-17 09:35:00','sara.verdi@yahoo.com',_binary '\0','Sara','3331112233','Verdi','B','OFFICE_WORKER',11),
(9,'2025-08-20 11:00:00','matteo.galli@yahoo.com',_binary '\0','Matteo','3492223344','Galli','C','OPERATOR',12),
(10,'2025-08-22 15:25:00','alice.marino@yahoo.com',_binary '\0','Alice','3203334455','Marino','B','OFFICE_WORKER',13),
(11,'2025-08-25 10:40:00','stefano.fontana@yahoo.com',_binary '\0','Stefano','3284445566','Fontana','C1','OPERATOR',14),
(12,'2025-08-27 14:55:00','francesca.rinaldi@yahoo.com',_binary '\0','Francesca','3315556677','Rinaldi','C1','ADMINISTRATOR',15),
(13,'2025-09-01 09:20:00','davide.costa@yahoo.com',_binary '\0','Davide','3356667788','Costa','B','OPERATOR',16),
(14,'2025-09-03 12:30:00','martina.riccardi@yahoo.com',_binary '\0','Martina','3377778899','Riccardi','B','ADMINISTRATOR',17),
(15,'2025-09-05 08:50:00','andrea.moretti@yahoo.com',_binary '\0','Andrea','3398889900','Moretti','C','OPERATOR',18),
(16,'2025-09-07 11:15:00','claudia.valentini@yahoo.com',_binary '\0','Claudia','3309990011','Valentini','C','OPERATOR',19),
(17,'2025-09-10 10:05:00','lorenzo.bellini@yahoo.com',_binary '\0','Lorenzo','3321112233','Bellini','B','OPERATOR',20);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Dumping data for table `invoices`
--

LOCK TABLES `invoices` WRITE;
/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `location`
--

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
INSERT INTO `location` VALUES (1,'Default','0','12345','Default'),(2,'Cesena','37','48100','elettricità'),(3,'Cesena','12','48100','acqua'),(4,'Cesena','25','48101','farini'),(5,'Ravenna','25','48200','via delre'),(6,'Forli','12','48202','Via Milano'),(7,'Rimini','45','48203','Corso Venezia'),(8,'Rimini','7','48203','Piazza Garibaldi'),(9,'Ravenna','88','48200','Via Torino'),(10,'Ravenna','101','48200','Viale Roma'),(11,'Ravenna','23','48200','Via Firenze'),(12,'Cesena','56','48201','Corso Milano'),(13,'Forlì','9','48202','Piazza Dante'),(14,'Rimini','78','48203','Viale Trento'),(15,'Ravenna','12','48200','Via Garibaldi'),(16,'Cesena','34','48201','Corso Umberto'),(17,'Forlì','7','48202','Piazza XX Settembre'),(18,'Rimini','65','48203','Via Roma'),(19,'Ravenna','18','48200','Viale Stazione'),(20,'Cesena','48','48201','Corso Matteotti');
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `schedule`
--

LOCK TABLES `schedule` WRITE;
/*!40000 ALTER TABLE `schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `trip`
--

LOCK TABLES `trip` WRITE;
/*!40000 ALTER TABLE `trip` DISABLE KEYS */;
/*!40000 ALTER TABLE `trip` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `trip_operators`
--

LOCK TABLES `trip_operators` WRITE;
/*!40000 ALTER TABLE `trip_operators` DISABLE KEYS */;
/*!40000 ALTER TABLE `trip_operators` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `vehicle`
--

LOCK TABLES `vehicle` WRITE;
/*!40000 ALTER TABLE `vehicle` DISABLE KEYS */;
INSERT INTO `vehicle` VALUES (1,'Fiat','2025-09-25','Ducato','2026-09-25','AB123CD',2020,'B',2,'IN_SERVICE'),(2,'Renault','2025-09-25','Master','2026-09-25','MN123OP',2021,'B',2,'IN_SERVICE'),(3,'Iveco','2025-09-25','EuroCargo','2026-09-25','QR456ST',2020,'C1',3,'IN_SERVICE'),(4,'Mercedes','2025-09-25','Actros','2026-09-25','UV789WX',2019,'C',4,'IN_SERVICE'),(5,'Ford','2025-09-25','Transit','2026-09-25','YZ234AB',2022,'B',2,'IN_SERVICE'),(6,'Volkswagen','2025-09-25','Crafter','2026-09-25','CD567EF',2021,'C1',3,'IN_SERVICE'),(7,'MAN','2025-09-25','TGM','2026-09-25','GH890IJ',2020,'C',4,'IN_SERVICE'),(8,'Fiat','2025-09-25','Ducato','2026-09-25','KL123MN',2022,'B',2,'IN_SERVICE'),(9,'Iveco','2025-09-25','Daily','2026-09-25','OP456QR',2021,'C1',3,'IN_SERVICE'),(10,'Mercedes','2025-09-25','Sprinter','2026-09-25','ST789UV',2020,'C',4,'IN_SERVICE');
/*!40000 ALTER TABLE `vehicle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `waste`
--

LOCK TABLES `waste` WRITE;
/*!40000 ALTER TABLE `waste` DISABLE KEYS */;
INSERT INTO `waste` VALUES (1,_binary '\0',_binary '\0',_binary '','Plastica'),(2,_binary '\0',_binary '\0',_binary '','Carta'),(3,_binary '\0',_binary '\0',_binary '','Vetro'),(4,_binary '\0',_binary '',_binary '\0','Medicinali');
/*!40000 ALTER TABLE `waste` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `wasteschedule`
--

LOCK TABLES `wasteschedule` WRITE;
/*!40000 ALTER TABLE `wasteschedule` DISABLE KEYS */;
INSERT INTO `wasteschedule` VALUES (1,'MONDAY',4),(2,'TUESDAY',3),(3,'FRIDAY',2),(4,'THURSDAY',1);
/*!40000 ALTER TABLE `wasteschedule` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
