-- =============================================================
-- IEMS Seed Data
-- Default departments, shifts, and positions for initial setup.
-- Run AFTER V1__initial_schema.sql
-- =============================================================

USE iems_db;

-- -------------------------------------------------
-- Departments
-- -------------------------------------------------
INSERT INTO `department` (`department_id`, `department_name`, `description`, `status`) VALUES
(1, 'Human Resource Management Office', '', 1),
(2, 'Accounting', '', 1),
(3, 'Budgeting', '', 1),
(4, 'Cashiering', '', 1),
(5, 'Public Employment Service Office (PESO)', '', 1),
(6, 'Treasurer''s Office', '', 1),
(7, 'Mayor''s Office', '', 1);

-- -------------------------------------------------
-- Shifts
-- -------------------------------------------------
INSERT INTO `shift` (`shift_id`, `shift_name`, `start_time`, `end_time`, `status`) VALUES
(1, 'Morning Shift', '08:00:00', '12:00:00', 1),
(2, 'Afternoon Shift', '13:00:00', '17:00:00', 1),
(3, 'Day Shift', '08:00:00', '17:00:00', 1),
(4, 'Night Shift', '22:00:00', '06:00:00', 1),
(5, 'Flexible Shift', '00:00:00', '23:59:00', 1),
(6, 'Overload', '00:00:00', '00:00:00', 1);

-- -------------------------------------------------
-- Positions (HRMO, Accounting, Budgeting, Cashiering)
-- -------------------------------------------------
INSERT INTO `position` (`position_id`, `position_name`, `position_desc`, `department_id`, `position_pay`, `status`) VALUES
(1, 'Administrative Assistant I', 'Human Resource Management Assistant', 1, NULL, 1),
(2, 'Administrative Assistant II', 'Human Resource Management Assistant', 1, NULL, 1),
(3, 'Administrative Assistant III', 'Human Resource Management Assistant', 1, NULL, 1),
(4, 'Administrative Assistant IV', 'Human Resource Management Assistant', 1, NULL, 1),
(5, 'Administrative Assistant V', 'Human Resource Management Assistant', 1, NULL, 1),
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
