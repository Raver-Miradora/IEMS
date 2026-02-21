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
-- Table structure for table `assignment`
--

CREATE TABLE `assignment` (
  `assignment_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT 0,
  `position_id` int(11) NOT NULL,
  `shift_id` int(11) NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `date_assigned` date DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `assignment`
--

INSERT INTO `assignment` (`assignment_id`, `user_id`, `position_id`, `shift_id`, `start_time`, `end_time`, `date_assigned`, `status`) VALUES
(1, 1, 1, 1, '08:00:00', '12:00:00', '2025-08-27', 0),
(2, 1, 1, 1, '08:00:00', '12:00:00', '2025-09-11', 0),
(3, 1, 1, 3, '08:00:00', '17:00:00', '2025-09-11', 1),
(4, 2, 19, 3, '08:00:00', '17:00:00', '2025-09-15', 0),
(5, 3, 19, 3, '08:00:00', '17:00:00', '2025-09-15', 1),
(6, 4, 27, 3, '08:00:00', '17:00:00', '2025-09-15', 0),
(7, 5, 6, 3, '08:00:00', '17:00:00', '2025-09-15', 0),
(8, 6, 14, 3, '08:00:00', '17:00:00', '2025-09-15', 1),
(9, 3, 11, 1, '00:00:00', '01:00:00', '2025-10-18', 0),
(20, 26, 15, 3, '08:00:00', '17:00:00', '2025-11-02', 1),
(24, 5, 17, 3, '08:00:00', '17:00:00', '2025-11-25', 0),
(25, 5, 1, 3, '08:00:00', '17:00:00', '2025-11-25', 0),
(26, 5, 1, 3, '08:00:00', '17:00:00', '2025-11-25', 0),
(27, 5, 6, 3, '08:00:00', '17:00:00', '2025-11-25', 0),
(28, 5, 31, 3, '08:00:00', '17:00:00', '2025-11-25', 0),
(29, 5, 31, 3, '08:00:00', '17:00:00', '2025-11-25', 0),
(30, 5, 18, 3, '08:00:00', '17:00:00', '2025-11-25', 1),
(31, 4, 31, 3, '08:00:00', '17:00:00', '2025-11-25', 1),
(32, 33, 31, 3, '08:00:00', '17:00:00', '2025-11-25', 0),
(33, 33, 14, 3, '08:00:00', '17:00:00', '2025-11-25', 1),
(34, 2, 15, 3, '08:00:00', '17:00:00', '2025-11-25', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `assignment`
--
ALTER TABLE `assignment`
  ADD PRIMARY KEY (`assignment_id`),
  ADD KEY `positionID` (`position_id`),
  ADD KEY `shift_id` (`shift_id`),
  ADD KEY `shift_position_ibfk_1` (`user_id`) USING BTREE;

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `assignment`
--
ALTER TABLE `assignment`
  MODIFY `assignment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `assignment`
--
ALTER TABLE `assignment`
  ADD CONSTRAINT `assignment_ibfk_2` FOREIGN KEY (`position_id`) REFERENCES `position` (`position_id`),
  ADD CONSTRAINT `assignment_ibfk_3` FOREIGN KEY (`shift_id`) REFERENCES `shift` (`shift_id`),
  ADD CONSTRAINT `assignment_ibfk_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
