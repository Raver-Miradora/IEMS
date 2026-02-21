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
-- Table structure for table `user_timeoff`
--

CREATE TABLE `user_timeoff` (
  `user_timeoff_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `timeoff_id` int(11) NOT NULL,
  `description` varchar(155) DEFAULT NULL,
  `attachment` varchar(155) DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` int(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_timeoff`
--

INSERT INTO `user_timeoff` (`user_timeoff_id`, `user_id`, `timeoff_id`, `description`, `attachment`, `start_date`, `end_date`, `status`) VALUES
(1, 1, 1, '', '', '2025-10-31', '2025-10-31', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `user_timeoff`
--
ALTER TABLE `user_timeoff`
  ADD PRIMARY KEY (`user_timeoff_id`),
  ADD KEY `timeoff_id` (`timeoff_id`),
  ADD KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `user_timeoff`
--
ALTER TABLE `user_timeoff`
  MODIFY `user_timeoff_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `user_timeoff`
--
ALTER TABLE `user_timeoff`
  ADD CONSTRAINT `user_timeoff_ibfk_1` FOREIGN KEY (`timeoff_id`) REFERENCES `timeoff` (`timeoff_id`),
  ADD CONSTRAINT `user_timeoff_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
