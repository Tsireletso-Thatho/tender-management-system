-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 05, 2026 at 12:51 PM
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
-- Database: `tsireletsothatho2333950`
--

-- --------------------------------------------------------

--
-- Table structure for table `awards`
--

CREATE TABLE `awards` (
  `award_id` int(11) NOT NULL,
  `tender_id` int(11) NOT NULL,
  `winning_bid_id` int(11) NOT NULL,
  `awarded_value` decimal(15,2) NOT NULL,
  `justification` text NOT NULL,
  `awarded_by` int(11) NOT NULL,
  `awarded_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `awards`
--


-- --------------------------------------------------------

--
-- Table structure for table `bids`
--

CREATE TABLE `bids` (
  `bid_id` int(11) NOT NULL,
  `tender_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `bid_amount` decimal(15,2) NOT NULL,
  `technical_compliance_statement` text NOT NULL,
  `proposed_timeline_days` int(11) NOT NULL,
  `supporting_document_path` varchar(500) DEFAULT NULL,
  `status` enum('SUBMITTED','EVALUATED','WON','NOT_WON') DEFAULT 'SUBMITTED',
  `submitted_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `bids`
--


-- --------------------------------------------------------

--
-- Table structure for table `email_logs`
--

CREATE TABLE `email_logs` (
  `log_id` int(11) NOT NULL,
  `tender_id` int(11) NOT NULL,
  `recipient_email` varchar(100) NOT NULL,
  `subject` varchar(200) NOT NULL,
  `outcome` varchar(20) NOT NULL,
  `sent_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` enum('SENT','FAILED') DEFAULT 'SENT'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `email_logs`
--


-- --------------------------------------------------------

--
-- Table structure for table `evaluation_committee`
--

CREATE TABLE `evaluation_committee` (
  `evaluator_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `department` varchar(50) DEFAULT 'Evaluation',
  `employee_number` varchar(20) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `evaluation_committee`
--

INSERT INTO `evaluation_committee` (`evaluator_id`, `user_id`, `full_name`, `department`, `employee_number`, `created_at`) VALUES
(1, 3, 'Thabo Mokoena', 'Evaluation', 'MPW-EVAL-001', '2026-04-12 10:26:21'),
(2, 4, 'Lerato Sefako', 'Evaluation', 'MPW-EVAL-002', '2026-04-12 10:26:21');

-- --------------------------------------------------------

--
-- Table structure for table `evaluation_scores`
--

CREATE TABLE `evaluation_scores` (
  `score_id` int(11) NOT NULL,
  `tender_id` int(11) NOT NULL,
  `bid_id` int(11) NOT NULL,
  `evaluator_id` int(11) NOT NULL,
  `technical_score` decimal(5,2) NOT NULL,
  `price_score` decimal(5,2) NOT NULL,
  `timeline_score` decimal(5,2) NOT NULL,
  `weighted_total` decimal(5,2) NOT NULL,
  `submitted_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `evaluation_scores`
--


-- --------------------------------------------------------

--
-- Table structure for table `procurement_officers`
--

CREATE TABLE `procurement_officers` (
  `officer_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `department` varchar(50) DEFAULT 'Procurement',
  `employee_number` varchar(20) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `procurement_officers`
--

INSERT INTO `procurement_officers` (`officer_id`, `user_id`, `full_name`, `department`, `employee_number`, `created_at`) VALUES
(1, 1, 'Mohapi Letsie', 'Procurement', 'MPW-OFF-001', '2026-04-12 10:26:21'),
(2, 2, 'Mamello Khosi', 'Procurement', 'MPW-OFF-002', '2026-04-12 10:26:21');

-- --------------------------------------------------------

--
-- Table structure for table `suppliers`
--

CREATE TABLE `suppliers` (
  `supplier_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `registration_number` varchar(20) NOT NULL,
  `company_name` varchar(150) NOT NULL,
  `physical_address` text NOT NULL,
  `contact_number` varchar(20) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `suppliers`
--


-- --------------------------------------------------------

--
-- Table structure for table `tenders`
--

CREATE TABLE `tenders` (
  `tender_id` int(11) NOT NULL,
  `reference_number` varchar(20) NOT NULL,
  `title` varchar(200) NOT NULL,
  `category` enum('CONSTRUCTION','ROADS','ELECTRICAL','PLUMBING','GENERAL_SERVICES') NOT NULL,
  `description` text NOT NULL,
  `estimated_value` decimal(15,2) NOT NULL,
  `submission_deadline` datetime NOT NULL,
  `status` enum('DRAFT','OPEN','CLOSED','UNDER_EVALUATION','EVALUATED','AWARDED') DEFAULT 'DRAFT',
  `notice_document_path` varchar(500) DEFAULT NULL,
  `created_by` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `published_at` datetime DEFAULT NULL,
  `closed_at` datetime DEFAULT NULL,
  `evaluation_started_at` datetime DEFAULT NULL,
  `evaluated_at` datetime DEFAULT NULL,
  `awarded_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `tenders`
--

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(64) NOT NULL,
  `role` enum('SUPPLIER','PROCUREMENT_OFFICER','EVALUATION_COMMITTEE') NOT NULL,
  `status` enum('ACTIVE','LOCKED') DEFAULT 'ACTIVE',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `email`, `password_hash`, `role`, `status`, `created_at`, `updated_at`) VALUES
(1, 'mohapi.letsie@mpw.gov.ls', 'e38b361ecbe5d43f8f2297588d1b49ad325873c49e52b67a42f7dcd8a72f2670', 'PROCUREMENT_OFFICER', 'ACTIVE', '2026-04-12 10:26:20', '2026-04-13 19:36:49'),
(2, 'mamello.khosi@mpw.gov.ls', 'd07db94576a49c47a8736c36b8d1b3a414b0ef5fc3c09fb0bf41dfe4cd1544c0', 'PROCUREMENT_OFFICER', 'ACTIVE', '2026-04-12 10:26:20', '2026-04-13 19:36:49'),
(3, 'thabo.mokoena@mpw.gov.ls', '7d57db1465c0f1279e0b5a3d70a82b518ae3e3bd7923c4baa1add6789ebcdcbf', 'EVALUATION_COMMITTEE', 'ACTIVE', '2026-04-12 10:26:21', '2026-04-13 19:36:49'),
(4, 'lerato.sefako@mpw.gov.ls', '341067342dfa93e61c7ed057d0052497e6056aa91f84528336dca41208879ab7', 'EVALUATION_COMMITTEE', 'ACTIVE', '2026-04-12 10:26:21', '2026-04-13 19:36:49');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `awards`
--
ALTER TABLE `awards`
  ADD PRIMARY KEY (`award_id`),
  ADD UNIQUE KEY `tender_id` (`tender_id`),
  ADD KEY `winning_bid_id` (`winning_bid_id`),
  ADD KEY `awarded_by` (`awarded_by`),
  ADD KEY `idx_tender_id` (`tender_id`);

--
-- Indexes for table `bids`
--
ALTER TABLE `bids`
  ADD PRIMARY KEY (`bid_id`),
  ADD UNIQUE KEY `unique_tender_supplier` (`tender_id`,`supplier_id`),
  ADD KEY `idx_tender_id` (`tender_id`),
  ADD KEY `idx_supplier_id` (`supplier_id`),
  ADD KEY `idx_status` (`status`);

--
-- Indexes for table `email_logs`
--
ALTER TABLE `email_logs`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `idx_tender_id` (`tender_id`);

--
-- Indexes for table `evaluation_committee`
--
ALTER TABLE `evaluation_committee`
  ADD PRIMARY KEY (`evaluator_id`),
  ADD UNIQUE KEY `user_id` (`user_id`),
  ADD UNIQUE KEY `employee_number` (`employee_number`),
  ADD KEY `idx_employee_number` (`employee_number`);

--
-- Indexes for table `evaluation_scores`
--
ALTER TABLE `evaluation_scores`
  ADD PRIMARY KEY (`score_id`),
  ADD UNIQUE KEY `unique_evaluator_bid` (`evaluator_id`,`bid_id`),
  ADD KEY `idx_tender_id` (`tender_id`),
  ADD KEY `idx_bid_id` (`bid_id`),
  ADD KEY `idx_evaluator_id` (`evaluator_id`);

--
-- Indexes for table `procurement_officers`
--
ALTER TABLE `procurement_officers`
  ADD PRIMARY KEY (`officer_id`),
  ADD UNIQUE KEY `user_id` (`user_id`),
  ADD UNIQUE KEY `employee_number` (`employee_number`),
  ADD KEY `idx_employee_number` (`employee_number`);

--
-- Indexes for table `suppliers`
--
ALTER TABLE `suppliers`
  ADD PRIMARY KEY (`supplier_id`),
  ADD UNIQUE KEY `user_id` (`user_id`),
  ADD UNIQUE KEY `registration_number` (`registration_number`),
  ADD KEY `idx_registration_number` (`registration_number`);

--
-- Indexes for table `tenders`
--
ALTER TABLE `tenders`
  ADD PRIMARY KEY (`tender_id`),
  ADD UNIQUE KEY `reference_number` (`reference_number`),
  ADD KEY `created_by` (`created_by`),
  ADD KEY `idx_reference_number` (`reference_number`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_category` (`category`),
  ADD KEY `idx_deadline` (`submission_deadline`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_email` (`email`),
  ADD KEY `idx_role` (`role`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `awards`
--
ALTER TABLE `awards`
  MODIFY `award_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `bids`
--
ALTER TABLE `bids`
  MODIFY `bid_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT for table `email_logs`
--
ALTER TABLE `email_logs`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `evaluation_committee`
--
ALTER TABLE `evaluation_committee`
  MODIFY `evaluator_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `evaluation_scores`
--
ALTER TABLE `evaluation_scores`
  MODIFY `score_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=77;

--
-- AUTO_INCREMENT for table `procurement_officers`
--
ALTER TABLE `procurement_officers`
  MODIFY `officer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `suppliers`
--
ALTER TABLE `suppliers`
  MODIFY `supplier_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `tenders`
--
ALTER TABLE `tenders`
  MODIFY `tender_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `awards`
--
ALTER TABLE `awards`
  ADD CONSTRAINT `awards_ibfk_1` FOREIGN KEY (`tender_id`) REFERENCES `tenders` (`tender_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `awards_ibfk_2` FOREIGN KEY (`winning_bid_id`) REFERENCES `bids` (`bid_id`),
  ADD CONSTRAINT `awards_ibfk_3` FOREIGN KEY (`awarded_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `bids`
--
ALTER TABLE `bids`
  ADD CONSTRAINT `bids_ibfk_1` FOREIGN KEY (`tender_id`) REFERENCES `tenders` (`tender_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `bids_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`) ON DELETE CASCADE;

--
-- Constraints for table `email_logs`
--
ALTER TABLE `email_logs`
  ADD CONSTRAINT `email_logs_ibfk_1` FOREIGN KEY (`tender_id`) REFERENCES `tenders` (`tender_id`) ON DELETE CASCADE;

--
-- Constraints for table `evaluation_committee`
--
ALTER TABLE `evaluation_committee`
  ADD CONSTRAINT `evaluation_committee_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `evaluation_scores`
--
ALTER TABLE `evaluation_scores`
  ADD CONSTRAINT `evaluation_scores_ibfk_1` FOREIGN KEY (`tender_id`) REFERENCES `tenders` (`tender_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `evaluation_scores_ibfk_2` FOREIGN KEY (`bid_id`) REFERENCES `bids` (`bid_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `evaluation_scores_ibfk_3` FOREIGN KEY (`evaluator_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `procurement_officers`
--
ALTER TABLE `procurement_officers`
  ADD CONSTRAINT `procurement_officers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `suppliers`
--
ALTER TABLE `suppliers`
  ADD CONSTRAINT `suppliers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `tenders`
--
ALTER TABLE `tenders`
  ADD CONSTRAINT `tenders_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
