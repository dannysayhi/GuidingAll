-- phpMyAdmin SQL Dump
-- version 4.4.14
-- http://www.phpmyadmin.net
--
-- 主機: 127.0.0.1
-- 產生時間： 2016-04-15 18:50:03
-- 伺服器版本: 5.6.26
-- PHP 版本： 5.6.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 資料庫： `guidingall`
--

-- --------------------------------------------------------

--
-- 資料表結構 `friendship`
--

CREATE TABLE IF NOT EXISTS `friendship` (
  `uid` int(11) NOT NULL,
  `user` varchar(16) DEFAULT NULL,
  `userFriend` varchar(16) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 資料表的匯出資料 `friendship`
--

INSERT INTO `friendship` (`uid`, `user`, `userFriend`) VALUES
(1, 'Danny', 'Samuel'),
(2, 'Phoebus', 'Samuel'),
(3, 'Samuel', 'Danny'),
(4, 'Samuel', 'Phoebus');

-- --------------------------------------------------------

--
-- 資料表結構 `lostdata`
--

CREATE TABLE IF NOT EXISTS `lostdata` (
  `Id` int(11) NOT NULL,
  `Owner` varchar(45) NOT NULL,
  `ItemName` varchar(45) NOT NULL,
  `Location` varchar(45) NOT NULL DEFAULT 'Location',
  `PNGsrc` varchar(20) NOT NULL DEFAULT 'nofloordata.png',
  `GeoPoint` varchar(50) DEFAULT NULL,
  `FoundTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

--
-- 資料表的匯出資料 `lostdata`
--

INSERT INTO `lostdata` (`Id`, `Owner`, `ItemName`, `Location`, `PNGsrc`, `GeoPoint`, `FoundTime`) VALUES
(1, 'Danny', '鑰匙', '資訊電機學院', 'floor.png', '[24.179261, 120.649530]', '2015-10-02 13:15:00'),
(2, 'Danny', '鑰匙', 'Location', 'floor.png', '[24.178992, 120.648312]', '2015-10-02 15:26:00'),
(3, 'Danny', '鑰匙', '逢甲大學警衛室', 'floor.png', '[24.178799, 120.646615]', '2015-10-02 15:27:00'),
(4, 'Danny', '深藍錢包', '無線網路實驗室 - Wireless Network Lab', 'floor1.png', '[24.165541, 120.643719]', '2015-12-17 10:20:52'),
(5, 'Danny', '深藍錢包', '第三國際會議廳 - 3th International Conference Hall', 'floor12.png', '[24.165541, 120.643719]', '2015-12-17 10:21:00'),
(6, 'Danny', '深藍錢包', '專題研究室 - Topics Research', 'floor123.png', '[24.165541, 120.643719]', '2015-12-17 10:21:11'),
(7, 'Danny', '深藍錢包', '專題研究室 - Topics Research', 'floor3.png', '[24.165541, 120.643719]', '2015-12-17 10:21:41'),
(8, 'Danny', '深藍錢包', '第三國際會議廳 - 3th International Conference Hall', 'floor32.png', '[24.165541, 120.643719]', '2015-12-17 10:22:02');

-- --------------------------------------------------------

--
-- 資料表結構 `project`
--

CREATE TABLE IF NOT EXISTS `project` (
  `Id` int(11) NOT NULL,
  `Owner` varchar(11) NOT NULL,
  `Name` text NOT NULL,
  `LostAddr` text,
  `LostDate` date DEFAULT NULL,
  `Describe` text NOT NULL,
  `Phone` varchar(12) DEFAULT NULL,
  `Reward` int(11) NOT NULL,
  `UploadDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `FoundDate` datetime DEFAULT NULL,
  `Count` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

--
-- 資料表的匯出資料 `project`
--

INSERT INTO `project` (`Id`, `Owner`, `Name`, `LostAddr`, `LostDate`, `Describe`, `Phone`, `Reward`, `UploadDate`, `FoundDate`, `Count`) VALUES
(1, 'Danny', '鑰匙', '資訊電機學院', '2015-10-02', '遺失在電腦教室忘記拿走', '0922123456', 500, '2015-10-02 10:05:33', '2015-10-02 15:27:00', 262),
(2, 'Danny', '法鬥QQ', '大鵬新城公園', '2015-10-02', '', '0922123456', 50000, '2015-10-02 10:05:33', '0000-00-00 00:00:00', 162),
(3, 'Danny', '捷安特腳踏車', '台中勤美', '2015-09-28', '', '0922123456', 2000, '2015-09-28 22:52:13', '0000-00-00 00:00:00', 500),
(4, 'Samuel', 'Dannyy', '台中勤美', '2015-09-27', '', '0933123235', 30000, '2015-09-27 21:05:23', '0000-00-00 00:00:00', 623),
(5, 'Samuel', '錢包', '台中新光三越', '2015-09-22', '', '0933123235', 500, '2015-09-22 10:08:22', '2015-10-01 14:23:43', 523),
(6, 'Phoebus', '阿土伯', '逢甲夜市', '2015-09-21', '在逢甲夜市附近走散後就找不到人了', '0943648532', 30000, '2015-09-21 19:20:53', '2015-09-30 13:52:43', 652),
(7, 'Phoebus', '鑰匙Key', '西門町', '2015-09-13', '好像是在西門町附近掉了', '0943648532', 700, '2015-09-13 22:10:46', '0000-00-00 00:00:00', 752),
(8, 'Samuel', '登山包', '象山', '2015-08-27', '爬象山的時候忘了帶走之後就遺失了', '0933123235', 3000, '2015-08-27 13:42:55', '0000-00-00 00:00:00', 523),
(9, 'JJChung', 'Pokey', '饒河街', '2015-08-26', '', '0948323456', 50000, '2015-08-26 10:26:23', '2015-08-30 13:52:43', 632),
(10, 'Sanuel', 'Piggy', '逢甲公園', '2015-07-29', '', '0933123235', 50000, '2015-07-29 13:22:49', '2015-08-01 16:12:43', 523),
(11, 'Danny', '小芳', '逢甲資電館', '2015-10-02', '在資電館走失', '0912368324', 100000, '2015-10-02 15:33:29', '0000-00-00 00:00:00', 208),
(12, 'Danny', '深藍錢包', '台中新光三越', '2015-12-17', '在新光三越一樓遺失', '0912368324', 500, '2015-12-17 10:20:33', NULL, 0),
(13, 'Danny', '深藍錢包', '台中新光三越', '2016-04-15', '在新光三越一樓遺失', '0912368324', 500, '2016-04-15 16:44:24', NULL, 0),
(14, 'Danny', '深藍錢包', '台中新光三越', '2016-04-15', '在新光三越一樓遺失', '0912368324', 500, '2016-04-15 18:09:10', NULL, 0);

-- --------------------------------------------------------

--
-- 資料表結構 `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `Name` varchar(10) NOT NULL,
  `Password` varchar(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- 資料表的匯出資料 `user`
--

INSERT INTO `user` (`Name`, `Password`) VALUES
('Danny', '123'),
('Phoebus', '456'),
('Samuel', '789'),
('Daniel', '000');

-- --------------------------------------------------------

--
-- 資料表結構 `useritem`
--

CREATE TABLE IF NOT EXISTS `useritem` (
  `Uid` int(11) NOT NULL,
  `itemUuid` varchar(45) DEFAULT NULL,
  `itemName` varchar(45) DEFAULT NULL,
  `userName` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 資料表的匯出資料 `useritem`
--

INSERT INTO `useritem` (`Uid`, `itemUuid`, `itemName`, `userName`) VALUES
(0, '74278bda-b644-4520-8f0c-720eaf059935', '小芳', 'Danny'),
(1, 'fa794de0-23f0-41e5-b8c6-ae7a6728fe1b', '亮黃錢包', 'Danny'),
(2, '303831AA-B644-4520-8F0C-720EAF059935', '錢包', 'Phoebus'),
(3, '74279bda-B644-4520-8f0c-720EAF059935', '深藍錢包', 'Danny');

--
-- 已匯出資料表的索引
--

--
-- 資料表索引 `friendship`
--
ALTER TABLE `friendship`
  ADD PRIMARY KEY (`uid`);

--
-- 資料表索引 `lostdata`
--
ALTER TABLE `lostdata`
  ADD PRIMARY KEY (`Id`);

--
-- 資料表索引 `project`
--
ALTER TABLE `project`
  ADD PRIMARY KEY (`Id`);

--
-- 資料表索引 `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`Name`);

--
-- 資料表索引 `useritem`
--
ALTER TABLE `useritem`
  ADD PRIMARY KEY (`Uid`);

--
-- 在匯出的資料表使用 AUTO_INCREMENT
--

--
-- 使用資料表 AUTO_INCREMENT `lostdata`
--
ALTER TABLE `lostdata`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=9;
--
-- 使用資料表 AUTO_INCREMENT `project`
--
ALTER TABLE `project`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=15;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
