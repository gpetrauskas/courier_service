-- MySQL dump 10.13  Distrib 8.0.46, for Linux (x86_64)
--
-- Host: localhost    Database: courier_initial
-- ------------------------------------------------------
-- Server version	8.0.46-0ubuntu0.24.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `city` varchar(255) DEFAULT NULL,
  `flat_number` varchar(255) DEFAULT NULL,
  `house_number` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `post_code` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES (1,'Hobbiton','2','1','Frodo Baggins','61244120','91000','Bagshot Row',3),(2,'Springwood',NULL,'1428','Freddy Krueger','61248998','98002','Elm Street',3),(3,'Bywater',NULL,'7','Bilbo Baggins','67122091','91001','Hills Road',3);
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admins`
--

DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK5d8jm5tcx9lmf9w2615kg0yhp` FOREIGN KEY (`id`) REFERENCES `persons` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admins`
--

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
INSERT INTO `admins` VALUES (1);
/*!40000 ALTER TABLE `admins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ban_history`
--

DROP TABLE IF EXISTS `ban_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ban_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action_by` varchar(255) NOT NULL,
  `action_time` datetime(6) NOT NULL,
  `banned` bit(1) NOT NULL,
  `person_id` bigint NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ban_history`
--

LOCK TABLES `ban_history` WRITE;
/*!40000 ALTER TABLE `ban_history` DISABLE KEYS */;
INSERT INTO `ban_history` VALUES (1,'admin@example.com','2026-06-22 20:32:51.563350',_binary '\0',4,'');
/*!40000 ALTER TABLE `ban_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `couriers`
--

DROP TABLE IF EXISTS `couriers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `couriers` (
  `has_active_task` bit(1) NOT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKnkd81d28mgkf48s97t79pabsg` FOREIGN KEY (`id`) REFERENCES `persons` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `couriers`
--

LOCK TABLES `couriers` WRITE;
/*!40000 ALTER TABLE `couriers` DISABLE KEYS */;
INSERT INTO `couriers` VALUES (_binary '\0',2);
/*!40000 ALTER TABLE `couriers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_options`
--

DROP TABLE IF EXISTS `delivery_options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_options` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(50) NOT NULL,
  `disabled` bit(1) NOT NULL,
  `name` varchar(20) NOT NULL,
  `price` decimal(38,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_spvnv84ftfj8gujrg778tx9vj` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_options`
--

LOCK TABLES `delivery_options` WRITE;
/*!40000 ALTER TABLE `delivery_options` DISABLE KEYS */;
INSERT INTO `delivery_options` VALUES (1,'Maximum weight: 1kg',_binary '\0','light_weight',1.20),(2,'Maximum weight: 5kg',_binary '\0','medium_weight',2.00),(3,'Maximum weight: 15kg',_binary '\0','heavy_weight',4.00),(4,'Maximum dimensions: 30cm x 30cm x 30cm',_binary '\0','small_size',1.00),(5,'Maximum dimensions: 60cm x 60cm x 60cm',_binary '\0','medium_size',2.50),(6,'Maximum dimensions: 1m x 1m x 1m',_binary '\0','large_size',8.00),(7,'Standard delivery within 5-7 days',_binary '\0','standard',1.00),(8,'Express delivery within 1-3 days',_binary '\0','express',3.00),(9,'Overnight delivery on the next business day',_binary '\0','overnight',9.00);
/*!40000 ALTER TABLE `delivery_options` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_task_item_notes`
--

DROP TABLE IF EXISTS `delivery_task_item_notes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_task_item_notes` (
  `task_item_id` bigint NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  KEY `FKao43alfov5ihf7uf6xn2loxev` (`task_item_id`),
  CONSTRAINT `FKao43alfov5ihf7uf6xn2loxev` FOREIGN KEY (`task_item_id`) REFERENCES `delivery_task_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_task_item_notes`
--

LOCK TABLES `delivery_task_item_notes` WRITE;
/*!40000 ALTER TABLE `delivery_task_item_notes` DISABLE KEYS */;
/*!40000 ALTER TABLE `delivery_task_item_notes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_task_items`
--

DROP TABLE IF EXISTS `delivery_task_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_task_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contents` varchar(255) DEFAULT NULL,
  `delivery_method_name` varchar(255) DEFAULT NULL,
  `parcel_id` bigint NOT NULL,
  `parcel_status` enum('WAITING_FOR_PAYMENT','PICKING_UP','DELIVERING','PICKED_UP','DELIVERED','AT_CHECKPOINT','FAILED_PICKUP','FAILED_DELIVERY','CANCELED','REMOVED_FROM_THE_LIST','RETURNED_TO_CHECKPOINT','NOT_SHIPPED') NOT NULL,
  `recipient_address_id` bigint NOT NULL,
  `sender_address_id` bigint NOT NULL,
  `task_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjyppom4gvma4icuf3c97d7sa5` (`parcel_id`),
  KEY `FKmh53halhxn8duiwjar4huvuo3` (`recipient_address_id`),
  KEY `FKjgpmumcud3gl36ib6i8q9wjur` (`sender_address_id`),
  KEY `FKrqvnid1u9xe7e3aose64vksui` (`task_id`),
  CONSTRAINT `FKjgpmumcud3gl36ib6i8q9wjur` FOREIGN KEY (`sender_address_id`) REFERENCES `order_addresses` (`id`),
  CONSTRAINT `FKjyppom4gvma4icuf3c97d7sa5` FOREIGN KEY (`parcel_id`) REFERENCES `parcels` (`id`),
  CONSTRAINT `FKmh53halhxn8duiwjar4huvuo3` FOREIGN KEY (`recipient_address_id`) REFERENCES `order_addresses` (`id`),
  CONSTRAINT `FKrqvnid1u9xe7e3aose64vksui` FOREIGN KEY (`task_id`) REFERENCES `delivery_tasks` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_task_items`
--

LOCK TABLES `delivery_task_items` WRITE;
/*!40000 ALTER TABLE `delivery_task_items` DISABLE KEYS */;
INSERT INTO `delivery_task_items` VALUES (1,'some stuff','express',2,'PICKED_UP',3,4,1),(2,'door code: 123123','standard',3,'FAILED_PICKUP',5,6,1);
/*!40000 ALTER TABLE `delivery_task_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_tasks`
--

DROP TABLE IF EXISTS `delivery_tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_tasks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `canceled_by_admin_id` bigint DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `courier_id` bigint DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `admin_id` bigint NOT NULL,
  `delivery_status` enum('IN_PROGRESS','COMPLETED','ASSIGNED','CANCELED','RETURNING_TO_STATION','AT_CHECKPOINT') NOT NULL,
  `task_type` enum('DELIVERY','PICKUP') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhwv1hgbkbc2v7k2u0fapt227x` (`courier_id`),
  CONSTRAINT `FKhwv1hgbkbc2v7k2u0fapt227x` FOREIGN KEY (`courier_id`) REFERENCES `couriers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_tasks`
--

LOCK TABLES `delivery_tasks` WRITE;
/*!40000 ALTER TABLE `delivery_tasks` DISABLE KEYS */;
INSERT INTO `delivery_tasks` VALUES (1,NULL,'2026-06-22 20:15:55.864748',2,'2026-06-22 20:14:21.384875',1,'COMPLETED','PICKUP');
/*!40000 ALTER TABLE `delivery_tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `message` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'2026-06-22 20:14:21.441479','Task was assigned to you, please be ready to leave asap','Task was assigned'),(2,'2026-06-22 20:15:04.041142','Courier checked in: Task ID: 1, Courier ID: 2','Courier 2 checked in.'),(3,'2026-06-22 20:22:47.490809','notification example for all users...','notification one'),(4,'2026-06-22 20:24:10.076407','notification example..','notification two'),(5,'2026-06-22 20:25:51.537142','example','notification '),(6,'2026-06-22 20:26:26.816648','example here...','example'),(7,'2026-06-22 20:26:34.666715','example here...','example');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_addresses`
--

DROP TABLE IF EXISTS `order_addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `city` varchar(255) DEFAULT NULL,
  `flat_number` varchar(255) DEFAULT NULL,
  `house_number` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `post_code` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_addresses`
--

LOCK TABLES `order_addresses` WRITE;
/*!40000 ALTER TABLE `order_addresses` DISABLE KEYS */;
INSERT INTO `order_addresses` VALUES (1,'Springwood',NULL,'1428','Freddy Krueger','61248998','98002','Elm Street'),(2,'Hobbiton','2','1','Frodo Baggins','61244120','91000','Bagshot Row'),(3,'Bywater',NULL,'7','Bilbo Baggins','67122091','91001','Hills Road'),(4,'Springwood',NULL,'1428','Freddy Krueger','61248998','98002','Elm Street'),(5,'Hobbiton','2','1','Frodo Baggins','61244120','91000','Bagshot Row'),(6,'Springwood',NULL,'1428','Freddy Krueger','61248998','98002','Elm Street'),(7,'Springwood',NULL,'1428','Freddy Krueger','61248998','98002','Elm Street'),(8,'Bywater',NULL,'7','Bilbo Baggins','67122091','91001','Hills Road'),(9,'Bywater',NULL,'7','Bilbo Baggins','67122091','91001','Hills Road'),(10,'Springwood',NULL,'1428','Freddy Krueger','61248998','98002','Elm Street'),(11,'Springwood',NULL,'1428','Freddy Krueger','61248998','98002','Elm Street'),(12,'Bywater',NULL,'7','Bilbo Baggins','67122091','91001','Hills Road');
/*!40000 ALTER TABLE `order_addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_date` datetime(6) NOT NULL,
  `delivery_method_description` varchar(255) DEFAULT NULL,
  `delivery_method_id` bigint DEFAULT NULL,
  `delivery_method_name` varchar(255) DEFAULT NULL,
  `delivery_method_price` decimal(38,2) DEFAULT NULL,
  `status` enum('PENDING','CONFIRMED','CANCELED','COMPLETED') NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `parcel_id` bigint DEFAULT NULL,
  `recipient_address_id` bigint NOT NULL,
  `sender_address_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hxq7clctth3c2e2n0li9lcm1x` (`recipient_address_id`),
  UNIQUE KEY `UK_hq74nytrktcjrc96nus43mwwh` (`sender_address_id`),
  UNIQUE KEY `UK_nny58wl5a264wrkylpoal5wb9` (`parcel_id`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK4dfm3fk1stahwbexbutgq1tw7` FOREIGN KEY (`sender_address_id`) REFERENCES `order_addresses` (`id`),
  CONSTRAINT `FK4swmt2fwcm3wm6j8ka86xfcao` FOREIGN KEY (`recipient_address_id`) REFERENCES `order_addresses` (`id`),
  CONSTRAINT `FK5ywn8erm35d7rufu6dwf8dumu` FOREIGN KEY (`parcel_id`) REFERENCES `parcels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'2026-06-22 20:01:43.570933','Standard delivery within 5-7 days',7,'standard',1.00,'CONFIRMED',3,1,1,2),(2,'2026-06-22 20:02:54.461028','Express delivery within 1-3 days',8,'express',3.00,'CONFIRMED',3,2,3,4),(3,'2026-06-22 20:04:08.659321','Standard delivery within 5-7 days',7,'standard',1.00,'CONFIRMED',3,3,5,6),(4,'2026-06-22 20:04:33.348027','Express delivery within 1-3 days',8,'express',3.00,'PENDING',3,4,7,8),(5,'2026-06-22 20:05:06.593419','Overnight delivery on the next business day',9,'overnight',9.00,'CONFIRMED',3,5,9,10),(6,'2026-06-22 20:10:17.675729','Standard delivery within 5-7 days',7,'standard',1.00,'PENDING',3,6,11,12);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parcels`
--

DROP TABLE IF EXISTS `parcels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parcels` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assigned` bit(1) NOT NULL,
  `contents` varchar(255) NOT NULL,
  `dimensions_id` bigint DEFAULT NULL,
  `dimensions_name` varchar(255) DEFAULT NULL,
  `dimensions_price` decimal(38,2) DEFAULT NULL,
  `failures_count` int DEFAULT NULL,
  `status` enum('WAITING_FOR_PAYMENT','PICKING_UP','DELIVERING','PICKED_UP','DELIVERED','AT_CHECKPOINT','FAILED_PICKUP','FAILED_DELIVERY','CANCELED','REMOVED_FROM_THE_LIST','RETURNED_TO_CHECKPOINT','NOT_SHIPPED') DEFAULT NULL,
  `tracking_number` varchar(255) NOT NULL,
  `weight_id` bigint DEFAULT NULL,
  `weight_name` varchar(255) DEFAULT NULL,
  `weight_price` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parcels`
--

LOCK TABLES `parcels` WRITE;
/*!40000 ALTER TABLE `parcels` DISABLE KEYS */;
INSERT INTO `parcels` VALUES (1,_binary '\0','few books',4,'small_size',1.00,0,'PICKING_UP','618debdf-a808-4c91-bb8b-17b396159f16',2,'medium_weight',2.00),(2,_binary '\0','some stuff',5,'medium_size',2.50,0,'PICKED_UP','ad0b59e0-be9a-4d05-ac34-f6508cfb49e7',2,'medium_weight',2.00),(3,_binary '\0','door code: 123123',4,'small_size',1.00,1,'PICKING_UP','723b10c4-e8b3-42fb-a43c-a184dc165536',2,'medium_weight',2.00),(4,_binary '\0','',6,'large_size',8.00,0,'WAITING_FOR_PAYMENT','c29caa6d-a2b2-49a8-9762-7c2fc4130d03',1,'light_weight',1.20),(5,_binary '\0','',4,'small_size',1.00,0,'PICKING_UP','701b477e-00e9-470f-8be3-418667abceb4',3,'heavy_weight',4.00),(6,_binary '\0','',5,'medium_size',2.50,0,'WAITING_FOR_PAYMENT','ff986451-fc07-4ebd-8c3c-ad5c5033c484',1,'light_weight',1.20);
/*!40000 ALTER TABLE `parcels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_attempts`
--

DROP TABLE IF EXISTS `payment_attempts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_attempts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `failure_reason` varchar(255) DEFAULT NULL,
  `provider_type` enum('CREDIT_CARD','PAYPAL','UNKNOWN') DEFAULT NULL,
  `status` enum('PENDING','FAILED','SUCCESS') NOT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `payment_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa3a9tqynt8vtwcmq9q8igujra` (`payment_id`),
  CONSTRAINT `FKa3a9tqynt8vtwcmq9q8igujra` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_attempts`
--

LOCK TABLES `payment_attempts` WRITE;
/*!40000 ALTER TABLE `payment_attempts` DISABLE KEYS */;
INSERT INTO `payment_attempts` VALUES (1,'2026-06-22 20:05:39','Credit card has insufficient funds','CREDIT_CARD','FAILED','',5),(2,'2026-06-22 20:06:05',NULL,'CREDIT_CARD','SUCCESS','cc_tx_a630ee22-5667-4628-a7c7-b607d8af0249',5),(3,'2026-06-22 20:07:08','Invalid credit card CVC','CREDIT_CARD','FAILED','',3),(4,'2026-06-22 20:07:13',NULL,'CREDIT_CARD','SUCCESS','cc_tx_838cca1d-e5ad-4f05-a40b-781598b3e544',3),(5,'2026-06-22 20:10:42',NULL,'CREDIT_CARD','SUCCESS','cc_tx_aef33c46-89f4-415b-8dbe-916dc9f48f50',2),(6,'2026-06-22 20:27:35',NULL,'CREDIT_CARD','SUCCESS','cc_tx_e0c2728b-7e73-45a0-8472-da88d213f55a',1);
/*!40000 ALTER TABLE `payment_attempts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_method`
--

DROP TABLE IF EXISTS `payment_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_method` (
  `payment_type` varchar(31) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `saved` bit(1) NOT NULL,
  `token` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `card_holder_name` varchar(255) NOT NULL,
  `expiry_date` varchar(255) NOT NULL,
  `last4` varchar(255) NOT NULL,
  `pp_email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkeh6a1awkqba7l425dq4bi0l3` (`user_id`),
  CONSTRAINT `FKkeh6a1awkqba7l425dq4bi0l3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_method`
--

LOCK TABLES `payment_method` WRITE;
/*!40000 ALTER TABLE `payment_method` DISABLE KEYS */;
INSERT INTO `payment_method` VALUES ('CREDIT_CARD',1,_binary '','tok_0c8078b9-ef06-4d6d-a2dc-af7b3843752d',3,'User Example','12/27','5432',NULL),('CREDIT_CARD',2,_binary '','tok_9e1196fc-7124-4a6f-b865-cd4b979163ba',3,'User Example','11/22','3211',NULL);
/*!40000 ALTER TABLE `payment_method` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) NOT NULL,
  `order_id` bigint NOT NULL,
  `status` enum('NOT_PAID','FAILED','PAID','CANCELED') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,4.00,1,'PAID'),(2,7.50,2,'PAID'),(3,4.00,3,'PAID'),(4,12.20,4,'NOT_PAID'),(5,14.00,5,'PAID'),(6,4.70,6,'NOT_PAID');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_notifications`
--

DROP TABLE IF EXISTS `person_notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `person_notifications` (
  `person_id` bigint NOT NULL,
  `notification_id` bigint NOT NULL,
  `is_read` bit(1) NOT NULL,
  `read_at` datetime(6) DEFAULT NULL,
  `received_at` datetime(6) NOT NULL,
  PRIMARY KEY (`person_id`,`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_notifications`
--

LOCK TABLES `person_notifications` WRITE;
/*!40000 ALTER TABLE `person_notifications` DISABLE KEYS */;
INSERT INTO `person_notifications` VALUES (1,2,_binary '\0',NULL,'2026-06-22 20:15:04.047427'),(2,1,_binary '','2026-06-22 20:15:16.945888','2026-06-22 20:14:21.456690'),(2,4,_binary '\0',NULL,'2026-06-22 20:24:10.080324'),(2,7,_binary '\0',NULL,'2026-06-22 20:26:34.678577'),(3,3,_binary '\0',NULL,'2026-06-22 20:22:47.496008'),(3,5,_binary '\0',NULL,'2026-06-22 20:25:51.541866'),(3,6,_binary '\0',NULL,'2026-06-22 20:26:26.825332');
/*!40000 ALTER TABLE `person_notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `persons`
--

DROP TABLE IF EXISTS `persons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `persons` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `blocked` bit(1) NOT NULL DEFAULT b'0',
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `deleted_date` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_1x5aosta48fbss4d5b3kuu0rd` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `persons`
--

LOCK TABLES `persons` WRITE;
/*!40000 ALTER TABLE `persons` DISABLE KEYS */;
INSERT INTO `persons` VALUES (1,_binary '\0',_binary '\0',NULL,'admin@example.com','Administrator X','$2a$10$AU3lKjdh2ojJhsYEFNnEAO3d0X36fwDZjb3g3coTBVigKOksrOWfq',NULL),(2,_binary '\0',_binary '\0',NULL,'courier@example.com','Courier X','$2a$10$x0PPW8PNjFAOnufc3dvVxuiUvcvLuUH5KdHqxcJhE.z0C5ActI./S',NULL),(3,_binary '\0',_binary '\0',NULL,'user@example.com','User Example','$2a$10$kzfk.xnaEQK8jMAd4ZU6wug0t5pKTuAsRtaeFAU7o2TTd0jXO8bD6',NULL),(4,_binary '',_binary '\0',NULL,'usery@example.com','User Y','$2a$10$C06JMZ.DwhdUfhs36jiUl.fGkS/Fg4QViVDU7Q9EqQRO2IDDVXaHq',NULL);
/*!40000 ALTER TABLE `persons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket_comments`
--

DROP TABLE IF EXISTS `ticket_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket_comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `author_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `ticket_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKji1sg1bs3ey11hi4a9rx6mh9m` (`author_id`),
  KEY `FKdoce3fj1osdn71h25dhfs160v` (`ticket_id`),
  CONSTRAINT `FKdoce3fj1osdn71h25dhfs160v` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`id`),
  CONSTRAINT `FKji1sg1bs3ey11hi4a9rx6mh9m` FOREIGN KEY (`author_id`) REFERENCES `persons` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket_comments`
--

LOCK TABLES `ticket_comments` WRITE;
/*!40000 ALTER TABLE `ticket_comments` DISABLE KEYS */;
INSERT INTO `ticket_comments` VALUES (1,3,'2026-06-22 20:12:20.659838','hi',1),(2,1,'2026-06-22 20:12:44.559524','hey',1);
/*!40000 ALTER TABLE `ticket_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS `tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assigned_to` bigint DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by_id` bigint DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `priority` enum('LOW','NORMAL','HIGH','URGENT') DEFAULT NULL,
  `status` enum('OPEN','IN_PROGRESS','RESOLVED','CLOSED') DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb77x2m4adaapb9q0gshkbc7g6` (`created_by_id`),
  CONSTRAINT `FKb77x2m4adaapb9q0gshkbc7g6` FOREIGN KEY (`created_by_id`) REFERENCES `persons` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tickets`
--

LOCK TABLES `tickets` WRITE;
/*!40000 ALTER TABLE `tickets` DISABLE KEYS */;
INSERT INTO `tickets` VALUES (1,NULL,NULL,'2026-06-22 20:12:03.276041',3,'no real issue just an example description','LOW','OPEN','Sample support ticket','2026-06-22 20:12:44.559547');
/*!40000 ALTER TABLE `tickets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `default_address_id` bigint DEFAULT NULL,
  `subscribed` bit(1) DEFAULT b'0',
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK5uc5tjg1ntoe9gtweyu57c9rv` FOREIGN KEY (`id`) REFERENCES `persons` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (NULL,_binary '\0',3),(NULL,_binary '\0',4);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-22 22:51:05
