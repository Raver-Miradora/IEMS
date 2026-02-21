-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 26, 2025 at 03:43 AM
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
-- Table structure for table `civil_service_eligibility`
--

CREATE TABLE `civil_service_eligibility` (
  `eligibility_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `eligibility_name` varchar(255) DEFAULT NULL,
  `rating` varchar(10) DEFAULT NULL,
  `exam_date` date DEFAULT NULL,
  `exam_place` varchar(255) DEFAULT NULL,
  `license_number` varchar(50) DEFAULT NULL,
  `validity_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `civil_service_eligibility`
--

INSERT INTO `civil_service_eligibility` (`eligibility_id`, `user_id`, `eligibility_name`, `rating`, `exam_date`, `exam_place`, `license_number`, `validity_date`) VALUES
(99, 26, 'CSE-Professional', '89.20', '2025-03-02', 'Camarines Sur National High School', '123', '2090-10-05'),
(100, 26, 'CSE-Professional', '89.20', '2025-03-02', 'Camarines Sur National High School', '123', '2090-10-05'),
(101, 26, 'CSE-Professional', '89.20', '2025-03-02', 'Camarines Sur National High School', '123', '2090-10-05'),
(102, 26, 'CSE-Professional', '89.20', '2025-03-02', 'Camarines Sur National High School', '123', '2090-10-05'),
(103, 26, 'CSE-Professional', '89.20', '2025-03-02', 'Camarines Sur National High School', '123', '2090-10-05'),
(104, 26, 'CSE-Professional', '89.20', '2025-03-02', 'Camarines Sur National High School', '123', '2090-10-05'),
(105, 26, 'CSE-Professional', '89.20', '2025-03-02', 'Camarines Sur National High School', '123', '2090-10-05'),
(115, 33, 'Licensure Examination for teachers', '80.0', '2021-11-10', 'Manila', '010101', '2025-01-10');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `civil_service_eligibility`
--
ALTER TABLE `civil_service_eligibility`
  ADD PRIMARY KEY (`eligibility_id`),
  ADD KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `civil_service_eligibility`
--
ALTER TABLE `civil_service_eligibility`
  MODIFY `eligibility_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=116;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `civil_service_eligibility`
--
ALTER TABLE `civil_service_eligibility`
  ADD CONSTRAINT `civil_service_eligibility_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
