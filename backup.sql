-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: May 12, 2026 at 08:51 AM
-- Server version: 9.1.0
-- PHP Version: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `marjane_ems`
--

--
-- Dumping data for table `availability`
--
INSERT INTO `departements` (`id`, `description`, `name`) VALUES
(1, 'IT Department - Hardware and Software Support', 'Information Technology'),
(2, 'HR Department - Recruitment and Staff Management', 'Human Resources'),
(3, 'Finance Department - Accounting and Financial Planning', 'Finance'),
(4, 'Sales Department - Customer Relations and Orders', 'Sales');

INSERT INTO `team_groups` (`id`, `name`, `number_of_members`) VALUES
(1, 'TS_IT', 1),
(2, 'STOCK', 0),
(3, 'RH', 0),
(4, 'SECURITY', 0),
(5, 'LOGISTICS', 0),
(6, 'OTHER', 0);


INSERT INTO `users` (`id`, `created_at`, `eid`, `email`, `first_name`, `last_name`, `password`, `phone`, `role`, `status`, `updated_at`, `username`, `department_id`, `team_group_id`) VALUES
(1, '2026-04-28 11:59:08.199616', 'A001', 'admin@marjane.ma', 'Ahmed', 'Alami', '$2a$10$aNDmG9n0qmKSPWl16asoZuEcK7VI1twwTT7/4S0/0ggrpgq.4tRN.', '', 'ADMIN', 'ACTIVE', '2026-05-08 10:59:02.791268', 'admin.user', 1, NULL),
(2, '2026-05-02 10:55:22.879229', 'T001', 'm@marjane.ma', 'Mohamed', 'Nejjari', '$2a$10$1RODgZucPnCgzHNQkVA7.O6pzS08sXnJhb3QkNwZZsdCxBxfzP.U2', '', 'TECHNICIAN', 'ACTIVE', '2026-05-09 09:58:10.059983', 'm.user', 1, 1),
(3, '2026-05-04 10:53:10.850568', 'E001', 'test@test.com', 'Test', 'User', '$2a$10$FsdzdzT98J5styN60lNw9emJxOYaa.Jm0HuFc//VgHQvm4eCsMdy6', NULL, 'EMPLOYEE', 'ACTIVE', '2026-05-09 11:11:17.559804', 'testuser', NULL, NULL),
(4, '2026-05-04 10:54:18.302607', 'E002', 'test2@test.com', 'Test2', 'User2', '$2a$10$VMNZhoGFIVyiiiMWW94iY.146C0TP2B.U6XheG/ajl67nF7XvbXHW', NULL, 'EMPLOYEE', 'ACTIVE', '2026-05-04 10:54:18.302607', 'testuser2', NULL, NULL),
(6, '2026-05-04 11:01:23.348664', 'E004', 'deltest@test.com', 'DelTest', 'Emp', '$2a$10$vItNBpU62LS6domKO35cVepAkUeU/nHaiYYUoK6I5bxeUbupKnbKu', '111', 'EMPLOYEE', 'INACTIVE', '2026-05-07 13:21:18.390214', 'demp', 3, NULL),
(7, '2026-05-04 11:02:23.157328', 'E005', 'ptest999@test.com', 'PTest', 'Emp', '$2a$10$1gTM6h/flH89aKXcaxmBm.ksIF2hvG4/daOGCl5QfUGMBiefqa32q', '111', 'EMPLOYEE', 'ACTIVE', '2026-05-04 11:02:23.157328', 'pemp', NULL, NULL),
(8, '2026-05-04 11:02:59.455075', 'E006', 'fulltest@test.com', 'Updated', 'User', '$2a$10$yR9LbBXYEDabQ0d6ENzC8eob3qUc/PQwQH.4V/l3SN1bdk3s.4gB.', '666', 'EMPLOYEE', 'ACTIVE', '2026-05-07 13:10:07.284489', 'fuser', NULL, NULL),
(9, '2026-05-04 11:03:05.341363', 'E007', 'deltest2@test.com', 'DelTest2', 'User', '$2a$10$e9fYoPtJZdJ3Im0Ppk.CWe6QUTrBXU4UEcgbPQju4S8KS1zWWIRWe', '777', 'EMPLOYEE', 'ACTIVE', '2026-05-04 11:03:05.341363', 'duser', NULL, NULL),
(12, '2026-05-11 11:26:04.214989', 'E008', 's.saadaoui@marjane.ma', 'Said', 'Saadaoui', '$2a$10$SRgGUQ8UBkFZXKRqxFLoIOGAl60eka6Nda0RYKTZGMdk69uwXiGZK', '0666666666', 'EMPLOYEE', 'ON_LEAVE', '2026-05-11 11:45:38.743198', 'ssaadaoui', 1, NULL);

INSERT INTO `availability` (`id`, `status`, `update_time`, `user_id`) VALUES
(1, 'UNAVAILABLE', '2026-05-08 10:21:38.433117', 3),
(2, 'UNAVAILABLE', '2026-05-08 10:43:22.271607', 1),
(3, 'UNAVAILABLE', '2026-05-11 11:45:38.787077', 12);

--
-- Dumping data for table `departements`
--


--
-- Dumping data for table `leaves`
--

INSERT INTO `leaves` (`id`, `created_at`, `end_date`, `start_date`, `status`, `subject`, `type`, `updated_at`, `approver_id`, `user_id`) VALUES
(4, '2026-05-08 10:52:26.933764', '2026-05-17', '2026-05-15', 'APPROVED', 'Approval lock test', 'ANNUAL', '2026-05-08 10:53:06.379355', 1, 1),
(6, '2026-05-11 11:37:11.931518', '2026-06-11', '2026-05-11', 'APPROVED', 'test', 'ANNUAL', '2026-05-11 11:45:38.743198', 1, 12);

--
-- Dumping data for table `team_groups`
--


--
-- Dumping data for table `tickets`
--

INSERT INTO `tickets` (`id`, `category`, `created_at`, `description`, `priority`, `resolved_at`, `status`, `title`, `updated_at`, `creator_id`, `technician_id`) VALUES
(1, NULL, '2026-05-06 10:11:14.631473', 'created during live test', NULL, NULL, 'IN_PROGRESS', 'Test ticket from script', '2026-05-07 13:21:50.400529', 3, NULL),
(2, NULL, '2026-05-07 13:34:45.265842', 'Toner need to be replaced', NULL, NULL, 'PENDING', 'Printer Toner issue', '2026-05-07 13:34:45.265842', 3, NULL),
(3, 'STOCK', '2026-05-08 09:54:11.401798', 'Testing that category and priority enum fields are saved correctly to database', 'MEDIUM', NULL, 'PENDING', 'Test Enum Mapping', '2026-05-09 10:48:11.769931', 3, NULL),
(4, 'TS_IT', '2026-05-09 10:01:45.918499', 'printer issue', 'MEDIUM', NULL, 'RESOLVED', 'Printer', '2026-05-09 10:02:27.653856', 2, 2),
(6, 'RH', '2026-05-11 11:28:27.860163', 'i have to be inactive', 'URGENT', NULL, 'PENDING', 'Inactive Me', '2026-05-11 11:28:27.860163', 12, NULL);

--
-- Dumping data for table `users`
--



/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
COMMIT;
