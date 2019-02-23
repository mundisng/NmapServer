CREATE DATABASE  IF NOT EXISTS `mydb` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `mydb`;
-- MySQL dump 10.13  Distrib 5.5.46, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: mydb
-- ------------------------------------------------------
-- Server version	5.5.46-0ubuntu0.14.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `SAgents`
--

DROP TABLE IF EXISTS `SAgents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SAgents` (
  `hash` int(11) NOT NULL,
  `device_name` varchar(45) DEFAULT NULL,
  `interface_ip` varchar(45) DEFAULT NULL,
  `interface_macaddr` varchar(45) DEFAULT NULL,
  `os_version` varchar(45) DEFAULT NULL,
  `nmap_version` varchar(45) DEFAULT NULL,
  `active` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SAgents`
--

LOCK TABLES `SAgents` WRITE;
/*!40000 ALTER TABLE `SAgents` DISABLE KEYS */;
/*!40000 ALTER TABLE `SAgents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nmapjobs`
--

DROP TABLE IF EXISTS `nmapjobs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nmapjobs` (
  `SAgents_hash` int(11) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`,`SAgents_hash`),
  KEY `fk_nmapjobs_SAgents_idx` (`SAgents_hash`),
  CONSTRAINT `fk_nmapjobs_SAgents` FOREIGN KEY (`SAgents_hash`) REFERENCES `SAgents` (`hash`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nmapjobs`
--

LOCK TABLES `nmapjobs` WRITE;
/*!40000 ALTER TABLE `nmapjobs` DISABLE KEYS */;
/*!40000 ALTER TABLE `nmapjobs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nmapresults`
--

DROP TABLE IF EXISTS `nmapresults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nmapresults` (
  `nmapjobs_SAgents_hash` int(11) NOT NULL,
  `nmapjobs_id` int(11) NOT NULL,
  `time` varchar(255) NOT NULL,
  `hostname` varchar(255) DEFAULT NULL,
  `tasks` varchar(3000) DEFAULT NULL,
  `results` varchar(15000) DEFAULT NULL,
  `periodic` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`time`,`nmapjobs_id`,`nmapjobs_SAgents_hash`),
  KEY `fk_nmapresults_nmapjobs_idx` (`nmapjobs_id`,`nmapjobs_SAgents_hash`),
  CONSTRAINT `fk_nmapresults_nmapjobs` FOREIGN KEY (`nmapjobs_id`, `nmapjobs_SAgents_hash`) REFERENCES `nmapjobs` (`id`, `SAgents_hash`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nmapresults`
--

LOCK TABLES `nmapresults` WRITE;
/*!40000 ALTER TABLE `nmapresults` DISABLE KEYS */;
/*!40000 ALTER TABLE `nmapresults` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `active` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`username`,`password`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('anap','2bcf588fe876fe533044dac0299cbae1d7d1124f614793507701917933fc036e',0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Table structure for table `androiduser`
--
DROP TABLE IF EXISTS `androiduser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `androiduser` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `androiduser` WRITE;
/*!40000 ALTER TABLE `androiduser` DISABLE KEYS */;
/*!40000 ALTER TABLE `androiduser` ENABLE KEYS */;
UNLOCK TABLES;


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-12-30 22:07:23
