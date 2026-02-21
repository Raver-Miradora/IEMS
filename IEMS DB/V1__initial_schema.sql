-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 26, 2025 at 03:49 AM
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
-- Stand-in structure for view `active_employee_view`
-- (See below for the actual view)
--
CREATE TABLE `active_employee_view` (
`user_id` int(1)
,`name` varchar(140)
,`privilege` varchar(45)
,`email` varchar(45)
,`user_cntct` varchar(11)
,`birth_date` date
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `active_users_view`
-- (See below for the actual view)
--
CREATE TABLE `active_users_view` (
`user_id` int(1)
,`name` varchar(183)
);

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

-- --------------------------------------------------------

--
-- Stand-in structure for view `attendance_summary_view`
-- (See below for the actual view)
--
CREATE TABLE `attendance_summary_view` (
`dtrDate` varchar(16)
,`day` int(2)
,`id` int(11)
,`name` varchar(91)
,`timeInAm` varchar(10)
,`timeOutAm` varchar(10)
,`timeInPm` varchar(10)
,`timeOutPm` varchar(10)
);

-- --------------------------------------------------------

--
-- Table structure for table `child`
--

CREATE TABLE `child` (
  `child_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

-- --------------------------------------------------------

--
-- Table structure for table `department`
--

CREATE TABLE `department` (
  `department_id` int(11) NOT NULL,
  `department_name` varchar(155) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Stand-in structure for view `dtr`
-- (See below for the actual view)
--
CREATE TABLE `dtr` (
`date` date
,`user_id` int(11)
,`firstname` varchar(45)
,`middlename` varchar(45)
,`lastname` varchar(45)
,`timeInAM` time
,`timeOutAM` time
,`timeInPM` time
,`timeOutPM` time
,`status` int(1)
);

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

-- --------------------------------------------------------

--
-- Table structure for table `employee_documents`
--

CREATE TABLE `employee_documents` (
  `document_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `document_type` enum('Resume','Offer Letter','Joining Letter','Contract & Agreement','Other') NOT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_data` longblob DEFAULT NULL,
  `google_drive_url` varchar(500) DEFAULT NULL,
  `uploaded_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Stand-in structure for view `employee_status_view`
-- (See below for the actual view)
--
CREATE TABLE `employee_status_view` (
`total_employees` bigint(21)
,`percentage_logged_in` decimal(29,4)
,`percentage_not_logged_in` decimal(30,4)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `employee_view`
-- (See below for the actual view)
--
CREATE TABLE `employee_view` (
`user_id` int(1)
,`name` varchar(140)
,`privilege` varchar(45)
,`email` varchar(45)
,`user_cntct` varchar(11)
,`birth_date` date
,`user_status` tinyint(2)
);

-- --------------------------------------------------------

--
-- Table structure for table `failed_jobs`
--

CREATE TABLE `failed_jobs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `connection` text NOT NULL,
  `queue` text NOT NULL,
  `payload` longtext NOT NULL,
  `exception` longtext NOT NULL,
  `failed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `fingerprint`
--

CREATE TABLE `fingerprint` (
  `user_id` int(11) NOT NULL,
  `fmd` blob NOT NULL,
  `width` int(11) NOT NULL,
  `height` int(11) NOT NULL,
  `resolution` int(11) NOT NULL,
  `finger_position` int(11) NOT NULL,
  `cbeff_id` int(11) NOT NULL,
  `register_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `migrations`
--

CREATE TABLE `migrations` (
  `id` int(10) UNSIGNED NOT NULL,
  `migration` varchar(255) NOT NULL,
  `batch` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notices`
--

CREATE TABLE `notices` (
  `notice_id` int(11) NOT NULL,
  `notice_title` varchar(255) NOT NULL,
  `notice_content` text NOT NULL,
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '1 = Active, 0 = Inactive',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Stand-in structure for view `overload_view_am`
-- (See below for the actual view)
--
CREATE TABLE `overload_view_am` (
`dtrDate` varchar(71)
,`day` int(2)
,`name` varchar(91)
,`timeIn` varchar(10)
,`timeOut` varchar(10)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `overload_view_pm`
-- (See below for the actual view)
--
CREATE TABLE `overload_view_pm` (
`dtrDate` varchar(71)
,`day` int(2)
,`name` varchar(91)
,`timeIn` varchar(10)
,`timeOut` varchar(10)
);

-- --------------------------------------------------------

--
-- Table structure for table `parent`
--

CREATE TABLE `parent` (
  `parent_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `parent_type` enum('Father','Mother') DEFAULT NULL,
  `surname` varchar(45) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `middle_name` varchar(45) DEFAULT NULL,
  `name_extension` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `password_reset_tokens`
--

CREATE TABLE `password_reset_tokens` (
  `email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `personal_access_tokens`
--

CREATE TABLE `personal_access_tokens` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `tokenable_type` varchar(255) NOT NULL,
  `tokenable_id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `token` varchar(64) NOT NULL,
  `abilities` text DEFAULT NULL,
  `last_used_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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

-- --------------------------------------------------------

--
-- Table structure for table `quotes`
--

CREATE TABLE `quotes` (
  `quote_id` int(11) NOT NULL,
  `quote_content` text NOT NULL,
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '1 = Active, 0 = Inactive'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Stand-in structure for view `recent_attendance_view`
-- (See below for the actual view)
--
CREATE TABLE `recent_attendance_view` (
`name` varchar(140)
,`event_time` time
,`event_type` varchar(8)
,`date` date
);

-- --------------------------------------------------------

--
-- Table structure for table `shift`
--

CREATE TABLE `shift` (
  `shift_id` int(11) NOT NULL,
  `shift_name` varchar(45) NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

-- --------------------------------------------------------

--
-- Stand-in structure for view `special_calendar_view`
-- (See below for the actual view)
--
CREATE TABLE `special_calendar_view` (
`id` int(1)
,`type` varchar(55)
,`description` varchar(155)
,`startDate` date
,`endDate` date
);

-- --------------------------------------------------------

--
-- Table structure for table `spouse`
--

CREATE TABLE `spouse` (
  `spouse_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `surname` varchar(45) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `middle_name` varchar(45) DEFAULT NULL,
  `name_extension` varchar(10) DEFAULT NULL,
  `occupation` varchar(100) DEFAULT NULL,
  `employer` varchar(100) DEFAULT NULL,
  `business_address` text DEFAULT NULL,
  `telephone` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `timeoff`
--

CREATE TABLE `timeoff` (
  `timeoff_id` int(11) NOT NULL,
  `timeoff_type` varchar(55) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_id` int(1) NOT NULL,
  `user_fname` varchar(45) NOT NULL,
  `user_mname` varchar(45) DEFAULT NULL,
  `user_lname` varchar(45) NOT NULL,
  `suffix` varchar(45) DEFAULT NULL,
  `email` varchar(45) NOT NULL,
  `password` varchar(100) NOT NULL,
  `privilege` varchar(45) NOT NULL,
  `user_cntct` varchar(11) DEFAULT NULL,
  `sex` varchar(10) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `residential_address` varchar(255) DEFAULT NULL,
  `user_img` longblob DEFAULT NULL,
  `user_status` tinyint(2) NOT NULL DEFAULT 1,
  `place_of_birth` varchar(255) DEFAULT NULL,
  `civil_status` varchar(45) DEFAULT NULL,
  `height` decimal(4,2) DEFAULT NULL,
  `weight` decimal(5,2) DEFAULT NULL,
  `blood_type` varchar(5) DEFAULT NULL,
  `gsis_id` varbinary(255) DEFAULT NULL,
  `pagibig_id` varbinary(255) DEFAULT NULL,
  `philhealth_no` varbinary(255) DEFAULT NULL,
  `sss_no` varbinary(255) DEFAULT NULL,
  `tin_no` varbinary(255) DEFAULT NULL,
  `agency_employee_no` varbinary(255) DEFAULT NULL,
  `citizenship` varchar(45) DEFAULT NULL,
  `permanent_address` text DEFAULT NULL,
  `telephone_no` varchar(45) DEFAULT NULL,
  `spouse_surname` varchar(45) DEFAULT NULL,
  `spouse_first_name` varchar(45) DEFAULT NULL,
  `spouse_middle_name` varchar(45) DEFAULT NULL,
  `spouse_name_extension` varchar(10) DEFAULT NULL,
  `spouse_occupation` varchar(100) DEFAULT NULL,
  `spouse_employer` varchar(100) DEFAULT NULL,
  `spouse_business_address` text DEFAULT NULL,
  `spouse_telephone` varchar(20) DEFAULT NULL,
  `father_surname` varchar(45) DEFAULT NULL,
  `father_first_name` varchar(45) DEFAULT NULL,
  `father_middle_name` varchar(45) DEFAULT NULL,
  `father_name_extension` varchar(10) DEFAULT NULL,
  `mother_surname` varchar(45) DEFAULT NULL,
  `mother_first_name` varchar(45) DEFAULT NULL,
  `mother_middle_name` varchar(45) DEFAULT NULL,
  `children_info` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `user`
--
DELIMITER $$
CREATE TRIGGER `AfterInsertUser` AFTER INSERT ON `user` FOR EACH ROW BEGIN
    
    INSERT INTO user_calendar (user_id, special_calendar_id, user_calendar_status)
    SELECT NEW.user_id, sc.special_calendar_id, 1
    FROM special_calendar sc
WHERE sc.status = 1;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `email_verified_at` timestamp NULL DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `remember_token` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_calendar`
--

CREATE TABLE `user_calendar` (
  `user_calendar_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `special_calendar_id` int(11) NOT NULL,
  `user_calendar_status` tinyint(2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Stand-in structure for view `user_calendar_schedule`
-- (See below for the actual view)
--
CREATE TABLE `user_calendar_schedule` (
`name` varchar(91)
,`type` varchar(55)
,`month` int(2)
,`day` int(2)
);

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

-- --------------------------------------------------------

--
-- Stand-in structure for view `user_timeoff_schedule`
-- (See below for the actual view)
--
CREATE TABLE `user_timeoff_schedule` (
`name` varchar(91)
,`type` varchar(55)
,`month` int(2)
,`day` int(2)
);

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

-- --------------------------------------------------------

--
-- Structure for view `active_employee_view`
--
DROP TABLE IF EXISTS `active_employee_view`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `active_employee_view`  AS SELECT `user`.`user_id` AS `user_id`, concat_ws(' ',`user`.`user_fname`,if(char_length(`user`.`user_mname`) > 0,concat(substr(`user`.`user_mname`,1,1),'.'),NULL),`user`.`user_lname`,`user`.`suffix`) AS `name`, `user`.`privilege` AS `privilege`, `user`.`email` AS `email`, `user`.`user_cntct` AS `user_cntct`, `user`.`birth_date` AS `birth_date` FROM `user` WHERE `user`.`user_status` = 1 ;

-- --------------------------------------------------------

--
-- Structure for view `active_users_view`
--
DROP TABLE IF EXISTS `active_users_view`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `active_users_view`  AS SELECT `user`.`user_id` AS `user_id`, concat_ws(' ',`user`.`user_fname`,`user`.`user_mname`,`user`.`user_lname`,`user`.`suffix`) AS `name` FROM `user` WHERE `user`.`user_status` = 1 ;

-- --------------------------------------------------------

--
-- Structure for view `attendance_summary_view`
--
DROP TABLE IF EXISTS `attendance_summary_view`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `attendance_summary_view`  AS SELECT concat(monthname(`a`.`date`),' , ',year(`a`.`date`)) AS `dtrDate`, dayofmonth(`a`.`date`) AS `day`, `a`.`user_id` AS `id`, concat(`u`.`user_fname`,' ',`u`.`user_lname`) AS `name`, time_format(case when min(case when `s`.`time_notation` = 'AM' then `s`.`time_in` end) = '13:00:00' then timediff(min(case when `s`.`time_notation` = 'AM' then `s`.`time_in` end),'12:00:00') else min(case when `s`.`time_notation` = 'AM' then `s`.`time_in` end) end,'%H:%i') AS `timeInAm`, time_format(case when max(case when `s`.`time_notation` = 'AM' then `s`.`time_out` end) = '13:00:00' then timediff(max(case when `s`.`time_notation` = 'AM' then `s`.`time_out` end),'12:00:00') else max(case when `s`.`time_notation` = 'AM' then `s`.`time_out` end) end,'%H:%i') AS `timeOutAm`, time_format(case when min(case when `s`.`time_notation` = 'PM' then `s`.`time_in` end) > '13:00:00' then timediff(min(case when `s`.`time_notation` = 'PM' then `s`.`time_in` end),'12:00:00') else min(case when `s`.`time_notation` = 'PM' then `s`.`time_in` end) end,'%H:%i') AS `timeInPm`, time_format(case when max(case when `s`.`time_notation` = 'PM' then `s`.`time_out` end) > '13:00:00' then timediff(max(case when `s`.`time_notation` = 'PM' then `s`.`time_out` end),'12:00:00') else max(case when `s`.`time_notation` = 'PM' then `s`.`time_out` end) end,'%H:%i') AS `timeOutPm` FROM ((`attendance` `a` join `user` `u` on(`a`.`user_id` = `u`.`user_id`)) left join `attendance` `s` on(`a`.`user_id` = `s`.`user_id` and `a`.`date` = `s`.`date`)) GROUP BY `a`.`date`, `a`.`user_id` ;

-- --------------------------------------------------------

--
-- Structure for view `dtr`
--
DROP TABLE IF EXISTS `dtr`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `dtr`  AS SELECT `a`.`date` AS `date`, `a`.`user_id` AS `user_id`, (select `u`.`user_fname` from `user` `u` where `u`.`user_id` = `a`.`user_id`) AS `firstname`, (select `u`.`user_mname` from `user` `u` where `u`.`user_id` = `a`.`user_id`) AS `middlename`, (select `u`.`user_lname` from `user` `u` where `u`.`user_id` = `a`.`user_id`) AS `lastname`, (select `s`.`time_in` from `attendance` `s` where `a`.`user_id` = `s`.`user_id` and `s`.`date` = `a`.`date` and `s`.`time_notation` = 'AM') AS `timeInAM`, (select `s`.`time_out` from `attendance` `s` where `a`.`user_id` = `s`.`user_id` and `s`.`date` = `a`.`date` and `s`.`time_notation` = 'AM') AS `timeOutAM`, (select `s`.`time_in` from `attendance` `s` where `a`.`user_id` = `s`.`user_id` and `s`.`date` = `a`.`date` and `s`.`time_notation` = 'PM') AS `timeInPM`, (select `s`.`time_out` from `attendance` `s` where `a`.`user_id` = `s`.`user_id` and `s`.`date` = `a`.`date` and `s`.`time_notation` = 'PM') AS `timeOutPM`, CASE WHEN (select `s`.`attendance_status` from `attendance` `s` where `a`.`user_id` = `s`.`user_id` AND `s`.`date` = `a`.`date` AND `s`.`time_notation` = 'AM') = 2 OR (select `s`.`attendance_status` from `attendance` `s` where `a`.`user_id` = `s`.`user_id` AND `s`.`date` = `a`.`date` AND `s`.`time_notation` = 'PM') = 2 THEN 2 WHEN (select `s`.`attendance_status` from `attendance` `s` where `a`.`user_id` = `s`.`user_id` AND `s`.`date` = `a`.`date` AND `s`.`time_notation` = 'AM') = 1 AND (select `s`.`attendance_status` from `attendance` `s` where `a`.`user_id` = `s`.`user_id` AND `s`.`date` = `a`.`date` AND `s`.`time_notation` = 'PM') = 1 THEN 1 WHEN (select `s`.`attendance_status` from `attendance` `s` where `a`.`user_id` = `s`.`user_id` AND `s`.`date` = `a`.`date` AND `s`.`time_notation` = 'AM') = 1 THEN 3 ELSE 2 END AS `status` FROM `attendance` AS `a` GROUP BY `a`.`date`, `a`.`user_id` ;

-- --------------------------------------------------------

--
-- Structure for view `employee_status_view`
--
DROP TABLE IF EXISTS `employee_status_view`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `employee_status_view`  AS SELECT count(distinct `u`.`user_id`) AS `total_employees`, sum(case when `a`.`date` = curdate() then 1 else 0 end) / count(distinct `u`.`user_id`) * 100 AS `percentage_logged_in`, (count(`u`.`user_id`) - sum(case when `a`.`date` = curdate() then 1 else 0 end)) / count(`u`.`user_id`) * 100 AS `percentage_not_logged_in` FROM (`user` `u` left join `attendance` `a` on(`u`.`user_id` = `a`.`user_id`)) WHERE `u`.`user_status` = 1 ;

-- --------------------------------------------------------

--
-- Structure for view `employee_view`
--
DROP TABLE IF EXISTS `employee_view`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `employee_view`  AS SELECT `user`.`user_id` AS `user_id`, concat_ws(' ',`user`.`user_fname`,if(char_length(`user`.`user_mname`) > 0,concat(substr(`user`.`user_mname`,1,1),'.'),NULL),`user`.`user_lname`,`user`.`suffix`) AS `name`, `user`.`privilege` AS `privilege`, `user`.`email` AS `email`, `user`.`user_cntct` AS `user_cntct`, `user`.`birth_date` AS `birth_date`, `user`.`user_status` AS `user_status` FROM `user` ;

-- --------------------------------------------------------

--
-- Structure for view `overload_view_am`
--
DROP TABLE IF EXISTS `overload_view_am`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `overload_view_am`  AS SELECT concat(date_format(`a`.`date`,'%M'),' , ',year(`a`.`date`)) AS `dtrDate`, dayofmonth(`a`.`date`) AS `day`, concat(`u`.`user_fname`,' ',`u`.`user_lname`) AS `name`, time_format(`a`.`time_in`,'%H:%i') AS `timeIn`, time_format(`ass`.`end_time`,'%H:%i') AS `timeOut` FROM (((`attendance` `a` join `user` `u` on(`a`.`user_id` = `u`.`user_id`)) join `assignment` `ass` on(`a`.`user_id` = `ass`.`user_id`)) join `shift` `o` on(`ass`.`shift_id` = `o`.`shift_id`)) WHERE `a`.`time_notation` = 'AM' AND `o`.`start_time` is not null AND `o`.`shift_name` = 'Overload' AND (`a`.`time_in` <= `ass`.`start_time` OR timediff(`a`.`time_in`,`ass`.`start_time`) <= '00:30:00') AND `ass`.`start_time` < '12:00:00' ;

-- --------------------------------------------------------

--
-- Structure for view `overload_view_pm`
--
DROP TABLE IF EXISTS `overload_view_pm`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `overload_view_pm`  AS SELECT concat(date_format(`a`.`date`,'%M'),' , ',year(`a`.`date`)) AS `dtrDate`, dayofmonth(`a`.`date`) AS `day`, concat(`u`.`user_fname`,' ',`u`.`user_lname`) AS `name`, CASE WHEN cast(`ass`.`start_time` as time) > '12:00:00' THEN time_format(timediff(`ass`.`start_time`,'12:00:00'),'%H:%i') ELSE time_format(`ass`.`start_time`,'%H:%i') END AS `timeIn`, CASE WHEN cast(`a`.`time_out` as time) > '12:00:00' THEN time_format(timediff(`a`.`time_out`,'12:00:00'),'%H:%i') ELSE time_format(`a`.`time_out`,'%H:%i') END AS `timeOut` FROM (((`attendance` `a` join `user` `u` on(`a`.`user_id` = `u`.`user_id`)) join `assignment` `ass` on(`a`.`user_id` = `ass`.`user_id`)) join `shift` `o` on(`ass`.`shift_id` = `o`.`shift_id`)) WHERE `a`.`time_notation` = 'PM' AND `o`.`start_time` is not null AND `o`.`shift_name` = 'Overload' AND timediff(`a`.`time_out`,`ass`.`start_time`) >= '00:30:00' AND `ass`.`start_time` > '12:00:00' ;

-- --------------------------------------------------------

--
-- Structure for view `recent_attendance_view`
--
DROP TABLE IF EXISTS `recent_attendance_view`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `recent_attendance_view`  AS SELECT concat_ws(' ',`u`.`user_fname`,if(char_length(`u`.`user_mname`) > 0,concat(substr(`u`.`user_mname`,1,1),'.'),NULL),`u`.`user_lname`,`u`.`suffix`) AS `name`, `a`.`time_in` AS `event_time`, 'Time In' AS `event_type`, `a`.`date` AS `date` FROM (`attendance` `a` join `user` `u` on(`a`.`user_id` = `u`.`user_id`)) WHERE `a`.`date` = curdate()union select concat_ws(' ',`u`.`user_fname`,if(char_length(`u`.`user_mname`) > 0,concat(substr(`u`.`user_mname`,1,1),'.'),NULL),`u`.`user_lname`,`u`.`suffix`) AS `name`,`a`.`time_out` AS `event_time`,'Time Out' AS `event_type`,`a`.`date` AS `date` from (`attendance` `a` join `user` `u` on(`a`.`user_id` = `u`.`user_id`)) where `a`.`date` = curdate() and `a`.`time_out` is not null order by `event_time` desc  ;

-- --------------------------------------------------------

--
-- Structure for view `special_calendar_view`
--
DROP TABLE IF EXISTS `special_calendar_view`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `special_calendar_view`  AS SELECT `u`.`user_id` AS `id`, `sp`.`sc_type` AS `type`, `sp`.`sc_desc` AS `description`, `sp`.`start_date` AS `startDate`, `sp`.`end_date` AS `endDate` FROM ((`user` `u` join `user_calendar` `uc` on(`u`.`user_id` = `uc`.`user_id`)) join `special_calendar` `sp` on(`uc`.`special_calendar_id` = `sp`.`special_calendar_id`)) WHERE `uc`.`user_calendar_status` = 1 ;

-- --------------------------------------------------------

--
-- Structure for view `user_calendar_schedule`
--
DROP TABLE IF EXISTS `user_calendar_schedule`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `user_calendar_schedule`  AS WITH date_series AS (SELECT `sc`.`sc_type` AS `sc_type`, concat(`u`.`user_fname`,' ',`u`.`user_lname`) AS `name`, `sc`.`start_date` AS `day`, `sc`.`end_date` AS `end_date`, `uc`.`user_calendar_status` AS `user_calendar_status`, `uc`.`user_calendar_id` AS `user_calendar_id` FROM ((`user_calendar` `uc` join `special_calendar` `sc` on(`uc`.`special_calendar_id` = `sc`.`special_calendar_id`)) join `user` `u` on(`uc`.`user_id` = `u`.`user_id`)) UNION ALL SELECT `date_series`.`sc_type` AS `sc_type`, `date_series`.`name` AS `name`, `date_series`.`day`+ interval 1 day AS `day + INTERVAL 1 DAY`, `date_series`.`end_date` AS `end_date`, `date_series`.`user_calendar_status` AS `user_calendar_status`, `date_series`.`user_calendar_id` AS `user_calendar_id` FROM `date_series` WHERE `date_series`.`day` < `date_series`.`end_date`)  SELECT `date_series`.`name` AS `name`, `date_series`.`sc_type` AS `type`, month(`date_series`.`day`) AS `month`, dayofmonth(`date_series`.`day`) AS `day` FROM `date_series` ORDER BY month(`date_series`.`day`) ASC, `date_series`.`user_calendar_id` ASC`user_calendar_id`  ;

-- --------------------------------------------------------

--
-- Structure for view `user_timeoff_schedule`
--
DROP TABLE IF EXISTS `user_timeoff_schedule`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `user_timeoff_schedule`  AS WITH date_series AS (SELECT `t`.`timeoff_type` AS `timeoff_type`, `u`.`user_fname` AS `user_fname`, `u`.`user_lname` AS `user_lname`, `ut`.`start_date` AS `day`, `ut`.`end_date` AS `end_date`, `ut`.`user_timeoff_id` AS `user_timeoff_id` FROM ((`user_timeoff` `ut` join `user` `u` on(`ut`.`user_id` = `u`.`user_id`)) join `timeoff` `t` on(`ut`.`timeoff_id` = `t`.`timeoff_id`)) WHERE `ut`.`status` = 1 UNION ALL SELECT `date_series`.`timeoff_type` AS `timeoff_type`, `date_series`.`user_fname` AS `user_fname`, `date_series`.`user_lname` AS `user_lname`, `date_series`.`day`+ interval 1 day AS `day + INTERVAL 1 DAY`, `date_series`.`end_date` AS `end_date`, `date_series`.`user_timeoff_id` AS `user_timeoff_id` FROM `date_series` WHERE `date_series`.`day` < `date_series`.`end_date`)  SELECT concat(`date_series`.`user_fname`,' ',`date_series`.`user_lname`) AS `name`, `date_series`.`timeoff_type` AS `type`, month(`date_series`.`day`) AS `month`, dayofmonth(`date_series`.`day`) AS `day` FROM `date_series` ORDER BY month(`date_series`.`day`) ASC, `date_series`.`user_timeoff_id` ASC`user_timeoff_id`  ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `activity_logs`
--
ALTER TABLE `activity_logs`
  ADD PRIMARY KEY (`log_id`);

--
-- Indexes for table `assignment`
--
ALTER TABLE `assignment`
  ADD PRIMARY KEY (`assignment_id`),
  ADD KEY `positionID` (`position_id`),
  ADD KEY `shift_id` (`shift_id`),
  ADD KEY `shift_position_ibfk_1` (`user_id`) USING BTREE;

--
-- Indexes for table `attendance`
--
ALTER TABLE `attendance`
  ADD PRIMARY KEY (`attendance_id`),
  ADD KEY `userID` (`user_id`);

--
-- Indexes for table `child`
--
ALTER TABLE `child`
  ADD PRIMARY KEY (`child_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `civil_service_eligibility`
--
ALTER TABLE `civil_service_eligibility`
  ADD PRIMARY KEY (`eligibility_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `department`
--
ALTER TABLE `department`
  ADD PRIMARY KEY (`department_id`);

--
-- Indexes for table `educational_background`
--
ALTER TABLE `educational_background`
  ADD PRIMARY KEY (`education_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `employee_documents`
--
ALTER TABLE `employee_documents`
  ADD PRIMARY KEY (`document_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `failed_jobs_uuid_unique` (`uuid`);

--
-- Indexes for table `fingerprint`
--
ALTER TABLE `fingerprint`
  ADD UNIQUE KEY `fmd` (`fmd`) USING HASH,
  ADD KEY `userID` (`user_id`);

--
-- Indexes for table `migrations`
--
ALTER TABLE `migrations`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `notices`
--
ALTER TABLE `notices`
  ADD PRIMARY KEY (`notice_id`);

--
-- Indexes for table `parent`
--
ALTER TABLE `parent`
  ADD PRIMARY KEY (`parent_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `personal_access_tokens_token_unique` (`token`),
  ADD KEY `personal_access_tokens_tokenable_type_tokenable_id_index` (`tokenable_type`,`tokenable_id`);

--
-- Indexes for table `position`
--
ALTER TABLE `position`
  ADD PRIMARY KEY (`position_id`),
  ADD KEY `departmentID` (`department_id`);

--
-- Indexes for table `quotes`
--
ALTER TABLE `quotes`
  ADD PRIMARY KEY (`quote_id`);

--
-- Indexes for table `shift`
--
ALTER TABLE `shift`
  ADD PRIMARY KEY (`shift_id`);

--
-- Indexes for table `special_calendar`
--
ALTER TABLE `special_calendar`
  ADD PRIMARY KEY (`special_calendar_id`);

--
-- Indexes for table `spouse`
--
ALTER TABLE `spouse`
  ADD PRIMARY KEY (`spouse_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `timeoff`
--
ALTER TABLE `timeoff`
  ADD PRIMARY KEY (`timeoff_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `users_email_unique` (`email`);

--
-- Indexes for table `user_calendar`
--
ALTER TABLE `user_calendar`
  ADD PRIMARY KEY (`user_calendar_id`),
  ADD KEY `userID` (`user_id`),
  ADD KEY `special_calendarID` (`special_calendar_id`);

--
-- Indexes for table `user_timeoff`
--
ALTER TABLE `user_timeoff`
  ADD PRIMARY KEY (`user_timeoff_id`),
  ADD KEY `timeoff_id` (`timeoff_id`),
  ADD KEY `user_id` (`user_id`);

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
-- AUTO_INCREMENT for table `activity_logs`
--
ALTER TABLE `activity_logs`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `assignment`
--
ALTER TABLE `assignment`
  MODIFY `assignment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `attendance`
--
ALTER TABLE `attendance`
  MODIFY `attendance_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `child`
--
ALTER TABLE `child`
  MODIFY `child_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `civil_service_eligibility`
--
ALTER TABLE `civil_service_eligibility`
  MODIFY `eligibility_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `department`
--
ALTER TABLE `department`
  MODIFY `department_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `educational_background`
--
ALTER TABLE `educational_background`
  MODIFY `education_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `employee_documents`
--
ALTER TABLE `employee_documents`
  MODIFY `document_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `migrations`
--
ALTER TABLE `migrations`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notices`
--
ALTER TABLE `notices`
  MODIFY `notice_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `parent`
--
ALTER TABLE `parent`
  MODIFY `parent_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `position`
--
ALTER TABLE `position`
  MODIFY `position_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `quotes`
--
ALTER TABLE `quotes`
  MODIFY `quote_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `shift`
--
ALTER TABLE `shift`
  MODIFY `shift_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `special_calendar`
--
ALTER TABLE `special_calendar`
  MODIFY `special_calendar_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `spouse`
--
ALTER TABLE `spouse`
  MODIFY `spouse_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `timeoff`
--
ALTER TABLE `timeoff`
  MODIFY `timeoff_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(1) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_calendar`
--
ALTER TABLE `user_calendar`
  MODIFY `user_calendar_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_timeoff`
--
ALTER TABLE `user_timeoff`
  MODIFY `user_timeoff_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `work_experience`
--
ALTER TABLE `work_experience`
  MODIFY `experience_id` int(11) NOT NULL AUTO_INCREMENT;

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

--
-- Constraints for table `attendance`
--
ALTER TABLE `attendance`
  ADD CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `child`
--
ALTER TABLE `child`
  ADD CONSTRAINT `child_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `civil_service_eligibility`
--
ALTER TABLE `civil_service_eligibility`
  ADD CONSTRAINT `civil_service_eligibility_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `educational_background`
--
ALTER TABLE `educational_background`
  ADD CONSTRAINT `educational_background_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `employee_documents`
--
ALTER TABLE `employee_documents`
  ADD CONSTRAINT `employee_documents_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `fingerprint`
--
ALTER TABLE `fingerprint`
  ADD CONSTRAINT `fingerprint_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `parent`
--
ALTER TABLE `parent`
  ADD CONSTRAINT `parent_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `position`
--
ALTER TABLE `position`
  ADD CONSTRAINT `position_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`);

--
-- Constraints for table `spouse`
--
ALTER TABLE `spouse`
  ADD CONSTRAINT `spouse_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `user_timeoff`
--
ALTER TABLE `user_timeoff`
  ADD CONSTRAINT `user_timeoff_ibfk_1` FOREIGN KEY (`timeoff_id`) REFERENCES `timeoff` (`timeoff_id`),
  ADD CONSTRAINT `user_timeoff_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `work_experience`
--
ALTER TABLE `work_experience`
  ADD CONSTRAINT `work_experience_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
