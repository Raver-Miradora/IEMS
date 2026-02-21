-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 26, 2025 at 03:42 AM
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
-- Table structure for table `activity_logs`
--

CREATE TABLE `activity_logs` (
  `log_id` int(11) NOT NULL,
  `log_timestamp` datetime DEFAULT current_timestamp(),
  `activity_by` varchar(100) NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `activity_description` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `activity_logs`
--

INSERT INTO `activity_logs` (`log_id`, `log_timestamp`, `activity_by`, `user_name`, `activity_description`) VALUES
(1, '2025-11-05 15:48:41', 'Admin', 'Raver Baturiano Miradora', 'Cleared all enrolled fingerprints for employee: Smith, Clarence (ID: LGU-L00003)'),
(2, '2025-11-05 15:49:03', 'Admin', 'Raver Baturiano Miradora', 'Successfully enrolled new fingerprints for employee: Smith, Clarence (ID: LGU-L00003)'),
(3, '2025-11-23 22:43:59', 'Admin', 'Raver Baturiano Miradora', 'Generated TXT attendance log for All Departments'),
(4, '2025-11-23 22:44:04', 'Admin', 'Raver Baturiano Miradora', 'Generated Excel (XLSX) attendance log for All Departments'),
(5, '2025-11-23 22:44:08', 'Admin', 'Raver Baturiano Miradora', 'Generated PDF attendance log for All Departments'),
(6, '2025-11-23 23:14:51', 'Admin', 'Raver Miradora', 'Added new special calendar event: Sample Holiday  (2025-11-05)'),
(7, '2025-11-23 23:34:04', 'Admin', 'Raver Baturiano Miradora', 'Added new employee: Dela Cruz, Juan R. (ID: 001-2025 DLP)'),
(8, '2025-11-24 00:25:39', 'Admin', 'Raver Miradora', 'Updated special calendar event (ID: 3) to: Sample Holiday '),
(9, '2025-11-24 00:27:49', 'Admin', 'Raver Miradora', 'Updated special calendar event (ID: 3) to: Sample Holiday '),
(10, '2025-11-24 00:28:51', 'Admin', 'Raver Miradora', 'Updated special calendar event (ID: 3) to: Sample Holiday '),
(11, '2025-11-24 01:37:45', 'Admin', 'Raver Baturiano Miradora', 'Generated TXT attendance log for All Departments'),
(12, '2025-11-24 01:38:03', 'Admin', 'Raver Baturiano Miradora', 'Generated Excel (XLSX) attendance log for All Departments'),
(13, '2025-11-24 01:38:11', 'Admin', 'Raver Baturiano Miradora', 'Generated PDF attendance log for All Departments'),
(14, '2025-11-24 01:47:50', 'Admin', 'Raver Baturiano Miradora', 'Added new employee: Dela Cruz, Juan R. (ID: 001-2025 DLP)'),
(15, '2025-11-24 02:15:38', 'Admin', 'Raver Baturiano Miradora', 'Added new employee: Dela Cruz, Juan R. (ID: 001-2025 DLP)'),
(16, '2025-11-25 00:21:14', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Smith, Clarence (ID: LGU-L00003)'),
(17, '2025-11-25 00:21:43', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Smith, Clarence (ID: LGU-L00003)'),
(18, '2025-11-25 00:22:39', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Smith, Clarence (ID: LGU-L00003)'),
(19, '2025-11-25 00:30:06', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Smith, Clarence (ID: LGU-L00003)'),
(20, '2025-11-25 00:30:18', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Smith, Clarence (ID: LGU-L00003)'),
(21, '2025-11-25 00:43:30', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Smith, Clarence (ID: LGU-L00003)'),
(22, '2025-11-25 00:47:24', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Smith, Clarence (ID: LGU-L00003)'),
(23, '2025-11-25 00:50:23', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Smith, Clarence (ID: LGU-L00003)'),
(24, '2025-11-25 00:52:21', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Smith, Clarence (ID: LGU-L00003)'),
(25, '2025-11-25 01:01:41', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Cartman, Eric (ID: LGU-L00004)'),
(26, '2025-11-25 01:03:47', 'Admin', 'Raver Baturiano Miradora', 'Added new employee: Dela Cruz, Juan R. (ID: LGU-L0008)'),
(27, '2025-11-25 01:03:59', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Dela Cruz, Juan R. (ID: LGU-L0008)'),
(28, '2025-11-25 01:04:28', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Dela Cruz, Juan R. (ID: LGU-L00008)'),
(29, '2025-11-25 01:12:55', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Bermejo, King D. (ID: LGU-L00005)'),
(30, '2025-11-25 12:11:29', 'Admin', 'Raver Baturiano Miradora', 'Successfully enrolled new fingerprints for employee: Dela Cruz, Juan R. (ID: LGU-L00008)'),
(31, '2025-11-25 12:36:13', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Dela Cruz, Juan R. (ID: LGU-L00008)'),
(32, '2025-11-25 12:38:59', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Dela Cruz, Juan R. (ID: LGU-L00008)'),
(33, '2025-11-25 12:40:03', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Dela Cruz, Juan R. (ID: LGU-L00008)'),
(34, '2025-11-25 13:11:12', 'Admin', 'Raver Baturiano Miradora', 'Updated employee details for: Dela Cruz, Juan R. (ID: LGU-L00008)');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `activity_logs`
--
ALTER TABLE `activity_logs`
  ADD PRIMARY KEY (`log_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `activity_logs`
--
ALTER TABLE `activity_logs`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
