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
-- Table structure for table `attendance`
--

CREATE TABLE `attendance` (
  `attendance_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `date` date NOT NULL,
  `time_in` time NOT NULL,
  `time_out` time DEFAULT NULL,
  `time_notation` varchar(45) DEFAULT NULL,
  `attendance_status` tinyint(5) DEFAULT NULL,
  `overtime_in` time DEFAULT NULL,
  `overtime_out` time DEFAULT NULL,
  `overtime_notation` varchar(2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `attendance`
--

INSERT INTO `attendance` (`attendance_id`, `user_id`, `date`, `time_in`, `time_out`, `time_notation`, `attendance_status`, `overtime_in`, `overtime_out`, `overtime_notation`) VALUES
(1, 1, '2025-09-10', '11:53:52', '11:53:58', 'AM', 2, NULL, NULL, NULL),
(2, 1, '2025-09-10', '22:46:02', '22:30:59', 'PM', 2, NULL, NULL, NULL),
(3, 1, '2025-09-11', '22:31:07', '22:31:12', 'PM', 2, NULL, NULL, NULL),
(4, 2, '2025-09-11', '23:03:49', '23:03:54', 'PM', 1, NULL, NULL, NULL),
(5, 1, '2025-09-13', '20:33:10', '20:33:23', 'PM', 2, NULL, NULL, NULL),
(6, 2, '2025-09-13', '20:33:45', '20:33:53', 'PM', 1, NULL, NULL, NULL),
(7, 1, '2025-09-14', '00:04:27', '13:20:02', 'AM', 1, NULL, NULL, NULL),
(8, 1, '2025-09-15', '13:20:11', '13:20:19', 'PM', 2, NULL, NULL, NULL),
(9, 2, '2025-09-15', '13:20:29', '13:20:38', 'PM', 1, NULL, NULL, NULL),
(10, 3, '2025-09-15', '15:18:00', '15:37:27', 'PM', 1, NULL, NULL, NULL),
(11, 4, '2025-09-15', '20:46:42', '20:49:59', 'PM', 1, NULL, NULL, NULL),
(12, 5, '2025-09-15', '20:57:46', '20:57:56', 'PM', 1, NULL, NULL, NULL),
(13, 6, '2025-09-15', '21:02:44', '21:03:07', 'PM', 1, NULL, NULL, NULL),
(14, 1, '2025-09-16', '20:55:35', '21:40:34', 'PM', 2, NULL, NULL, NULL),
(15, 2, '2025-09-16', '21:08:08', '21:40:53', 'PM', 2, NULL, NULL, NULL),
(16, 3, '2025-09-16', '21:08:30', '21:11:47', 'PM', 2, NULL, NULL, NULL),
(17, 5, '2025-09-16', '21:41:45', '21:43:16', 'PM', 2, NULL, NULL, NULL),
(18, 4, '2025-09-16', '21:43:30', '21:46:33', 'PM', 2, NULL, NULL, NULL),
(19, 1, '2025-09-17', '00:42:48', '00:51:51', 'AM', 1, NULL, NULL, NULL),
(20, 3, '2025-09-17', '00:54:06', NULL, 'AM', 1, NULL, NULL, NULL),
(21, 1, '2025-09-20', '10:35:12', '11:23:14', 'AM', 2, NULL, NULL, NULL),
(22, 5, '2025-09-20', '10:35:46', '11:36:39', 'AM', 2, NULL, NULL, NULL),
(23, 2, '2025-09-20', '10:35:53', '11:36:49', 'AM', 2, NULL, NULL, NULL),
(24, 4, '2025-09-20', '10:35:59', '11:36:55', 'AM', 2, NULL, NULL, NULL),
(25, 1, '2025-09-20', '23:43:40', NULL, 'PM', 2, NULL, NULL, NULL),
(26, 1, '2025-09-22', '11:59:20', NULL, 'AM', 2, NULL, NULL, NULL),
(27, 3, '2025-09-22', '11:59:45', NULL, 'AM', 2, NULL, NULL, NULL),
(28, 5, '2025-09-22', '11:59:55', NULL, 'AM', 2, NULL, NULL, NULL),
(29, 2, '2025-09-22', '12:01:51', NULL, 'PM', 1, NULL, NULL, NULL),
(30, 4, '2025-09-22', '12:02:00', NULL, 'PM', 1, NULL, NULL, NULL),
(31, 1, '2025-09-23', '18:25:23', NULL, 'PM', 2, NULL, NULL, NULL),
(32, 3, '2025-09-23', '20:35:52', NULL, 'PM', 2, NULL, NULL, NULL),
(33, 4, '2025-09-23', '20:36:13', NULL, 'PM', 2, NULL, NULL, NULL),
(34, 5, '2025-09-23', '20:36:18', NULL, 'PM', 2, NULL, NULL, NULL),
(35, 2, '2025-09-23', '20:36:26', NULL, 'PM', 2, NULL, NULL, NULL),
(36, 1, '2025-09-26', '00:00:00', '00:00:00', 'AM', 1, '19:28:59', '18:51:18', 'OT'),
(37, 3, '2025-09-26', '00:00:00', '00:00:00', 'AM', 1, '19:11:12', NULL, 'OT'),
(38, 1, '2025-09-26', '19:11:48', NULL, 'PM', 2, '19:28:59', NULL, 'OT'),
(39, 1, '2025-09-28', '12:36:39', '11:55:57', 'PM', 1, '10:21:51', '11:56:08', 'OT'),
(40, 2, '2025-09-28', '10:23:18', '10:23:21', 'AM', 2, NULL, NULL, NULL),
(41, 5, '2025-09-28', '10:23:49', NULL, 'AM', 2, NULL, NULL, NULL),
(42, 3, '2025-09-28', '10:26:14', '10:26:19', 'AM', 2, '10:26:44', NULL, 'OT'),
(43, 4, '2025-09-28', '12:38:05', '12:37:48', 'PM', 1, '12:39:02', '12:39:13', 'OT'),
(44, 1, '2025-09-30', '14:00:32', '14:13:21', 'PM', 2, '14:13:52', NULL, 'OT'),
(45, 5, '2025-09-30', '14:00:46', NULL, 'PM', 2, NULL, NULL, NULL),
(46, 3, '2025-09-30', '14:01:06', '14:13:42', 'PM', 2, NULL, NULL, NULL),
(47, 2, '2025-09-30', '14:36:53', NULL, 'PM', 2, NULL, NULL, NULL),
(48, 1, '2025-10-06', '08:44:19', '08:48:32', 'AM', 2, NULL, NULL, NULL),
(49, 5, '2025-10-06', '08:44:24', NULL, 'AM', 2, NULL, NULL, NULL),
(50, 3, '2025-10-06', '08:45:27', '08:46:07', 'AM', 2, NULL, NULL, NULL),
(51, 4, '2025-10-06', '08:48:04', NULL, 'AM', 2, NULL, NULL, NULL),
(52, 2, '2025-10-06', '08:48:22', NULL, 'AM', 2, NULL, NULL, NULL),
(53, 1, '2025-10-09', '00:00:00', NULL, 'AM', 1, '17:14:24', NULL, 'OT'),
(54, 3, '2025-10-09', '00:00:00', NULL, 'AM', 1, '17:17:10', NULL, 'OT'),
(55, 5, '2025-10-09', '00:00:00', NULL, 'AM', 1, '17:17:16', NULL, 'OT'),
(56, 2, '2025-10-09', '00:00:00', NULL, 'AM', 1, '17:20:01', NULL, 'OT'),
(57, 4, '2025-10-09', '00:00:00', NULL, 'AM', 1, '17:20:12', NULL, 'OT'),
(58, 1, '2025-10-10', '00:00:00', NULL, 'AM', 1, '22:08:43', NULL, 'OT'),
(59, 1, '2025-10-11', '07:55:24', '12:00:00', 'AM', 1, NULL, NULL, NULL),
(60, 3, '2025-10-11', '07:57:21', '07:59:01', 'AM', 1, NULL, NULL, NULL),
(61, 2, '2025-10-11', '07:59:19', NULL, 'AM', 1, NULL, NULL, NULL),
(62, 4, '2025-10-11', '07:59:37', NULL, 'AM', 1, NULL, NULL, NULL),
(63, 5, '2025-10-11', '08:00:45', NULL, 'AM', 2, NULL, NULL, NULL),
(64, 3, '2025-10-18', '00:00:00', NULL, 'AM', 1, '21:42:05', '21:42:20', 'OT'),
(65, 1, '2025-10-28', '09:45:37', NULL, 'AM', 2, NULL, NULL, NULL),
(66, 5, '2025-10-28', '09:45:48', NULL, 'AM', 2, NULL, NULL, NULL),
(67, 3, '2025-10-28', '09:45:53', NULL, 'AM', 2, NULL, NULL, NULL),
(68, 2, '2025-10-28', '09:46:03', NULL, 'AM', 2, NULL, NULL, NULL),
(69, 4, '2025-10-28', '09:46:13', NULL, 'AM', 2, NULL, NULL, NULL),
(70, 1, '2025-10-31', '18:05:37', NULL, 'PM', 2, NULL, NULL, NULL),
(71, 3, '2025-10-31', '19:08:50', NULL, 'PM', 2, NULL, NULL, NULL),
(73, 26, '2025-11-02', '00:00:00', NULL, 'AM', 1, '19:30:26', NULL, 'OT'),
(74, 26, '2025-11-03', '01:04:38', '12:49:06', 'AM', 1, '00:03:18', '00:15:24', 'OT'),
(75, 1, '2025-11-03', '12:46:41', '01:14:38', 'PM', 1, '01:20:12', '01:15:54', 'OT'),
(76, 5, '2025-11-03', '12:50:34', '00:48:22', 'PM', 1, NULL, NULL, NULL),
(77, 3, '2025-11-03', '12:47:32', '12:47:17', 'PM', 1, '01:20:47', '01:21:27', 'OT'),
(78, 1, '2025-11-03', '14:12:44', NULL, 'PM', 2, NULL, NULL, NULL),
(79, 3, '2025-11-03', '14:13:47', NULL, 'PM', 2, NULL, NULL, NULL),
(80, 2, '2025-11-03', '14:14:23', '14:17:53', 'PM', 2, NULL, NULL, NULL),
(81, 2, '2025-11-03', '14:18:05', NULL, 'PM', 2, '14:36:27', '14:36:30', 'OT'),
(82, 2, '2025-11-03', '00:00:00', NULL, 'AM', 1, '14:36:32', NULL, 'OT'),
(83, 5, '2025-11-03', '14:50:12', NULL, 'PM', 2, '14:50:42', '14:50:50', 'OT'),
(84, 5, '2025-11-03', '00:00:00', NULL, 'AM', 1, '14:50:52', NULL, 'OT'),
(85, 26, '2025-11-03', '14:51:46', NULL, 'PM', 2, NULL, NULL, NULL),
(86, 1, '2025-11-04', '22:23:25', '22:23:54', 'PM', 2, NULL, NULL, NULL),
(87, 1, '2025-11-05', '15:47:31', '15:50:38', 'PM', 2, NULL, NULL, NULL),
(88, 5, '2025-11-05', '15:50:13', '15:51:10', 'PM', 2, NULL, NULL, NULL),
(89, 1, '2025-11-24', '23:54:32', '23:55:32', 'PM', 1, '18:54:53', '18:55:12', 'OT'),
(90, 2, '2025-11-24', '23:53:52', '23:25:39', 'PM', 2, NULL, NULL, NULL),
(91, 3, '2025-11-24', '23:56:59', '23:57:10', 'PM', 2, NULL, NULL, NULL),
(92, 26, '2025-11-24', '23:57:12', '23:58:15', 'PM', 2, NULL, NULL, NULL),
(93, 5, '2025-11-24', '23:57:20', '23:58:55', 'PM', 2, NULL, NULL, NULL),
(94, 1, '2025-11-25', '07:31:48', '11:57:07', 'AM', 1, NULL, NULL, NULL),
(95, 3, '2025-11-25', '07:44:29', '11:57:58', 'AM', 1, NULL, NULL, NULL),
(96, 2, '2025-11-25', '07:51:37', '11:58:31', 'AM', 1, NULL, NULL, NULL),
(97, 5, '2025-11-25', '08:09:40', '11:59:23', 'AM', 1, NULL, NULL, NULL),
(98, 2, '2025-11-25', '07:59:07', NULL, 'AM', 2, NULL, NULL, NULL),
(99, 1, '2025-11-25', '12:07:35', NULL, 'PM', 1, NULL, NULL, NULL),
(100, 3, '2025-11-25', '12:09:58', NULL, 'PM', 1, NULL, NULL, NULL),
(101, 26, '2025-11-25', '12:10:06', NULL, 'PM', 1, NULL, NULL, NULL),
(102, 5, '2025-11-25', '12:10:20', NULL, 'PM', 1, NULL, NULL, NULL),
(103, 33, '2025-11-25', '12:12:24', NULL, 'PM', 1, NULL, NULL, NULL);

--
-- Triggers `attendance`
--
DELIMITER $$
CREATE TRIGGER `before_insert_combined_trigger` BEFORE INSERT ON `attendance` FOR EACH ROW BEGIN
    DECLARE earliest_shift_start_time TIME;
    
    
    SELECT MIN(a.start_time)
    INTO earliest_shift_start_time
    FROM user u
    JOIN assignment a ON u.user_id = a.user_id
    JOIN shift s ON a.shift_id = s.shift_id
    WHERE u.user_id = NEW.user_id
        AND a.status = 1
        AND (
            (s.shift_name NOT LIKE 'Overload' AND NEW.time_in < '12:00:00' AND NEW.time_in > earliest_shift_start_time)
            OR
            (s.shift_name LIKE 'Day Shift' AND NEW.time_in BETWEEN '08:00:01' AND '12:00:00')
            OR
            (TIME(a.start_time) = '00:00:00' AND NEW.time_in > '23:00:00' AND NEW.time_in > TIME(a.start_time))
            OR
            (s.shift_name LIKE 'Day Shift' AND NEW.time_in BETWEEN '13:00:01' AND '23:59:59')
            );

    
    IF earliest_shift_start_time IS NOT NULL THEN
        SET NEW.attendance_status = 2;
    ELSE
        SET NEW.attendance_status = 1;
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `set_time_notation` BEFORE INSERT ON `attendance` FOR EACH ROW BEGIN
    IF EXTRACT(HOUR FROM NEW.time_in) >= 12 THEN
        SET NEW.time_notation = 'PM';
    ELSE
        SET NEW.time_notation = 'AM';
    END IF;
END
$$
DELIMITER ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `attendance`
--
ALTER TABLE `attendance`
  ADD PRIMARY KEY (`attendance_id`),
  ADD KEY `userID` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `attendance`
--
ALTER TABLE `attendance`
  MODIFY `attendance_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=104;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `attendance`
--
ALTER TABLE `attendance`
  ADD CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
