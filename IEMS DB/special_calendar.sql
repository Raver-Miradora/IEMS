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
-- Table structure for table `special_calendar`
--

CREATE TABLE `special_calendar` (
  `special_calendar_id` int(11) NOT NULL,
  `sc_type` varchar(55) NOT NULL,
  `sc_desc` varchar(155) NOT NULL,
  `attachment` varchar(155) DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `schedule_type` varchar(50) DEFAULT 'Full Day',
  `status` tinyint(2) NOT NULL DEFAULT 1,
  `holiday_type` varchar(100) DEFAULT 'Regular Holiday'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `special_calendar`
--

INSERT INTO `special_calendar` (`special_calendar_id`, `sc_type`, `sc_desc`, `attachment`, `start_date`, `end_date`, `schedule_type`, `status`, `holiday_type`) VALUES
(1, 'Holiday', 'All Saints\' Day', '', '2025-10-29', '2025-10-29', 'Full Day', 1, 'Regular Holiday'),
(2, 'Suspension', 'Suspension of work due to Tropical Storm Tino', '', '2025-11-03', '2025-11-03', 'AM Only (Suspension)', 1, 'Regular Holiday'),
(3, 'Holiday', 'Sample Holiday ', '', '2025-11-05', '2025-11-05', 'Full Day', 1, 'Special Working Holiday');

--
-- Triggers `special_calendar`
--
DELIMITER $$
CREATE TRIGGER `AfterInsertSpecialCalendar` AFTER INSERT ON `special_calendar` FOR EACH ROW BEGIN
    
    INSERT INTO user_calendar (user_id, special_calendar_id, user_calendar_status)
    SELECT DISTINCT u.user_id, NEW.special_calendar_id, 1
    FROM user u
    WHERE u.user_status = 1;
END
$$
DELIMITER ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `special_calendar`
--
ALTER TABLE `special_calendar`
  ADD PRIMARY KEY (`special_calendar_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `special_calendar`
--
ALTER TABLE `special_calendar`
  MODIFY `special_calendar_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
