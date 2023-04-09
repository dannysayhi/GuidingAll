-- phpMyAdmin SQL Dump
-- version 4.3.11
-- http://www.phpmyadmin.net
--
-- 主機: 127.0.0.1
-- 產生時間： 2015 �?10 ??06 ??05:15
-- 伺服器版本: 5.6.24
-- PHP 版本： 5.6.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- 資料庫： `guidingall`
--
CREATE DATABASE IF NOT EXISTS `guidingall` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `guidingall`;

-- --------------------------------------------------------

--
-- 資料表結構 `project`
--

CREATE TABLE IF NOT EXISTS `project` (
  `Id` int(11) NOT NULL,
  `Name` text NOT NULL,
  `Describe` text NOT NULL,
  `Reward` int(11) NOT NULL,
  `UploadDate` datetime NOT NULL,
  `FoundDate` datetime NOT NULL,
  `Count` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

--
-- 資料表的匯出資料 `project`
--

INSERT INTO `project` (`Id`, `Name`, `Describe`, `Reward`, `UploadDate`, `FoundDate`, `Count`) VALUES
(1, 'danny鑰匙', '', 500, '2015-10-02 10:05:33', '0000-00-00 00:00:00', 262),
(2, '法鬥QQ', '', 50000, '2015-10-02 10:05:33', '0000-00-00 00:00:00', 162),
(3, '捷安特腳踏車', '', 2000, '2015-09-28 22:52:13', '0000-00-00 00:00:00', 500),
(4, 'dannyy', '', 30000, '2015-09-27 21:05:23', '0000-00-00 00:00:00', 623),
(5, 'Samuel錢包', '', 500, '2015-09-22 10:08:22', '0000-00-00 00:00:00', 523),
(6, '阿土伯', '在逢甲夜市附近走散後就找不到人了', 30000, '2015-09-21 19:20:53', '2015-09-30 13:52:43', 652),
(7, '鑰匙Key', '好像是在西門町附近掉了', 700, '2015-09-13 22:10:46', '0000-00-00 00:00:00', 752),
(8, '登山包', '爬象山的時候忘了帶走之後就遺失了', 3000, '2015-08-27 13:42:55', '0000-00-00 00:00:00', 523),
(9, 'Pokey', '', 50000, '2015-08-26 10:26:23', '2015-08-30 13:52:43', 632),
(10, 'Piggy', '', 50000, '2015-07-29 13:22:49', '2015-08-01 16:12:43', 523),
(11, '小芳', '', 100000, '2015-10-02 15:33:29', '0000-00-00 00:00:00', 208);

--
-- 已匯出資料表的索引
--

--
-- 資料表索引 `project`
--
ALTER TABLE `project`
  ADD PRIMARY KEY (`Id`);

--
-- 在匯出的資料表使用 AUTO_INCREMENT
--

--
-- 使用資料表 AUTO_INCREMENT `project`
--
ALTER TABLE `project`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=12;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
