-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 26, 2025 at 03:47 AM
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
-- Table structure for table `work_experience`
--

CREATE TABLE `work_experience` (
  `experience_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `position_title` varchar(255) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `monthly_salary` decimal(10,2) DEFAULT NULL,
  `salary_grade` varchar(20) DEFAULT NULL,
  `appointment_status` varchar(50) DEFAULT NULL,
  `is_government_service` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `work_experience`
--

INSERT INTO `work_experience` (`experience_id`, `user_id`, `start_date`, `end_date`, `position_title`, `company`, `monthly_salary`, `salary_grade`, `appointment_status`, `is_government_service`) VALUES
(97, 26, '2004-10-05', '2005-10-05', 'Manager', 'Krusty Krabb', 100000.00, '12', 'Permanent', 0),
(98, 26, '2004-10-05', '2005-10-05', 'Manager', 'Krusty Krabb', 100000.00, '12', 'Permanent', 0),
(99, 26, '2004-10-05', '2005-10-05', 'Manager', 'Krusty Krabb', 100000.00, '12', 'Permanent', 0),
(100, 26, '2004-10-05', '2005-10-05', 'Manager', 'Krusty Krabb', 100000.00, '12', 'Permanent', 0),
(101, 26, '2004-10-05', '2005-10-05', 'Manager', 'Krusty Krabb', 100000.00, '12', 'Permanent', 0),
(102, 26, '2004-10-05', '2005-10-05', 'Manager', 'Krusty Krabb', 100000.00, '12', 'Permanent', 0),
(112, 33, '2021-10-04', '2023-10-23', 'Hindi Masabi', 'Hindi Makita', 10000.00, '6', 'Casual', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `work_experience`
--
ALTER TABLE `work_experience`
  ADD PRIMARY KEY (`experience_id`),
  ADD KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `work_experience`
--
ALTER TABLE `work_experience`
  MODIFY `experience_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=113;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `work_experience`
--
ALTER TABLE `work_experience`
  ADD CONSTRAINT `work_experience_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
