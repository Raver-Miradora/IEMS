-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 26, 2025 at 03:46 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `test19`
--

-- --------------------------------------------------------

--
-- Table structure for table `position`
--

CREATE TABLE `position` (
  `position_id` int(11) NOT NULL,
  `position_name` varchar(155) NOT NULL,
  `position_desc` varchar(255) NOT NULL,
  `department_id` int(11) NOT NULL,
  `position_pay` float DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `position`
--

INSERT INTO `position` (`position_id`, `position_name`, `position_desc`, `department_id`, `position_pay`, `status`) VALUES
(1, 'Administrative Assistant I', 'Human Resource Management Assistant', 1, NULL, 1),
(2, 'Administrative Assistant II', 'Human Reource Management Assistant', 1, NULL, 1),
(3, 'Administrative Assistant III', 'Human Reource Management Assistant', 1, NULL, 1),
(4, 'Administrative Assistant IV', 'Human Reource Management Assistant', 1, NULL, 1),
(5, 'Administrative Assistant V', 'Human Reource Management Assistant', 1, NULL, 1),
(6, 'Administrative Aide I', 'Human Resource Management Aide', 1, NULL, 1),
(7, 'Administrative Aide II', 'Human Resource Management Aide', 1, NULL, 1),
(8, 'Administrative Aide III', 'Human Resource Management Aide', 1, NULL, 1),
(9, 'Administrative Aide IV', 'Human Resource Management Aide', 1, NULL, 1),
(10, 'Administrative Officer I', 'Human Resource Management Officer', 1, NULL, 1),
(11, 'Administrative Officer II', 'Human Resource Management Officer', 1, NULL, 1),
(12, 'Administrative Officer III', 'Human Resource Management Officer', 1, NULL, 1),
(13, 'Administrative Officer IV', 'Human Resource Management Officer', 1, NULL, 1),
(14, 'Administrative Officer V', 'Human Resource Management Officer', 1, NULL, 1),
(15, 'Administrative Aide I', 'Accounting Clerk', 2, NULL, 1),
(16, 'Administrative Aide II', 'Accounting Clerk', 2, NULL, 1),
(17, 'Administrative Aide III', 'Accounting Clerk', 2, NULL, 1),
(18, 'Administrative Aide IV', 'Accounting Clerk', 2, NULL, 1),
(19, 'Administrative Aide I', 'Budgeting Aide', 3, NULL, 1),
(20, 'Administrative Aide II', 'Budgeting Aide', 3, NULL, 1),
(21, 'Administrative Aide III', 'Budgeting Aide', 3, NULL, 1),
(22, 'Administrative Aide IV', 'Budgeting Aide', 3, NULL, 1),
(23, 'Administrative Assistant I', 'Budgeting Assistant', 3, NULL, 1),
(24, 'Administrative Assistant II', 'Budgeting Assistant', 3, NULL, 1),
(25, 'Administrative Assistant III', 'Budgeting Assistant', 3, NULL, 1),
(26, 'Administrative Assistant IV', 'Budgeting Assistant', 3, NULL, 1),
(27, 'Administrative Assistant V', 'Budgeting Assistant', 3, NULL, 1),
(28, 'Administrative Aide IV', 'Cash Clerk I', 4, NULL, 1),
(29, 'Administrative Aide V', 'Cash Clerk II', 4, NULL, 1),
(30, 'Administrative Assistant II', 'Cash Clerk III', 4, NULL, 1),
(31, 'Administrative Officer I', 'Cashier I', 4, NULL, 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `position`
--
ALTER TABLE `position`
  ADD PRIMARY KEY (`position_id`),
  ADD KEY `departmentID` (`department_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `position`
--
ALTER TABLE `position`
  MODIFY `position_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `position`
--
ALTER TABLE `position`
  ADD CONSTRAINT `position_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
