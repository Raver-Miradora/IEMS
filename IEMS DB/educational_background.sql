-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 26, 2025 at 03:44 AM
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
-- Table structure for table `educational_background`
--

CREATE TABLE `educational_background` (
  `education_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `level` varchar(50) DEFAULT NULL,
  `school_name` varchar(255) DEFAULT NULL,
  `degree_course` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `units_earned` varchar(50) DEFAULT NULL,
  `year_graduated` varchar(10) DEFAULT NULL,
  `honors` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `educational_background`
--

INSERT INTO `educational_background` (`education_id`, `user_id`, `level`, `school_name`, `degree_course`, `start_date`, `end_date`, `units_earned`, `year_graduated`, `honors`) VALUES
(105, 26, 'Elementary', 'asdvasdv', 'asdvasv', '2003-10-05', '2004-10-05', '21', '10/05/2004', '1'),
(106, 26, 'Secondary', 'asdvasd', 'asdfbfdb', '2003-10-05', '2004-10-05', '21', '10/05/2004', '1'),
(107, 26, 'Vocational', 'sdfbsd', 'fasda', '2003-10-05', '2004-10-05', '21', '10/05/2004', '1'),
(108, 26, 'College', 'awgrf', 'asdfgdfg', '2003-10-05', '2004-10-05', '21', '10/05/2004', '1'),
(109, 26, 'Graduate Studies', 'sdfgsdfb', 'adfshgar', '2003-10-05', '2004-10-05', '21', '10/05/2004', '1'),
(137, 33, 'Elementary', 'Sablay E.S', 'Primary Education', '2001-10-01', '2004-10-01', '', '2004', ''),
(138, 33, 'Secondary', 'Mabagal N.H.S', 'Secondary Education', '2005-10-01', '2009-10-01', '', '2009', ''),
(139, 33, 'College', 'Mataas State University', 'Bachelor of Science in Information Technology', '2010-10-01', '2014-10-01', '', '2014', '');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `educational_background`
--
ALTER TABLE `educational_background`
  ADD PRIMARY KEY (`education_id`),
  ADD KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `educational_background`
--
ALTER TABLE `educational_background`
  MODIFY `education_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=140;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `educational_background`
--
ALTER TABLE `educational_background`
  ADD CONSTRAINT `educational_background_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
